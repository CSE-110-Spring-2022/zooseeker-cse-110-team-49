package com.example.cse110_team49;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.List;
import java.util.Map;

public class PlanRouteActivity extends AppCompatActivity {

    Exhibit closestExhibit;
    Boolean flag = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_route);

        Bundle extras = getIntent().getExtras();
        String currentLocationID;

        System.out.println(flag);

        if (flag) {
            currentLocationID = extras.getString("from");

        }
        else{
            currentLocationID = closestExhibit.getItemId();
        }

        Map<String, ZooDataItem.VertexInfo> vInfo = ZooDataItem.loadVertexInfoJSON(this, "sample_node_info.json");
        Map<String, ZooDataItem.EdgeInfo> eInfo = ZooDataItem.loadEdgeInfoJSON(this, "sample_edge_info.json");

        Context context = getApplicationContext();
        ExhibitDatabase db = ExhibitDatabase.getSingleton(context);
        ExhibitDao exhibitDao = db.exhibitDao();
        List<Exhibit> exhibits = exhibitDao.getAll();

        Graph<String, IdentifiedWeightedEdge> g = ZooDataItem.loadZooGraphJSON(this.getApplicationContext(),"sample_zoo_graph.json");

        double minDist = Double.POSITIVE_INFINITY;


        for (Exhibit exhibit: exhibits) {
            DijkstraShortestPath d = new DijkstraShortestPath(g);
            double weight = d.getPathWeight(currentLocationID, exhibit.getItemId());
            if (weight < minDist){
                minDist = weight;
                closestExhibit = exhibit;
            }
        }

        GraphPath<String, IdentifiedWeightedEdge> path = DijkstraShortestPath.findPathBetween(g, currentLocationID, closestExhibit.getItemId());

        int i = 1;
        for (IdentifiedWeightedEdge e : path.getEdgeList()) {
            String message = i + ". Walk on " + eInfo.get(e.getId()).street + " " + (int)g.getEdgeWeight(e) + " ft towards "  + vInfo.get(g.getEdgeTarget(e).toString()).name;
            i++;
//            System.out.println(message);
        }


    }

    public void onNextClicked(View view){
        Context context = getApplicationContext();
        ExhibitDatabase db = ExhibitDatabase.getSingleton(context);
        ExhibitDao exhibitDao = db.exhibitDao();

        flag = false;
        exhibitDao.delete(closestExhibit);
        recreate();
    }


    public void onGoBackClicked(View view) {
        finish();
    }
}