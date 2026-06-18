package com.sailing.service;

import com.sailing.dto.LoginReqDTO;
import com.sailing.dto.LoginRespDTO;
import com.sailing.dto.UserInfoDTO;

public interface AuthService {

    LoginRespDTO login(LoginReqDTO loginReqDTO);

    UserInfoDTO getCurrentUser(Long userId);
}
