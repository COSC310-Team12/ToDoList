package com.example.todolist;

import android.view.View;

/*
This is an interface implemented by the MainActivity
*/

public interface ToDoClickListener {
    void onEditClick(View view, int position);

    void onCheckClick(View view, int position);

    void onCreated(ToDoAdapter.MyViewHolder holder, int position);
}
