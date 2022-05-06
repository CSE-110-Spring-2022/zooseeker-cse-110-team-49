package com.example.cse110_team49;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class ZooActivityTest {
    ExhibitDatabase testDb;
    ExhibitDao exhibitDao;

    private static void forceLayout(RecyclerView recyclerView){
        recyclerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        recyclerView.layout(0, 0, 1080, 2280);
    }

    @Before
    public void resetDatabase() {
        Context context = ApplicationProvider.getApplicationContext();
        testDb = Room.inMemoryDatabaseBuilder(context, ExhibitDatabase.class)
                .allowMainThreadQueries()
                .build();
        ExhibitDatabase.injectTestDatabase(testDb);

        List<Exhibit> todos = new ArrayList<>();

        Map<String, ZooDataItem.VertexInfo> vInfo = ZooDataItem.loadVertexInfoJSON(context,"sample_node_info.json");
        for (Map.Entry<String, ZooDataItem.VertexInfo> entry: vInfo.entrySet()){
            ZooDataItem.VertexInfo value = entry.getValue();
            Exhibit newExhibit = new Exhibit(value.id, value.name);
            todos.add(newExhibit);
        }


        exhibitDao = testDb.exhibitDao();
        exhibitDao.insertAll(todos);
    }

    @Test
    public void testExhibitCount() {

        List<Exhibit> exhibitList = exhibitDao.getAll();
//        String newSearchedExhibit = "mammal";

        ActivityScenario<ExhibitListViewActivity> scenario
                = ActivityScenario.launch(ExhibitListViewActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.moveToState(Lifecycle.State.RESUMED);

        scenario.onActivity(activity -> {
//            List<Exhibit> exhibitList = exhibitDao.getAll();

//            EditText searchBar = activity.findViewById(R.id.searchInput);
//            Button searchButton = activity.findViewById(R.id.searchButton);
//
//            searchBar.setText(newSearchedExhibit);
//            searchButton.performClick();
            TextView countView = activity.findViewById(R.id.count);
            int count = Integer.parseInt(countView.getText().toString().split(":")[1]);
            assertEquals(exhibitList.size(), count);
        });

    }
}
