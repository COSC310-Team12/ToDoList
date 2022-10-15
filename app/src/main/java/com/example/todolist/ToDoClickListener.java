package com.example.todolist;

import android.view.View;

public interface ToDoClickListener {
    void onEditClick(View view, int position);
    void onCheckClick(View view, int position);
}
