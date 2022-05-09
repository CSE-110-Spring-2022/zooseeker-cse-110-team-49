package com.example.cse110_team49;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.widget.TextView;


import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class ZooDataCheck {

    @Test
    public void testCorrectionOfLoadNodeId() {
        String[] keys = new String[] {"entrance_exit_gate", "entrance_plaza", "gorillas", "gators", "lions", "elephant_odyssey", "arctic_foxes"};
        Map<String, ZooDataItem.VertexInfo> vInfo;
        vInfo = ZooDataItem.loadVertexInfoJSON(ApplicationProvider.getApplicationContext(), "sample_node_info.json");
        for (Map.Entry<String, ZooDataItem.VertexInfo> entry : vInfo.entrySet()){
                String key = entry.getKey();
                assertTrue(Arrays.asList(keys).contains(key));
            }
    }

    @Test
    public void testCorrectionOfLoadNodeName() {
        String[] keys = new String[] {"Entrance and Exit Gate", "Entrance Plaza", "Gorillas", "Alligators", "Lions", "Elephant Odyssey", "Arctic Foxes"};
        Map<String, ZooDataItem.VertexInfo> vInfo;
        vInfo = ZooDataItem.loadVertexInfoJSON(ApplicationProvider.getApplicationContext(), "sample_node_info.json");
        for (Map.Entry<String, ZooDataItem.VertexInfo> entry : vInfo.entrySet()){
            String key = entry.getValue().name;;
            assertTrue(Arrays.asList(keys).contains(key));
        }
    }


}
