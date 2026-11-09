package com.marketplace.plv.order;

import com.marketplace.plv.buyer.Buyer;
import com.marketplace.plv.buyer.BuyerRepository;
import com.marketplace.plv.cart.CartRepository;
import com.marketplace.plv.cartitem.CartItem;
import com.marketplace.plv.cartitem.CartItemRepository;
import com.marketplace.plv.order.dto.OrderResponse;
import com.marketplace.plv.order.dto.OrderStatusUpdateRequest;
import com.marketplace.plv.orderitem.OrderItem;
import com.marketplace.plv.orderitem.OrderItemMapper;
import com.marketplace.plv.orderitem.OrderItemRepository;
import com.marketplace.plv.orderitem.dto.OrderItemResponse;
import com.marketplace.plv.product_stocks.ProductStocksRepository;
import com.marketplace.plv.seller.Seller;
import com.marketplace.plv.seller.SellerRepository;
import com.marketplace.plv.seller.dto.SellerOrderUpdateRequest;
import com.marketplace.plv.shared.auth.AuthCookieService;
import com.marketplace.plv.shared.entity.ApiResponse;
import com.marketplace.plv.shared.exceptions.ForbiddenAccessException;
import com.marketplace.plv.shared.exceptions.conflict.ConflictException;
import com.marketplace.plv.shared.exceptions.notfound.CartNotFoundException;
import com.marketplace.plv.shared.exceptions.notfound.NotFoundException;
import com.marketplace.plv.shared.exceptions.notfound.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

	private static final EnumSet<OrderStatus> CANCELLABLE_STATUSES = EnumSet.of(OrderStatus.PENDING);
	private static final EnumSet<OrderStatus> REFUNDABLE_STATUSES =
			EnumSet.of(OrderStatus.PAID, OrderStatus.CONFIRMED, OrderStatus.PACKED,
					OrderStatus.SHIPPED, OrderStatus.COMPLETED);

	private final OrderRepository orderRepository;
	private final OrderItemRepository orderItemRepository;
	private final OrderItemMapper orderItemMapper;
	private final OrderMapper orderMapper;
	private final BuyerRepository buyerRepository;
	private final SellerRepository sellerRepository;
	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	private final ProductStocksRepository productStocksRepository;
	private final AuthCookieService authCookieService;

	@Transactional
	public ApiResponse<List<OrderResponse>> checkout() {
		var buyer = currentBuyer();
		var cart = cartRepository.findByBuyerId(buyer.getId())
				.orElseThrow(() -> new CartNotFoundException("No cart found for this buyer"));
		var cartItems = cartItemRepository.findAllByCartIdWithProductAndSeller(cart.getId());
		if (cartItems.isEmpty()) {
			throw new ConflictException("Cannot check out an empty cart");
		}

		var sellersById = new LinkedHashMap<Long, Seller>();
		var itemsBySellerId = new LinkedHashMap<Long, List<CartItem>>();
		for (var cartItem : cartItems.stream()
				.sorted(Comparator.comparing(item -> item.getProduct().getId()))
				.toList()) {
			reserveStock(cartItem);
		}
		for (var cartItem : cartItems) {
			var seller = cartItem.getProduct().getStore().getSeller();
			sellersById.putIfAbsent(seller.getId(), seller);
			itemsBySellerId.computeIfAbsent(seller.getId(), ignored -> new ArrayList<>()).add(cartItem);
		}

		var responses = new ArrayList<OrderResponse>(itemsBySellerId.size());
		for (var entry : itemsBySellerId.entrySet()) {
			var sellerId = entry.getKey();
			var sellerItems = entry.getValue();
			var total = sellerItems.stream()
					.map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
					.reduce(BigDecimal.ZERO, BigDecimal::add);

			var order = orderRepository.save(orderMapper.createOrder(
					buyer, sellersById.get(sellerId), total, buyer.getDeliveryBuilding()));
			var orderItems = sellerItems.stream()
					.map(item -> orderItemMapper.fromCartItem(order, item))
					.toList();
			orderItemRepository.saveAll(orderItems);
			responses.add(toResponse(order, orderItems));
		}

		cartItemRepository.deleteAllByCartId(cart.getId());
		return ApiResponse.successful(List.copyOf(responses), "Order checkout completed");
	}

	@Transactional(readOnly = true)
	public List<OrderResponse> getBuyerOrders() {
		var buyer = currentBuyer();
		return orderRepository.findAllByBuyerIdOrderByCreatedAtDesc(buyer.getId()).stream()
				.map(this::toResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public OrderResponse getBuyerOrder(Long orderId) {
		var buyer = currentBuyer();
		var order = findOrder(orderId);
		if (!order.getBuyer().getId().equals(buyer.getId())) {
			throw new ForbiddenAccessException("You can only view your own orders");
		}
		return toResponse(order);
	}

	@Transactional
	public ApiResponse<OrderResponse> updateBuyerOrderStatus(
			Long orderId,
			OrderStatusUpdateRequest request
	) {
		var buyer = currentBuyer();
		var order = findOrderForUpdate(orderId);
		if (!order.getBuyer().getId().equals(buyer.getId())) {
			throw new ForbiddenAccessException("You can only update your own orders");
		}

		var target = request.status();
		if (target == OrderStatus.CANCELLED && CANCELLABLE_STATUSES.contains(order.getStatus())) {
			restoreStock(orderId);
		} else if (target == OrderStatus.REFUNDED && REFUNDABLE_STATUSES.contains(order.getStatus())) {
			restoreStock(orderId);
		} else {
			throw new ConflictException("This order cannot transition from " + order.getStatus() + " to " + target);
		}

		order.changeStatus(target);
		return ApiResponse.successful(toResponse(order), "Order status updated");
	}

	@Transactional(readOnly = true)
	public List<OrderResponse> getSellerOrders() {
		var seller = currentSeller();
		return orderRepository.findAllBySellerIdOrderByCreatedAtDesc(seller.getId()).stream()
				.map(this::toResponse)
				.toList();
	}

	@Transactional
	public ApiResponse<OrderResponse> updateSellerOrder(
			Long orderId,
			SellerOrderUpdateRequest request
	) {
		var seller = currentSeller();
		var order = findOrderForUpdate(orderId);
		if (order.getSeller() == null || !order.getSeller().getId().equals(seller.getId())) {
			throw new ForbiddenAccessException("You can only update orders containing your products");
		}
		if (request.status() == null && request.currentLocation() == null) {
			throw new IllegalArgumentException("Provide an order status or current location");
		}
		var location = request.currentLocation() == null ? null : request.currentLocation().trim();
		if (location != null && location.isEmpty()) {
			throw new IllegalArgumentException("Current location must not be blank");
		}
		if (location != null && EnumSet.of(
				OrderStatus.COMPLETED, OrderStatus.CANCELLED, OrderStatus.REFUNDED).contains(order.getStatus())) {
			throw new ConflictException("Cannot update logistics for a terminal order");
		}
		if (request.status() != null) {
			validateSellerTransition(order.getStatus(), request.status());
			order.changeStatus(request.status());
		}
		if (location != null) {
			order.updateCurrentLocation(location);
		}

		return ApiResponse.successful(toResponse(order), "Order logistics updated");
	}

	private Buyer currentBuyer() {
		var accountId = authCookieService.getUserIdFromContext();
		if (accountId == null) {
			throw new ForbiddenAccessException("Invalid session");
		}
		return buyerRepository.findByAccountId(accountId)
				.orElseThrow(() -> new UserNotFoundException("Buyer profile not found"));
	}

	private Seller currentSeller() {
		var accountId = authCookieService.getUserIdFromContext();
		if (accountId == null) {
			throw new ForbiddenAccessException("Invalid session");
		}
		return sellerRepository.findByAccountId(accountId)
				.orElseThrow(() -> new ForbiddenAccessException("Seller profile not found"));
	}

	private Order findOrder(Long orderId) {
		return orderRepository.findById(orderId)
				.orElseThrow(() -> new NotFoundException("Order not found"));
	}

	private Order findOrderForUpdate(Long orderId) {
		return orderRepository.findByIdForUpdate(orderId)
				.orElseThrow(() -> new NotFoundException("Order not found"));
	}

	private void reserveStock(CartItem cartItem) {
		var stockRows = productStocksRepository.findAllByProductIdForUpdate(cartItem.getProduct().getId());
		var available = stockRows.stream()
				.mapToLong(stockRow -> stockRow.getStockQuantity() == null ? 0 : stockRow.getStockQuantity())
				.sum();
		if (available < cartItem.getQuantity()) {
			throw new ConflictException("Insufficient stock for product " + cartItem.getProduct().getId());
		}
		var remaining = cartItem.getQuantity();
		for (var stockRow : stockRows) {
			var stockQuantity = stockRow.getStockQuantity() == null ? 0 : stockRow.getStockQuantity();
			var reserved = Math.min(stockQuantity, remaining);
			stockRow.setStockQuantity(stockQuantity - reserved);
			remaining -= reserved;
			if (remaining == 0) {
				break;
			}
		}
	}

	private void restoreStock(Long orderId) {
		for (var orderItem : orderItemRepository.findAllByOrderIdOrderById(orderId).stream()
				.sorted(Comparator.comparing(item -> item.getProduct().getId()))
				.toList()) {
			var stockRows = productStocksRepository.findAllByProductIdForUpdate(orderItem.getProduct().getId());
			if (stockRows.isEmpty()) {
				throw new NotFoundException("Stock record not found for product " + orderItem.getProduct().getId());
			}
			var firstStockRow = stockRows.getFirst();
			firstStockRow.setStockQuantity(Math.addExact(firstStockRow.getStockQuantity(), orderItem.getQuantity()));
		}
	}

	private void validateSellerTransition(OrderStatus current, OrderStatus target) {
		var next = switch (current) {
			case PENDING -> OrderStatus.PAID;
			case PAID -> OrderStatus.CONFIRMED;
			case CONFIRMED -> OrderStatus.PACKED;
			case PACKED -> OrderStatus.SHIPPED;
			case SHIPPED -> OrderStatus.COMPLETED;
			default -> null;
		};
		if (target != next) {
			throw new ConflictException("Invalid seller transition from " + current + " to " + target);
		}
	}

	private OrderResponse toResponse(Order order) {
		return toResponse(order, orderItemRepository.findAllByOrderIdOrderById(order.getId()));
	}

	private OrderResponse toResponse(Order order, List<OrderItem> items) {
		return orderMapper.entityToResponse(order, items.stream()
				.map(orderItemMapper::entityToResponse)
				.toList());
	}

}
