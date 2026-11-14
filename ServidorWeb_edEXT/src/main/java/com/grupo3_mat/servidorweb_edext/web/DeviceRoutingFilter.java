package com.grupo3_mat.servidorweb_edext.web;

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
public class DeviceRoutingFilter implements Filter {
    private static final String DEVICE_COOKIE = "edext_device";
    private static final String MOBILE_VALUE = "mobile";
    private static final String DESKTOP_VALUE = "desktop";
    private static final Pattern MOBILE_USER_AGENT =
            Pattern.compile("(?i)(android|iphone|ipod|ipad|mobile|windows phone)");
    private static final Pattern DESKTOP_USER_AGENT =
            Pattern.compile("(?i)(windows nt|macintosh|x11;.*linux x86_64|linux x86_64)");
    private static final Pattern MOBILE_PLATFORM =
            Pattern.compile("(?i)(android|ios)");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (isMobileDevice(httpRequest)) {
            rememberDevice(httpRequest, httpResponse, MOBILE_VALUE);
            if (isMobileImageRequest(httpRequest)) {
                chain.doFilter(request, response);
            } else {
                redirectToMobile(httpRequest, httpResponse);
            }
            return;
        }

        String rememberedDevice = getRememberedDevice(httpRequest);
        if (MOBILE_VALUE.equals(rememberedDevice)) {
            if (isMobileImageRequest(httpRequest)) {
                chain.doFilter(request, response);
            } else {
                redirectToMobile(httpRequest, httpResponse);
            }
            return;
        }
        if (DESKTOP_VALUE.equals(rememberedDevice)) {
            chain.doFilter(request, response);
            return;
        }

        if (isDesktopDevice(httpRequest)) {
            rememberDevice(httpRequest, httpResponse, DESKTOP_VALUE);
            chain.doFilter(request, response);
            return;
        }

        sendDeviceDetectionPage(httpResponse);
    }

    private static boolean isMobileDevice(HttpServletRequest request) {
        String platform = request.getHeader("Sec-CH-UA-Platform");
        String mobileHint = request.getHeader("Sec-CH-UA-Mobile");
        return hasMobileUserAgent(request)
                || (platform != null && MOBILE_PLATFORM.matcher(platform).find())
                || "?1".equals(mobileHint);
    }

    private static boolean hasMobileUserAgent(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return userAgent != null && MOBILE_USER_AGENT.matcher(userAgent).find();
    }

    private static boolean isDesktopDevice(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        String platform = request.getHeader("Sec-CH-UA-Platform");
        return (userAgent != null && DESKTOP_USER_AGENT.matcher(userAgent).find())
                || (platform != null && !MOBILE_PLATFORM.matcher(platform).find());
    }

    private static String getRememberedDevice(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        return Arrays.stream(cookies)
                .filter(cookie -> DEVICE_COOKIE.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    private static void rememberDevice(
            HttpServletRequest request, HttpServletResponse response, String device) {
        String header = DEVICE_COOKIE + "=" + device
                + "; Path=/; Max-Age=31536000; HttpOnly; SameSite=Lax";
        if (request.isSecure()) {
            header += "; Secure";
        }
        response.addHeader("Set-Cookie", header);
    }

    private static boolean isMobileImageRequest(HttpServletRequest request) {
        String requestPath = request.getRequestURI().substring(request.getContextPath().length());
        return "/imagenes-curso".equals(requestPath) || requestPath.startsWith("/imagenes-curso/");
    }

    private static void redirectToMobile(
            HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Cache-Control", "no-store");
        response.sendRedirect("/mobile/");
    }

    private static void sendDeviceDetectionPage(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store");
        response.getWriter().write("""
                <!doctype html>
                <html lang="es">
                <head>
                    <meta charset="utf-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1">
                    <title>edEXT</title>
                </head>
                <body>
                    <p>Abriendo edEXT…</p>
                    <script>
                        const touchDevice = navigator.maxTouchPoints > 0
                            || 'ontouchstart' in window;
                        const tabletSizedScreen = Math.min(screen.width, screen.height) <= 1024;
                        const mobileDevice = /android|iphone|ipad|ipod|mobile|windows phone/i
                            .test(navigator.userAgent)
                            || (touchDevice && tabletSizedScreen);
                        const device = mobileDevice ? 'mobile' : 'desktop';
                        document.cookie = 'edext_device=' + device
                            + '; Max-Age=31536000; Path=/; SameSite=Lax'
                            + (location.protocol === 'https:' ? '; Secure' : '');
                        location.replace(mobileDevice
                            ? '/mobile/'
                            : location.pathname + location.search + location.hash);
                    </script>
                </body>
                </html>
                """);
    }
}
