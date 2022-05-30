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
public class ZooDataItemCheck {
    @Test
    public void testCorrectionOfLoadNodeId() {
        String[] keys = new String[] {"flamingo", "capuchin", "orangutan", "siamang", "fern_canyon", "parker_aviary"};
        Map<String, ZooDataItem> vInfo;
        vInfo = ZooDataItem.loadZooItemInfoJSON(ApplicationProvider.getApplicationContext(), "exhibit_info.json");
        int count=0;
        for (Map.Entry<String, ZooDataItem> entry : vInfo.entrySet()){
            for (String i:keys){
                String key = entry.getKey();
                if(i.equals(key)){
                    count++;
                }
            }
        }
        assertEquals(count,keys.length);
    }

    @Test
    public void testCorrectionOfLoadNodeName() {
        String[] keys = new String[] {"Blue Capped Motmot", "Scripps Aviary", "Spoonbill", "Hippos", "Crocodiles", "Owens Aviary"};
        Map<String, ZooDataItem> vInfo;
        vInfo = ZooDataItem.loadZooItemInfoJSON(ApplicationProvider.getApplicationContext(), "exhibit_info.json");
        int count=0;
        for (Map.Entry<String, ZooDataItem> entry : vInfo.entrySet()){
            for (String i:keys){
                String key = entry.getValue().name;
                if(i.equals(key)){
                    count++;
                }
            }
        }
        assertEquals(count,keys.length);
    }

    @Test
    public void testCorrectionOfLoadLatLng() {
        String[] keys = new String[] {"Hippos", "Scripps Aviary"};
        double[] lats={32.74531131120979,32.7475300638514};
        double[] lngs={ -117.16626781198586,-117.17681064859705};
        int cur_ind=0;
        Map<String, ZooDataItem> vInfo;
        vInfo = ZooDataItem.loadZooItemInfoJSON(ApplicationProvider.getApplicationContext(), "exhibit_info.json");
        int count=0;
        for (String i:keys){
            for (Map.Entry<String, ZooDataItem> entry : vInfo.entrySet()){
                String key = entry.getValue().name;
                if(i.equals(key)){
                    count++;
                    double cur_lat = entry.getValue().lat;
                    double cur_lng = entry.getValue().lng;
                    assertEquals(cur_lat,lats[cur_ind],0.001);
                    assertEquals(cur_lng,lngs[cur_ind],0.001);
                    cur_ind++;

                }
            }
        }
        assertEquals(count,keys.length);
    }



}
