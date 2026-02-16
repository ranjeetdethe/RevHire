package com.revhire.security;

import com.revhire.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        HttpSession session = request.getSession();
        session.setAttribute("user", user);

        // Redirect based on role
        if (user.getRole() == User.UserRole.ADMIN) {
            response.sendRedirect("/admin/dashboard");
        } else if (user.getRole() == User.UserRole.EMPLOYER) {
            response.sendRedirect("/employer/dashboard");
        } else if (user.getRole() == User.UserRole.JOB_SEEKER) {
            response.sendRedirect("/seeker/dashboard");
        } else {
            response.sendRedirect("/home");
        }
    }
}
