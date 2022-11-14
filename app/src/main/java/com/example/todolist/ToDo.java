package com.example.todolist;

import android.content.Intent;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/*
This is a serializable class to store information about to-dos.
*/

public class ToDo implements Serializable {
    private final UUID uuid = UUID.randomUUID(); // Implementation recommended by https://www.baeldung.com/java-uuid
    private String text;
    private boolean done;
    private Date date;
    private float maxGrade;
    private float gradeReceived;
    private final ArrayList<String> tags = new ArrayList<>();


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

    public float getMaxGrade() { return maxGrade; }

    public void setMaxGrade(float maxGrade) { this.maxGrade = maxGrade; }

    public float getGradeReceived() { return gradeReceived; }

    public void setGradeReceived(float gradeReceived) { this.gradeReceived = gradeReceived; }

    public boolean addTag(String tag) {
        if (!tag.equals("")) {
            tags.add(tag);
        }
        else
            return false;
        return true;
    }

    public boolean removeTag(String tag) {
        return tags.remove(tag);
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    @NonNull
    @Override
    public String toString() {
        return "ToDo{" +
                "text='" + text + '\'' +
                ", done=" + done +
                ", date=" + date +
                ", tags=" + tags +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        return getClass() == o.getClass() && uuid.equals(((ToDo) o).uuid);
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
    public static Comparator<ToDo> TotalMarksComparator= new Comparator<ToDo>() {
        @Override
        public int compare(ToDo t1, ToDo t2) {
            if(t2.getMaxGrade()-t1.getMaxGrade()>0)
                return 1;
            else if((t2.getMaxGrade()-t1.getMaxGrade()<0))
                return -1;
            else return 0;
        }
    };
}

