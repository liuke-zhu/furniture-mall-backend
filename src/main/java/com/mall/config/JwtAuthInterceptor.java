package com.mall.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mall.common.api.Result;
import com.mall.common.api.ResultCode;
import com.mall.common.constant.SecurityConstants;
import com.mall.common.util.JwtUtils;
import com.mall.common.util.RequestContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class JwtAuthInterceptor implements HandlerInterceptor {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        if (isWhitelistPath(path)) {
            return true;
        }

        String authorization = request.getHeader(SecurityConstants.TOKEN_HEADER);
        if (!StringUtils.hasText(authorization) || !authorization.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            writeUnauthorized(response, "未登录或 token 缺失");
            return false;
        }

        try {
            String token = authorization.substring(SecurityConstants.TOKEN_PREFIX.length());
            Claims claims = jwtUtils.parseToken(token);
            Long userId = claims.get(SecurityConstants.USER_ID_CLAIM, Long.class);
            String username = claims.get(SecurityConstants.USERNAME_CLAIM, String.class);
            String role = claims.get(SecurityConstants.ROLE_CLAIM, String.class);
            RequestContext.set(userId, username, role);
        } catch (Exception exception) {
            writeUnauthorized(response, "token 无效或已过期");
            return false;
        }

        if (isAdminPath(path) && !SecurityConstants.ROLE_ADMIN.equals(RequestContext.getRole())) {
            writeForbidden(response, "无权限访问该资源");
            return false;
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        RequestContext.clear();
    }

    private boolean isWhitelistPath(String path) {
        return PATH_MATCHER.match("/api/auth/register", path)
                || PATH_MATCHER.match("/api/auth/login", path)
                || PATH_MATCHER.match("/api/admin/auth/**", path)
                || PATH_MATCHER.match("/api/health", path)
                || PATH_MATCHER.match("/api/home/**", path)
                || PATH_MATCHER.match("/api/category/**", path)
                || PATH_MATCHER.match("/api/product/**", path);
    }

    private boolean isAdminPath(String path) {
        return PATH_MATCHER.match("/api/admin/**", path)
                && !PATH_MATCHER.match("/api/admin/auth/**", path);
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        writeError(response, HttpServletResponse.SC_UNAUTHORIZED, ResultCode.UNAUTHORIZED, message);
    }

    private void writeForbidden(HttpServletResponse response, String message) throws IOException {
        writeError(response, HttpServletResponse.SC_FORBIDDEN, ResultCode.FORBIDDEN, message);
    }

    private void writeError(HttpServletResponse response, int status, ResultCode resultCode, String message)
            throws IOException {
        response.setStatus(status);
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(Result.failed(resultCode, message)));
    }
}
