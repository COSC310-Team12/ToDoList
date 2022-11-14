package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class GradeReceived extends AppCompatActivity {
    private ArrayList<ToDo> toDoList;
    private int toDoIndex;
    private ToDo toDo;
    private TextView textView;
    private EditText gradeInputEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade_received);
        setTitle("Enter Grade Received");
        Intent intent=getIntent();
        toDoList = (ArrayList<ToDo>) intent.getSerializableExtra("ToDoList");
        toDoIndex = intent.getIntExtra("Index", 0);
        toDo = toDoList.get(toDoIndex);
        textView=(TextView) findViewById(R.id.gradeDisplayText);
        gradeInputEditText=(EditText) findViewById(R.id.inputGradeEditText);
        System.out.println(toDo.getMaxGrade());
        textView.setText("Enter the Grade received for "+toDo.getText()+" ( /"+toDo.getMaxGrade()+")");
    }
    public void submitGradeReceived(View view){
        float gradeReceived=Float.parseFloat(gradeInputEditText.getText().toString());
        toDo.setGradeReceived(gradeReceived);
        finish();
    }
}