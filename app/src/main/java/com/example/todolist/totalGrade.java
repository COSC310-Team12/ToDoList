package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
/*
This Activity is used to get the total possible grade for a Graded Task whenever we set a Task as Graded.
 */
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
        title.setText("Enter Total Grade for "+toDoList.get(toDoIndex).getText());
    }
    public void submitTotalGrade(View view){
        float totalGrade=Float.parseFloat(totalGradeInput.getText().toString());
        System.out.println("Total GRADE: "+totalGrade);
        ToDo toDo=toDoList.get(toDoIndex); // obtains the corresponding toDo
        toDo.setMaxGrade(totalGrade);
        System.out.println(toDo.getMaxGrade());
        setResult(RESULT_OK, new Intent().putExtra("ToDoList", toDoList));
        finish();
    }
}