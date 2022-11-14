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
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.skydoves.powermenu.CustomPowerMenu;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.PortUnreachableException;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.concurrent.ExecutorService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;



/*
This class controls the main screen. It extends our custom ToDoClickListener.
*/

public class MainActivity extends AppCompatActivity implements ToDoClickListener {

    private ArrayList<ToDo> toDoList, completed, filtered;
    private RecyclerView toDoRecyclerView, completedRecyclerView;
    private boolean showCompleted = false, showIncomplete = true;
    private ImageView dropdownIcon, dropdownIcon2;
    private EditText inputToDo;

    NotificationManagerCompat notificationManagerCompat;
    android.app.Notification notification;
    private int RequestPermission = 1;

    private SearchView searchView;
    private List<FilterPowerMenuItem> filterItems;
    private ArrayList<String> filterList = new ArrayList<>();
    private final HashMap<String, Boolean> filters = new HashMap<>();
    private final static int EDIT_TODO_ACTIVITY_REQUEST = 1, ADD_TAGS_ACTIVITY_REQUEST = 2;
    private int newestCreatedToDo = -1;
    private MyScrollListener toDoScrollListener, completedScrollListener;
    private FloatingActionButton toTopButton;
    private final boolean[] toTop = new boolean[2];
    private int toTopControl = 0; // 0: controlling incomplete list; 1: controlling completed list
    private int sortingType=0; // this is used to control the sort of the tasks.



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

        toDoRecyclerView = findViewById(R.id.toDoRecyclerView);
        completedRecyclerView = findViewById(R.id.completedRecyclerView);

        inputToDo = findViewById(R.id.inputToDo);
        dropdownIcon = findViewById(R.id.dropdownIcon);
        dropdownIcon2 = findViewById(R.id.dropdownIcon2);
        toTopButton = findViewById(R.id.floatingActionButton);
        searchView = findViewById(R.id.searchView);

        completedRecyclerView.setVisibility(View.GONE);
//        toTopButton.hide();

