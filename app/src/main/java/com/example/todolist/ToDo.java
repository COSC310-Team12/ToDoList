package com.example.todolist;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
This is a serializable class to store information about to-dos.
*/

public class ToDo implements Serializable {
    private String text;
    private boolean done;
    private Date date;
    private ArrayList<String> tags = new ArrayList<>();

    public ToDo(String text) {
        this.text = text;
    }

    // all arg constructor
    public ToDo(String text, boolean done, Date date) {
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean addTag(String tag) {
        if (tag != "")
            tags.add(tag);
        else
            return false;
        return true;
    }

    public List<String> getTags() {
        return tags;
    }
}

