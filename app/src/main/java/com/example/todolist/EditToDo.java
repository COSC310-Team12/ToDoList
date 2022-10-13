package com.example.todolist;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class EditToDo extends AppCompatActivity {
    TextInputEditText name, date;
    ToDo toDo;
    ArrayList<ToDo> toDoList;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_todo);

        setTitle("Edit Task");

        Intent intent = getIntent();

        int index = intent.getIntExtra("Index",-1);

        //noinspection unchecked
        toDoList = (ArrayList<ToDo>) intent.getSerializableExtra("ToDoList");
        toDo = toDoList.get(index);

        ((TextView)findViewById(R.id.editTaskTextView)).setText("Edit \"" + toDo.getText() + "\"");

        name = findViewById(R.id.editTaskName);
        date = findViewById(R.id.editTaskDueDate);

        name.setText(toDo.getText());
        date.setText(toDo.getDate());
    }

    public void submit(View view) {
        // Set new name and date, if the user entered them
        //noinspection ConstantConditions
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

    public void deleteButton(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Delete");
        alert.setMessage("Are you sure you want to delete?");
        alert.setPositiveButton("Yes",
                (dialog, which) -> {
            toDoList.remove(toDo);

            dialog.dismiss();

            Intent intent = new Intent(view.getContext(), MainActivity.class);
            // Notify main activity to show message
            intent.putExtra("Notification",0);
            intent.putExtra("ToDoList",toDoList);
            intent.putExtra("deletedToDo", toDo);
            startActivity(intent);
        });
        alert.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        alert.show();
    }
}