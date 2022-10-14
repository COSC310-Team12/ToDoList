package com.example.todolist;

import java.io.Serializable;

/*
This is a serializable class to store information about to-dos.
*/

public class ToDo implements Serializable {
    private String text;
    private boolean done;
    private String date;

    public ToDo(String text) {
        this.text = text;
    }

    // all arg constructor
    public ToDo(String text, boolean done, String date) {
        this.text = text;
        this.done = done;
        this.date = date;
    }

    // getters and setters
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

