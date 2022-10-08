package com.example.todolist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

// boiler-plate code to set RecyclerView to ArrayList content
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private final ArrayList<ToDo> toDoList;

    public RecyclerAdapter(ArrayList<ToDo> toDoList) {
        this.toDoList = toDoList;
    }

    @NonNull
    @Override
    public RecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.MyViewHolder holder, int position) {
        String toDoText= toDoList.get(position).getText();
        holder.toDoText.setText(toDoText);
    }

    @Override
    public int getItemCount() {
        return toDoList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView toDoText;
        public MyViewHolder(final View view) {
            super(view);
            toDoText = view.findViewById(R.id.toDoText);
        }
    }
}
