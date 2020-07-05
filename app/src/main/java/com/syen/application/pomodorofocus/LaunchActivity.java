package com.syen.application.pomodorofocus;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.util.Random;

// The launching page for the app, it does some initialising functions
public class LaunchActivity extends AppCompatActivity {
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launch_page);
        // Setting random quotes from the string array
        TextView quote = findViewById(R.id.start_quote);
        String[] theQuotes = getResources().getStringArray(R.array.start_quote);
        Random random = new Random();
        Context context = getApplicationContext();
        quote.setText(theQuotes[random.nextInt(theQuotes.length)]);
        Apps.setPm(this);
        SimpleArrayAdapter.loadJson(getApplicationContext());
        try {
            StatsFragment.initDay1(getApplicationContext());
        } catch (ParseException e) {
            Log.e("CHECK336",e.toString());
        }
        // Checking if usage permission is given
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), context.getPackageName());
        if (mode != AppOpsManager.MODE_ALLOWED){
            // if not, it'll redirect to settings and asks for permission
            Toast.makeText(getApplicationContext(), "Please grant permission to continue",
                    Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }
    }
    // Whenever the page is touched
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_UP) {
            // It'll redirect to main activity
            Toast.makeText(getApplicationContext(),
                    "Taking some time to load....",
                    Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            return true;

        } else {
            return false;
        }
    }

}
