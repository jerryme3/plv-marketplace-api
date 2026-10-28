package com.marketplace.plv.category;

import com.marketplace.plv.category.dto.CategoryRequest;
import com.marketplace.plv.category.dto.CategoryResponse;
import com.marketplace.plv.shared.Mapper;
import org.springframework.stereotype.Component;

@Component
public final class CategoryMapper implements Mapper<Category, CategoryRequest, CategoryResponse> {

	@Override
	public Category reqToEntity(CategoryRequest request) {
		return Category.builder()
				.name(request.name())
				.description(request.description())
				.build();
	}

	@Override
	public CategoryResponse entityToResponse(Category category) {
		return new CategoryResponse(category.getId(), category.getName(), category.getDescription());
	}
}
