package com.example.cse110_team49;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DisplaySearchResults extends AppCompatActivity {

    public RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_search_results);

        Bundle extras = getIntent().getExtras();
        String input = extras.getString("input");

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
                    if (!s.contains(value.id)) {
                        s.add(value.id);
                    }
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
                    if (!s.contains(value.id)) {
                        s.add(value.id);
                    }
                    reversedVInfo.put(nameValue, s);
                }
            }

        }
        ArrayList<String> idList = reversedVInfo.get(input);
        List<ZooDataItem.VertexInfo> animalList = new ArrayList<>();
        for (String s: idList){
            animalList.add(vInfo.get(s));
        }

        System.out.println(animalList.get(0).name);

//        System.out.println(input);
//
//        System.out.println(reversedVInfo);
//        System.out.println(idList);

        AnimalAdapter adapter = new AnimalAdapter();
        adapter.setHasStableIds(true);

        recyclerView = findViewById(R.id.animal_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setAnimalItems(animalList);

    }
}