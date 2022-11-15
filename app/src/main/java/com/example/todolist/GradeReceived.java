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
    private ToDo toDo;
    private EditText gradeInputEditText;
    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade_received);
        setTitle("Enter Grade Received");
        Intent intent=getIntent();
        toDoList = (ArrayList<ToDo>) intent.getSerializableExtra("ToDoList");
        int toDoIndex = intent.getIntExtra("Index", 0);
        toDo = toDoList.get(toDoIndex);
        TextView textView1 = findViewById(R.id.gradeDisplayText);
        gradeInputEditText = findViewById(R.id.inputGradeEditText);
        System.out.println(toDo.getMaxGrade());
        textView1.setText(String.format("Enter the Grade received for %s ( /%s)", toDo.getText(), toDo.getMaxGrade()));
        if(toDo.getGradeReceived()!=0)
            gradeInputEditText.setText(String.valueOf(toDo.getGradeReceived()));
        //allows to submit the Grade Received by pressing the enter key.
        gradeInputEditText.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == 6 || keyEvent.getAction() == 0) {
                GradeReceived.this.submitGradeReceived(textView);
            }
            return true;
        });
    }

    public void submitGradeReceived(View view){
        float gradeReceived=Float.parseFloat(gradeInputEditText.getText().toString());
        toDo.setGradeReceived(gradeReceived);
        setResult(RESULT_OK, new Intent().putExtra("ToDoList", toDoList));
        finish();
    }
}