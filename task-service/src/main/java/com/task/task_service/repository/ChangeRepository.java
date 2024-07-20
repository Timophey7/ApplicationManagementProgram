package com.task.task_service.repository;

import com.task.task_service.models.Change;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChangeRepository extends JpaRepository<Change,Integer> {

    Change findChangeByChangeTitle(String changeTitle);

}
