package com.example.cse110_team49;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.jgrapht.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

/**
* Show list of added exhibits
* */
public class ExhibitListViewActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    private ExhibitListViewModel viewModel;
    private String currentLocationID;
    Map<String, ZooDataItem.VertexInfo> vInfo;
    private boolean is_detailed=false;

    /**
    * List all exhibits added to the database by the user
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exhibit_list_view);

        // get current location id from MainActivity
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
        adapter.setOnDeleteButtonClickedHandler(viewModel::deleteExhibit);
        adapter.setOnNavigateButtonClicked((exhibit) -> {
            Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);
            intent.putExtra("destination", exhibit.getItemId());
            intent.putExtra("from", currentLocationID);
            intent.putExtra("detailed", is_detailed);
            ExhibitDatabase db = ExhibitDatabase.getSingleton(getApplicationContext());
            ExhibitDao exhibitDao = db.exhibitDao();
            exhibitDao.delete(exhibit);
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
        loadStatus();


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

            // Update the counter
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

            // Delete specific exhibit
            adapter.setOnDeleteButtonClickedHandler(viewModel::deleteExhibit);

            // Navigate to specific exhibit
            adapter.setOnNavigateButtonClicked((exhibit) -> {
                Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);
                intent.putExtra("destination", exhibit.getItemId());
                intent.putExtra("from", currentLocationID);
                intent.putExtra("detailed", is_detailed);
                ExhibitDatabase db = ExhibitDatabase.getSingleton(getApplicationContext());
                ExhibitDao exhibitDao = db.exhibitDao();
                exhibitDao.delete(exhibit);
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

    /**
    * Display the count
    */
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

    @Override
    protected void onDestroy(){
        super.onDestroy();
        saveStatus();
    }

    public void onPlanRouteClicked(View view) {
        Intent intent=new Intent(this, PlanRouteActivity.class);
        intent.putExtra("from", currentLocationID);
        intent.putExtra("detailed", is_detailed);
        startActivityForResult(intent, 3);
    }

    public void clearAll(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure to clear all?");
        builder.setTitle("clear all");
        builder.setCancelable(false);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Context context = getApplicationContext();
                ExhibitDatabase db = ExhibitDatabase.getSingleton(context);
                ExhibitDao exhibitDao = db.exhibitDao();
                List<Exhibit> list = exhibitDao.getAll();
                for (Exhibit e : list) {
                    exhibitDao.delete(e);
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        if (checked){
            is_detailed=true;
        }
        else{
            is_detailed=false;
        }
        System.out.println("is_detailed: "+is_detailed);
    }

    public void loadStatus(){
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        CheckBox statusView =findViewById(R.id.is_detail);
        boolean old_status= preferences.getBoolean("detailed",false);
        statusView.setChecked(old_status);
    }

    public void saveStatus(){
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        CheckBox statusView =findViewById(R.id.is_detail);
        editor.putBoolean("detailed", statusView.isChecked());
        editor.apply();
    }

}