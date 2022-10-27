package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;

public class AddTagActivity extends AppCompatActivity implements TagClickListener {
    private TextInputEditText name;
    private TextInputLayout nameBox;
    private RecyclerView recyclerView;
    private TagAdapter tagAdapter;
    private ArrayList<String> tagList;
    private ArrayList<ToDo> toDoList;
    private int toDoIndex;
    private ToDo toDo;
    private HashMap<String, Boolean> filters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tag);

        setTitle("Edit Tags");

        Intent intent = getIntent();
        //noinspection unchecked
        toDoList = (ArrayList<ToDo>) intent.getSerializableExtra("ToDoList");
        toDoIndex = intent.getIntExtra("Index",0);
        toDo = toDoList.get(toDoIndex);
        //noinspection unchecked
        filters = (HashMap<String, Boolean>) intent.getSerializableExtra("Filters");

        name = findViewById(R.id.editTagName);
        nameBox = findViewById(R.id.tagBox);
        recyclerView = findViewById(R.id.recyclerView);

        // make box go red if the tag name is blank
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

        setAdapter();
    }

    private void setAdapter() {
        // boiler-plate code
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        tagAdapter = new TagAdapter(tagList);
        recyclerView.setAdapter(tagAdapter);
        tagAdapter.setClickListener(this);
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

    @Override
    public void onEditClick(View view, int position) {

    }
}