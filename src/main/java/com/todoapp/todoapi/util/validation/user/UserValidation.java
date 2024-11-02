package com.todoapp.todoapi.util.validation.user;

import com.todoapp.todoapi.model.User;
import com.todoapp.todoapi.util.validation.EmailUtil;
import com.todoapp.todoapi.util.validation.PasswordUtil;

public class UserValidation {

    private UserValidation(){}

    public static String userValidate(User user){
        String emailResult = EmailUtil.emailValidate(user.getEmail());
        if(emailResult.length() > 0){
            return emailResult;
        }

        return PasswordUtil.passwordValidate(user.getPassword());
    }
}
