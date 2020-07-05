package com.syen.application.pomodorofocus;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

// A fragment that displays all the apps
// Most parts are done by adapter, so quite empty here
public class WhiteListFragments extends Fragment {
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        View view = inflater.inflate(R.layout.white_list_check, container, false);
        SimpleArrayAdapter arrayAdapter = new SimpleArrayAdapter(context, MainActivity.allApps);
        ListView listView = view.findViewById(R.id.white_listView);
        if (listView == null){
            Log.e("ERROR336", "null list");
        }
        listView.setAdapter(arrayAdapter);

        return view;

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);


    }
}
