package com.sailing.controller;

import com.sailing.common.Result;
import com.sailing.common.SecurityUser;
import com.sailing.dto.LoginReqDTO;
import com.sailing.dto.LoginRespDTO;
import com.sailing.dto.UserInfoDTO;
import com.sailing.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public Result<LoginRespDTO> login(@Valid @RequestBody LoginReqDTO loginReqDTO) {
        LoginRespDTO loginRespDTO = authService.login(loginReqDTO);
        return Result.success(loginRespDTO);
    }

    @GetMapping("/me")
    public Result<UserInfoDTO> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        Long userId = securityUser.getUserId();
        UserInfoDTO userInfoDTO = authService.getCurrentUser(userId);
        return Result.success(userInfoDTO);
    }
}
