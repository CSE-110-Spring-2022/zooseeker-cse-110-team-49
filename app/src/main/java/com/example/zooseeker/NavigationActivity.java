package com.example.zooseeker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.ArrayList;
import java.util.List;

/**
 Navigate from current location to user-specified destination
*/
public class NavigationActivity extends AppCompatActivity {
    public String destination;
    public Boolean detailed;
    public ListManager lm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        lm = new ListManager(this);

        // From ExhibitListViewActivity.class
        Bundle extras = getIntent().getExtras();
        destination = extras.getString("destination");
        detailed = extras.getBoolean("detailed");
        System.out.println("detailed here: "+detailed);
        String currentLocation = extras.getString("from");

        var exhibitInfo = lm.getExhibitInfo();
        var eInfo = lm.getTrailInfo();
        var g = lm.getGraph();


        // Map<String, ZooDataItem.VertexInfo> vInfo = ZooDataItem.loadVertexInfoJSON(this, "sample_node_info.json");
        // Map<String, ZooDataItem.EdgeInfo> eInfo = ZooDataItem.loadEdgeInfoJSON(this, "sample_edge_info.json");

        String destinationName = exhibitInfo.get(destination).name;
        String currentLocationName = exhibitInfo.get(currentLocation).name;

        //Graph<String, IdentifiedWeightedEdge> g = ZooDataItem.loadZooGraphJSON(this.getApplicationContext(),"sample_zoo_graph.json");

        GraphPath<String, IdentifiedWeightedEdge> path = DijkstraShortestPath.findPathBetween(g, currentLocation, destination);

        TextView from = findViewById(R.id.from);
        TextView to = findViewById(R.id.to);
        TextView navigation = findViewById(R.id.nav);

        navigation.setMovementMethod(new ScrollingMovementMethod());

        from.setText(currentLocationName);
        to.setText(destinationName);


        int i = 1;
        List<ArrayList<String>> stepInfo = new ArrayList<ArrayList<String>>();
        for (IdentifiedWeightedEdge e : path.getEdgeList()) {

            // find the direction we go through each edge by comparing their distance from current location.
            ZooDataItem vnear;
            ZooDataItem vfar;
            ZooDataItem v1 = exhibitInfo.get(g.getEdgeTarget(e).toString());
            ZooDataItem v2 = exhibitInfo.get(g.getEdgeSource(e).toString());
            GraphPath<String, IdentifiedWeightedEdge> route1 = DijkstraShortestPath.findPathBetween(g, currentLocation, v1.id);
            GraphPath<String, IdentifiedWeightedEdge> route2 = DijkstraShortestPath.findPathBetween(g, currentLocation, v2.id);
            double dist1 = route1.getWeight();
            double dist2 = route2.getWeight();

            if(dist1 < dist2) {
//                vnear = v1;
                vfar  = v2;
            }
            else{
//                vnear = v2;
                vfar  = v1;
            }
            ArrayList<String> oneStep = new ArrayList<>();
            oneStep.add(Integer.toString(i));
            oneStep.add(eInfo.get(e.getId()).street);
            oneStep.add(Double.toString(g.getEdgeWeight(e)));
//            oneStep.add(vnear.name);
            oneStep.add(vfar.name);
            stepInfo.add(oneStep);
            i++;
        }
        String message = "";
        if (stepInfo.size() == 0){
            message = "You've already arrived at your destination!";
        }
        else {
            if (detailed) {
                int count = 0;
                for (int j = 0; j < stepInfo.size(); j++) {
                    if (Double.parseDouble(stepInfo.get(j).get(2)) == 0 && j == stepInfo.size()-1) {
                        message += (Integer.parseInt(stepInfo.get(j).get(0)) - count) + ". " + "Explore in " + stepInfo.get(j).get(3) + "\n";
                    }
                    else if (Double.parseDouble(stepInfo.get(j).get(2)) == 0) {
                        count++;
                        continue;
                    }
                    else {
                        message += (Integer.parseInt(stepInfo.get(j).get(0)) - count) + ". ";
                        if (j > 0) {
                            if (stepInfo.get(j).get(1).equals(stepInfo.get(j - 1).get(1))) {
                                message += "Continue on ";
                            } else {
                                message += "Proceed on ";
                            }
                        } else {
                            message += "Proceed on ";
                        }
                        message += stepInfo.get(j).get(1) + " " + stepInfo.get(j).get(2) + " ft towards " + stepInfo.get(j).get(3) + "\n";

                    }
                }
            } else {
                ArrayList<ArrayList<String>> briefInfo = new ArrayList<>();
                briefInfo.add(stepInfo.get(0));
                for (int j = 1; j < stepInfo.size(); j++) {
                    ArrayList<String> recent = briefInfo.get(briefInfo.size() - 1);
                    if (stepInfo.get(j).get(1).equals(recent.get(1))) {
                        ArrayList<String> newBrief = new ArrayList<>();
                        newBrief.add(Integer.toString(briefInfo.size()));
                        newBrief.add(recent.get(1));
                        newBrief.add(Double.toString(Double.parseDouble(recent.get(2)) + Double.parseDouble(stepInfo.get(j).get(2))));
                        newBrief.add(stepInfo.get(j).get(3));
                        briefInfo.set(briefInfo.size() - 1, newBrief);
                    }
                    else {
                        var oldBrief = stepInfo.get(j);
                        oldBrief.set(0, Integer.toString(briefInfo.size() + 1));
                        briefInfo.add(oldBrief);
                    }
                }
                for (int j = 0; j < briefInfo.size(); j++) {
                    ArrayList<String> info = briefInfo.get(j);
                    if (Double.parseDouble(info.get(2)) == 0 && j == briefInfo.size() - 1) {
                        message += "==> Explore in " + info.get(3) + "\n";
                    } else if (Double.parseDouble(info.get(2)) == 0) {
                        continue;
                    }
                    else {
                        message += "==> Proceed on " + info.get(1) + " " + info.get(2) + " ft to " + info.get(3) + "\n";
                    }
                }
            }
        }
        navigation.setText(message);
    }

    public void back(View view) {
        finish();
    }
}