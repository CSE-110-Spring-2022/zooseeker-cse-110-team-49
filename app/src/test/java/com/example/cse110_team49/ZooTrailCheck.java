package com.example.cse110_team49;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class ZooTrailCheck {

    @Test
    public void testCorrectionOfLoadTrailId() {
        String[] keys = new String[] {"gate_to_front", "front_to_monkey", "monkey_to_flamingo",
                "flamingo_to_capuchin", "capuchin_to_hippo_monkey"};
        Map<String, Trail> vInfo;
        vInfo = ZooDataItem.loadTrailJSON(ApplicationProvider.getApplicationContext(), "trail_info.json");
        int count=0;
        for (Map.Entry<String, Trail> entry : vInfo.entrySet()){
            for (int i=0;i<keys.length;i++){
                String key = entry.getValue().id;
                if(keys[i].equals(key)){
                    count++;
                    break;
                }
            }
        }
        assertEquals(count,keys.length);
    }


    @Test
    public void testCorrectionOfLoadTrailStreet() {
        String[] keys = new String[] {"gate_to_front", "front_to_monkey", "monkey_to_flamingo",
                "flamingo_to_capuchin", "capuchin_to_hippo_monkey"};
        String[] values = new String[] {"Gate Path", "Front Street", "Monkey Trail",
                "Monkey Trail", "Monkey Trail"};
        Map<String, Trail> vInfo;
        vInfo = ZooDataItem.loadTrailJSON(ApplicationProvider.getApplicationContext(), "trail_info.json");
        int count=0;
        for (int i=0;i<keys.length;i++){
            for (Map.Entry<String, Trail> entry : vInfo.entrySet()){
                String key = entry.getValue().id;
                if(keys[i].equals(key)){
                    assertTrue(entry.getValue().street.equals(values[count]));
                    count++;
                    break;
                }
            }
        }
        assertEquals(count,keys.length);
    }
}
