package com.example.todolist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/*
This class initializes the RecyclerView to ArrayList content.
Because it extends the abstract class RecyclerView.Adapter, it needs to implement its methods.
The inner class MyViewHolder sets an OnClick listener on individual to-do's.
*/

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.MyViewHolder> {

    private final ArrayList<ToDo> toDoList;
    private ToDoClickListener toDoClickListener;

    public ToDoAdapter(ArrayList<ToDo> toDoList) {
        this.toDoList = toDoList;
    }

    // creates a new ViewHolder if there are no pre-existing ones
    @NonNull
    @Override
    public ToDoAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_item, parent, false);
        return new MyViewHolder(itemView);
    }

    //
    @Override
    public void onBindViewHolder(@NonNull ToDoAdapter.MyViewHolder holder, int position) {
        String toDoText = toDoList.get(position).getText();
        holder.checkBox.setText(toDoText);
        holder.checkBox.setChecked(toDoList.get(position).isDone());
        holder.checkBox.setOnCheckedChangeListener((compoundButton, b) -> toDoClickListener.onCheckClick(compoundButton, holder.getAdapterPosition()));
    }

    // return collection size or 0 if toDoList is null
    @Override
    public int getItemCount() {
        return toDoList == null ? 0 : toDoList.size();
    }

    // setting ClickListener
    public void setClickListener(ToDoClickListener toDoClickListener) {
        this.toDoClickListener = toDoClickListener;
    }

    // inner class
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final CheckBox checkBox;

        public MyViewHolder(final View view) {
            super(view);
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
