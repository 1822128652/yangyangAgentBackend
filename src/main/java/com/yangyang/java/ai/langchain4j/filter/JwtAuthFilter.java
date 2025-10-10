package com.yangyang.java.ai.langchain4j.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;
import com.yangyang.java.ai.langchain4j.doctor.common.Result;
import com.yangyang.java.ai.langchain4j.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.checkerframework.checker.units.qual.C;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

// 10.9 add 创建拦截器
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    // 放行的接口路径
    private static final List<String> EXCLUDE_PATHS = List.of(
            "/doctor/login",
            "/doc.html"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return EXCLUDE_PATHS.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // 未携带 token
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("缺少 token，拒绝访问：{}", request.getRequestURI());
            writeUnauthorizedResponse(response, "未登录或登录已过期");
            return;
        }

        String token = authHeader.substring(7);
        try{
            Claims claims = JwtUtils.parseToken(token);
            // 保存用户信息到 request
            request.setAttribute("doctorId", claims.get("id"));
            request.setAttribute("doctorName", claims.get("name"));
            request.setAttribute("department", claims.get("department"));
            request.setAttribute("doctorUsername", claims.get("account"));
            filterChain.doFilter(request, response);
        }catch(Exception e){
            log.warn("token 无效：{}", e.getMessage());
            writeUnauthorizedResponse(response, "Token 无效或已过期，请重新登录");
        }
    }

    private void writeUnauthorizedResponse(HttpServletResponse response, String msg) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        new ObjectMapper().writeValue(response.getWriter(), Result.error(msg));
    }
}
