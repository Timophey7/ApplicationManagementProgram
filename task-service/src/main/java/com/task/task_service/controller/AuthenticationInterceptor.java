package com.task.task_service.controller;

import com.task.task_service.models.app.AppUser;
import com.task.task_service.models.enums.Role;
import com.task.task_service.repository.AppUserRepository;
import com.task.task_service.service.utils.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class AuthenticationInterceptor {

    private final JwtService jwtService;
    private final AppUserRepository appUserRepository;

    @ModelAttribute
    public void addUserDetails(HttpServletRequest request, Model model) {
        String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader.substring(7);
        String email = jwtService.extractUsername(token);
        AppUser appUserByUserEmail = appUserRepository.findAppUserByUserEmail(email);
        if (appUserByUserEmail == null){
            log.info("user is null");
        }else {
            log.info(appUserByUserEmail.toString());
            Role userRole = appUserByUserEmail.getUserRole();
            model.addAttribute("email", email);
            model.addAttribute("role", userRole);
        }
    }
}
