package com.example.cse110_team49;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // "source" and "sink" are graph terms for the start and end
        String start = "entrance_exit_gate";
        String goal = "elephant_odyssey";

        // 1. Load the graph...
//        Graph<String, IdentifiedWeightedEdge> g = ZooDataItem.loadZooGraphJSON("sample_zoo_graph.json");
//        GraphPath<String, IdentifiedWeightedEdge> path = DijkstraShortestPath.findPathBetween(g, start, goal);

        // 2. Load the information about our nodes and edges...
//        try {
//            System.out.println("Hello");
//            Map<String, ZooDataItem.VertexInfo> vInfo = ZooDataItem.loadVertexInfoJSON(this, "sample_node_info.json");
//            System.out.println(vInfo);
//        } catch (Exception e){
//            System.out.println("exception");
//        }

        Map<String, ZooDataItem.VertexInfo> vInfo = ZooDataItem.loadVertexInfoJSON(this, "sample_node_info.json");
        Map<String, ArrayList<String>>  reversedVInfo = new HashMap<>();
        for (Map.Entry<String, ZooDataItem.VertexInfo> entry : vInfo.entrySet()){
            String key = entry.getKey();
            ZooDataItem.VertexInfo value = entry.getValue();
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
        System.out.println(reversedVInfo);


//        Map<String, ZooDataItem.EdgeInfo> eInfo = ZooDataItem.loadEdgeInfoJSON("sample_edge_info.json");

//        System.out.printf("The shortest path from '%s' to '%s' is:\n", start, goal);

//        int i = 1;
//        for (IdentifiedWeightedEdge e : path.getEdgeList()) {
//            System.out.printf("  %d. Walk %.0f meters along %s from '%s' to '%s'.\n",
//                    i,
//                    g.getEdgeWeight(e),
//                    eInfo.get(e.getId()).street,
//                    vInfo.get(g.getEdgeSource(e).toString()).name,
//                    vInfo.get(g.getEdgeTarget(e).toString()).name);
//            i++;
//        }
    }
}