package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class totalGrade extends AppCompatActivity {
    private TextView title;
    private EditText totalGradeInput;
    private ArrayList<ToDo> toDoList;
    private int toDoIndex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_grade);
        setTitle("Set Total Grade");
        Intent intent=getIntent();
        toDoList=(ArrayList<ToDo>) intent.getSerializableExtra("ToDoList");
        toDoIndex=toDoIndex = intent.getIntExtra("Index", 0);
        title=(TextView) findViewById(R.id.totalGradeTitle);
        totalGradeInput=(EditText) findViewById(R.id.totalGradeInput);
        title.setText("Enter Total Grade for "+intent.getStringExtra("todo"));
    }
    public void submitTotalGrade(View view){
        float totalGrade=Float.parseFloat(totalGradeInput.getText().toString());
        ToDo toDo=toDoList.get(toDoIndex); // obtains the corresponding toDo
        toDo.setMaxGrade(totalGrade);
        toDo.addTag("Graded");
        finish();
    }
}