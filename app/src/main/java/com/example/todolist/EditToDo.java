package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;

public class EditToDo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_todo);

        String toDoText = getIntent().getStringExtra("text");

        setTitle("Edit to do");

        EditText editToDo = findViewById(R.id.editToDoText);
        EditText editDueDate = findViewById(R.id.editToDoDate);

        editToDo.setText(toDoText);
    }
}