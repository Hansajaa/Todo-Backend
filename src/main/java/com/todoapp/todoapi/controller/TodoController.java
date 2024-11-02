package com.todoapp.todoapi.controller;

import com.todoapp.todoapi.entity.TodoEntity;
import com.todoapp.todoapi.exception.TodoException;
import com.todoapp.todoapi.exception.UserException;
import com.todoapp.todoapi.model.Todo;
import com.todoapp.todoapi.model.response.ResponseModel;
import com.todoapp.todoapi.service.TodoService;
import com.todoapp.todoapi.util.status.StatusCodes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("api/v1/todo")
@CrossOrigin
@Slf4j
@RequiredArgsConstructor
public class TodoController {

    private final String TODO_SAVE_SUCCESS_MESSAGE = "successfully saved todo";
    private final String TODO_UPDATE_SUCCESS_MESSAGE = "successfully update todo";
    private final String SUCCESS_TODO_DELETE_MESSAGE = "Successfully deleted Todo";
    private final String TODO_NOT_FOUND = "Todos not found with id : ";
    private final String USER_NOT_FOUND = "User not found with ID : ";
    private final String SUCCESSFULLY_GET_TODOS = "All todos successfully fetched from database";

    private final TodoService todoService;

    @PostMapping("/saveTodo")
    ResponseEntity<ResponseModel> saveTodo(@RequestBody Todo todo){
        try {
            TodoEntity savedTodo = todoService.saveTodo(todo);

            if (savedTodo == null){
                return ResponseEntity.status(StatusCodes.NOT_FOUND).body(ResponseModel.builder()
                        .statusCode(StatusCodes.NOT_FOUND)
                        .message(USER_NOT_FOUND+todo.getUserID())
                        .build());
            }

            return ResponseEntity.status(StatusCodes.OK).body(ResponseModel.builder()
                    .statusCode(StatusCodes.OK)
                    .message(TODO_SAVE_SUCCESS_MESSAGE)
                    .body(savedTodo)
                    .build());
        } catch (UserException e) {
            return ResponseEntity.status(StatusCodes.NOT_FOUND).body(ResponseModel.builder()
                    .statusCode(StatusCodes.NOT_FOUND)
                    .message(USER_NOT_FOUND+todo.getUserID())
                    .build());
        }
    }

    @GetMapping("/getAllTodosWithPagination/{userID}")
    ResponseEntity<ResponseModel> getAllTodosWithPagination(
            @PathVariable Long userID,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int pageSize){

        try {
           Page<TodoEntity> allTodosBelongsToOffset = todoService.getAllTodosWithPagination(userID,offset,pageSize);

           return ResponseEntity.status(StatusCodes.OK).body(ResponseModel.builder()
                   .statusCode(StatusCodes.OK)
                   .message(SUCCESSFULLY_GET_TODOS)
                   .body(allTodosBelongsToOffset)
                   .build());
       } catch (TodoException e) {
           log.info(e.getMessage());
           return ResponseEntity.status(StatusCodes.NOT_FOUND).body(ResponseModel.builder()
                   .statusCode(StatusCodes.NOT_FOUND)
                   .message(e.getMessage())
                   .body(null)
                   .build());
       }
   }

   @PutMapping("/updateTodo")
   ResponseEntity<ResponseModel> updateTodo(@RequestBody Todo todo){
       try {
           TodoEntity newTodoEntity = todoService.updateTodo(todo);

           if(!(newTodoEntity == null)){
               return ResponseEntity.status(StatusCodes.OK).body(ResponseModel.builder()
                       .statusCode(StatusCodes.OK)
                       .message(TODO_UPDATE_SUCCESS_MESSAGE)
                       .body(newTodoEntity)
                       .build());
           }

           return ResponseEntity.status(StatusCodes.NOT_FOUND).body(ResponseModel.builder()
                   .statusCode(StatusCodes.NOT_FOUND)
                   .message(TODO_NOT_FOUND)
                   .body(null)
                   .build());
       } catch (TodoException e) {
           return ResponseEntity.status(StatusCodes.NOT_FOUND).body(ResponseModel.builder()
                   .statusCode(StatusCodes.NOT_FOUND)
                   .message(TODO_NOT_FOUND)
                   .body(null)
                   .build());
       }
   }

