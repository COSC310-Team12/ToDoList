package com.example.todolist;

import java.util.Comparator;

public class sortToDos implements Comparator<ToDo> {
    public int compare(ToDo a, ToDo b){
        return a.getDate().compareTo(b.getDate());
    }
}
