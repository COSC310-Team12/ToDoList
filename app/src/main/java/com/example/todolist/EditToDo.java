package com.example.todolist;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

        int index = intent.getIntExtra("Index",-1);

        //noinspection unchecked
        toDoList = (ArrayList<ToDo>) intent.getSerializableExtra("ToDoList");
        toDo = toDoList.get(index);

        ((TextView)findViewById(R.id.editTaskTextView)).setText("Edit \"" + toDo.getText() + "\"");

        name = findViewById(R.id.editTaskName);
        date = findViewById(R.id.editTaskDueDate);
        dueDateBox = findViewById(R.id.dueDateBox);
        nameBox = findViewById(R.id.titleBox);
        coordinatorLayout = findViewById(R.id.myCoordinatorLayout);

        name.setText(toDo.getText());
        if (toDo.getDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            date.setText(sdf.format(toDo.getDate()));
        }

        // Make box go red if the name is blank
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Change box red if it is empty
                titleError(charSequence.length() <= 0);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // Make the date box go red if there is an invalid date. Also convert a valid date to a Date object to store in the To Do object
        date.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Check that the entered text is a valid date
                if (charSequence.length() > 0) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                        sdf.setLenient(false);
                        newDate = sdf.parse(charSequence.toString());

                        // It is a valid date
                        validDate = true;
                        // Make sure text field is regularly colored
                        dueDateError(false);
                    } catch (ParseException e) {
                        // Invalid date
                        validDate = false;

                        // Clear the Date stored if the user entered a valid date, then changed it to be invalid
                        newDate = null;

                        // Only give the user the red box of judgement if they have entered a whole date
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

        // Make pressing enter in the final text box submit the changes
        date.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (keyEvent.getAction() == 0)
                submit(textView);
            return true;
        });
    }

    public void submit(View view) {
        // Set new name and date, if the user entered them

        //noinspection ConstantConditions
        String newName = name.getText().toString();
        if (!newName.equals(""))
            toDo.setText(newName);
        else {
            nameBox.setBoxStrokeColor(getResources().getColor(R.color.error_red));
            nameBox.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.error_red)));
            makeNotification("Please enter a task name");
            return;
        }

        if (validDate) {
            toDo.setDate(newDate);
        } else {
            dueDateBox.setBoxStrokeColor(getResources().getColor(R.color.error_red));
            dueDateBox.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.error_red)));
            makeNotification("Please enter a valid date");
            return;
        }

        goBack(view);
    }

    public void goBack(View view) {
        // Return to main activity
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("ToDoList",toDoList);
        startActivity(intent);
    }

    public void deleteButton(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Delete");
        alert.setMessage("Are you sure you want to delete?");
        alert.setPositiveButton("Yes",
                (dialog, which) -> {
            toDoList.remove(toDo);

            dialog.dismiss();

            Intent intent = new Intent(view.getContext(), MainActivity.class);
            // Notify main activity to show message
            intent.putExtra("Notification",0);
            intent.putExtra("ToDoList",toDoList);
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