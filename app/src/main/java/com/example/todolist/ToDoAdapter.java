package com.example.todolist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/*
This class is responsible for displaying the content of our toDoList ArrayList in the RecyclerView.
Because it extends the abstract class RecyclerView.Adapter, it needs to implement its methods.
The inner class MyViewHolder sets an OnClick listener on individual to-do items.

See this youtube video for a good introduction to RecyclerAdapters:
https://www.youtube.com/watch?v=9rcrYFO1ogc&list=WL&index=2
*/

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.MyViewHolder> {

    private final ArrayList<ToDo> toDoList;
    private ToDoClickListener toDoClickListener;

    // constructor to initialize toDoList to values from toDoList in MainActivity
    public ToDoAdapter(ArrayList<ToDo> toDoList) {
        this.toDoList = toDoList;
    }

    // creates individual to-do item
    @NonNull
    @Override
    public ToDoAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // uses todo_item.xml as template
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_item, parent, false);
        return new MyViewHolder(itemView);
    }

    // can set data for to-do item at specific position, doing this using toDoList ArrayList
    @Override
    public void onBindViewHolder(@NonNull ToDoAdapter.MyViewHolder holder, int position) {
        String toDoText = toDoList.get(position).getText();
        Date toDoDate = toDoList.get(position).getDate();

        String pattern = "MM-dd-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        holder.checkBox.setText(toDoText);
        if (toDoDate != null) holder.dateView.setText("Due date: " + simpleDateFormat.format(toDoDate));
        holder.checkBox.setChecked(toDoList.get(position).isDone());
        holder.checkBox.setOnCheckedChangeListener((compoundButton, b) -> toDoClickListener.onCheckClick(compoundButton, holder.getAdapterPosition()));
    }

    // returns number of rows = size of toDoList or 0 if toDoList is null
    @Override
    public int getItemCount() {
        return toDoList == null ? 0 : toDoList.size();
    }

    // setting ClickListener
    public void setClickListener(ToDoClickListener toDoClickListener) {
        this.toDoClickListener = toDoClickListener;
    }

    // inner class responsible for managing to-do items
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // can access views (checkbox and dots image) contained in to-do item in here
        private final CheckBox checkBox;
        private final TextView dateView;

        public MyViewHolder(final View view) {
            super(view);
            dateView = view.findViewById(R.id.dateView);
            checkBox = view.findViewById(R.id.checkBox);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (toDoClickListener != null)
                toDoClickListener.onEditClick(view, getAdapterPosition());
        }
    }
}
