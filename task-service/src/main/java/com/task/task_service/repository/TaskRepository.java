package com.task.task_service.repository;

import com.task.task_service.models.tasks.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task,Integer> {


    @Query(value = """
            SELECT * FROM controloftask.app_tasks
            WHERE app_unique_code = :code
            ORDER BY priority_enums DESC
            LIMIT :value
            OFFSET :offset
            """, nativeQuery = true)
    Optional<List<Task>> sortTaskByHighPriority(@Param("code") String code,@Param("offset") int offset,@Param("value") int value);

    @Query(value = """
            SELECT * FROM controloftask.app_tasks
            WHERE app_unique_code = :code
            ORDER BY priority_enums ASC
            LIMIT :value
            OFFSET :offset
            """, nativeQuery = true)
    Optional<List<Task>> sortTaskByLowPriority(@Param("code") String code,@Param("offset") int offset,@Param("value") int value);

    @Query(value = """
            SELECT *, TIMESTAMPDIFF(DAY, CURDATE(), end_task_work) AS days_left
            FROM controloftask.app_tasks
            WHERE end_task_work > CURDATE() AND app_unique_code = :code
            ORDER BY days_left ASC
            LIMIT :value
            OFFSET :offset
            """, nativeQuery = true)
    Optional<List<Task>> sortTaskByClosestDate(@Param("code") String code,@Param("offset") int offset,@Param("value") int value);

    @Query(value = """
            SELECT *, TIMESTAMPDIFF(DAY, CURDATE(), end_task_work) AS days_left
            FROM controloftask.app_tasks
            WHERE end_task_work > CURDATE() AND app_unique_code = :code
            ORDER BY days_left DESC
            LIMIT :value
            OFFSET :offset
            """, nativeQuery = true)
    Optional<List<Task>> sortTaskByDistantDate(@Param("code") String code,@Param("offset") int offset,@Param("value") int value);


    @Query(value = "SELECT * FROM controloftask.app_tasks WHERE app_unique_code = :code",nativeQuery = true)
    List<Task> findAllTasksByAppUniqueCode(@Param("code") String code);

    boolean existsByTaskNameAndAppUniqueCode(String taskName,String uniqueCode);

    @Query(value = """
            SELECT * FROM controloftask.app_tasks
            WHERE app_unique_code = :uniqueCode
            LIMIT :value
            OFFSET :offset
            """, nativeQuery = true)
    Optional<List<Task>> getAllAppTasks(
            @Param("offset") int offset,
            @Param("value") int value,
            @Param("uniqueCode") String uniqueCode
    );

}
