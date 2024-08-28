package com.task.task_service.repository;

import com.task.task_service.models.change.Change;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChangeRepository extends JpaRepository<Change, Integer> {
    boolean existsByChangeTitle(String changeTitle);

    List<Change> getChangesByAppUniqueCode(String uniqueCode);
}
