package com.example.cse110_team49;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    TextView currentLocation;
    Dialog dialog;


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
        List<String> nodeNameList = new ArrayList<String>();

        vInfo.forEach((k, v) -> {
            possible_list_AL.addAll(v.tags);
            nodeNameList.add(v.name);
        });

        // search list
        List<String> new_possible_list_AL = possible_list_AL.stream().distinct().collect(Collectors.toList());
        String[] possible_list = new String[new_possible_list_AL.size()];


        // all possible location
        possible_list = new_possible_list_AL.toArray(possible_list);

        currentLocation = findViewById(R.id.setCurrentLocation);

        currentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Initialize dialog
               dialog = new Dialog(MainActivity.this);

               // set custom dialog
               dialog.setContentView(R.layout.search_location_spinner);

               // set custom height and width
               dialog.getWindow().setLayout(650, 800);

               // set transparent background
               dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

               // change here !!!!!!
               ArrayAdapter<String> adapter_curLocation = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, nodeNameList);
               // show dialog
               dialog.show();


               // Initialize and assign variable
               EditText editText = dialog.findViewById(R.id.edit_text);
               ListView listView = dialog.findViewById(R.id.list_view);


               // set adapter
               listView.setAdapter(adapter_curLocation);
               editText.addTextChangedListener(new TextWatcher() {
                   @Override
                   public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                   }

                   @Override
                   public void onTextChanged(CharSequence s, int start, int before, int count) {
                       adapter_curLocation.getFilter().filter(s);
                   }

                   @Override
                   public void afterTextChanged(Editable s) {

                   }
               });
               listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                   @Override
                   public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                       // when item selected from list
                       // set selected item on textView
                       currentLocation.setText(adapter_curLocation.getItem(position));

                       // Dismiss dialog
                       dialog.dismiss();
                   }
               });

            }
        });

        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.searchInput);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, possible_list);
        textView.setAdapter(adapter);

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