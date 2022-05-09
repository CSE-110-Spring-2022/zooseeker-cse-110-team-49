package com.example.cse110_team49;

import static org.junit.Assert.assertEquals;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class GraphTest {
    Map<String, ZooDataItem.VertexInfo> vInfo;
    private Graph<String, IdentifiedWeightedEdge> g;

    @Before
    public void loadInfo() {
        vInfo = ZooDataItem.loadVertexInfoJSON(ApplicationProvider.getApplicationContext(), "sample_node_info.json");
        g     = ZooDataItem.loadZooGraphJSON  (ApplicationProvider.getApplicationContext(), "sample_zoo_graph.json");
    }

    @Test
    public void loadNodeTest() {
        ZooDataItem.VertexInfo info = vInfo.get("entrance_plaza");
        assertEquals(info.name, "Entrance Plaza");
        assertEquals(info.kind, ZooDataItem.VertexInfo.Kind.INTERSECTION);
        assertEquals(info.tags, new ArrayList<>());
    }

    @Test
    public void loadGraphTest() {
        IdentifiedWeightedEdge e = new IdentifiedWeightedEdge();
        assertEquals(g.containsVertex("gorillas"), true);
        assertEquals(g.containsEdge("gorillas", "lions"), true);

    }

    @Test
    public void shortestPathTest() {
        DijkstraShortestPath d = new DijkstraShortestPath(g);
        double weight = d.getPathWeight("entrance_exit_gate", "gators");
        assertEquals(weight, 110.0, 10^-6);
    }

}