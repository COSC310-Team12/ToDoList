package com.example.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Time;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements ToDoClickListener {

    private ArrayList<ToDo> toDoList, completed;
    private RecyclerView recyclerView;
    private ToDoAdapter recyclerAdapter;
    private EditText inputToDo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        inputToDo = findViewById(R.id.inputToDo);

        Button submitButton = findViewById(R.id.submitButton);

        submitButton.setOnClickListener(this::createToDo);
        // allowing use to add to-dos by pressing enter
        inputToDo.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == 6 || keyEvent.getAction() == 0) {
                MainActivity.this.createToDo(textView);
            }
            return true;
        });

        Intent intent = getIntent();

        // Load in data
        loadData(intent);

        // Handle any notifications requested by previous activity
        if (intent.hasExtra("Notification")) {
            int notification = intent.getIntExtra("Notification", -1);
            switch (notification) {
                case 0: Snackbar sb = Snackbar.make(findViewById(R.id.myCoordinatorLayout), "Task deleted",Snackbar.LENGTH_LONG);
                sb.setAction("UNDO", view -> {
                    // Undo delete
                    toDoList.add((ToDo) intent.getSerializableExtra("deletedToDo"));
                    recyclerAdapter.notifyItemInserted(recyclerAdapter.getItemCount());
                });
                sb.show(); break;
            }
        }
    }

    private void loadData() {
        loadData(new Intent());
    }

    private void loadData(Intent intent) {
        if (intent.hasExtra("ToDoList")) {
            // Load toDoList from previous activity if it was passed in the intent
            //noinspection unchecked
            toDoList = (ArrayList<ToDo>) intent.getSerializableExtra("ToDoList");
            // Get completed tasks from the save file
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(getFilesDir(), "savedToDos.dat")))){
                // Try loading from saved file
                in.readObject();
                //noinspection unchecked
                completed = (ArrayList<ToDo>) in.readObject();
            } catch (FileNotFoundException e) {
                completed = new ArrayList<>();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            // Save changes after editing
            save();
        } else try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(getFilesDir(), "savedToDos.dat")))){
            // FOR DEBUGGING ONLY
            final boolean LOAD_FROM_FILE = true;
            if (!LOAD_FROM_FILE) throw new FileNotFoundException("Not loading from file for debug purposes. To change this behavior, change LOAD_FROM_FILE to true");
            // Try loading from saved file
            //noinspection unchecked
            toDoList = (ArrayList<ToDo>) in.readObject();
            //noinspection unchecked
            completed = (ArrayList<ToDo>) in.readObject();
        } catch (FileNotFoundException e) {
            // Load default tasks
            toDoList = new ArrayList<>();
            completed = new ArrayList<>();
            setToDos();
            // Save new state
            save();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        setAdapter();
    }

    private void save() {
        // Save toDoList array
        File file = new File(getFilesDir(), "savedToDos.dat");
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(toDoList);
            out.writeObject(completed);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createToDo(View v) {
        if (!isEmpty(inputToDo)) {
            // adding user input to-do to array list
            toDoList.add(new ToDo(inputToDo.getText().toString()));
            // need to call this so UI updates and newly added item is displayed
            recyclerAdapter.notifyItemInserted(recyclerAdapter.getItemCount());
            // clearing user input after to-do is submitted
            inputToDo.getText().clear();
            // Save changes
            save();
        } else {
            // Ask the user to enter a name for the task
            Snackbar.make(findViewById(R.id.myCoordinatorLayout), "Please enter a task name",Snackbar.LENGTH_LONG).show();
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
    public void onEditClick(View view, int position) {
        Intent i = new Intent(this, EditToDo.class);
        // Send toDoList so that we can edit it there, then reload it when returning to main activity
        i.putExtra("ToDoList", toDoList);
        i.putExtra("Index", position);
        startActivity(i);
    }

    @Override
    public void onCheckClick(View view, int position) {
        // Move completed task to the completed list
        ToDo completedTask = toDoList.remove(position);
        completedTask.setDone(!completedTask.isDone());
        completed.add(completedTask);

        // Save changes
        save();

        // load data again
        loadData();

        // Alert the user of their action
        makeNotification("Completed \"" + completedTask.getText() + "\"");
    }

    public void makeNotification(String msg) {
        Snackbar sb = Snackbar.make(findViewById(R.id.myCoordinatorLayout), msg,Snackbar.LENGTH_LONG);
        sb.show();
    }
}