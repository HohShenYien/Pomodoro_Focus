package com.syen.application.pomodorofocus;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

// Main fragment for count down part
public class CountDownFragment extends Fragment {
    private Context context;
    private View view;
    private CountDownTimer countDownTimer = null;
    private TextView timeTxt, modeTxt;
    private EditText durationTxt, sessionTxt, restTxt;
    private Button startBtn;
    private long duration, rest;
    private int session;
    private DateFormat dateFormat = new SimpleDateFormat("mm:ss");
    private final int MODE_STUDY = 1, MODE_REST = 0;
    private Date time;
    private Intent service;
    private MediaPlayer completeSound, startSound;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        view = inflater.inflate(R.layout.count_down, container, false);
        loadViews();
        // Loading two sounds for completion and starting
        completeSound = MediaPlayer.create(context, R.raw.completed);
        startSound = MediaPlayer.create(context, R.raw.start);
        // Background service
        service = new Intent(context, BgService.class);
        // This is to ensure all phones' timezones are synchronised
        // and won't affect the time displayed in countdown
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Checking for textfields
                if (durationTxt.getText() == null || durationTxt.getText().toString().equals("")||
                        durationTxt.getText().toString().equals("0")||sessionTxt.getText().toString().equals("0")||
                        sessionTxt.getText() == null || sessionTxt.getText().toString().equals("")||
                        restTxt.getText() == null || restTxt.getText().toString().equals("")||
                        restTxt.getText().toString().equals("0")){
                    Toast.makeText(context, "Please make sure there is no empty placeholders" ,
                            Toast.LENGTH_LONG).show();
                }
                else{
                    // Make sure no floats
                    try {
                        // Getting inputs
                        duration = Integer.parseInt(durationTxt.getText().toString()) * 60 * 1000;
                        session = Integer.parseInt(sessionTxt.getText().toString());
                        rest = Integer.parseInt(restTxt.getText().toString()) * 60 * 1000;
                        if (duration <= 0 || session <= 0 || rest <= 0){
                            // Make sure no negative or 0
                            throw new Exception("negative or 0 input values");
                        }
                        // Disabling input fields and menu
                        MainActivity.disablemenu();
                        startBtn.setVisibility(View.GONE);
                        durationTxt.setEnabled(false);
                        sessionTxt.setEnabled(false);
                        restTxt.setEnabled(false);
                        startPomodoro(session, duration, MODE_STUDY, rest);

                    } catch (Exception e){
                        Toast.makeText(context, "Please type valid integers above 1",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        return view;
    }

    private void loadViews(){
        timeTxt = view.findViewById(R.id.timeTxt);
        // On default the time is zero
        timeTxt.setText("00:00");
        durationTxt = view.findViewById(R.id.durationTxt);
        // Default values for pomodoro
        durationTxt.setText("25");
        sessionTxt = view.findViewById(R.id.sessionTxt);
        sessionTxt.setText("4");
        restTxt = view.findViewById(R.id.restTxt);
        restTxt.setText("5");
        startBtn = view.findViewById(R.id.startBtn);
        modeTxt = view.findViewById(R.id.modeTxt);
        modeTxt.setText("idle");
    }

    // Start the countdown and appblock mode
    private void startPomodoro(final int count, final long length, final int mode, final long rest){
        // This is a simple recursive functions, upon finishing, count will be 0
        if (count == 0){
            // Returns everything back to original
            MainActivity.showMenu();
            startBtn.setVisibility(View.VISIBLE);
            durationTxt.setEnabled(true);
            sessionTxt.setEnabled(true);
            restTxt.setEnabled(true);
            countDownTimer.cancel();
            // Store a complete session into database
            StatsFragment.database.usageDao().insertSession(session, (int) (length * session / 60000),
                    StatsFragment.today);
            modeTxt.setText("Idle");
        }
        else{
            long timerTime;
            final int returnMode;
            final int returnCount;
            // Check if the time has to be study time or rest time
            // and set for correct instruction
            if (mode == MODE_STUDY){
                timerTime = length;
                returnMode = MODE_REST;
                returnCount = count - 1;
                modeTxt.setText("Focusing");
                context.startService(service);
                startSound.start();
            }
            else{
                timerTime = rest;
                returnMode = MODE_STUDY;
                returnCount = count;
                modeTxt.setText("Resting");
            }
            // Start a new thread that counts down
            countDownTimer = new CountDownTimer(timerTime, 1000) {
                // Every 1 second it'll update
                public void onTick(long millisUntilFinished) {
                    time = new Date(millisUntilFinished);
                    timeTxt.setText(dateFormat.format(time));
                }
                public void onFinish() {
                    if (mode == MODE_STUDY){
                        // Ends a service
                        context.stopService(service);
                        completeSound.start();
                    }
                    startPomodoro(returnCount, length, returnMode, rest);
                }
            };
            countDownTimer.start();
        }
    }

}
