package com.example.todolist;

import androidx.annotation.NonNull;

// Class the defines each item in a PowerMenu. This one is for the filter menu on the main activity
public class FilterPowerMenuItem {
    private String title;
    private boolean checked;

    public FilterPowerMenuItem(String title, Boolean checked) {
        this.title = title;
        this.checked = checked;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @NonNull
    @Override
    public String toString() {
        return "FilterPowerMenuItem{" +
                "title='" + title + '\'' +
                ", checked=" + checked +
                '}';
    }
}
