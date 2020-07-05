package com.syen.application.pomodorofocus;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

// A fragment that utilises mpandroidchart library to draw charts
public class StatsFragment extends Fragment {
    private View view;
    private BarChart timeChart, sessionChart;
    public static Calendar day1 = null;
    private static SharedPreferences sharedPreferences;
    private static final String DATE_CODE = "Date_Location";
    private List<BarEntry> sessionEntries, timeEntries;
    public static UsageDB database;
    public static int today;
    private List<UsagePOJO> usages;
    private List<String> days;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Getting views
        view = inflater.inflate(R.layout.stats_fragment, container, false);
        timeChart = view.findViewById(R.id.time_chart);
        sessionChart = view.findViewById(R.id.session_chart);
        setGraph();
        return view;
    }

    // Must be initialised at first
    // initialise day1 and today variable
    public static void initDay1(Context context) throws ParseException {
        sharedPreferences = context.getSharedPreferences(SimpleArrayAdapter.PREF_CODE, Context.MODE_PRIVATE);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/YYYY");
        database = Room.databaseBuilder(context, UsageDB.class, "Usage").
                allowMainThreadQueries().
                build();
        if (sharedPreferences.contains(DATE_CODE)){
            String tempDate = sharedPreferences.getString(DATE_CODE, "x");
            day1 = Calendar.getInstance();
            day1.setTime(simpleDateFormat.parse(tempDate));
            // Convert today to days since day1
            today = (int) TimeUnit.MILLISECONDS.toDays(-day1.compareTo(Calendar.getInstance()));

        }else {
            // day1 is set to today's date at 12:00 midnight
            // Just to prevent any conversion errors
            Date today = new Date(System.currentTimeMillis());
            // day1 is stored in sharedPreferences
            sharedPreferences.edit().putString(DATE_CODE,simpleDateFormat.format(today)).apply();
            day1 = Calendar.getInstance();
            day1.set(Calendar.HOUR, 0);
            day1.set(Calendar.MINUTE, 0);
            day1.set(Calendar.SECOND, 0);
            day1.set(Calendar.MILLISECOND, 0);
            // today is counted as days from day1
            StatsFragment.today = 1;
        }
    }

    // The function that plots 2 graphs
    // PS: the function only works for consecutive days
    private void setGraph(){
        usages = database.usageDao().getLast7days();
        days = new ArrayList<>();

        sessionEntries = new ArrayList<>();
        timeEntries = new ArrayList<>();

        // Didn't include year because it'll be too long
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM");
        int count = 0;
        for (UsagePOJO usage: usages){
            // Making sure that the date is within 7 days
            if (usage.date <= today && usage.date >= today - 6) {
                sessionEntries.add(new BarEntry(count, usage.sessions));
                timeEntries.add(new BarEntry(count, usage.durations));
                count ++;
            }
        }
        // If there's insufficient data, it'll replace with blanks
        for (int i = 7; i > usages.size(); i--){
            sessionEntries.add(new BarEntry(i - 1, 0));
            timeEntries.add(new BarEntry(i - 1, 0));
        }
        Calendar today = Calendar.getInstance();
        for (int i = 0; i < 7; i++){
            // Making labels
            days.add(simpleDateFormat.format(today.getTime()));
            today.add(Calendar.DAY_OF_MONTH, -1);
        }
        // Getting the label format
        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return days.get((int) value);
            }
        };
        // Some setups for axes
        XAxis xAxisTime = timeChart.getXAxis();
        XAxis xAxisSession = sessionChart.getXAxis();
        xAxisTime.setGranularity(1f);
        xAxisTime.setValueFormatter(formatter);
        xAxisSession.setGranularity(1f);
        xAxisSession.setValueFormatter(formatter);

        // More setup for dataset and colors
        BarDataSet setSession = new BarDataSet(sessionEntries, "Sessions");
        BarDataSet setTime = new BarDataSet(timeEntries, "Durations");
        setSession.setColors(ColorTemplate.COLORFUL_COLORS);
        setTime.setColors(ColorTemplate.COLORFUL_COLORS);

        // Final setups for the charts
        BarData dataSession = new BarData(setSession);
        BarData dataTime = new BarData(setTime);
        timeChart.setData(dataTime);
        timeChart.getAxisRight().setEnabled(false);// Make sure it doesn't have double y-axes
        sessionChart.setData(dataSession);
        sessionChart.getAxisRight().setEnabled(false);
        timeChart.setFitBars(true);// Make sure all plots are fit into 1 chart
        sessionChart.setFitBars(true);
        timeChart.invalidate();// This refreshes the chart
        sessionChart.invalidate();
    }
}
