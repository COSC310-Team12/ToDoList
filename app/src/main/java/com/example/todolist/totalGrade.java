package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class totalGrade extends AppCompatActivity {
TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_grade);
        Intent intent=getIntent();
        title=(TextView) findViewById(R.id.totalGradeTitle);
        title.setText(intent.getStringExtra("todo"));
    }
}