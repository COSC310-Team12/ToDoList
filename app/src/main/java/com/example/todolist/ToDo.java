package com.example.todolist;

import java.io.Serializable;

public class ToDo implements Serializable {
    private String text;
    private boolean done;
    private String date;

    public ToDo(String text) {
        this.text = text;
    }

    public ToDo(String text, boolean done, String date) {
        this.text = text;
        this.done = done;
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

