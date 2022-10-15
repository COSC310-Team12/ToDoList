package com.example.todolist;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Date;

/*
This class controls the edit to-do page. Users can navigate to this page by clicking on a to-do
on the main page. This triggers the onEditClick() method and sends the to-do array list to this class.
On this page, users can change the text of their to-do, and set a due date.
Error checking is performed for both of those operations. Once the user is done,
they can return to the main page.
*/

public class EditToDo extends AppCompatActivity {
    TextInputEditText name, date;
    TextInputLayout dueDateBox, nameBox;
    ToDo toDo;
    ArrayList<ToDo> toDoList;
    Date newDate;
    CoordinatorLayout coordinatorLayout;
    boolean validDate = true;

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
        if (activityTitle.length() > 25) {
            activityTitle = activityTitle.substring(0, 25);
            activityTitle += "...";
        }

        ((TextView) findViewById(R.id.editTaskTextView)).setText("Edit \"" + activityTitle + "\"");

        name = findViewById(R.id.editTaskName);
        date = findViewById(R.id.editTaskDueDate);
        dueDateBox = findViewById(R.id.dueDateBox);
        nameBox = findViewById(R.id.titleBox);
        coordinatorLayout = findViewById(R.id.myCoordinatorLayout);

        Button cancelEditButton = findViewById(R.id.cancelEditButton);
        Button deleteButton = findViewById(R.id.deleteButton);
        Button submitEditButton = findViewById(R.id.submitEditButton);

        cancelEditButton.setOnClickListener(this::goBack);
        deleteButton.setOnClickListener(this::deleteToDo);
        submitEditButton.setOnClickListener(this::submit);

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

        goBack(view);
    }

    public void goBack(View view) {
        // return to main activity
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("ToDoList", toDoList);
        startActivity(intent);
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
                    startActivity(intent);
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
}