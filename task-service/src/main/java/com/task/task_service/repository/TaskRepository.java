package com.task.task_service.repository;

import com.task.task_service.models.tasks.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task,Integer> {


    @Query(value = "SELECT *\n" +
            "FROM controloftask.app_tasks\n" +
            "WHERE app_id = :appId\n" +
            "ORDER BY priority_enums DESC;", nativeQuery = true)
    List<Task> sortTaskByHighPriority(@Param("appId") int appId);

    @Query(value = "SELECT *\n" +
            "FROM controloftask.app_tasks\n" +
            "WHERE app_id = :appId\n" +
            "ORDER BY priority_enums ASC;", nativeQuery = true)
    List<Task> sortTaskByLowPriority(@Param("appId") int appId);

    @Query(value = "SELECT *,TIMESTAMPDIFF(DAY, CURDATE(), tasks.end_task_work) AS days_left\n" +
            "FROM controloftask.app_tasks\n" +
            "WHERE tasks.end_task_work > CURDATE() and app_id = :appId\n" +
            "ORDER BY days_left ASC;", nativeQuery = true)
    List<Task> sortTaskByClosestDate(@Param("appId") int appId);

    @Query(value = "SELECT *,TIMESTAMPDIFF(DAY, CURDATE(), tasks.end_task_work) AS days_left\n" +
            "FROM controloftask.app_tasks\n" +
            "WHERE tasks.end_task_work > CURDATE() and app_id = :appId\n" +
            "ORDER BY days_left DESC;", nativeQuery = true)
    List<Task> sortTaskByDistantDate(@Param("appId") int appId);

    @Query(value = "SELECT * FROM controloftask.app_tasks WHERE app_id = :appId",nativeQuery = true)
    List<Task> findAllTasksByAppId(@Param("appId") int appId);

}
