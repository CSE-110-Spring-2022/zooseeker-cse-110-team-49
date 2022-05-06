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
import java.util.Timer;
import java.util.TimerTask;

public class ExhibitListViewActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    private ExhibitListViewModel viewModel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exhibit_list_view);

        Bundle extras = getIntent().getExtras();
        String currentLocationID = extras.getString("currentId");

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
        adapter.setOnDeleteButtonClickedHandler(viewModel::deleteExhibit);
        adapter.setOnNavigateButtonClicked((exhibit) -> {
            Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);
            intent.putExtra("destination", exhibit.getItemId());
            intent.putExtra("from", currentLocationID);
            startActivity(intent);
        });
        viewModel.getExhibits().observe(this, adapter::setExhibits);
        recyclerView = findViewById(R.id.exhibits);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
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
        finish();
    }

    public void onPlanRouteClicked(View view) {
        Intent intent=new Intent(this,PlanRouteActivity.class);
        startActivity(intent);
    }
}