package com.marketplace.plv.admin;

import com.marketplace.plv.admin.dto.RoleUpdateRequest;
import com.marketplace.plv.shared.entity.ApiResponse;
import com.marketplace.plv.user.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

	private final AdminService adminService;

	@GetMapping
	String helloAdmin() {
		return adminService.greetAdmin();
	}

	@PutMapping("/promote")
	ApiResponse<UserResponse> promoteUserToAdmin(@Valid @RequestBody RoleUpdateRequest request) {
		return adminService.promoteUserToAdmin(request);
	}

	@PutMapping("/demote")
	ApiResponse<UserResponse> demoteAdminToUser(@Valid @RequestBody RoleUpdateRequest request) {
		return adminService.demoteAdminToUser(request);
	}

}
