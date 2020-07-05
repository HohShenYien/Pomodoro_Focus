package com.syen.application.pomodorofocus;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// The table entity
@Entity (tableName = "Usage")
public class UsageEntity {
    @PrimaryKey
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "session")
    public int session;

    @ColumnInfo(name = "duration")
    public int duration;

    @ColumnInfo(name = "date")
    public int date;
}
