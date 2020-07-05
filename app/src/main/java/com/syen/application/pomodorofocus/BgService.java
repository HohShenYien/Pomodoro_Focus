package com.syen.application.pomodorofocus;

import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.*;
import android.os.*;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

// A background service that runs every 5s to check if any blacklisted app is opened
public class BgService extends Service {

    public Context context = this;
    public Handler handler = null;
    public static Runnable runnable = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        handler = new Handler();
        // Making a thread that runs indefinitely
        runnable = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
            public void run() {
                getForegroundPackageNameClassNameByUsageStats();
                handler.postDelayed(runnable, 5000);
            }
        };

        handler.postDelayed(runnable, 5000);
    }

    @Override
    public void onDestroy() {
        /* IF YOU WANT THIS SERVICE KILLED WITH THE APP THEN UNCOMMENT THE FOLLOWING LINE */
        handler.removeCallbacks(runnable);
        Toast.makeText(this, "1 session ended", Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {
        return flags;
    }

    // Using usagestats to get foreground apps
    @RequiresApi(api = 22)
    public void getForegroundPackageNameClassNameByUsageStats() {
        String packageNameByUsageStats = null;
        // Only works for api level after lollipop
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Get a usageStatsManager that contains all background information
            UsageStatsManager mUsageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
            // It checks apps that are opened within 5s interval
            final long INTERVAL = 5000;
            final long end = System.currentTimeMillis();
            final long begin = end - INTERVAL;
            // Then it gets all the usage events
            final UsageEvents usageEvents = mUsageStatsManager.queryEvents(begin, end);
            while (usageEvents.hasNextEvent()) {
                UsageEvents.Event event = new UsageEvents.Event();
                usageEvents.getNextEvent(event);
                // For the events that are of type activity resumed,
                // Whether it is create or continue another app, the following will invoke
                if (event.getEventType() == UsageEvents.Event.ACTIVITY_RESUMED) {
                    packageNameByUsageStats = event.getPackageName();
                    Log.e("Package name", packageNameByUsageStats);
                    // Check if the app is in whitelist
                    if (SimpleArrayAdapter.whiteList.containsKey(packageNameByUsageStats) &&
                            !SimpleArrayAdapter.whiteList.get(packageNameByUsageStats)){
                        // If not, it'll redirect to another page
                        // to prevent user from accessing blacklisted apps
                        Intent intent = new Intent("android.intent.category.LAUNCHER");
                        intent.setClassName("com.syen.application.pomodorofocus",
                                "com.syen.application.pomodorofocus.BgActivity");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            }
        }
    }

}