package com.todoapp.todoapi.util.validation;

public class PasswordUtil {

    private PasswordUtil(){}

    public static String passwordValidate(String password){
        if(password.isEmpty()){
            return "Password is required";
        }
        return "";
    }
}
