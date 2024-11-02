package com.todoapp.todoapi.service;

import com.todoapp.todoapi.entity.UserEntity;
import com.todoapp.todoapi.exception.UserException;
import com.todoapp.todoapi.model.User;
import com.todoapp.todoapi.model.response.ResponseModel;

public interface UserService {
    UserEntity registerUser(User user) throws UserException;
    String loginUser(User user) throws UserException;
}
