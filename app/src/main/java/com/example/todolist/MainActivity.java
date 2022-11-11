package com.example.todolist;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
/*
This class controls the main screen. It extends our custom ToDoClickListener.
*/

public class MainActivity extends AppCompatActivity implements ToDoClickListener {

    private ArrayList<ToDo> toDoList, completed;
    private RecyclerView recyclerView;
    private ToDoAdapter recyclerAdapter;
    private EditText inputToDo;
    NotificationManagerCompat notificationManagerCompat;
    android.app.Notification notification;
    private int RequestPermission = 1;
    // initialization code
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Creates the notification channel that can be toggled on in the app info settings - Default is off.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel("NotifyLate", "Late Task Notification", NotificationManager.IMPORTANCE_HIGH);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

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

        // load in data
        loadData(intent);

        // handle any notifications requested by previous activity
        if (intent.hasExtra("Notification")) {
            int notification = intent.getIntExtra("Notification", -1);
            if (notification == 0) {
                Snackbar sb = Snackbar.make(findViewById(R.id.myCoordinatorLayout), "Task deleted", Snackbar.LENGTH_LONG);
                sb.setAction("UNDO", view -> {
                    // undo delete
                    toDoList.add((ToDo) intent.getSerializableExtra("deletedToDo"));
                    recyclerAdapter.notifyItemInserted(recyclerAdapter.getItemCount());
                });
                sb.show();
            }
        }
    }

    private void loadData() {
        loadData(new Intent());
    }

    private void loadData(Intent intent) {
        if (intent.hasExtra("ToDoList")) {
            // load toDoList from previous activity if it was passed in the intent
            // noinspection unchecked
            toDoList = (ArrayList<ToDo>) intent.getSerializableExtra("ToDoList");
            // get completed tasks from the save file
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(getFilesDir(), "savedToDos.dat")))) {
                // try loading from saved file
                in.readObject();
                // noinspection unchecked
                completed = (ArrayList<ToDo>) in.readObject();
            } catch (FileNotFoundException e) {
                completed = new ArrayList<>();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            // save changes after editing
            save();
        } else
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(getFilesDir(), "savedToDos.dat")))) {
                // FOR DEBUGGING ONLY
                final boolean LOAD_FROM_FILE = true;
                if (!LOAD_FROM_FILE)
                    throw new FileNotFoundException("Not loading from file for debug purposes. To change this behavior, change LOAD_FROM_FILE to true");
                // try loading from saved file
                // noinspection unchecked
                toDoList = (ArrayList<ToDo>) in.readObject();
                // noinspection unchecked
                completed = (ArrayList<ToDo>) in.readObject();
            } catch (FileNotFoundException e) {
                // load default tasks
                toDoList = new ArrayList<>();
                completed = new ArrayList<>();
                // save new state
                save();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        setAdapter();
    }

    private void save() {
        // save toDoList array
        File file = new File(getFilesDir(), "savedToDos.dat");
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(toDoList);
            out.writeObject(completed);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // create new to-do from user input
    public void createToDo(View v) {
        // only allow user to add to-do if they entered text
        if (!isEmpty(inputToDo)) {
            // adding user input to-do to array list
            toDoList.add(new ToDo(inputToDo.getText().toString()));
            // need to call this so UI updates and newly added item is displayed
            recyclerAdapter.notifyItemInserted(recyclerAdapter.getItemCount());
            // clearing user input after to-do is submitted
            inputToDo.getText().clear();
            // save changes
            save();
        } else {
            // ask the user to enter a name for the task
            Snackbar.make(findViewById(R.id.myCoordinatorLayout), "Please enter a task name", Snackbar.LENGTH_LONG).show();
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

    // bound to the RecyclerView elements (individual to-dos), called when user clicks
    // on three dots on to-do
    @Override
    public void onEditClick(View view, int position) {
        Intent i = new Intent(this, EditToDo.class);
        // send toDoList so that we can edit it there, then reload it when returning to main activity
        i.putExtra("ToDoList", toDoList);
        i.putExtra("Index", position);
        startActivity(i);
    }

    // called when users click the checkbox on a to-do
    @Override
    public void onCheckClick(View view, int position) {
        // move completed task to the completed list
        ToDo completedTask = toDoList.remove(position);
        completedTask.setDone(!completedTask.isDone());
        completed.add(completedTask);

        // save changes
        save();

        // load data again
        loadData();

        // alert the user of their action
        makeNotification("Completed \"" + completedTask.getText() + "\"");
    }

    public void makeNotification(String msg) {
        Snackbar sb = Snackbar.make(findViewById(R.id.myCoordinatorLayout), msg, Snackbar.LENGTH_LONG);
        sb.show();

    }
    //Int dueSoon - 0 = due soon, 1 = past due, task is the name/task header that is late
    public void sendNotification(int dueSoon, String task) {
        //this if checks if permission has been granted yet - if yes you make the notification, if not asks for permission
        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED){
            NotificationCompat.Builder builder;
            if(dueSoon == 0)
            {
                builder = new NotificationCompat.Builder(this, "NotifyLate")
                        .setSmallIcon(R.drawable.notificationbell)
                        .setContentTitle(task + " is almost due")
                        .setContentText(task + " is due soon!");

            }
            else
            {
                builder = new NotificationCompat.Builder(this, "NotifyLate")
                        .setSmallIcon(R.drawable.notificationbell)
                        .setContentTitle("Late Task!")
                        .setContentText(task + " is overdue!");

            }
            notification = builder.build();
            notificationManagerCompat = NotificationManagerCompat.from(this);
            notificationManagerCompat.notify(1, notification);
        }else{
            requestPermissions();
        }

    }
    //this is for creating getting the permission when creating notifications & the pop up screen - automatically enables notifications if yes
    private void requestPermissions() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)){
            new AlertDialog.Builder(this)
                    .setTitle("Notification Permission")
                    .setMessage("Todo would like to send you notifications when a task is due soon or past-due")
                    .setPositiveButton("Agree", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.POST_NOTIFICATIONS}, RequestPermission);

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();


        }else{
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.POST_NOTIFICATIONS}, RequestPermission);
        }
    }

    //Checks to see if the permission is granted or denied
    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == RequestPermission) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();

            }else{
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }

        }
    }
}