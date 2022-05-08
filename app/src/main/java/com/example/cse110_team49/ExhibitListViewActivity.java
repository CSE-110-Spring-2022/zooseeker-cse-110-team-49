package com.example.cse110_team49;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import org.jgrapht.Graph;

import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ExhibitListViewActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    private ExhibitListViewModel viewModel;
    private String currentLocationID;
    Map<String, ZooDataItem.VertexInfo> vInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exhibit_list_view);

        Bundle extras = getIntent().getExtras();
        currentLocationID = extras.getString("currentId");

        viewModel = new ViewModelProvider(this)
                .get(ExhibitListViewModel.class);

        Graph<String, IdentifiedWeightedEdge> g = ZooDataItem.loadZooGraphJSON(this.getApplicationContext(),"sample_zoo_graph.json");
        vInfo = ZooDataItem.loadVertexInfoJSON(this, "sample_node_info.json");


        new Timer().scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run(){
                initializeView();
            }
        },0,500);

        ExhibitAdapter adapter = new ExhibitAdapter();
        adapter.loadGraph(g);
        adapter.loadCurrentLocation(currentLocationID);

        adapter.setHasStableIds(true);
        adapter.setOnNavigateButtonClicked((exhibit) -> {
            Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);
            intent.putExtra("destination", exhibit.getItemId());
            intent.putExtra("from", currentLocationID);
            ExhibitDatabase db = ExhibitDatabase.getSingleton(getApplicationContext());
            ExhibitDao exhibitDao = db.exhibitDao();
            startActivityForResult(intent, 2);
        });
        viewModel.getExhibits().observe(this, adapter::setExhibits);
        recyclerView = findViewById(R.id.exhibits);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        //------------
        String first_cl_id=currentLocationID;
        String first_cl_name=null;
        for (Map.Entry<String, ZooDataItem.VertexInfo> entry : vInfo.entrySet()) {
            String cur_id = entry.getKey();
            if (cur_id.equals(first_cl_id)) {
                first_cl_name=entry.getValue().name;
            }
        }
        TextView cur_location = findViewById(R.id.cur_location);
        cur_location.setText("Current location:\n"+first_cl_name);
        //------------
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 2) {
            setResult(2, data);
            currentLocationID = data.getExtras().getString("MESSAGE");


            viewModel = new ViewModelProvider(this)
                    .get(ExhibitListViewModel.class);

            Graph<String, IdentifiedWeightedEdge> g = ZooDataItem.loadZooGraphJSON(this.getApplicationContext(),"sample_zoo_graph.json");


            new Timer().scheduleAtFixedRate(new TimerTask(){
                @Override
                public void run(){
                    initializeView();
                }
            },0,500);

            ExhibitAdapter adapter = new ExhibitAdapter();
            adapter.loadGraph(g);
            adapter.loadCurrentLocation(currentLocationID);
            adapter.setHasStableIds(true);
            adapter.setOnNavigateButtonClicked((exhibit) -> {
                Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);
                intent.putExtra("destination", exhibit.getItemId());
                intent.putExtra("from", currentLocationID);
                startActivityForResult(intent, 2);
            });
            viewModel.getExhibits().observe(this, adapter::setExhibits);
            recyclerView = findViewById(R.id.exhibits);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);

            //------------
            String first_cl_id=currentLocationID;
            String first_cl_name=null;
            for (Map.Entry<String, ZooDataItem.VertexInfo> entry : vInfo.entrySet()) {
                String cur_id = entry.getKey();
                if (cur_id.equals(first_cl_id)) {
                    first_cl_name=entry.getValue().name;
                }
            }
            TextView cur_location = findViewById(R.id.cur_location);
            cur_location.setText("Current location:\n"+first_cl_name);
            //------------
        }
    }

    public void initializeView() {
        Context context = getApplicationContext();
        ExhibitDatabase db = ExhibitDatabase.getSingleton(context);
        ExhibitDao exhibitDao = db.exhibitDao();
        TextView countView = findViewById(R.id.count);
        long theCount = 0;
        if (exhibitDao.getAll() != null) {
            theCount = exhibitDao.getAll().size();
        }
        countView.setText("Count:" + String.valueOf(theCount));
    }

    public void onGoBackClicked(View view) {
        Intent intent = new Intent();
        intent.putExtra("MESSAGE", currentLocationID);
        setResult(2, intent);
        finish();
    }

    public void onPlanRouteClicked(View view) {
        Intent intent=new Intent(this, PlanRouteActivity.class);
        intent.putExtra("from", currentLocationID);
        startActivityForResult(intent, 3);
    }
}