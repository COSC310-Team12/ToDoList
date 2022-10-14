package com.example.todolist;

import java.io.Serializable;
import java.util.Date;

public class ToDo implements Serializable {
    private String text;
    private boolean done;
    private Date date;

    public ToDo(String text) {
        this.text = text;
    }

    public ToDo(String text, boolean done, Date date) {
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

