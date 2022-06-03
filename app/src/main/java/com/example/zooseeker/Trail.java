package com.example.zooseeker;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

@Entity(tableName = "trails")
public class Trail {
    @PrimaryKey
    @ColumnInfo(name = "id")
    @SerializedName("id")
    @NonNull
    public final String id;

    @ColumnInfo(name = "street")
    @SerializedName("street")
    @NonNull
    public final String street;

    public Trail(@NonNull String id,
                 @NonNull String street) {
        this.id = id;
        this.street = street;
    }

    public static List<Trail> fromJson(Reader infoReader) {
        var gson = new Gson();
        var type = new TypeToken<List<Trail>>() {
        }.getType();
        return gson.fromJson(infoReader, type);
    }

    public static void toJson(List<Trail> infos, Writer writer) throws IOException {
        var gson = new Gson();
        var type = new TypeToken<List<Trail>>() {
        }.getType();
        gson.toJson(infos, type, writer);
        writer.flush();
        writer.close();
    }
}
