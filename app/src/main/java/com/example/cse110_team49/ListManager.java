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
                    s.add(value.id);
                    reversedVInfo.put(tagValue, s);
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
                    s.add(value.id);
                    reversedVInfo.put(nameValue, s);
                }
            }
        }
        return reversedVInfo;
    }

    public Map<String, ZooDataItem> getExhibitInfo() {
        return exhibitInfo;
    }

    public Graph<String, IdentifiedWeightedEdge> getGraph() { return graphInfo; }

    Map<String, Trail> getTrailInfo() { return trailInfo; }


}
