package com.smartcourier.admin.integration;

import com.smartcourier.admin.dto.UserSummaryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "auth-service")
public interface AuthClient {

    @GetMapping("/api/auth/users")
    List<UserSummaryResponse> getUsers();
}
