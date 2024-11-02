package com.todoapp.todoapi.service;

import com.todoapp.todoapi.entity.TodoEntity;
import com.todoapp.todoapi.exception.TodoException;
import com.todoapp.todoapi.exception.UserException;
import com.todoapp.todoapi.model.Todo;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TodoService {
    TodoEntity saveTodo(Todo todo) throws UserException;
    Page<TodoEntity> getAllTodosWithPagination(Long userID, int offset, int pageSize) throws TodoException;
    TodoEntity updateTodo(Todo todo) throws TodoException;
    Boolean deleteTodo(Long id);
    Page<TodoEntity> getAllTodosWithPaginationAndSort(Long userID, int offset, int pageSize, String field) throws TodoException;
    List<TodoEntity> searchTodos(Long userId, String title, String body, String status, String priority) throws TodoException;
    List<TodoEntity> findByUserIdAndCompletionStatus(Long userId, Boolean status) throws TodoException;
    TodoEntity toggleCompletionStatus(Long userId, Long todoId) throws TodoException;
}
