package com.example.todolist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.skydoves.powermenu.CustomPowerMenu;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;


/*
This class controls the main screen. It extends our custom ToDoClickListener.
*/

@SuppressWarnings("deprecation")
public class MainActivity extends AppCompatActivity implements ToDoClickListener {

    private ArrayList<ToDo> toDoList, completed, filtered;
    private RecyclerView toDoRecyclerView, completedRecyclerView;
    private boolean showCompleted = false, showIncomplete = true;
    private ImageView dropdownIcon, dropdownIcon2;
    private EditText inputToDo;
    private static ArrayList<String[]> lang;
    private String language;

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

    private boolean connected;
    String transText;
    Translate translate;



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
        //ToDoList://paypalpay
        toDoRecyclerView = findViewById(R.id.toDoRecyclerView);
        completedRecyclerView = findViewById(R.id.completedRecyclerView);
        languageOptions();
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
        powerMenu.setOnMenuItemClickListener((position, item) -> {
            powerMenu.dismiss();
            if(position==0)
                sortingType = 0;
            if (position==1)
                sortingType = 1;
            if(position==2)
                sortingType=2;
            loadData();
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
        out.add("Ungraded");
        out.add("Graded");
        ArrayList<String> nonDefault = new ArrayList<>();
        for (ToDo toDo : toDoList) {
            loop:

            for (String tag : toDo.getTags()) {
                if (tag.equals("Graded") || tag.equals("Ungraded"))
                    continue;
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
            // By default task is ungraded
            newToDo.addTag("Ungraded");
            // adding user input to-do to array list
            toDoList.add(newToDo);
            // clearing user input after to-do is submitted
            inputToDo.getText().clear();
            // save changes
            save();
            // Clear filters
            for (String key : filters.keySet())
                filters.put(key,false);
            // Clear search
            searchView.setQuery("",true);
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
        itemList.add(new PowerMenuItem("Donate?", false));
        itemList.add(new PowerMenuItem("Translate Task", false));
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
                Intent i = new Intent(this, EditToDoActivity.class);
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
                Intent i = new Intent(this, EditTagActivity.class);
                // send toDoList so that we can edit it there, then reload it when returning to main activity
                i.putExtra("ToDoList", toDoList);
                i.putExtra("Index", toDoList.indexOf(clickedToDo));
                startActivityForResult(i, ADD_TAGS_ACTIVITY_REQUEST);
            } else if(item.getTitle().equals("Set task as 'Graded'")) {
                clickedToDo.addTag("Graded");
                clickedToDo.removeTag("Ungraded");
                Intent i = new Intent(this, TotalGradeActivity.class);
                i.putExtra("ToDoList", toDoList);
                i.putExtra("Index", toDoList.indexOf(clickedToDo));
                startActivityForResult(i, EDIT_TODO_ACTIVITY_REQUEST);
            } else if(item.getTitle().equals("Enter Grade Received")) { // This will show up only for Graded ToDos.
                Intent i=new Intent(this,GradeReceived.class);
                i.putExtra("ToDoList", toDoList);
                i.putExtra("Index", toDoList.indexOf(clickedToDo));
                startActivityForResult(i,EDIT_TODO_ACTIVITY_REQUEST);
            } else if(item.getTitle().equals("Donate?")){
                Intent i = new Intent(this, payPalPayment.class);
                startActivity(i);
            }
            else if(item.getTitle().equals("Translate Task")){
                if (checkInternetConnection()){
                    getTranslateService();
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Select a language");
                    String[] languages = getLanguages();
                    builder.setItems(languages, (dialog, which) -> {
                        dialog.dismiss();
                        language = languages[which];
                        clickedToDo.setText(translate(clickedToDo.getText()));
                        save();
                        loadData();
                    });
                    builder.show();
                }else{
                    makeNotification("No connection, cannot translate!");
                }
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
    public void getTranslateService(){
        //This gets credentials & verifies it
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try (InputStream is = getResources().openRawResource(R.raw.credentials)){

            final GoogleCredentials myCred = GoogleCredentials.fromStream(is);

            TranslateOptions translateOptions = TranslateOptions.newBuilder().setCredentials(myCred).build();
            translate = (Translate) translateOptions.getService();
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    public String translate(String item){
        //Translates the title into the desired language
        language = langCode(language);
        Translation translation = translate.translate(item, Translate.TranslateOption.targetLanguage(language), Translate.TranslateOption.model("base"));
        transText = translation.getTranslatedText();
        return transText;

    }

    public boolean checkInternetConnection(){
        //Checks if you can connect to the internet
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;

        return connected;
    }
    public void languageOptions (){
        lang = new ArrayList<>();
        lang.add( new String[]{"Afrikaans","af"});
        lang.add( new String[]{"Albanian","sq"});
        lang.add( new String[]{"Amharic","am"});
        lang.add( new String[]{"Arabic","ar"});
        lang.add( new String[]{"Armenian","hy"});
        lang.add( new String[]{"Assamese","as"});
        lang.add( new String[]{"Aymara","ay"});
        lang.add( new String[]{"Azerbaijani","az"});
        lang.add( new String[]{"Bambara","bm"});
        lang.add( new String[]{"Basque","eu"});
        lang.add( new String[]{"Belarusian","be"});
        lang.add( new String[]{"Bengali","bn"});
        lang.add( new String[]{"Bhojpuri","bho"});
        lang.add( new String[]{"Bosnian","bs"});
        lang.add( new String[]{"Bulgarian","bg"});
        lang.add( new String[]{"Catalan","ca"});
        lang.add( new String[]{"Cebuano","ceb"});
        lang.add( new String[]{"Chinese Simplified","zh-CN"});
        lang.add( new String[]{"Chinese Traditional","zh-TW"});
        lang.add( new String[]{"Corsican","co"});
        lang.add( new String[]{"Croatian","hr"});
        lang.add( new String[]{"Czech","cs"});
        lang.add( new String[]{"Danish","da"});
        lang.add( new String[]{"Dhivehi","dv"});
        lang.add( new String[]{"Dogri","doi"});
        lang.add( new String[]{"Dutch","nl"});
        lang.add( new String[]{"English","en"});
        lang.add( new String[]{"Esperanto","eo"});
        lang.add( new String[]{"Estonian","et"});
        lang.add( new String[]{"Ewe","ee"});
        lang.add( new String[]{"Filipino","fil"});
        lang.add( new String[]{"Finnish","fi"});
        lang.add( new String[]{"French","fr"});
        lang.add( new String[]{"Frisian","fy"});
        lang.add( new String[]{"Galician","gl"});
        lang.add( new String[]{"Georgian","ka"});
        lang.add( new String[]{"German","de"});
        lang.add( new String[]{"Greek","el"});
        lang.add( new String[]{"Guarani","gn"});
        lang.add( new String[]{"Gujarati","gu"});
        lang.add( new String[]{"Haitian","ht"});
        lang.add( new String[]{"Hausa","ha"});
        lang.add( new String[]{"Hawaiian","haw"});
        lang.add( new String[]{"Hebrew","he"});
        lang.add( new String[]{"Hindi","hi"});
        lang.add( new String[]{"Hmong","hmn"});
        lang.add( new String[]{"Hungarian","hu"});
        lang.add( new String[]{"Icelandic","is"});
        lang.add( new String[]{"Igbo","ig"});
        lang.add( new String[]{"Ilocano","ilo"});
        lang.add( new String[]{"Indonesian","id"});
        lang.add( new String[]{"Irish","ga"});
        lang.add( new String[]{"Italian","it"});
        lang.add( new String[]{"Japanese","ja"});
        lang.add( new String[]{"Javanese","jw"});
        lang.add( new String[]{"Kannada","kn"});
        lang.add( new String[]{"Kazakh","kk"});
        lang.add( new String[]{"Khmer","km"});
        lang.add( new String[]{"Kinyarwanda","rw"});
        lang.add( new String[]{"Konkani","gom"});
        lang.add( new String[]{"Korean","ko"});
        lang.add( new String[]{"Krio","kri"});
        lang.add( new String[]{"Kurdish","ku"});
        lang.add( new String[]{"Kurdish","ckb"});
        lang.add( new String[]{"Kyrgyz","ky"});
        lang.add( new String[]{"Lao","lo"});
        lang.add( new String[]{"Latin","la"});
        lang.add( new String[]{"Latvian","lv"});
        lang.add( new String[]{"Lingala","ln"});
        lang.add( new String[]{"Lithuanian","lt"});
        lang.add( new String[]{"Luganda","lg"});
        lang.add( new String[]{"Luxembourgish","lb"});
        lang.add( new String[]{"Macedonian","mk"});
        lang.add( new String[]{"Maithili","mai"});
        lang.add( new String[]{"Malagasy","mg"});
        lang.add( new String[]{"Malay","ms"});
        lang.add( new String[]{"Malayalam","ml"});
        lang.add( new String[]{"Maltese","mt"});
        lang.add( new String[]{"Maori","mi"});
        lang.add( new String[]{"Marathi","mr"});
        lang.add( new String[]{"Meiteilon Manipuri","mni-Mtei"});
        lang.add( new String[]{"Mizo","lus"});
        lang.add( new String[]{"Mongolian","mn"});
        lang.add( new String[]{"Myanmar","my"});
        lang.add( new String[]{"Nepali","ne"});
        lang.add( new String[]{"Norwegian","no"});
        lang.add( new String[]{"Nyanja","ny"});
        lang.add( new String[]{"Odia","or"});
        lang.add( new String[]{"Oromo","om"});
        lang.add( new String[]{"Pashto","ps"});
        lang.add( new String[]{"Persian","fa"});
        lang.add( new String[]{"Polish","pl"});
        lang.add( new String[]{"Portuguese","pt"});
        lang.add( new String[]{"Punjabi","pa"});
        lang.add( new String[]{"Quechua","qu"});
        lang.add( new String[]{"Romanian","ro"});
        lang.add( new String[]{"Russian","ru"});
        lang.add( new String[]{"Samoan","sm"});
        lang.add( new String[]{"Sanskrit","sa"});
        lang.add( new String[]{"Scots","gd"});
        lang.add( new String[]{"Sepedi","nso"});
        lang.add( new String[]{"Serbian","sr"});
        lang.add( new String[]{"Sesotho","st"});
        lang.add( new String[]{"Shona","sn"});
        lang.add( new String[]{"Sindhi","sd"});
        lang.add( new String[]{"Sinhala","si"});
        lang.add( new String[]{"Slovak","sk"});
        lang.add( new String[]{"Slovenian","sl"});
        lang.add( new String[]{"Somali","so"});
        lang.add( new String[]{"Spanish","es"});
        lang.add( new String[]{"Sundanese","su"});
        lang.add( new String[]{"Swahili","sw"});
        lang.add( new String[]{"Swedish","sv"});
        lang.add( new String[]{"Tagalog","tl"});
        lang.add( new String[]{"Tajik","tg"});
        lang.add( new String[]{"Tamil","ta"});
        lang.add( new String[]{"Tatar","tt"});
        lang.add( new String[]{"Telugu","te"});
        lang.add( new String[]{"Thai","th"});
        lang.add( new String[]{"Tigrinya","ti"});
        lang.add( new String[]{"Tsonga","ts"});
        lang.add( new String[]{"Turkish","tr"});
        lang.add( new String[]{"Turkmen","tk"});
        lang.add( new String[]{"Twi","(Akan)","ak"});
        lang.add( new String[]{"Ukrainian","uk"});
        lang.add( new String[]{"Urdu","ur"});
        lang.add( new String[]{"Uyghur","ug"});
        lang.add( new String[]{"Uzbek","uz"});
        lang.add( new String[]{"Vietnamese","vi"});
        lang.add( new String[]{"Welsh","cy"});
        lang.add( new String[]{"Xhosa","xh"});
        lang.add( new String[]{"Yiddish","yi"});
        lang.add( new String[]{"Yoruba","yo"});
        lang.add( new String[]{"Zulu","zu"});
    }

    private String langCode(String language) {
        //This takes the language name & grabs the language code to use in translate call
        language = language.toLowerCase(Locale.ROOT);
        for (String[] languageCode : lang) {
            if (languageCode[0].equalsIgnoreCase(language) || languageCode[1].equalsIgnoreCase(language))
                return languageCode[1];

        }
        return "en";
    }

    private String[] getLanguages() {
        //This gets all the languages into a list for the Alert Builder
        languageOptions();
        String[] langStr = new String[lang.size()];
        //copying the main arrayList into a string list
        for (int i = 0; i < lang.size(); i++)
            langStr[i] = lang.get(i)[0];
        return langStr;
    }
}

