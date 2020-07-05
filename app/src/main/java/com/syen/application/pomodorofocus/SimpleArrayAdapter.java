package com.syen.application.pomodorofocus;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;

// An adapter for displaying whitelist
public class SimpleArrayAdapter extends ArrayAdapter<Apps> {
    // Containing all the apps with activity in phone
    private List<Apps> theApps;
    private Context context;
    // A static hashMap that will store if an app is in white list
    public static HashMap<String, Boolean> whiteList;
    private static SharedPreferences sharedPreferences;
    private final static String JSON_CODE = "WhereIsMyJson";
    final static String PREF_CODE = "TheLocationPref";

    // A simple constructor to get context and apps list
    public SimpleArrayAdapter(@NonNull Context context, @NonNull List<Apps> objects) {
        super(context, 0, objects);
        Apps.setPm(context);
        this.theApps = objects;
        this.context = context;
    }

    // Important method to call to check all the whitelist stored in the beginning of the app
    public static void loadJson(Context context){
        sharedPreferences = context.getSharedPreferences(PREF_CODE, Context.MODE_PRIVATE);
        String jsonString = sharedPreferences.getString(JSON_CODE, "{}");
        Gson gson = new Gson();
        java.lang.reflect.Type type = new TypeToken<HashMap<String, Boolean>>(){}.getType();
        whiteList = gson.fromJson(jsonString, type);
    }

    // It will save the whitelist once the app is closed
    public static void saveJson(){
        Gson gson = new Gson();
        sharedPreferences.edit().putString(JSON_CODE, gson.toJson(whiteList)).apply();
    }
    // Below two viewType and count are magical functions
    // that I got from stackoverflow that prevented random checkboxes
    // getting checked
    // Magical UWU
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        if(getCount() < 1)
            return 1;
        return getCount();
    }

    // The function that is called for item at every position
    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        final Apps curApp = theApps.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.app_row, parent, false);
        }

        // Getting and setting app names and icons
        final TextView app_name = convertView.findViewById(R.id.app_name);
        app_name.setText(curApp.getName());
        final ImageView app_icon = convertView.findViewById(R.id.app_icon);
        app_icon.setImageBitmap(curApp.getIcon(context));
        CheckBox checkBox = convertView.findViewById(R.id.check_box);
        // Check if it is in white list and whether it is true or false
        if (whiteList.containsKey(curApp.getPkgName())){
            if (whiteList.get(curApp.getPkgName())){
                checkBox.setChecked(true);
            }
            else{
                checkBox.setChecked(false);
            }
        } else{
            whiteList.put(curApp.getPkgName(), false);
            checkBox.setChecked(false);
        }
        // Click listener which will change state of apps in white list
        // and change text color for fun
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d("CHECK336",curApp.getName() + " is changed");
                if (isChecked) {
                    whiteList.put(curApp.getPkgName(), true);
                    app_name.setTextColor(Color.parseColor("#81EAE0"));
                } else{
                    whiteList.put(curApp.getPkgName(), false);
                    app_name.setTextColor(Color.parseColor("#000000"));
                }
            }
        });
        return convertView;
    }
}
