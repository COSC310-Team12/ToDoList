package com.example.todolist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

// boiler-plate code to set RecyclerView to ArrayList content
public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.MyViewHolder> {

    private final ArrayList<ToDo> toDoList;
    private ToDoClickListener toDoClickListener;

    public ToDoAdapter(ArrayList<ToDo> toDoList) {
        this.toDoList = toDoList;
    }

    @NonNull
    @Override
    public ToDoAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ToDoAdapter.MyViewHolder holder, int position) {
        String toDoText= toDoList.get(position).getText();
        holder.checkBox.setText(toDoText);
        holder.checkBox.setChecked(toDoList.get(position).isDone());
        holder.checkBox.setOnCheckedChangeListener((compoundButton, b) -> toDoClickListener.onCheckClick(compoundButton, holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return toDoList == null? 0: toDoList.size();
    }

    public void setClickListener(ToDoClickListener toDoClickListener) {
        this.toDoClickListener = toDoClickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final CheckBox checkBox;
        public MyViewHolder(final View view) {
            super(view);
            checkBox = view.findViewById(R.id.checkBox);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (toDoClickListener != null) toDoClickListener.onEditClick(view, getAdapterPosition());
        }
    }
}
