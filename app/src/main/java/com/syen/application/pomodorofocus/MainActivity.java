package com.syen.application.pomodorofocus;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

// On opening, the launch page will open, then it'll redirect to the main page
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "CHECK336";
    public static List<Apps> allApps = new ArrayList<>();
    private static BottomNavigationView bottomNavigationView;
    private TextView quote;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sendName();
        // Setting up bottomNavigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        // Setting default fragment on startup
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new CountDownFragment()).commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Making sure the whitelist apps is saved
        SimpleArrayAdapter.saveJson();
    }

    private void sendName(){
        final PackageManager pm = getPackageManager();
        //get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        // This app must be in white list
        SimpleArrayAdapter.whiteList.put("com.syen.application.pomodorofocus", true);
        for (ApplicationInfo packageInfo : packages) {
            // Make sure it has at least a name for package
            if (pm.getLaunchIntentForPackage(packageInfo.packageName) != null) {
                if (!packageInfo.packageName.equals("com.syen.application.pomodorofocus")) {
                    try {
                        // I put all the apps into allApps
                        allApps.add(new Apps(packageInfo.packageName));
                        // On default the apps is false in white list
                        if (!SimpleArrayAdapter.whiteList.containsKey(packageInfo.packageName)){
                            SimpleArrayAdapter.whiteList.put(packageInfo.packageName, false);
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        Log.e("CHECK336", e.toString());
                    }
                }
            }
        }
    }

    // Listener for bottom menu action
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener(){

                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    // Make sure the item clicked is set checked
                    item.setChecked(true);
                    Fragment selectedFragment = null;
                    // set the fragments for corresponding items clicked
                    switch (item.getItemId()) {
                        case R.id.nav_pomodoro:
                            selectedFragment = new CountDownFragment();
                            break;
                        case R.id.nav_whiteList:
                            selectedFragment = new WhiteListFragments();
                            break;
                        case R.id.nav_stats:
                            selectedFragment = new StatsFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    return false;
                }
            };

    // disable all items in menu so that it can't be selected during focus period
    public static void disablemenu(){
        for (int i = 0; i < bottomNavigationView.getChildCount(); i++){
            bottomNavigationView.getChildAt(i).setVisibility(View.GONE);
        }
    }

    // Show back the menu after completing the session
    public static void showMenu(){
        for (int i = 0; i < bottomNavigationView.getChildCount(); i++){
            bottomNavigationView.getChildAt(i).setVisibility(View.VISIBLE);
        }
    }

}
