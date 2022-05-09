package com.albertoornelas.todoappfirebase.domain;

public class Todo {
    private String idTodo;
    private String titleTodo;
    private String descriptionTodo;

    public Todo(String titleTodo, String descriptionTodo) {
        this.titleTodo = titleTodo;
        this.descriptionTodo = descriptionTodo;
    }

    public Todo(String idTodo, String titleTodo, String descriptionTodo) {
        this.idTodo = idTodo;
        this.titleTodo = titleTodo;
        this.descriptionTodo = descriptionTodo;
    }

    public String getIdTodo() {
        return idTodo;
    }

    public void setIdTodo(String idTodo) {
        this.idTodo = idTodo;
    }

    public String getTitleTodo() {
        return titleTodo;
    }

    public void setTitleTodo(String titleTodo) {
        this.titleTodo = titleTodo;
    }

    public String getDescriptionTodo() {
        return descriptionTodo;
    }

    public void setDescriptionTodo(String descriptionTodo) {
        this.descriptionTodo = descriptionTodo;
    }
}
