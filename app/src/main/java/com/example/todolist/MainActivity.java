package com.example.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ToDoClickListener {

    private ArrayList<ToDo> toDoList;
    private RecyclerView recyclerView;
    private ToDoAdapter recyclerAdapter;
    private EditText inputToDo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init the toDoList to a blank one if none is returned from the previous activity (i.e. EditToDo)
        Intent intent = getIntent();
        if (intent.hasExtra("ToDoList"))
            toDoList = (ArrayList<ToDo>) intent.getSerializableExtra("ToDoList");
        if (savedInstanceState != null) {
            toDoList = (ArrayList<ToDo>) savedInstanceState.getSerializable("ToDoList");
        } else {
            toDoList = new ArrayList<>();
        }

        recyclerView = findViewById(R.id.recyclerView);
        inputToDo = findViewById(R.id.inputToDo);

        Button submitButton = findViewById(R.id.submitButton);

        setAdapter();

        submitButton.setOnClickListener(this::createToDo);
        // allowing use to add to-dos by pressing enter
        inputToDo.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (keyEvent.getAction() == 0)
                createToDo(textView);
            return true;
        });
    }

    @Override
    protected void onDestroy() {
        // call the superclass method first
        super.onDestroy();
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("ToDoList", toDoList);
        super.onSaveInstanceState(outState);
    }

    public void createToDo(View v) {
        if (!isEmpty(inputToDo)) {
            // adding user input to-do to array list
            toDoList.add(new ToDo(inputToDo.getText().toString()));
            // need to call this so UI updates and newly added item is displayed
            recyclerAdapter.notifyItemInserted(recyclerAdapter.getItemCount());
            // clearing user input after to-do is submitted
            inputToDo.getText().clear();
        } else {
            // Ask the user to enter a name for the task
            Snackbar sb = Snackbar.make(findViewById(R.id.myCoordinatorLayout), "Please enter a task name", Snackbar.LENGTH_LONG);
            sb.show();
        }
    }

    private void setAdapter() {
        // boiler-plate code
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerAdapter = new ToDoAdapter(toDoList);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.setClickListener(this);
    }

    // utility method
    private boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }

    @Override
    public void onClick(View view, int position) {
        final ToDo toDo = toDoList.get(position);
        Intent i = new Intent(this, EditToDo.class);
        // Send to-do object so that we can edit it there
        i.putExtra("ToDoList", toDoList);
        i.putExtra("Index", position);
        startActivity(i);
    }
}