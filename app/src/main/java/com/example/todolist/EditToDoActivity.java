package com.example.todolist;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

/*
This class controls the edit to-do page. Users can navigate to this page by clicking on a to-do
on the main page. This triggers the onEditClick() method and sends the to-do array list to this class.
On this page, users can change the text of their to-do, and set a due date.
Error checking is performed for both of those operations. Once the user is done,
they can return to the main page.
*/

public class EditToDoActivity extends AppCompatActivity {
    TextInputEditText name, date;
    TextInputLayout dueDateBox, nameBox;
    ToDo toDo;
    ArrayList<ToDo> toDoList;
    Date newDate;
    CoordinatorLayout coordinatorLayout;
    boolean validDate = true;
    int RequestPermission = 1;
    boolean notificationOn = false;
    String taskName;

    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_todo);

        setTitle("Edit Task");

        Intent intent = getIntent();

        int index = intent.getIntExtra("Index", -1);

        // noinspection unchecked
        toDoList = (ArrayList<ToDo>) intent.getSerializableExtra("ToDoList");
        toDo = toDoList.get(index);

        String activityTitle = toDo.getText();
        activityTitle = abbreviateIfTooLong(activityTitle);

        ((TextView) findViewById(R.id.addTagsTitle)).setText("Edit \"" + activityTitle + "\"");

        name = findViewById(R.id.editTaskName);
        date = findViewById(R.id.editTaskDueDate);
        dueDateBox = findViewById(R.id.dueDateBox);
        nameBox = findViewById(R.id.titleBox);
        coordinatorLayout = findViewById(R.id.myCoordinatorLayout);


        Button cancelEditButton = findViewById(R.id.cancelEditButton);
        Button deleteButton = findViewById(R.id.deleteButton);
        Button submitEditButton = findViewById(R.id.doneButton);
        Button donoButton = findViewById(R.id.donateButton);

        CheckBox notify = findViewById(R.id.Notify);
        cancelEditButton.setOnClickListener(this::goBack);
        deleteButton.setOnClickListener(this::deleteToDo);
        submitEditButton.setOnClickListener(this::submit);
        donoButton.setOnClickListener(this::donation);
        taskName = activityTitle;


        name.setText(toDo.getText());
        if (toDo.getDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            date.setText(sdf.format(toDo.getDate()));
        }

        // make box go red if the name is blank
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // change box red if it is empty
                titleError(charSequence.length() <= 0);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // make the date box go red if there is an invalid date. Also convert a valid date to a Date object to store in the To Do object
        date.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // check that the entered text is a valid date
                if (charSequence.length() > 0) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                        sdf.setLenient(false);
                        newDate = sdf.parse(charSequence.toString());
                        // it is a valid date
                        validDate = true;
                        // make sure text field is regularly colored
                        dueDateError(false);
                    } catch (ParseException e) {
                        // invalid date
                        validDate = false;
                        // clear the Date stored if the user entered a valid date, then changed it to be invalid
                        newDate = null;
                        // only give the user the red box of judgement if they have entered a whole date
                        dueDateError(charSequence.toString().split("/").length > 2);
                    }
                } else {
                    validDate = true;
                    newDate = null;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // make pressing enter in the final text box submit the changes
        date.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == 6 || keyEvent.getAction() == 0)
                submit(textView);
            return true;
        });

        notify.setOnClickListener(v -> {
            if(notify.isChecked()){
                if (ContextCompat.checkSelfPermission(EditToDoActivity.this,
                        Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                    notificationOn = true;
                }
                else{
                    requestPermissions();
                }
            }
            else{
                notificationOn = false;
            }
        });
    }

    public static String abbreviateIfTooLong(String activityTitle) {
        if (activityTitle.length() > 25) {
            activityTitle = activityTitle.substring(0, 25);
            activityTitle += "...";
        }
        return activityTitle;
    }

    // called by submit button
    public void submit(View view) {
        // set new name and date, if the user entered them
        String newName = Objects.requireNonNull(name.getText()).toString();
        if (!newName.equals(""))
            toDo.setText(newName);
        else {
            titleError(true);
            makeNotification("Please enter a task name");
            return;
        }
        if (validDate) {
            toDo.setDate(newDate);
        } else {
            dueDateError(true);
            makeNotification("Please enter a valid date");
            return;
        }
        if(notificationOn){
            notificationCaller(getNotification("Late Task", String.format("%s is late", taskName)), 6000);
        }

        goBack(view);
    }

    public void goBack(View view) {
        // return to main activity
        setResult(RESULT_OK, new Intent().putExtra("ToDoList", toDoList));
        finish();
    }

    public void deleteToDo(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Delete");
        alert.setMessage("Are you sure you want to delete?");
        alert.setPositiveButton("Yes",
                (dialog, which) -> {
                    toDoList.remove(toDo);
                    dialog.dismiss();
                    Intent intent = new Intent(view.getContext(), MainActivity.class);
                    // notify main activity to show message
                    intent.putExtra("Notification", 0);
                    intent.putExtra("ToDoList", toDoList);
                    intent.putExtra("deletedToDo", toDo);
                    setResult(RESULT_OK, intent);
                    finish();
                });
        alert.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        alert.show();
    }

    private void makeNotification(String msg) {
        Snackbar sb = Snackbar.make(findViewById(R.id.myCoordinatorLayout), msg, Snackbar.LENGTH_LONG);
        sb.show();
    }

    private void titleError(boolean error) {
        if (error) {
            nameBox.setBoxStrokeColor(getResources().getColor(R.color.error_red));
            nameBox.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.error_red)));
            nameBox.setHelperText("Please enter a title");
        } else {
            nameBox.setBoxStrokeColor(getResources().getColor(R.color.purple_500));
            nameBox.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.purple_500)));
            nameBox.setHelperText(" ");
        }
    }

    private void dueDateError(boolean error) {
        if (error) {
            dueDateBox.setBoxStrokeColor(getResources().getColor(R.color.error_red));
            dueDateBox.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.error_red)));
            dueDateBox.setHelperText("Invalid date");
        } else {
            dueDateBox.setBoxStrokeColor(getResources().getColor(R.color.purple_500));
            dueDateBox.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.purple_500)));
            dueDateBox.setHelperText(" ");
        }
    }

//Creates the pending intent & will send the notification to the sender class, which will then send the message
    private void notificationCaller(Notification notification, int delay) {
        Intent notifyInt = new Intent(this, NotificationSender.class);

        notifyInt.putExtra(NotificationSender.NotificationID, 1);
        notifyInt.putExtra(NotificationSender.NOTIFICATION, notification);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notifyInt, PendingIntent.FLAG_IMMUTABLE);

        long AlarmTimer = SystemClock.elapsedRealtime()+delay;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        assert alarmManager != null;
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, AlarmTimer, pendingIntent);

    }
    //generic notification builder method
    private Notification getNotification(String title, String content) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "NotifyLate")
                    .setSmallIcon(R.drawable.notificationbell)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setAutoCancel(true);

            return builder.build();


    }

    //method for creating dialogue box & asking for permissions
    private void requestPermissions() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)){
            new AlertDialog.Builder(this)
                    .setTitle("Notification Permission")
                    .setMessage("Todo would like to send you notifications when a task is due soon or past-due")
                    .setPositiveButton("Agree", (dialog, which) -> ActivityCompat.requestPermissions(EditToDoActivity.this, new String[] {Manifest.permission.POST_NOTIFICATIONS}, RequestPermission))
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
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

    public void donation(View view){
        Intent donate = new Intent(EditToDoActivity.this, payPalPayment.class);
        startActivity(donate);
    }

}