   @DeleteMapping("/deleteTodo/{id}")
   ResponseEntity<ResponseModel> deleteTodo(@PathVariable Long id){
       Boolean isDeleted = todoService.deleteTodo(id);

       if(isDeleted){
           return ResponseEntity.status(StatusCodes.OK).body(ResponseModel.builder()
                   .statusCode(StatusCodes.OK)
                   .message(SUCCESS_TODO_DELETE_MESSAGE)
                   .body(null)
                   .build());
       }

       return ResponseEntity.status(StatusCodes.BAD_REQUEST).body(ResponseModel.builder()
               .statusCode(StatusCodes.BAD_REQUEST)
               .message(TODO_NOT_FOUND)
               .body(null)
               .build());
   }

    @GetMapping("/getAllTodosWithPaginationAndSort/{userID}")
    ResponseEntity<ResponseModel> getAllTodosWithPaginationAndSort(
            @PathVariable Long userID,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam String field){

        try {
            Page<TodoEntity> allTodosBelongsToOffsetWithSort = todoService.getAllTodosWithPaginationAndSort(userID,offset,pageSize,field);

            return ResponseEntity.status(StatusCodes.OK).body(ResponseModel.builder()
                    .statusCode(StatusCodes.OK)
                    .message(SUCCESSFULLY_GET_TODOS)
                    .body(allTodosBelongsToOffsetWithSort)
                    .build());
        } catch (TodoException e) {
            log.info(e.getMessage());
            return ResponseEntity.status(StatusCodes.NOT_FOUND).body(ResponseModel.builder()
                    .statusCode(StatusCodes.NOT_FOUND)
                    .message(e.getMessage())
                    .body(null)
                    .build());
        }
    }

    @GetMapping("/search/{userId}")
    ResponseEntity<ResponseModel> searchTodos(
            @PathVariable Long userId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String body,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority
    ){

        try {
            List<TodoEntity> todoEntities = todoService.searchTodos(userId, title, body, status, priority);

            if(!(todoEntities.size() == 0)){
                return ResponseEntity.status(StatusCodes.OK).body(ResponseModel.builder()
                        .statusCode(StatusCodes.OK)
                        .message(SUCCESSFULLY_GET_TODOS)
                        .body(todoEntities)
                        .build());
            }


            return ResponseEntity.status(StatusCodes.NOT_FOUND).body(ResponseModel.builder()
                    .statusCode(StatusCodes.NOT_FOUND)
                    .message(TODO_NOT_FOUND+userId)
                    .body(null)
                    .build());
        }catch(TodoException e){
            return ResponseEntity.status(StatusCodes.BAD_REQUEST).body(ResponseModel.builder()
                    .statusCode(StatusCodes.BAD_REQUEST)
                    .message(e.getMessage())
                    .body(null)
                    .build());
        }

    }

    @GetMapping("/todosByStatus/{userId}")
    ResponseEntity<ResponseModel> trackCompletionStatus(@PathVariable Long userId, @RequestParam Boolean status){

        try{
            List<TodoEntity> byUserIdAndCompletionStatus = todoService.findByUserIdAndCompletionStatus(userId, status);

            return ResponseEntity.status(StatusCodes.OK).body(ResponseModel.builder()
                    .statusCode(StatusCodes.OK)
                    .message(SUCCESSFULLY_GET_TODOS)
                    .body(byUserIdAndCompletionStatus)
                    .build());
        }catch(Exception e){
            return ResponseEntity.status(StatusCodes.BAD_REQUEST).body(ResponseModel.builder()
                    .statusCode(StatusCodes.BAD_REQUEST)
                    .message(e.getMessage())
                    .body(null)
                    .build());
        }
    }

    @PutMapping("/toggleCompletion/{userId}/{todoId}")
    public ResponseEntity<ResponseModel> toggleCompletionStatus(@PathVariable Long userId, @PathVariable Long todoId) {
        try {
            TodoEntity todoEntity = todoService.toggleCompletionStatus(userId, todoId);

            return ResponseEntity.status(StatusCodes.OK).body(ResponseModel.builder()
                    .statusCode(StatusCodes.OK)
                    .message(TODO_UPDATE_SUCCESS_MESSAGE)
                    .body(todoEntity)
                    .build());
        } catch (TodoException e) {
            return ResponseEntity.status(StatusCodes.BAD_REQUEST).body(ResponseModel.builder()
                    .statusCode(StatusCodes.BAD_REQUEST)
                    .message(e.getMessage())
                    .body(null)
                    .build());
        }
    }
}
