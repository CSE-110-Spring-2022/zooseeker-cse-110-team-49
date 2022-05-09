package com.example.cse110_team49;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ExhibitDao {

    @Insert
    List<Long> insertAll(List<Exhibit> exhibit);

    @Insert
    long insert(Exhibit exhibit);

    @Query("SELECT * FROM `exhibit_table` ORDER BY `id`")
    LiveData<List<Exhibit>> getAllLive();

    @Query("SELECT * FROM `exhibit_table` WHERE `name`=:name")
    Exhibit get(String name);

    @Query("SELECT * FROM `exhibit_table` ORDER BY `id`")
    List<Exhibit> getAll();

    @Update
    int update(Exhibit todoListItem);

    @Delete
    int delete(Exhibit todoListItem);

}
