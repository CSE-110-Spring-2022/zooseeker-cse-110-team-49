package com.example.zooseeker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
* Display search results
* */
public class DisplaySearchResultsActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    public ListManager lm;
    public Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_search_results);
        lm = new ListManager(this);

        // From MainActivity
        Bundle extras = getIntent().getExtras();
        String input = extras.getString("input").toLowerCase();

        // Map<String, ZooDataItem.VertexInfo> vInfo = ZooDataItem.loadVertexInfoJSON(this, "sample_node_info.json");
        Map<String, ArrayList<String>> reversedVInfo = lm.getReversedInfo();

        AnimalAdapter adapter = new AnimalAdapter();
        adapter.setHasStableIds(true);
        recyclerView = findViewById(R.id.animal_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        /**
        * Display alert when the user search for an absent animal.
        * When user click "OK", finish this activity and return to MainActivity.
        * */
        if (!reversedVInfo.containsKey(input)) {
            Utils.alertDialogShow(this,"Sorry we don't have this animal");
        }
        else {
            ArrayList<String> idList = reversedVInfo.get(input);
            List<ZooDataItem> animalList = new ArrayList<>();
            for (String s : idList) {
                animalList.add(lm.getExhibitInfo().get(s));
            }
            adapter.setAnimalItems(animalList);
        }
    }

    public void onSearchGoBackClicked(View view) {
        finish();
    }

}