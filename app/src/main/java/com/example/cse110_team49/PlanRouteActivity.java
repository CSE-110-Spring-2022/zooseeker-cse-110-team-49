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


    Exhibit closestExhibit;

    Map<String, ZooDataItem.VertexInfo> vInfo;
    Map<String, ZooDataItem.EdgeInfo> eInfo;
    Graph<String, IdentifiedWeightedEdge> g;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_route);

        Context context = getApplicationContext();
        ExhibitDatabase db = ExhibitDatabase.getSingleton(context);
        ExhibitDao exhibitDao = db.exhibitDao();
        List<Exhibit> exhibits = exhibitDao.getAll();

        System.out.println(exhibits.size());

        if (exhibits.size() == 0){
            Utils.alertDialogShow(PlanRouteActivity.this,"You need to add an animal before planning");
        }

        Bundle extras = getIntent().getExtras();
        String currentLocationID = extras.getString("from");

        vInfo = ZooDataItem.loadVertexInfoJSON(this, "sample_node_info.json");
        eInfo = ZooDataItem.loadEdgeInfoJSON(this, "sample_edge_info.json");





        g = ZooDataItem.loadZooGraphJSON(this.getApplicationContext(),"sample_zoo_graph.json");




        new Timer().scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run(){
                initializeView();
            }
        },0,500);

        vInfo = ZooDataItem.loadVertexInfoJSON(this, "sample_node_info.json");
        eInfo = ZooDataItem.loadEdgeInfoJSON(this, "sample_edge_info.json");
        g = ZooDataItem.loadZooGraphJSON(this.getApplicationContext(), "sample_zoo_graph.json");



        update(currentLocationID);
    }


    public void clear(){

    }

    public void display(String currentLocationId, GraphPath<String, IdentifiedWeightedEdge> path){

        int i = 1;
        for (IdentifiedWeightedEdge e : path.getEdgeList()) {
            ZooDataItem.VertexInfo vnear;
            ZooDataItem.VertexInfo vfar;
            ZooDataItem.VertexInfo v1 = vInfo.get(g.getEdgeTarget(e).toString());
            ZooDataItem.VertexInfo v2 = vInfo.get(g.getEdgeSource(e).toString());
            GraphPath<String, IdentifiedWeightedEdge> route1 = DijkstraShortestPath.findPathBetween(g, currentLocationId, v1.id);
            GraphPath<String, IdentifiedWeightedEdge> route2 = DijkstraShortestPath.findPathBetween(g, currentLocationId, v2.id);
            double dist1 = route1.getWeight();
            double dist2 = route2.getWeight();

            if(dist1 < dist2) {
                vnear = v1;
                vfar  = v2;
            }
            else{
                vnear = v2;
                vfar  = v1;
            }

            String message = i + ". Walk on " + eInfo.get(e.getId()).street + " " + (int)g.getEdgeWeight(e) + " ft from " + vnear.name + " to "  + vfar.name + "\n";
            System.out.println(message);
            i++;
        }
    }

    public void update(String lastClosestExhibitId) {


        Context context = getApplicationContext();
        ExhibitDatabase db = ExhibitDatabase.getSingleton(context);
        ExhibitDao exhibitDao = db.exhibitDao();
        List<Exhibit> exhibits = exhibitDao.getAll();


        double minDist = Double.POSITIVE_INFINITY;

        double nextMinDist = Double.POSITIVE_INFINITY;
        Exhibit nextClosestExhibit=null;


        for (Exhibit exhibit : exhibits) {
            DijkstraShortestPath d = new DijkstraShortestPath(g);
            double weight = d.getPathWeight(lastClosestExhibitId, exhibit.getItemId());
            if (weight < minDist) {
                minDist = weight;
                closestExhibit = exhibit;
            }
        }

        if (closestExhibit != null) {
            for (Exhibit exhibit: exhibits) {
                if(exhibit.getItemId().equals(lastClosestExhibitId) || exhibit.getItemId().equals(closestExhibit.getItemId())){
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


        TextView from = findViewById(R.id.plan_from);
        TextView to = findViewById(R.id.plan_to);
        TextView navigation = findViewById(R.id.plan_nav);

        from.setText(vInfo.get(lastClosestExhibitId).name);
        to.setText(closestExhibit.getName());


        TextView nextView = findViewById(R.id.nextStop);
        if (nextClosestExhibit != null) {
            nextView.setText("Your closest next stop is: "+nextClosestExhibit.getName());
        }
        else if (exhibits.size() == 1){
            nextView.setText("Your are almost done your visit");
        }
        else{
            nextView.setText("You have finished your plan!");
        }

        GraphPath<String, IdentifiedWeightedEdge> path = DijkstraShortestPath.findPathBetween(g, lastClosestExhibitId, closestExhibit.getItemId());

        display(lastClosestExhibitId, path);


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
        if(theCount>2){
            countView.setText(String.valueOf(theCount - 1)+" unvisited exhibitions remaining");
        }
        else if(theCount == 1 || theCount == 2){
            countView.setText(String.valueOf(theCount - 1)+" unvisited exhibition remaining");
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

        exhibitDao.delete(closestExhibit);
        clear();

        update(closestExhibitId);

    }



    public void onGoBackClicked(View view) {
        finish();
    }
}