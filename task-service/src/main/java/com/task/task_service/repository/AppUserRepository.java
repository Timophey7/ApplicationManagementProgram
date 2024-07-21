package com.task.task_service.repository;

import com.task.task_service.models.app.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser,Integer> {

    AppUser findAppUserByUserEmail(String email);

}
