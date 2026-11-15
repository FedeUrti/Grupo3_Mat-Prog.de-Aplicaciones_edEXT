package com.grupo3_mat.mobile.web;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;

@WebFilter("/*")
public class MobileAccessFilter implements Filter {
    private static final String DEVICE_COOKIE = "edext_device";
    private static final String WEB_CONTEXT_PATH = getWebContextPath();
    private static final Pattern MOBILE_USER_AGENT =
            Pattern.compile("(?i)(android|iphone|ipod|ipad|mobile|windows phone)");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        if (!isMobileDevice(httpRequest)) {
            httpResponse.setHeader("Cache-Control", "no-store");
            httpResponse.sendRedirect(WEB_CONTEXT_PATH + ("/".equals(WEB_CONTEXT_PATH) ? "" : "/"));
            return;
        }

        if (hasMobileUserAgent(httpRequest) && !hasMobileCookie(httpRequest)) {
            String cookie = DEVICE_COOKIE
                    + "=mobile; Path=/; Max-Age=31536000; HttpOnly; SameSite=Lax";
            if (httpRequest.isSecure()) {
                cookie += "; Secure";
            }
            httpResponse.addHeader("Set-Cookie", cookie);
        }

        chain.doFilter(request, response);
    }

    private static boolean isMobileDevice(HttpServletRequest request) {
        return hasMobileCookie(request) || hasMobileUserAgent(request);
    }

    private static boolean hasMobileCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        return cookies != null && Arrays.stream(cookies)
                .anyMatch(cookie -> DEVICE_COOKIE.equals(cookie.getName())
                        && "mobile".equals(cookie.getValue()));
    }

    private static boolean hasMobileUserAgent(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return userAgent != null && MOBILE_USER_AGENT.matcher(userAgent).find();
    }

    private static String getWebContextPath() {
        String configured = System.getenv("EDEXT_WEB_CONTEXT_PATH");
        if (configured == null || configured.isBlank()) {
            return "/web";
        }
        String contextPath = configured.trim();
        if (!contextPath.startsWith("/") || contextPath.startsWith("//")
                || contextPath.contains("?") || contextPath.contains("#")) {
            throw new IllegalStateException(
                    "EDEXT_WEB_CONTEXT_PATH debe ser una ruta local, por ejemplo /web o /.");
        }
        while (contextPath.length() > 1 && contextPath.endsWith("/")) {
            contextPath = contextPath.substring(0, contextPath.length() - 1);
        }
        return contextPath;
    }
}
