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
            String message = i + ". Walk on " + eInfo.get(e.getId()).street + " " + (int)g.getEdgeWeight(e) + " ft towards "  + vInfo.get(g.getEdgeTarget(e).toString()).name + "\n";
            navigation.setText(navigation.getText() + message);
            i++;
        }

    }
    public void onGoBackClicked(View view) {
        finish();
    }
}