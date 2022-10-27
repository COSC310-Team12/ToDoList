package com.example.todolist;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

/*
This is a serializable class to store information about to-dos.
*/

public class ToDo implements Serializable {
    private String text;
    private boolean done;
    private Date date;

    public ToDo(String text) {
        this.text = text;
    }

    // all arg constructor
    public ToDo(String text, boolean done, Date date) {
        this.text = text;
        this.done = done;
        this.date = date;
    }
    public static Comparator<ToDo> DueDateAscComparator= new Comparator<ToDo>() {
        @Override
        public int compare(ToDo t1, ToDo t2) {
            if(t1.getDate()==null)
                return 1;
            else if(t2.getDate()==null)
                return -1;
            else return t1.getDate().compareTo(t2.getDate());
        }
    };
    public static Comparator<ToDo> DueDateDescComparator= new Comparator<ToDo>() {
        @Override
        public int compare(ToDo t1, ToDo t2) {
            if(t1.getDate()==null)
                return 1;
            else if(t2.getDate()==null)
                return -1;
            else return t2.getDate().compareTo(t1.getDate());
        }
    };


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
}

