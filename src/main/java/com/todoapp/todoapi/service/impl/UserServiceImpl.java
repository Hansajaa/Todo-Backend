package com.todoapp.todoapi.service.impl;

import com.todoapp.todoapi.entity.UserEntity;
import com.todoapp.todoapi.exception.UserException;
import com.todoapp.todoapi.model.User;
import com.todoapp.todoapi.model.response.ResponseModel;
import com.todoapp.todoapi.repository.UserRepository;
import com.todoapp.todoapi.service.UserService;
import com.todoapp.todoapi.util.status.StatusCodes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private String USER_CANNOT_SAVE = "serviceLayer:error :- user cannot save";
    private String USER_SAVE = "serviceLayer:success :- user save successfully";

    private final ModelMapper mapper;
    private final UserRepository repository;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);


    private final JWTService jwtService;

    private final AuthenticationManager authManager;

    @Override
    public UserEntity registerUser(User user) throws UserException {
        try{
            UserEntity userEntity = mapper.map(user, UserEntity.class);
            userEntity.setPassword(encoder.encode(user.getPassword()));
            repository.save(userEntity);
            log.info(USER_SAVE);
            return userEntity;
        }catch(Exception e){
            log.error(USER_CANNOT_SAVE);
            throw new UserException(USER_CANNOT_SAVE);
        }
    }

    @Override
    public String loginUser(User user) throws UserException {

        Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        if (authentication.isAuthenticated()) {
            log.info("login Success");
            return jwtService.generateToken(user.getEmail());
        } else {
            log.error("login failed");
           throw new UserException("Login failed");
        }
    }
}
