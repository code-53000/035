package com.sailing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sailing.common.BusinessException;
import com.sailing.common.JwtTokenUtil;
import com.sailing.dto.LoginReqDTO;
import com.sailing.dto.LoginRespDTO;
import com.sailing.dto.UserInfoDTO;
import com.sailing.entity.SysUser;
import com.sailing.mapper.SysUserMapper;
import com.sailing.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AuthServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements AuthService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public LoginRespDTO login(LoginReqDTO loginReqDTO) {
        if (!StringUtils.hasText(loginReqDTO.getUsername())) {
            throw new BusinessException("用户名不能为空");
        }
        if (!StringUtils.hasText(loginReqDTO.getPassword())) {
            throw new BusinessException("密码不能为空");
        }
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, loginReqDTO.getUsername());
        SysUser user = baseMapper.selectOne(wrapper);
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        if (!SysUser.STATUS_ENABLED.equals(user.getStatus())) {
            throw new BusinessException("账号已被禁用");
        }
        if (!passwordEncoder.matches(loginReqDTO.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        String token = jwtTokenUtil.generateToken(user.getId(), user.getUsername());
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setId(user.getId());
        userInfoDTO.setUsername(user.getUsername());
        userInfoDTO.setRealName(user.getRealName());
        userInfoDTO.setPhone(user.getPhone());
        userInfoDTO.setRole(user.getRole());
        userInfoDTO.setStatus(user.getStatus());
        LoginRespDTO respDTO = new LoginRespDTO();
        respDTO.setToken(token);
        respDTO.setUserInfo(userInfoDTO);
        respDTO.setUser(userInfoDTO);
        respDTO.setUserId(user.getId());
        respDTO.setUsername(user.getUsername());
        respDTO.setRealName(user.getRealName());
        respDTO.setRole(user.getRole());
        respDTO.setPhone(user.getPhone());
        return respDTO;
    }

    @Override
    public UserInfoDTO getCurrentUser(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        SysUser user = baseMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setId(user.getId());
        userInfoDTO.setUsername(user.getUsername());
        userInfoDTO.setRealName(user.getRealName());
        userInfoDTO.setPhone(user.getPhone());
        userInfoDTO.setRole(user.getRole());
        userInfoDTO.setStatus(user.getStatus());
        return userInfoDTO;
    }
}
