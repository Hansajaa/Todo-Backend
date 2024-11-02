package com.todoapp.todoapi.controller;

import com.todoapp.todoapi.entity.UserEntity;
import com.todoapp.todoapi.exception.UserException;
import com.todoapp.todoapi.model.User;
import com.todoapp.todoapi.model.response.ResponseModel;
import com.todoapp.todoapi.service.UserService;
import com.todoapp.todoapi.util.status.StatusCodes;
import com.todoapp.todoapi.util.validation.user.UserValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("api/v1/user")
@CrossOrigin
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final String USER_SAVED = "user saved successfully";
    private final String USER_CANNOT_SAVE = "User cannot register";

    private final UserService service;

    @PostMapping("/register")
    ResponseEntity<ResponseModel> registerUser(@RequestBody User user){

        String validateResult = UserValidation.userValidate(user);

        if(validateResult.length() > 0){
            log.error(validateResult);
            return ResponseEntity.status(StatusCodes.BAD_REQUEST).body(
                    ResponseModel.builder()
                            .statusCode(StatusCodes.BAD_REQUEST)
                            .message(validateResult)
                            .body(null)
                            .build()
            );
        }

        try{
            UserEntity savedUser = service.registerUser(user);
            log.info(USER_SAVED);

            return ResponseEntity.status(StatusCodes.OK).body(ResponseModel.builder()
                    .statusCode(StatusCodes.OK)
                    .message(USER_SAVED)
                    .body(savedUser)
                    .build());
        }catch(UserException e){
            log.error(e.getMessage());
            return ResponseEntity.status(StatusCodes.BAD_REQUEST).body(ResponseModel.builder()
                    .statusCode(StatusCodes.BAD_REQUEST)
                    .message(USER_CANNOT_SAVE)
                    .body(null)
                    .build());
        }
    }

    @PostMapping("/login")
    ResponseEntity<ResponseModel> loginUser(@RequestBody User user){

        String validateResult = UserValidation.userValidate(user);

        if(validateResult.length() > 0){
            log.error(validateResult);
            return ResponseEntity.status(StatusCodes.BAD_REQUEST).body(
                    ResponseModel.builder()
                            .statusCode(StatusCodes.BAD_REQUEST)
                            .message(validateResult)
                            .body(null)
                            .build()
            );
        }

        try {
            String token = service.loginUser(user);
            return ResponseEntity.status(StatusCodes.OK).body(ResponseModel.builder()
                    .statusCode(StatusCodes.OK)
                    .message("login success")
                    .body(token)
                    .build());
        } catch (UserException e) {
            return ResponseEntity.status(StatusCodes.UNAUTHORIZED).body(
                    ResponseModel.builder()
                            .statusCode(StatusCodes.UNAUTHORIZED)
                            .message("login failed")
                            .body(null)
                            .build()
            );
        }
    }
}
