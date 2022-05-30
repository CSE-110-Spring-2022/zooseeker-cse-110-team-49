package com.example.cse110_team49;

import androidx.appcompat.app.AppCompatActivity;

import org.jgrapht.Graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListManager {
    Map<String, ZooDataItem> exhibitInfo;
    Map<String, Trail> trailInfo;
    Graph<String, IdentifiedWeightedEdge> graphInfo;
    AppCompatActivity context;

    ListManager(AppCompatActivity context) {
        exhibitInfo = ZooDataItem.loadZooItemInfoJSON(context, "exhibit_info.json");
        trailInfo = ZooDataItem.loadTrailJSON(context, "trail_info.json");
        graphInfo = ZooDataItem.loadZooGraphJSON(context, "zoo_graph.json");

        for (Map.Entry<String, ZooDataItem> entry : exhibitInfo.entrySet()){
            String key = entry.getKey();
            ZooDataItem value = entry.getValue();
            if (value.groupId != null) {
                ZooDataItem parent =  exhibitInfo.get(value.groupId);
                var uniqueID = value.name + "+" + parent.name;
                graphInfo.addVertex(key);
                var e = graphInfo.addEdge(key, parent.id);
                e.setId(uniqueID);
                graphInfo.setEdgeWeight(e, 0);
                Trail t = new Trail(uniqueID, uniqueID);
                trailInfo.put(uniqueID, t);
            }
        }

    }

    public Map<String, ArrayList<String>> getReversedInfo() {
        Map<String, ArrayList<String>> reversedVInfo = new HashMap<>();

        for (Map.Entry<String, ZooDataItem> entry : exhibitInfo.entrySet()){
            String key = entry.getKey();
            ZooDataItem value = entry.getValue();
            for (String tagValue: value.tags) {
                ArrayList<String> s = reversedVInfo.get(tagValue);
                if (s == null) {
                    ArrayList<String> newIdList = new ArrayList<>();
                    newIdList.add(value.id);
                    reversedVInfo.put(tagValue, newIdList);
                }
                else {
                    if (!s.contains(value.id)) {
                        s.add(value.id);
                        reversedVInfo.put(tagValue, s);
                    }
                }
            }


            for (String nameValue: value.name.toLowerCase().split(" ")){
                ArrayList<String> s = reversedVInfo.get(nameValue);
                if (s == null) {
                    ArrayList<String> newIdList = new ArrayList<>();
                    newIdList.add(value.id);
                    reversedVInfo.put(nameValue, newIdList);
                }
                else {
                    if (!s.contains(value.id)) {
                        s.add(value.id);
                        reversedVInfo.put(nameValue, s);
                    }
                }
            }
        }
        return reversedVInfo;
    }

    public String getNearestExhibit(double lat, double lon) {
        String result = "";
        ZooLocation currentLocation = new ZooLocation(lat, lon);
        double minDist = Double.POSITIVE_INFINITY;
        for (Map.Entry<String, ZooDataItem> entry : exhibitInfo.entrySet()) {
            String key = entry.getKey();
            ZooDataItem value = entry.getValue();
            if (value.lat == null || value.lng == null) {
                continue;
            }
            ZooLocation newLocation = new ZooLocation(value.lat, value.lng);
            result = currentLocation.dist(newLocation) < minDist ? value.id : result;
            minDist = Math.min(currentLocation.dist(newLocation), minDist);
        }
        return result;
    }

    public String getIdFromName(String name) {
        for (Map.Entry<String, ZooDataItem> entry : exhibitInfo.entrySet()) {
            String key = entry.getKey();
            ZooDataItem value = entry.getValue();
            if (value.name.equals(name)) {
                return key;
            }
        }
        return null;
    }

    public Map<String, ZooDataItem> getExhibitInfo() {
        return exhibitInfo;
    }

    public Graph<String, IdentifiedWeightedEdge> getGraph() { return graphInfo; }

    Map<String, Trail> getTrailInfo() { return trailInfo; }


}
