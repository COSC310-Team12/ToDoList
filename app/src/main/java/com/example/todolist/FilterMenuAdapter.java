package com.example.todolist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.skydoves.powermenu.MenuBaseAdapter;

public class FilterMenuAdapter extends MenuBaseAdapter<FilterPowerMenuItem> {
    @Override
    public View getView(int index, View view, ViewGroup viewGroup) {
        final Context context = viewGroup.getContext();

        FilterPowerMenuItem item = (FilterPowerMenuItem) getItem(index);

        // Inflate the menu item
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.filter_menu_item, viewGroup, false);

        // Set the attributes of the menu list item
        final CheckBox filterCheckBox = view.findViewById(R.id.filterCheckBox);
        filterCheckBox.setText(item.getTitle());
        filterCheckBox.setChecked(item.isChecked());
        filterCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> item.setChecked(filterCheckBox.isChecked()));

        return super.getView(index, view, viewGroup);
    }
}
