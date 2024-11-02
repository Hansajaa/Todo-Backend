package com.todoapp.todoapi.service.impl;

import com.todoapp.todoapi.entity.TodoEntity;
import com.todoapp.todoapi.entity.UserEntity;
import com.todoapp.todoapi.exception.TodoException;
import com.todoapp.todoapi.exception.UserException;
import com.todoapp.todoapi.model.Todo;
import com.todoapp.todoapi.repository.TodoRepository;
import com.todoapp.todoapi.repository.UserRepository;
import com.todoapp.todoapi.service.TodoService;
import com.todoapp.todoapi.util.status.todo.TodoStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {

    private final String TODO_SAVE_SUCCESS_MESSAGE = "successfully saved todo";
    private final String TODO_UPDATE_SUCCESS_MESSAGE = "successfully update todo";
    private final String USER_NOT_FOUND = "User not found with ID : ";
    private final String SUCCESSFULLY_GET_TODOS = "All todos successfully fetched from database";
    private final String TODO_NOT_FOUND = "Todos not found with id : ";
    private final String SUCCESS_TODO_DELETE_MESSAGE = "Successfully deleted Todo";

    private final UserRepository userRepository;
    private final TodoRepository todoRepository;
    private final ModelMapper mapper;

    @Override
    public TodoEntity saveTodo(Todo todo) throws UserException {
        try{
            Optional<UserEntity> userBelongsTodo = userRepository.findById(todo.getUserID());

            if(userBelongsTodo.isPresent()){
                TodoEntity todoEntity = mapper.map(todo, TodoEntity.class);
                todoEntity.setId(null);
                todoEntity.setUser(userBelongsTodo.get());
                TodoEntity savedTodo = todoRepository.save(todoEntity);
                log.info(TODO_SAVE_SUCCESS_MESSAGE);
                return savedTodo;
            }

            log.error(USER_NOT_FOUND+todo.getUserID());
            return null;
        }catch(Exception e){
            log.error(USER_NOT_FOUND+todo.getUserID());
            throw new UserException(USER_NOT_FOUND+todo.getUserID());
        }
    }


    @Override
    public Page<TodoEntity> getAllTodosWithPagination(Long userID, int offset, int pageSize) throws TodoException {
        try{
            Page<TodoEntity> allTodosByUserId = todoRepository.findAllByUserIdWithPagination(userID, PageRequest.of(offset, pageSize));

            if(!(allTodosByUserId.getNumberOfElements() == 0)){
                log.info(SUCCESSFULLY_GET_TODOS);
                return allTodosByUserId;
            }

            log.error("No elements exists for page : "+offset);
            throw new TodoException("No elements exists for page : "+offset);
        }catch(Exception e){
            log.error("Cannot fetch todos with user ID : "+userID);
            throw new TodoException("Cannot fetch todos with user ID : "+userID);
        }
    }

    @Override
    public TodoEntity updateTodo(Todo todo) throws TodoException {
        try{
            Optional<TodoEntity> entity = todoRepository.findById(todo.getId());
            if(entity.isPresent()){
                TodoEntity newEntity = mapper.map(todo, TodoEntity.class);
                TodoEntity updatedEntity = todoRepository.save(newEntity);
                log.info(TODO_UPDATE_SUCCESS_MESSAGE);
                return updatedEntity;
            }

            log.error(TODO_NOT_FOUND+todo.getId());
            return null;
        }catch(Exception e){
            log.error(e.getMessage());
            throw new TodoException(e.getMessage());
        }
    }

    @Override
    public Boolean deleteTodo(Long id) {
        Optional<TodoEntity> todo = todoRepository.findById(id);

        if(todo.isPresent()){
            todoRepository.deleteById(id);
            log.info(SUCCESS_TODO_DELETE_MESSAGE);
            return true;
        }

        log.info(TODO_NOT_FOUND+id);
        return false;
    }

    @Override
    public Page<TodoEntity> getAllTodosWithPaginationAndSort(Long userID, int offset, int pageSize, String field) throws TodoException {
        try{
            Page<TodoEntity> allTodosByUserId = todoRepository.findAllByUserIdWithPagination(userID, PageRequest.of(offset, pageSize).withSort(Sort.by(field)));

            if(!(allTodosByUserId.getNumberOfElements() == 0)){
                log.info(SUCCESSFULLY_GET_TODOS);
                return allTodosByUserId;
            }

            log.error("No elements exists for page : "+offset);
            throw new TodoException("No elements exists for page : "+offset);
        }catch(Exception e){
            log.warn(e.getMessage());
            log.error("Cannot fetch todos with user ID : "+userID);
            throw new TodoException("Cannot fetch todos with user ID : "+userID);
        }
    }

    public List<TodoEntity> searchTodos(Long userId, String title, String body, String status, String priority) throws TodoException {
        try {
            List<TodoEntity> todoEntities = todoRepository.searchTodos(userId, title, body, status, priority);
            if (!(todoEntities.size() == 0)){
                log.info(SUCCESSFULLY_GET_TODOS);
                return todoEntities;
            }

            log.info(TODO_NOT_FOUND+userId);
            return todoEntities;
        }catch(Exception e){
            log.error(e.getMessage());
            throw new TodoException(e.getMessage());
        }

    }

    @Override
    public List<TodoEntity> findByUserIdAndCompletionStatus(Long userId, Boolean status) throws TodoException {

        try{
            List<TodoEntity> byUserIdAndCompletionStatus = todoRepository.findByUserIdAndCompletionStatus(userId, status);

            if (!(byUserIdAndCompletionStatus.size() == 0)){
                log.info(SUCCESSFULLY_GET_TODOS);
                return byUserIdAndCompletionStatus;
            }

            log.error(TODO_NOT_FOUND+userId);
            throw new TodoException(TODO_NOT_FOUND+userId);
        }catch(Exception e){
            log.error(e.getMessage());
            throw new TodoException(e.getMessage());
        }
    }

    @Override
    public TodoEntity toggleCompletionStatus(Long userId, Long todoId) throws TodoException {
        Optional<TodoEntity> todoOptional = todoRepository.findById(todoId);

        if (!todoOptional.isPresent()) {
            log.error(TODO_NOT_FOUND+userId);
            throw new TodoException(TODO_NOT_FOUND+userId);
        }

        TodoEntity todo = todoOptional.get();
        todo.setCompletionStatus(!todo.getCompletionStatus()); // Toggle the status
        if(todo.getCompletionStatus()){
            todo.setStatus(TodoStatus.COMPLETED);
        }else{
            todo.setStatus(TodoStatus.PENDING);
        }
        TodoEntity updatedTodo = todoRepository.save(todo);

        log.info(TODO_UPDATE_SUCCESS_MESSAGE);
        return updatedTodo;
    }
}
