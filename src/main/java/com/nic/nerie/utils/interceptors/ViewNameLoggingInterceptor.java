package com.nic.nerie.utils.interceptors;

import java.util.Arrays;
import java.util.Enumeration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class ViewNameLoggingInterceptor implements HandlerInterceptor{

    @Value("${app.interceptor.view-trace.enabled:true}")
    private boolean enabled;

    private static final String START_TIME = "startTime";

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

        if (!(handler instanceof HandlerMethod)) {
            return true; // ignore css/js/images
        }

        request.setAttribute(START_TIME, System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {

        if (!(handler instanceof HandlerMethod)) {
            return;
        }

        HandlerMethod hm = (HandlerMethod) handler;

        String controller = hm.getBeanType().getSimpleName();
        String method = hm.getMethod().getName();
        String httpMethod = request.getMethod();
        String uri = request.getRequestURI();

        int status = response.getStatus();

        long start = (long) request.getAttribute(START_TIME);
        long time = System.currentTimeMillis() - start;

        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));

        System.out.println("\n");
        System.out.println("════════════════════════════════════════════════════════════");
        System.out.println("🧭 SPRING MVC REQUEST TRACE");
        System.out.println("════════════════════════════════════════════════════════════");

        System.out.println("➡ URL        : " + httpMethod + " " + uri);
        System.out.println("🎯 Controller : " + controller);
        System.out.println("🔧 Method     : " + method + "()");

        if (isAjax) {
            System.out.println("⚡ Type       : AJAX");
        }

        System.out.println("📡 Status     : " + status);
        System.out.println("⏱ Time       : " + time + " ms");

        System.out.println("════════════════════════════════════════════════════════════");
        System.out.println("\n");
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) {

        if (!(handler instanceof HandlerMethod)) return;

        if (modelAndView == null) return;

        String view = modelAndView.getViewName();

        System.out.println("🖼️ View       : " + view);

        if (view != null && !view.startsWith("redirect:")) {
            System.out.println("📁 Template   : templates/" + view + ".html");
        } else {
            System.out.println("🔁 Redirect   : " + view);
        }

        // -------- Model Attributes --------
    // if (!modelAndView.getModel().isEmpty()) {
    //     System.out.println("📦  Model Attributes:");
    //     modelAndView.getModel().forEach((k, v) -> {
    //         System.out.println("     • " + k + " = " + v);
    //     });
    // }

    // -------- Query Parameters --------
    // if (!request.getParameterMap().isEmpty()) {
    //     System.out.println("🔍  Request Parameters:");
    //     request.getParameterMap().forEach((k, v) -> {
    //         System.out.println("     • " + k + " = " + Arrays.toString(v));
    //     });
    // }

    // -------- Session Attributes --------
    // HttpSession session = request.getSession(false);
    // if (session != null) {
    //     Enumeration<String> names = session.getAttributeNames();

    //     if (names.hasMoreElements()) {
    //         System.out.println("🗄️  Session Attributes:");

    //         while (names.hasMoreElements()) {
    //             String name = names.nextElement();
    //             System.out.println("     • " + name + " = " + session.getAttribute(name));
    //         }
    //     }
    // }
    }
}

