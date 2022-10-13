package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class EditToDo extends AppCompatActivity {
    TextInputEditText name, date;
    ToDo toDo;
    ArrayList<ToDo> toDoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_todo);

        setTitle("Edit Task");

        Intent intent = getIntent();

        int index = intent.getIntExtra("Index",-1);

        toDoList = (ArrayList<ToDo>) intent.getSerializableExtra("ToDoList");
        toDo = toDoList.get(index);

        ((TextView)findViewById(R.id.editTaskTextView)).setText("Edit \"" + toDo.getText() + "\"");

        name = findViewById(R.id.editTaskName);
        date = findViewById(R.id.editTaskDueDate);
    }

    public void submit(View view) {
        // Set new name and date, if the user entered them
        String newName = name.getText().toString(), newDate = date.getText().toString();
        if (!newName.equals(""))
            toDo.setText(name.getText().toString());
        if (!newDate.equals(""))
            toDo.setDate(newDate);

        goBack(view);
    }

    public void goBack(View view) {
        // Return to main activity
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("ToDoList",toDoList);
        startActivity(intent);
    }
}