package com.todoapp.todoapi.exception;

public class TodoException extends Exception{
   public TodoException(String message){
       super("ToDo : "+message);
   }
}
