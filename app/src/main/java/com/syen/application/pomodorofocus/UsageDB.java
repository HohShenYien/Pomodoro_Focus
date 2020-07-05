package com.syen.application.pomodorofocus;

import androidx.room.Database;
import androidx.room.RoomDatabase;

// The database class
@Database(entities = {UsageEntity.class}, version = 1, exportSchema = false)
public abstract class UsageDB extends RoomDatabase {
    public abstract UsageDao usageDao();
}
