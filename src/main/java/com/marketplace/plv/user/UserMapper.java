package com.marketplace.plv.user;

import com.marketplace.plv.shared.Mapper;
import com.marketplace.plv.user.dto.UserRequest;
import com.marketplace.plv.user.dto.UserResponse;
import org.springframework.stereotype.Component;

@Component
public final class UserMapper implements Mapper<User, UserRequest, UserResponse> {
}