        toDoRecyclerView.addOnScrollListener(toDoScrollListener = new MyScrollListener());
        completedRecyclerView.addOnScrollListener(completedScrollListener = new MyScrollListener());

        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchToDos(newText);
                return true;
            }
        });

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

    public void onSort(View view){
        ArrayList<PowerMenuItem> list=new ArrayList<>();
        list.add(new PowerMenuItem("Ascending Due Date",false));
        list.add(new PowerMenuItem("Descending Due Date",false));
        list.add(new PowerMenuItem("Total Marks",false));
        PowerMenu powerMenu = new PowerMenu.Builder(this)
                .addItemList(list) // list has "Novel", "Poetry", "Art"
                .setAnimation(MenuAnimation.SHOWUP_TOP_LEFT) // Animation start point (TOP | LEFT).
                .setMenuRadius(10f) // sets the corner radius.
                .setMenuShadow(10f) // sets the shadow.
                .setTextColor(ContextCompat.getColor(this, R.color.black))
                .setTextGravity(Gravity.CENTER)
                .setTextTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD))
                .setSelectedTextColor(Color.WHITE)
                .setMenuColor(Color.WHITE)
                .setSelectedMenuColor(ContextCompat.getColor(this, R.color.purple_500)).build();
        powerMenu.setOnMenuItemClickListener(new OnMenuItemClickListener<PowerMenuItem>() {
            @Override
            public void onItemClick(int position, PowerMenuItem item) {
                powerMenu.dismiss();
                if(position==0)
                    sortingType = 0;
                if (position==1)
                    sortingType = 1;
                if(position==2)
                    sortingType=2;
                loadData();
            }
        });
        powerMenu.showAsDropDown(view);

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
        } catch (Exception e) {
            // load defaults
            toDoList = new ArrayList<>();
            // save new state
            save();
            e.printStackTrace();
        }

        // Make sure any old filters are removed
        Set<String> s = filters.keySet();
        String[] keyArr = new String[s.size()];
        keyArr = s.toArray(keyArr);
        for (String filter : keyArr) {
            if (!filterList.contains(filter))
                filters.remove(filter);
        }

        // Apply the filter to each item to see if it should be displayed to the user
        filterList = getFilterList();
        filtered = new ArrayList<>();
        completed = new ArrayList<>();
        for (ToDo toDo : toDoList) {
            if (!toDo.isDone() && filterAllows(toDo))
                filtered.add(toDo);
            else if (toDo.isDone() && filterAllows(toDo))
                completed.add(toDo);
        }
        if(sortingType==0) {
            Collections.sort(filtered, ToDo.DueDateAscComparator);
            Collections.sort(completed, ToDo.DueDateAscComparator);
        }
        if(sortingType==1) {
            Collections.sort(filtered, ToDo.DueDateDescComparator);
            Collections.sort(completed, ToDo.DueDateDescComparator);
        }
        if(sortingType==2){
            for (ToDo task: filtered
                 ) {
                System.out.println(task.getText()+" total: "+task.getMaxGrade());
                System.out.println(task.getText()+" Received: "+task.getGradeReceived());
            }
            Collections.sort(filtered, ToDo.TotalMarksComparator);
            Collections.sort(completed, ToDo.TotalMarksComparator);
        }
        setAdapter();
    }

    // Generate the list of all filters from all saved tasks
    private ArrayList<String> getFilterList() {
        ArrayList<String> out = new ArrayList<>();
        out.add("Graded");
        out.add("Ungraded");
        ArrayList<String> nonDefault = new ArrayList<>();
        for (ToDo toDo : toDoList) {
            loop:

            for (String tag : toDo.getTags()) {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // create new to-do from user input
    public void createToDo(View v) {
        // only allow user to add to-do if they entered text
        if (!isEmpty(inputToDo)) {
            ToDo newToDo = new ToDo(inputToDo.getText().toString());
            // adding user input to-do to array list
            toDoList.add(newToDo);
            // clearing user input after to-do is submitted
            inputToDo.getText().clear();
            // Clear search
            searchView.setQuery("",true);
            // Clear filters
            for (String key : filters.keySet())
                filters.put(key,false);
            // save changes
            save();
            loadData();
            // Make sure incomplete tasks list is open, not completed tasks
            if (!showIncomplete)
                showCompleted(findViewById(R.id.showIncomplete));
            // Get the position of the new to-do
            newestCreatedToDo = indexOf(filtered, newToDo); // get index in case sorting doesn't put it at the bottom
            // Scroll to new item
            toDoRecyclerView.scrollToPosition(newestCreatedToDo);
        } else {
            // ask the user to enter a name for the task
            makeNotification("Please enter a task name");
        }
    }

    private void setAdapter() {
        // Set adapter for uncompleted tasks
        // boiler-plate code
        toDoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        toDoRecyclerView.setItemAnimator(new DefaultItemAnimator());

        ToDoAdapter toDoRecyclerAdapter = new ToDoAdapter(filtered); // Changed this to use `filtered` so that we only show the user items that match the filter
        toDoRecyclerView.setAdapter(toDoRecyclerAdapter);
        toDoRecyclerAdapter.setClickListener(this);

        // Set adapter for complete tasks
        completedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        completedRecyclerView.setItemAnimator(new DefaultItemAnimator());

        ToDoAdapter completedRecyclerAdapter = new ToDoAdapter(completed);
        completedRecyclerView.setAdapter(completedRecyclerAdapter);
        completedRecyclerAdapter.setClickListener(this);
    }

    // utility method
    private boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }

    // indexOf method that uses .equals() instead of ==
    private int indexOf(ArrayList<ToDo> list, ToDo toDo) {
        for (int i = 0; i < list.size(); i++)
            if (list.get(i).equals(toDo))
                return i;
        return -1;
    }

    // method creates a new array list according to user search and calls recyclerAdapter to update data
    private void searchToDos(String text) {
        if (!text.equals("")) {
            ArrayList<ToDo> toDoSearchResults = new ArrayList<>();
            ArrayList<ToDo> completedSearchResults = new ArrayList<>();
            for (ToDo todo : filtered) {
                if (todo.getText().toLowerCase().contains(text.toLowerCase())) {
                    toDoSearchResults.add(todo);
                }
            }
            filtered = toDoSearchResults;
            for (ToDo todo : completed) {
                if (todo.getText().toLowerCase().contains(text.toLowerCase())) {
                    completedSearchResults.add(todo);
                }
            }
            completed = completedSearchResults;

            setAdapter();
        } else {
            loadData();
        }
    }

    // bound to the RecyclerView elements (individual to-dos), called when user clicks
    // on three dots on to-do
    @Override
    public void onEditClick(View view, int position) {
        // Determine if the clicked to do is in completed or toDoList
        CheckBox checkBox = view.findViewById(R.id.checkBox);
        boolean taskComplete = checkBox.isChecked();
        ToDo clickedToDo = (taskComplete ? completed : filtered).get(position);

        ArrayList<PowerMenuItem> itemList = new ArrayList<>();
        itemList.add(new PowerMenuItem("Edit", false));
        itemList.add(new PowerMenuItem("Edit Tags", false));
        itemList.add(new PowerMenuItem("Delete", false));
        itemList.add(new PowerMenuItem("Set task as 'Graded'",false));
        if(clickedToDo.getTags().contains("Graded")){
            itemList.add(new PowerMenuItem("Enter Grade Received",false));
        }

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
        // Set what happens when each option is clicked
        powerMenu.setOnMenuItemClickListener((position1, item) -> {
            powerMenu.dismiss();
            if (item.getTitle().equals("Edit")) { // Edit item
                Intent i = new Intent(this, EditToDo.class);
                // send toDoList so that we can edit it there, then reload it when returning to main activity
                i.putExtra("ToDoList", toDoList);
                i.putExtra("Index", toDoList.indexOf(clickedToDo));
                startActivityForResult(i, EDIT_TODO_ACTIVITY_REQUEST);
            } else if (item.getTitle().equals("Delete")) { // Delete item
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Delete");
                alert.setMessage("Are you sure you want to delete?");
                alert.setPositiveButton("Yes",
                        (dialog, which) -> {
                            toDoList.remove(clickedToDo);

                            dialog.dismiss();

                            save();
                            loadData();

                            Snackbar sb = Snackbar.make(findViewById(R.id.myCoordinatorLayout), "Task deleted", Snackbar.LENGTH_LONG);
                            sb.setAction("UNDO", view1 -> {
                                // undo delete
                                toDoList.add(clickedToDo);
                                save();
                                loadData();
                            });
                            sb.show();
                        });
                alert.setNegativeButton("No", (dialog, which) -> dialog.cancel());

                alert.show();

            } else if (item.getTitle().equals("Edit Tags")) {
                Intent i = new Intent(this, AddTagActivity.class);
                // send toDoList so that we can edit it there, then reload it when returning to main activity
                i.putExtra("ToDoList", toDoList);
                i.putExtra("Index", toDoList.indexOf(clickedToDo));
                i.putExtra("tagName",""); // default tag(empty string)
                startActivityForResult(i, ADD_TAGS_ACTIVITY_REQUEST);
            } else if(item.getTitle().equals("Set task as 'Graded'")) {
                Intent i = new Intent(this, AddTagActivity.class);
                i.putExtra("ToDoList", toDoList);
                i.putExtra("Index", toDoList.indexOf(clickedToDo));
                i.putExtra("tagName","Graded"); // This is the tag that will be added to the activity if this is selected.
                startActivityForResult(i, ADD_TAGS_ACTIVITY_REQUEST);
            } else if(item.getTitle().equals("Enter Grade Received")) { // This will show up only for Graded ToDos.
                Intent i=new Intent(this,GradeReceived.class);
                i.putExtra("ToDoList", toDoList);
                i.putExtra("Index", toDoList.indexOf(clickedToDo));
                startActivity(i);
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
        // Code for delaying moving the task so that the checkbox animation finishes
        new Handler().postDelayed(() -> {
            // Check if it is check or unchecked to determine what action to take
            // True means it's moving from uncompleted to complete
            // False means it's moving from complete to uncompleted
            boolean checked = ((CheckBox) view).isChecked();

            // Get the to do object that's been checked/unchecked
            ToDo completedTask;
            if (checked) {
                completedTask = filtered.get(position);
                // alert the user of their action if it's not
                makeNotification("Completed \"" + completedTask.getText() + "\"");
            } else {
                completedTask = completed.get(position);
                // alert the user of their action
                makeNotification("Moved \"" + completedTask.getText() + "\" to uncompleted");
            }

            completedTask.setDone(!completedTask.isDone());

            // save changes
            // load data again
            save();
            loadData();
        }, 300);
    }

    // Called after each ViewHolder is initialized in the onBindViewHolder method in ToDoAdapter
    public void onTaskCreated(ToDoAdapter.MyViewHolder holder, int position) {
        // When a view-holder is created, check if it needs to be highlighted
        if (position != -1 && position == newestCreatedToDo) {
            // Creating an array of two colors
            ColorDrawable[] colors = new ColorDrawable[]{new ColorDrawable(Color.WHITE), new ColorDrawable(Color.parseColor("#cfcfcf"))};

            // When button is clicked, A transition is created
            // and applied to the background with specified duration
            TransitionDrawable transition = new TransitionDrawable(colors);
            holder.itemView.setBackground(transition);
            int duration = 200;
            transition.startTransition(duration);
            // After the transition is completed, reverse it
            new Handler().postDelayed(() -> transition.reverseTransition(duration), duration);

            // Reset the newestCreatedToDo variable to -1 so that it doesn't keep highlighting
            newestCreatedToDo = -1;
        }
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

    // Called when the user clicks the filter button
    // Handles applying a filter to the displayed tasks
    public void openFilters(View view) {
        // Load in filterItems
        filterItems = new ArrayList<>();
        for (String tag : filterList) {
            if (!filters.containsKey(tag))
                filters.put(tag,false);
            filterItems.add(new FilterPowerMenuItem(tag, filters.get(tag)));
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
            searchToDos(searchView.getQuery().toString());
        });
        customPowerMenu.showAsDropDown(view); // view is where the menu is anchored
    }

    // Called when the user expands or collapses either the incomplete or completed list
    public void showCompleted(View view) {
        if (view.getId() == R.id.showCompletedButton || view.getId() == R.id.dropdownIcon) {
            showCompleted = !showCompleted;
            dropdownIcon.setImageResource(showCompleted ? R.drawable.dropdown_down : R.drawable.dropdown_right);
            completedRecyclerView.setVisibility(showCompleted ? View.VISIBLE : View.GONE);
        } else if (view.getId() == R.id.showIncomplete || view.getId() == R.id.dropdownIcon2) {
            showIncomplete = !showIncomplete;
            dropdownIcon2.setImageResource(showIncomplete ? R.drawable.dropdown_down : R.drawable.dropdown_right);
            toDoRecyclerView.setVisibility(showIncomplete ? View.VISIBLE : View.GONE);
        }

        // Determine which list is being controlled by the "to top" button
        if (showIncomplete) { // Case where button controls incomplete list
            toTopButton.show();
            toTopControl = 0;
        } else if (showCompleted) { // Case where button controls completed list
            toTopButton.show();
            toTopControl = 1;
        } else // Case where there should be no button
            toTopButton.hide();

        // Check if the functionality of the "to top" button needs to change
        gotoTopBottomSwap();
    }

    public void scrollToTop(View view) {
        if (toTop[toTopControl]) {
            if (toTopControl == 0) {
                toDoRecyclerView.smoothScrollToPosition(0);
                toDoScrollListener.netScrollY = 0;
            } else {
                completedRecyclerView.smoothScrollToPosition(0);
                completedScrollListener.netScrollY = 0;
            }
        } else {
            if (toTopControl == 0) {
                toDoRecyclerView.smoothScrollToPosition(Integer.MAX_VALUE);
                toDoScrollListener.netScrollY = Integer.MAX_VALUE;
            } else {
                completedRecyclerView.smoothScrollToPosition(Integer.MAX_VALUE);
                completedScrollListener.netScrollY = Integer.MAX_VALUE;
            }
        }
        gotoTopBottomSwap();
    }

    // Swap functionality of the "to top" button between "to top" and "to bottom"
    private void gotoTopBottomSwap() {
        if (showIncomplete ? toDoScrollListener.netScrollY == 0 : !showCompleted || completedScrollListener.netScrollY == 0) {
            toTop[toTopControl] = false;
            toTopButton.setImageResource(R.drawable.dropdown_down);
        } else {
            toTop[toTopControl] = true;
            toTopButton.setImageResource(R.drawable.dropdown_up);
        }
    }

    private class MyScrollListener extends RecyclerView.OnScrollListener {
        int netScrollY = 0;

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int oldPos = netScrollY;
            netScrollY = Math.max(0, netScrollY + dy);
            if (oldPos == 0 || netScrollY == 0)
                gotoTopBottomSwap();
        }
    }
}
