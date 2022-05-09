package com.example.cse110_team49;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

@Entity(tableName = "exhibit_table")
public class Exhibit {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    private String name;

    @NonNull
    private String itemId;

    Exhibit(@NonNull String itemId, @NonNull String name) {
        this.itemId = itemId;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String getItemId() {
        return itemId;
    }

    @Override
    public String toString() {
        return "Exhibit{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", itemId='" + itemId + '\'' +
                '}';
    }

    /**
     * Get Exhibits from Json. Can be used for testing
     * */
    public static List<Exhibit> loadJSON(Context context, String path) {
        try {
            InputStream input = context.getAssets().open(path);
            Reader reader = new InputStreamReader(input);
            Gson gson = new Gson();
            Type type = new TypeToken<List<Exhibit>>(){}.getType();
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

}
