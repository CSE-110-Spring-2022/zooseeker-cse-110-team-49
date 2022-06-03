package com.example.zooseeker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class ZooGraphCheck {

    @Test
    public void testGraphNode() {
        Graph<String, IdentifiedWeightedEdge> g;


        g = ZooDataItem.loadZooGraphJSON(ApplicationProvider.getApplicationContext(), "sample_zoo_graph.json");


        assertTrue(g.containsVertex("entrance_exit_gate"));
        assertTrue(g.containsVertex("entrance_plaza"));
        assertTrue(g.containsVertex("gorillas"));
        assertTrue(g.containsVertex("lions"));
        assertTrue(g.containsVertex("gators"));
        assertTrue(g.containsVertex("elephant_odyssey"));
        assertTrue(g.containsVertex("arctic_foxes"));

    }


    @Test
    public void testGraphEdge() {
        Graph<String, IdentifiedWeightedEdge> g;


        g = ZooDataItem.loadZooGraphJSON(ApplicationProvider.getApplicationContext(), "sample_zoo_graph.json");


        assertTrue(g.containsEdge("entrance_exit_gate", "entrance_plaza"));
        assertTrue(g.containsEdge("entrance_plaza", "gorillas"));
        assertTrue(g.containsEdge("gorillas", "lions"));
        assertTrue(g.containsEdge("lions", "elephant_odyssey"));
        assertTrue(g.containsEdge("entrance_plaza", "arctic_foxes"));
        assertTrue(g.containsEdge("entrance_plaza", "gators"));
        assertTrue(g.containsEdge("gators", "lions"));

    }

    @Test
    public void testGraphEdgeWeight() {
        Graph<String, IdentifiedWeightedEdge> g;
        g = ZooDataItem.loadZooGraphJSON(ApplicationProvider.getApplicationContext(), "sample_zoo_graph.json");

        assertEquals(g.getEdgeWeight(g.getEdge("entrance_exit_gate", "entrance_plaza")), 10.0,0.05);
        assertEquals(g.getEdgeWeight(g.getEdge("entrance_plaza", "gorillas")), 200.0,0.1);
        assertEquals(g.getEdgeWeight(g.getEdge("gorillas", "lions")), 200.0,0.1);
        assertEquals(g.getEdgeWeight(g.getEdge("lions", "elephant_odyssey")), 200.0,0.1);
        assertEquals(g.getEdgeWeight(g.getEdge("entrance_plaza", "arctic_foxes")), 300.0,0.1);
        assertEquals(g.getEdgeWeight(g.getEdge("entrance_plaza", "gators")), 100.0,0.1);
        assertEquals(g.getEdgeWeight(g.getEdge("gators", "lions")), 200.0,0.1);


    }

    @Test
    public void testStreet() {
        String[] streets = new String[] {"Entrance Way", "Africa Rocks Street", "Africa Rocks Street", "Africa Rocks Street", "Arctic Avenue", "Reptile Road", "Sharp Teeth Shortcut"};
        Map<String, ZooDataItem.EdgeInfo> eInfo;

        eInfo = ZooDataItem.loadEdgeInfoJSON(ApplicationProvider.getApplicationContext(), "sample_edge_info.json");

        for (Map.Entry<String, ZooDataItem.EdgeInfo> entry : eInfo.entrySet()){
            String street = entry.getValue().street;
            assertTrue(Arrays.asList(streets).contains(street));
        }
    }

    @Test
    public void shortestPathTest() {
        Graph<String, IdentifiedWeightedEdge> g;
        g = ZooDataItem.loadZooGraphJSON(ApplicationProvider.getApplicationContext(), "sample_zoo_graph.json");
        DijkstraShortestPath d = new DijkstraShortestPath(g);
        double weight = d.getPathWeight("entrance_exit_gate", "gators");
        assertEquals(weight, 110.0, 10^-6);
    }

}
