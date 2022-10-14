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

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EditToDo extends AppCompatActivity {
    TextInputEditText name, date;
    TextInputLayout dueDateBox;
    ToDo toDo;
    ArrayList<ToDo> toDoList;
    Date newDate;

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

        name.setText(toDo.getText());
        if (toDo.getDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            date.setText(sdf.format(toDo.getDate()));
        }

        // Make the date box go red if there is an invalid date. Also convert a valid date to a Date object to store in the To Do object
        date.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Check that the entered text is a valid date
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                    sdf.setLenient(false);
                    newDate = sdf.parse(charSequence.toString());

                    // It is a valid date
                    // Make sure text field is regularly colored
                    dueDateBox.setBoxStrokeColor(getResources().getColor(R.color.purple_500));
                    dueDateBox.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.purple_500)));
                } catch (ParseException e) {
                    // Invalid date

                    // Clear the Date stored if the user entered a valid date, then changed it to be invalid
                    newDate = null;

                    // Only give the user the red box of judgement if they have entered a whole date
                    if (charSequence.toString().split("/").length > 2) {
                        dueDateBox.setBoxStrokeColor(getResources().getColor(R.color.error_red));
                        dueDateBox.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.error_red)));
                    } else {
                        dueDateBox.setBoxStrokeColor(getResources().getColor(R.color.purple_500));
                        dueDateBox.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.purple_500)));
                    }
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
            toDo.setText(name.getText().toString());
        if (newDate != null)
            toDo.setDate(newDate);

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
}