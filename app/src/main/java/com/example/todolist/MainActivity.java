package com.example.todolist;

import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<ToDo> toDoList;
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private EditText inputToDo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toDoList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        inputToDo = findViewById(R.id.inputToDo);

        Button submitButton = findViewById(R.id.submitButton);

        setAdapter();
        setToDos();

        submitButton.setOnClickListener(view -> {
            createTask(view);
        });
        EditText taskEntryBox = findViewById(R.id.inputToDo);
        taskEntryBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == 0 && taskEntryBox.getText().toString().length() > 0)
                    createTask(textView);
                return true;
            }
        });
    }

    public void createTask(View v) {
        if (!isEmpty(inputToDo)) {
            // adding user input to-do to array list
            toDoList.add(new ToDo(inputToDo.getText().toString()));
            // need to call this so UI updates and newly added item is displayed
            recyclerAdapter.notifyItemInserted(recyclerAdapter.getItemCount());
            // clearing user input after to-do is submitted
            inputToDo.getText().clear();
        } else {
            // toast message if user tries to submit empty String
            Toast toast = Toast.makeText(getApplicationContext(), "Please enter a to-do", Toast.LENGTH_LONG);
            // TODO: this does not work. Figure out how to make toast appear at the top of the screen
            toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
            toast.show();
        }
    }

    private void setToDos() {
        // may implement with database later on
        toDoList.add(new ToDo("Finish prototype"));
        toDoList.add(new ToDo("Improve design"));
    }

    private void setAdapter() {
        // boiler-plate code
        recyclerAdapter = new RecyclerAdapter(toDoList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recyclerAdapter);
    }

    private boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }
}