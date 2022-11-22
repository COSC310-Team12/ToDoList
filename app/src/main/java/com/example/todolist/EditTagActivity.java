package com.example.todolist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class EditTagActivity extends AppCompatActivity implements TagClickListener {
    private EditText tagNameEditText;
    private RecyclerView recyclerView;
    private ArrayList<ToDo> toDoList;
    private int toDoIndex;
    private ToDo toDo;
    private CoordinatorLayout snackbarPlaceholder;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tag);

        setTitle("Edit Tags");
        Intent intent = getIntent();
        //noinspection unchecked
        toDoList = (ArrayList<ToDo>) intent.getSerializableExtra("ToDoList");
        toDoIndex = intent.getIntExtra("Index", 0);
        toDo = toDoList.get(toDoIndex);

        tagNameEditText = findViewById(R.id.editTextTagName);
        Button addButton = findViewById(R.id.addButton);
        Button doneButton = findViewById(R.id.doneButton);
        recyclerView = findViewById(R.id.toDoRecyclerView);
        TextView activityTitle1 = findViewById(R.id.addTagsTitle);
        snackbarPlaceholder = findViewById(R.id.myCoordinatorLayout);

        String activityTitle = toDo.getText();
        if (activityTitle.length() > 25) {
            activityTitle = activityTitle.substring(0, 25);
            activityTitle += "...";
        }

        activityTitle1.setText("Edit Tags for \"" + activityTitle + "\"");

        addButton.setOnClickListener(view -> addTag());

        doneButton.setOnClickListener(view -> goBack());

        // make pressing enter in the final text box submit the changes
        tagNameEditText.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == 6 || keyEvent.getAction() == 0)
                addTag();
            return true;
        });

        setAdapter();
    }

    private void setAdapter() {
        // boiler-plate code
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        @SuppressWarnings("unchecked") ArrayList<String> tags = (ArrayList<String>) toDo.getTags().clone();
        // Don't let the user change the ungraded tag
        // They can delete the graded tag if they no longer wish for the task to be considered graded
        tags.remove("Ungraded");
        TagAdapter tagAdapter = new TagAdapter(tags);
        recyclerView.setAdapter(tagAdapter);
        tagAdapter.setClickListener(this);
    }

    private void addTag() {
        String tag = tagNameEditText.getText().toString();
        tagNameEditText.setText("");
        if (!tag.isEmpty()) {
            if (!toDo.getTags().contains(tag)) {
                toDo.addTag(tag);
                setAdapter();
                if(tag.equals("Graded")){ // In case the tag added is graded, then we want to get the total possible Grade for th given to do so we call Activity totalGrade.class
                    Intent intent=new Intent(this, TotalGradeActivity.class);
                    intent.putExtra("ToDoList", toDoList);
                    intent.putExtra("Index",toDoIndex);
                    intent.putExtra("todo",toDo.getText());
                    startActivity(intent);
                }
            } else {
                Snackbar.make(snackbarPlaceholder, "Tag is already on task", Snackbar.LENGTH_SHORT).show();
            }
        } else {
            Snackbar.make(snackbarPlaceholder, "Please enter a tag name", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void goBack() {
        // return to main activity
        setResult(RESULT_OK, new Intent().putExtra("ToDoList", toDoList));
        finish();
    }

    @Override
    public void onDeleteClick(View view, int position) {
        String removedTag = toDo.getTags().get(position);
        // if task is no longer graded, reset max grade
        if (removedTag.equals("Graded"))
            toDo.setMaxGrade(0);
        toDo.removeTag(removedTag);
        setAdapter();
    }
}