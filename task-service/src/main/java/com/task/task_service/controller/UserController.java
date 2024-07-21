package com.task.task_service.controller;

import com.task.task_service.models.app.AppUser;
import com.task.task_service.models.enums.Role;
import com.task.task_service.repository.AppUserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
@RequestMapping("/v1/tracker")
public class UserController {

    AppUserRepository appUserRepository;

    @PostMapping("/apps/{uniqueCode}/users/{userEmail}/setRole")
    public ResponseEntity<?> setUserRole(@PathVariable("userEmail") String userEmail, Model model){
        if (model.getAttribute("role") == Role.ADMIN){
            AppUser appUserByUserEmail = appUserRepository.findAppUserByUserEmail(userEmail);
            appUserByUserEmail.setUserRole(Role.ADMIN);
            appUserRepository.save(appUserByUserEmail);
            return ResponseEntity.ok(appUserByUserEmail);
        }else {
            return ResponseEntity.badRequest().body("you have not role for this act");
        }
    }


}
