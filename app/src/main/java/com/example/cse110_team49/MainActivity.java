package com.example.cse110_team49;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // "source" and "sink" are graph terms for the start and end
//        String start = "entrance_exit_gate";
//        String goal = "elephant_odyssey";



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
        List<String> possible_list_AL = new ArrayList<String>();


        vInfo.forEach((k, v) -> {
            possible_list_AL.addAll(v.tags);
        });

        List<String> new_possible_list_AL = possible_list_AL.stream()
                                                                .distinct()
                                                                .collect(Collectors.toList());


        String[] possible_list = new String[new_possible_list_AL.size()];

        possible_list = new_possible_list_AL.toArray(possible_list);





        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.searchInput);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, possible_list);
        textView.setAdapter(adapter);


//        System.out.println(reversedVInfo);

        findViewById(R.id.myList).setOnClickListener(view -> {
            Intent intent = new Intent(this, ExhibitListViewActivity.class);
            startActivity(intent);
        });


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

    public void setOnClicktoSearch(View view) {
        Intent search_page = new Intent(this, DisplaySearchResultsActivity.class);
        TextView search_input = findViewById(R.id.searchInput);

        search_page.putExtra("input", search_input.getText().toString());
        startActivity(search_page);
    }
}