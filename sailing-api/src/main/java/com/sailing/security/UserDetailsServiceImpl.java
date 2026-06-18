package com.sailing.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sailing.common.SecurityUser;
import com.sailing.entity.SysUser;
import com.sailing.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username)
        );
        if (sysUser == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        return new SecurityUser(sysUser);
    }
}
