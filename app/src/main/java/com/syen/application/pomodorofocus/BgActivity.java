package com.syen.application.pomodorofocus;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

// The activity that will start when an app false in white list is opened
public class BgActivity extends AppCompatActivity {
    private TextView motivation;
    @Override
    protected void onCreate(Bundle savedInstances){
        super.onCreate(savedInstances);
        setContentView(R.layout.background_service);
        // Setting a random motivation sentence from the string array
        motivation = findViewById(R.id.bgMotivationTxt);
        String[] motivationTexts = getResources().getStringArray(R.array.motivation);
        Random random = new Random();
        motivation.setText(motivationTexts[random.nextInt(motivationTexts.length)]);
    }
}
