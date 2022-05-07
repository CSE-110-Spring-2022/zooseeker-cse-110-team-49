package com.example.cse110_team49;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.Map;

public class NavigationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        Bundle extras = getIntent().getExtras();
        String destination = extras.getString("destination");
        String currentLocation = extras.getString("from");

        Map<String, ZooDataItem.VertexInfo> vInfo = ZooDataItem.loadVertexInfoJSON(this, "sample_node_info.json");
        Map<String, ZooDataItem.EdgeInfo> eInfo = ZooDataItem.loadEdgeInfoJSON(this, "sample_edge_info.json");

        String destinationName = vInfo.get(destination).name;
        String currentLocationName = vInfo.get(currentLocation).name;

        Graph<String, IdentifiedWeightedEdge> g = ZooDataItem.loadZooGraphJSON(this.getApplicationContext(),"sample_zoo_graph.json");
        GraphPath<String, IdentifiedWeightedEdge> path = DijkstraShortestPath.findPathBetween(g, currentLocation, destination);

        TextView from = findViewById(R.id.from);
        TextView to = findViewById(R.id.to);
        TextView navigation = findViewById(R.id.nav);

        from.setText(currentLocationName);
        to.setText(destinationName);

        int i = 1;
        for (IdentifiedWeightedEdge e : path.getEdgeList()) {
            ZooDataItem.VertexInfo vnear;
            ZooDataItem.VertexInfo vfar;
            ZooDataItem.VertexInfo v1 = vInfo.get(g.getEdgeTarget(e).toString());
            ZooDataItem.VertexInfo v2 = vInfo.get(g.getEdgeSource(e).toString());
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

            String message = i + ". Walk on " + eInfo.get(e.getId()).street + " " + (int)g.getEdgeWeight(e) + " ft from " + vnear.name + " to "  + vfar.name + "\n";
            String currentMessage = navigation.getText().toString();
            if (currentMessage.equals("You've already arrived at your destination!")){
                currentMessage = "";
            }
            navigation.setText(currentMessage + message);
            i++;
        }

    }
    public void onGoBackClicked(View view) {
        finish();
    }
}