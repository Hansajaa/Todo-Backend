package com.todoapp.todoapi.util.validation;

public class EmailUtil {

    private EmailUtil(){}

    public static String emailValidate(String email){
        if(email.isEmpty()){
            return "Email is required";
        }
        return "";
    }
}
