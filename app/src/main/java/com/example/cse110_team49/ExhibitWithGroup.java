package com.example.cse110_team49;
import android.util.Pair;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.Locale;

/**
 * Represents a group, with an optional group.
 */
public class ExhibitWithGroup {
    @Embedded
    public ZooDataItem exhibit;
    @Relation(
            parentColumn = "group_id",
            entityColumn = "id"
    )
    public ZooDataItem group = null;

    public String getExhibitName() {
        return exhibit.name;
    }

    public String getGroupName() {
        if (group == null) return " ";
        return group.name;
    }

    public String getCoordString() {
        var coords = getCoords();
        return String.format(Locale.getDefault(), "%3.6f, %3.6f", coords.first, coords.second);
    }

    public Pair<Double, Double> getCoords() {
        if (group != null) {
            return Pair.create(group.lat, group.lng);
        } else {
            return Pair.create(exhibit.lat, exhibit.lng);
        }
    }

    public boolean isCloseTo(Pair<Double, Double> otherCoords) {
        return isCloseTo(otherCoords, 0.001);
    }

    public boolean isCloseTo(Pair<Double, Double> otherCoords, double delta) {
        var coords = getCoords();
        if (coords == null
                || otherCoords == null
                || coords.first == null || coords.second == null
                || otherCoords.first == null || otherCoords.second == null) return false;
        var dLat = coords.first - otherCoords.first;
        var dLng = coords.second - otherCoords.second;
        return Math.sqrt(Math.pow(dLat, 2) + Math.pow(dLng, 2)) < delta;
    }
}
