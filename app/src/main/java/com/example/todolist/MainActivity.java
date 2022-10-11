package com.example.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

        toDoList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        inputToDo = findViewById(R.id.inputToDo);

        Button submitButton = findViewById(R.id.submitButton);

        setToDos();
        setAdapter();

        submitButton.setOnClickListener(this::createToDo);
        // allowing use to add to-dos by pressing enter
        inputToDo.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (keyEvent.getAction() == 0 && inputToDo.getText().toString().length() > 0)
                createToDo(textView);
            return true;
        });
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
            // toast message if user tries to submit empty String
            Toast toast = Toast.makeText(getApplicationContext(), "Please enter a to-do", Toast.LENGTH_LONG);
            // TODO: this does not work. Figure out how to make toast appear at the top of the screen
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
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerAdapter = new ToDoAdapter(toDoList);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.setClickListener(this);
    }

    private boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }

    @Override
    public void onClick(View view, int position) {
        final ToDo toDo = toDoList.get(position);
        System.out.println("\n");
        System.out.println(toDo.getText());
        System.out.println("\n");
        Intent i = new Intent(this, EditToDo.class);
        i.putExtra("text", toDo.getText());
        startActivity(i);
    }
}