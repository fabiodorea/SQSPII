package com.sinqia.sqspii.interceptor;

import com.sinqia.sqspii.context.TenantContext;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class RequestInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object object) throws Exception {
        System.out.println("In preHandle we are Intercepting the Request");
        System.out.println("____________________________________________");
        String requestURI = request.getRequestURI();
        //String tenantID = request.getHeader("X-TenantID");
        //System.out.println("RequestURI::" + requestURI + " || Search for X-TenantID  :: " + tenantID);
        //System.out.println("____________________________________________");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            if (authentication.getPrincipal() instanceof KeycloakPrincipal) {
                KeycloakPrincipal<KeycloakSecurityContext> kp = (KeycloakPrincipal<KeycloakSecurityContext>) authentication.getPrincipal();
                // retrieving username here
                String username = kp.getKeycloakSecurityContext().getToken().getPreferredUsername();

                if (username == null) {
                    response.getWriter().write("preferred username not present in the JWT");
                    response.setStatus(400);
                    return false;
                }

                System.out.println("RequestURI::" + requestURI + " || Search for Preferred Username  :: " + username);
                TenantContext.setCurrentTenant(username);
            }
        }
        return true;
    }

    @Override
    public void postHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception {
        TenantContext.clear();
    }

}