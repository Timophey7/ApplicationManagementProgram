package com.task.task_service.repository;

import com.task.task_service.models.app.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Integer> {

    List<AppUser> findAppUsersByUserEmail(String email);

    AppUser findUserByUserEmailAndAppUniqueCode(String email, String uniqueCode);

}
