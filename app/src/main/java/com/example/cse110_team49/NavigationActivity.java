package com.example.cse110_team49;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.List;
import java.util.Map;

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
                vnear = v1;
                vfar  = v2;
            }
            else{
                vnear = v2;
                vfar  = v1;
            }
            String message;
            if(detailed){
                message= "detailed version todo..."+ "\n"; //todo
            }
            else{//simplified version
                message= i + ". Walk on " + eInfo.get(e.getId()).street + " " + (int)g.getEdgeWeight(e)
                        + " ft from " + vnear.name + " to "  + vfar.name + "\n";
            }

            String currentMessage = navigation.getText().toString();
            if (currentMessage.equals("You've already arrived at your destination!")){
                currentMessage = "";
            }
            navigation.setText(currentMessage + message);
            i++;
        }

    }
    public void onGoBackClicked(View view) {
        Intent intent = new Intent();
        intent.putExtra("MESSAGE", destination);
        setResult(2,intent);
        finish();
    }
}