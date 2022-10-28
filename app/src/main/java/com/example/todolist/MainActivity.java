package com.example.todolist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.skydoves.powermenu.CustomPowerMenu;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.OnDismissedListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


/*
This class controls the main screen. It extends our custom ToDoClickListener.
*/

public class MainActivity extends AppCompatActivity implements ToDoClickListener {

    private ArrayList<ToDo> toDoList, completed, filtered;
    private RecyclerView recyclerView;
    private ToDoAdapter recyclerAdapter;
    private EditText inputToDo;
    private List<FilterPowerMenuItem> filterItems;
    private ArrayList<String> filterList = new ArrayList<>();
    private final HashMap<String, Boolean> filters = new HashMap<>();
    private final static int EDIT_TODO_ACTIVITY_REQUEST = 1, ADD_TAGS_ACTIVITY_REQUEST = 2;

    // initialization code
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

        // load in data from file
        loadData();
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        // Read in from file
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(getFilesDir(), "savedToDos.dat")))) {
            // FOR DEBUGGING ONLY
            final boolean LOAD_FROM_FILE = true;
            if (!LOAD_FROM_FILE)
                throw new FileNotFoundException("Not loading from file for debug purposes. To change this behavior, change LOAD_FROM_FILE to true");
            // try loading from saved file
            toDoList = (ArrayList<ToDo>) in.readObject();
            completed = (ArrayList<ToDo>) in.readObject();
        } catch (FileNotFoundException | ClassNotFoundException e) {
            // load defaults
            toDoList = new ArrayList<>();
            completed = new ArrayList<>();
            // save new state
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Apply the filter to each item to see if it should be displayed to the user
        filterList = getFilterList();
        filtered = new ArrayList<>();
        for (ToDo toDo : toDoList)
            if (filterAllows(toDo))
                filtered.add(toDo);

        setAdapter();
    }

    // Generate the list of all filters from all saved tasks
    private ArrayList<String> getFilterList() {
        ArrayList<String> out = new ArrayList<>();
        out.add("Graded");
        out.add("Ungraded");
        ArrayList<String> nonDefault = new ArrayList<>();
        for (ToDo toDo : toDoList) {
            loop: for (String tag : toDo.getTags()) {
                for (String s : nonDefault)
                    if (s.equals(tag))
                        continue loop;
                nonDefault.add(tag);
            }
        }
        for (ToDo toDo : completed) {
            loop: for (String tag : toDo.getTags()) {
                for (String s : nonDefault)
                    if (s.equals(tag))
                        continue loop;
                nonDefault.add(tag);
            }
        }

        Collections.sort(nonDefault);

        out.addAll(nonDefault);

        return out;
    }

    // Determines if the To Do item is allowed by the filter
    private boolean filterAllows(ToDo toDo) {
        // Make sure any old filters are removed
        for (String filter : filters.keySet()) {
            if (!filterList.contains(filter))
                filters.remove(filter);
        }

        // If no filter is selected
        if (!filters.containsValue(true))
            return true;
        // If an item has at least one tag that matches the filter, let it through
        for (String tag : toDo.getTags()) {
            if (Boolean.TRUE.equals(filters.get(tag)))
                return true;
        }
        // If no tag matched the filter, don't let it through
        return false;
    }

    // Should be called directly after changing toDoList or completed
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
            loadData();
        } else {
            // ask the user to enter a name for the task
            Snackbar.make(findViewById(R.id.myCoordinatorLayout), "Please enter a task name", Snackbar.LENGTH_LONG).show();
        }
    }

    private void setAdapter() {
        // boiler-plate code
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerAdapter = new ToDoAdapter(filtered); // Changed this to use `filtered` so that we only show the user items that match the filter
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
        ArrayList<PowerMenuItem> itemList = new ArrayList<>();
        itemList.add(new PowerMenuItem("Edit", false));
        itemList.add(new PowerMenuItem("Edit Tags", false));
        itemList.add(new PowerMenuItem("Delete", false));

        PowerMenu powerMenu = new PowerMenu.Builder(this)
                .addItemList(itemList)
                .setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT) // Animation start point (TOP | RIGHT).
                .setMenuRadius(10f) // sets the corner radius.
                .setMenuShadow(10f) // sets the shadow.
                .setTextColor(ContextCompat.getColor(this, R.color.black))
                .setTextGravity(Gravity.CENTER)
                .setSelectedTextColor(Color.WHITE)
                .setMenuColor(Color.WHITE)
                .setSelectedMenuColor(ContextCompat.getColor(this, R.color.purple_500))
                .build();
        powerMenu.setOnMenuItemClickListener((position1, item) -> {
            powerMenu.dismiss();
            if (item.getTitle().equals("Edit")) { // Edit item
                Intent i = new Intent(this, EditToDo.class);
                // send toDoList so that we can edit it there, then reload it when returning to main activity
                i.putExtra("ToDoList", toDoList);
                i.putExtra("Index", position);
                startActivityForResult(i, EDIT_TODO_ACTIVITY_REQUEST);
            }
            else if (item.getTitle().equals("Delete")) { // Delete item
                ToDo deletedTodo = toDoList.get(position);
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Delete");
                alert.setMessage("Are you sure you want to delete?");
                alert.setPositiveButton("Yes",
                        (dialog, which) -> {
                            toDoList.remove(deletedTodo);

                            dialog.dismiss();

                            save();
                            loadData();

                            Snackbar sb = Snackbar.make(findViewById(R.id.myCoordinatorLayout), "Task deleted", Snackbar.LENGTH_LONG);
                            sb.setAction("UNDO", view1 -> {
                                // undo delete
                                toDoList.add(deletedTodo);
                                save();
                                loadData();
                            });
                            sb.show();
                        });
                alert.setNegativeButton("No", (dialog, which) -> dialog.cancel());

                alert.show();

            }
            else if (item.getTitle().equals("Edit Tags")) {
                Intent i = new Intent(this, AddTagActivity.class);
                // send toDoList so that we can edit it there, then reload it when returning to main activity
                i.putExtra("ToDoList", toDoList);
                i.putExtra("Index", position);
                startActivityForResult(i, ADD_TAGS_ACTIVITY_REQUEST);
            }
        });
        powerMenu.showAsAnchorRightBottom(view); // view is where the menu is anchored
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            // For returning from edit activity
            if (requestCode == EDIT_TODO_ACTIVITY_REQUEST) {
                if (data != null) {
                    toDoList = (ArrayList<ToDo>) data.getSerializableExtra("ToDoList");
                    save();
                    loadData();

                    // handle any notifications requested by previous activity
                    if (data.hasExtra("Notification")) {
                        int notification = data.getIntExtra("Notification", -1);
                        if (notification == 0) {
                            Snackbar sb = Snackbar.make(findViewById(R.id.myCoordinatorLayout), "Task deleted", Snackbar.LENGTH_LONG);
                            sb.setAction("UNDO", view -> {
                                // undo delete
                                toDoList.add((ToDo) data.getSerializableExtra("deletedToDo"));
                                save();
                                loadData();
                            });
                            sb.show();
                        }
                    }
                }
            }
            // For returning from the add tags activity
            else if (requestCode == ADD_TAGS_ACTIVITY_REQUEST) {
                if (data != null) {
                    toDoList = (ArrayList<ToDo>) data.getSerializableExtra("ToDoList");
                    save();
                    loadData();
                }
            }
        }
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

    // Called when the user clicks the filter button
    // Handles applying a filter to the displayed tasks
    public void openFilters(View view) {
        // Ensure the filterItems list is not null
        if (filterItems == null)
            filterItems = new ArrayList<>();

        // Make sure all the filters in the menu still exist
        loop: for (int i = filterItems.size() - 1; i >= 0; i--) {
            String tag = filterItems.get(i).getTitle();
            for (String s : filterList)
                if (tag.equals(s))
                    continue loop;
            filterItems.remove(i);
        }

        // Make sure all existing filters are in the menu
        upperLoop: for (String s : filterList) {
            for (FilterPowerMenuItem i : filterItems)
                if (i.getTitle().equals(s))
                    continue upperLoop;
            filterItems.add(new FilterPowerMenuItem(s));
        }
        CustomPowerMenu<FilterPowerMenuItem, FilterMenuAdapter> customPowerMenu = new CustomPowerMenu.Builder<>(this, new FilterMenuAdapter())
                .addItemList(filterItems)
                .setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT)
                .setMenuRadius(10f)
                .setMenuShadow(10f)
                .build();
        customPowerMenu.setOnDismissedListener(() -> {
            // When menu is closed, update the filter and reload list, which uses will apply the filter
            for (FilterPowerMenuItem item : filterItems)
                filters.put(item.getTitle(), item.isChecked());
            loadData();
        });
        customPowerMenu.showAsDropDown(view); // view is where the menu is anchored
    }
}