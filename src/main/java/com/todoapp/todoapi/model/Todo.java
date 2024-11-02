package com.todoapp.todoapi.model;

import lombok.Data;

import java.util.Date;

@Data
public class Todo {
    private Long id;
    private String title;
    private String body;
    private String Status;
    private Date date;
    private String priority;
    private Long userID;
    private Boolean completionStatus = false;
}
