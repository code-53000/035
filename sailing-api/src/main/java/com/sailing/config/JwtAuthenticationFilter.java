package com.sailing.config;

import cn.hutool.core.util.StrUtil;
import com.sailing.common.JwtTokenUtil;
import com.sailing.common.SecurityUser;
import com.sailing.entity.SysUser;
import com.sailing.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (StrUtil.isNotBlank(authHeader) && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                Long userId = jwtTokenUtil.getUserIdFromToken(token);
                if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    SysUser sysUser = sysUserMapper.selectById(userId);
                    if (sysUser != null && sysUser.getStatus() == 1) {
                        SecurityUser securityUser = new SecurityUser(sysUser);
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(securityUser, null, securityUser.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (Exception e) {
                logger.warn("JWT token解析失败: " + e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }
}
