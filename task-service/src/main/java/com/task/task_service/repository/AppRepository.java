package com.task.task_service.repository;

import com.task.task_service.models.app.App;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppRepository extends JpaRepository<App,Integer> {

    App findAppByName(String appName);

    Optional<App> findAppByUniqueCode(String uniqueCode);

}
