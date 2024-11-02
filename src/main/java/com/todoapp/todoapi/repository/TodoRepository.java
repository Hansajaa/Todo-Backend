package com.todoapp.todoapi.repository;

import com.todoapp.todoapi.entity.TodoEntity;
import com.todoapp.todoapi.model.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface TodoRepository extends JpaRepository<TodoEntity,Long> {

    @Query(value = "SELECT * FROM todo_item WHERE user_id = :userId",
            countQuery = "SELECT COUNT(*) FROM todo_item WHERE user_id = :userId",
            nativeQuery = true)
    Page<TodoEntity> findAllByUserIdWithPagination(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT t FROM todo_item t " +
            "WHERE (t.user.id = :userId)" +
            "AND (:title IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%'))) " +
            "AND (:body IS NULL OR LOWER(t.body) LIKE LOWER(CONCAT('%', :body, '%'))) " +
            "AND (:status IS NULL OR LOWER(t.Status) LIKE LOWER(CONCAT('%', :status, '%'))) " +
            "AND (:priority IS NULL OR LOWER(t.priority) LIKE LOWER(CONCAT('%', :priority, '%')))")
    List<TodoEntity> searchTodos(@Param("userId") Long userId,
                                 @Param("title") String title,
                                 @Param("body") String body,
                                 @Param("status") String status,
                                 @Param("priority") String priority);

    @Query("SELECT t FROM todo_item t WHERE t.user.id = :userId AND t.completionStatus = :status")
    List<TodoEntity> findByUserIdAndCompletionStatus(@Param("userId") Long userId, @Param("status") Boolean status);
}
