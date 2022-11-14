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
    private EditText totalGradeInput;
    private ArrayList<ToDo> toDoList;
    private int toDoIndex;
    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_grade);
        setTitle("Set Total Grade");
        Intent intent=getIntent();
        toDoList=(ArrayList<ToDo>) intent.getSerializableExtra("ToDoList");
        toDoIndex= intent.getIntExtra("Index", 0);
        TextView title = findViewById(R.id.totalGradeTitle);
        totalGradeInput= findViewById(R.id.totalGradeInput);
        title.setText(String.format("Enter Total Grade for %s", toDoList.get(toDoIndex).getText()));
        totalGradeInput.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == 6 || keyEvent.getAction() == 0) {
                totalGrade.this.submitTotalGrade(textView);
            }
            return true;
        });
    }
    public void submitTotalGrade(View view){
        float totalGrade=Float.parseFloat(totalGradeInput.getText().toString());
        System.out.println("Total GRADE: "+totalGrade);
        ToDo toDo=toDoList.get(toDoIndex); // obtains the corresponding to Do
        toDo.setMaxGrade(totalGrade);
        System.out.println(toDo.getMaxGrade());
        setResult(RESULT_OK, new Intent().putExtra("ToDoList", toDoList));
        finish();
    }
}