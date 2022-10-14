package com.example.todolist;

import android.view.View;

/*
This is an interface implemented by the MainActivity
*/

public interface ToDoClickListener {
    void onClick(View view, int position);
}
