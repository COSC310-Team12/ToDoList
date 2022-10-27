package com.example.todolist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/*
This class is responsible for displaying the content of our tagList ArrayList in the RecyclerView.
Because it extends the abstract class RecyclerView.Adapter, it needs to implement its methods.
The inner class MyViewHolder sets an OnClick listener on individual to-do items.

See this youtube video for a good introduction to RecyclerAdapters:
https://www.youtube.com/watch?v=9rcrYFO1ogc&list=WL&index=2
*/

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.MyViewHolder> {

    private final ArrayList<String> tagList;
    private TagClickListener tagClickListener;

    // constructor to initialize tagList to values from tagList in MainActivity
    public TagAdapter(ArrayList<String> tagList) {
        this.tagList = tagList;
    }

    // creates individual to-do item
    @NonNull
    @Override
    public TagAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // uses todo_item.xml as template
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_item, parent, false);
        return new MyViewHolder(itemView);
    }

    // can set data for to-do item at specific position, doing this using tagList ArrayList
    @Override
    public void onBindViewHolder(@NonNull TagAdapter.MyViewHolder holder, int position) {
        String tagText = tagList.get(position);
        holder.text.setText(tagText);
    }

    // returns number of rows = size of tagList or 0 if tagList is null
    @Override
    public int getItemCount() {
        return tagList == null ? 0 : tagList.size();
    }

    // setting ClickListener
    public void setClickListener(TagClickListener tagClickListener) {
        this.tagClickListener = tagClickListener;
    }

    // inner class responsible for managing to-do items
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // can access views (checkbox and dots image) contained in to-do item in here
        private final TextView text;

        public MyViewHolder(final View view) {
            super(view);
            text = view.findViewById(R.id.tagName);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (tagClickListener != null)
                tagClickListener.onEditClick(view, getAdapterPosition());
        }
    }
}
