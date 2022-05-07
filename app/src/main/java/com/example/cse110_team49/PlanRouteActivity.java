package com.example.cse110_team49;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class PlanRouteActivity extends AppCompatActivity {

    String closestExhibitId;
    Exhibit closestExhibit;
    Boolean flag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_route);

        Bundle extras = getIntent().getExtras();
        String currentLocationID = extras.getString("from");

        Map<String, ZooDataItem.VertexInfo> vInfo = ZooDataItem.loadVertexInfoJSON(this, "sample_node_info.json");
        Map<String, ZooDataItem.EdgeInfo> eInfo = ZooDataItem.loadEdgeInfoJSON(this, "sample_edge_info.json");

        Context context = getApplicationContext();
        ExhibitDatabase db = ExhibitDatabase.getSingleton(context);
        ExhibitDao exhibitDao = db.exhibitDao();
        List<Exhibit> exhibits = exhibitDao.getAll();

        Graph<String, IdentifiedWeightedEdge> g = ZooDataItem.loadZooGraphJSON(this.getApplicationContext(),"sample_zoo_graph.json");

        double minDist = Double.POSITIVE_INFINITY;
        double nextMinDist = Double.POSITIVE_INFINITY;
        Exhibit nextClosestExhibit=null;
        Exhibit closestExhibit=null;

        for (Exhibit exhibit: exhibits) {
            if(exhibit.getItemId().equals("Entrance and Exit Gate") || exhibit.getItemId().equals(currentLocationID)){
                continue;
            }
            DijkstraShortestPath d = new DijkstraShortestPath(g);
            double weight = d.getPathWeight(currentLocationID, exhibit.getItemId());
            if (weight < minDist){
                minDist = weight;
                closestExhibit = exhibit;
            }
        }
        if (closestExhibit != null) {
            for (Exhibit exhibit: exhibits) {
                if(exhibit.getItemId().equals(currentLocationID) || exhibit.getItemId().equals(closestExhibit.getItemId())){
                    continue;
                }
                DijkstraShortestPath d = new DijkstraShortestPath(g);
                double weight = d.getPathWeight(closestExhibit.getItemId(),exhibit.getItemId());
                if (weight < nextMinDist){
                    nextMinDist = weight;
                    nextClosestExhibit = exhibit;
                }
            }
        }
        TextView nextView = findViewById(R.id.nextStop);
        if (nextClosestExhibit != null) {
            nextView.setText("Your closest next stop is: "+nextClosestExhibit.getName());
        }
        else{
            nextView.setText("Your are almost done your visit");
        }
        String message="";
        if(closestExhibit!=null){
            GraphPath<String, IdentifiedWeightedEdge> path = DijkstraShortestPath.findPathBetween(g, currentLocationID, closestExhibit.getItemId());
            int i = 1;
            for (IdentifiedWeightedEdge e : path.getEdgeList()) {
                message = i + ". Walk on " + eInfo.get(e.getId()).street + g.getEdgeWeight(e) + " ft towards "  + vInfo.get(g.getEdgeTarget(e).toString()).name;
                i++;
            }
        }
        System.out.println(message);


        new Timer().scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run(){
                initializeView();
            }
        },0,500);

//        System.out.println(extras.getString("from"));

        SharedPreferences storage = getSharedPreferences("Storage", 0);
        flag = storage.getBoolean("flag", false);
        if (!flag){
            closestExhibitId = extras.getString("from");
            flag = true;
        }
        else {
            closestExhibitId = storage.getString("closestExhibit", "");
        }

//        System.out.println(closestExhibitId);
//        flag = storage.getBoolean("flag", true);






//        System.out.println(flag);

//        if (flag) {
//            currentLocationID = extras.getString("from");
//            flag = false;
//        }
//        else{
        currentLocationID = closestExhibitId;
//        }



        for (Exhibit exhibit: exhibits) {
            DijkstraShortestPath d = new DijkstraShortestPath(g);
            double weight = d.getPathWeight(currentLocationID, exhibit.getItemId());
            if (weight < minDist){
                minDist = weight;
                closestExhibit = exhibit;
            }
        }



    }

    public void initializeView() {
        Context context = getApplicationContext();
        ExhibitDatabase db = ExhibitDatabase.getSingleton(context);
        ExhibitDao exhibitDao = db.exhibitDao();
        TextView countView = findViewById(R.id.num_remaining);
        long theCount = 0;
        if (exhibitDao.getAll() != null) {
            theCount = exhibitDao.getAll().size();
        }
        if(theCount>1){
            countView.setText(String.valueOf(theCount)+" unvisited exhibitions remaining");
        }
        else if(theCount==1){
            countView.setText(String.valueOf(theCount)+" unvisited exhibition remaining");
        }
        else{
            countView.setText("You have no unvisited exhibition remaining");
        }

    }

    public void onNextClicked(View view){
        Context context = getApplicationContext();
        ExhibitDatabase db = ExhibitDatabase.getSingleton(context);
        ExhibitDao exhibitDao = db.exhibitDao();

        String closestExhibitId = closestExhibit.getItemId();

        SharedPreferences storage = getSharedPreferences("Storage",0);
        SharedPreferences.Editor edit = storage.edit();
        edit.putString("closestExhibit", closestExhibitId);
        edit.putBoolean("flag", flag);
        edit.commit();
        exhibitDao.delete(closestExhibit);
        recreate();
    }


    public void onGoBackClicked(View view) {
        finish();
    }
}