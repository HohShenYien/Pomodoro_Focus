package com.syen.application.pomodorofocus;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

// A simple dao for usages
@Dao
public interface UsageDao {
    // Insert for every sessions completed
    @Query("INSERT INTO Usage (session, duration, date) VALUES(:session, :duration, :date)")
    void insertSession(int session, int duration, int date);

    // Get all the sessions and durations sums for each day
    // not last 7 days because I failed to do so, so it'll be provessed in statsFragment :(
    @Query("SELECT date,SUM(session) AS sessions,SUM(duration) AS durations FROM Usage " +
            "GROUP BY date ")
    List<UsagePOJO> getLast7days();

}
