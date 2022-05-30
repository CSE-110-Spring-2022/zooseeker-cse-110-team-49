package com.example.cse110_team49;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 Plan route, navigate to nearest exhibit everytime the user finishes watching a exhibit
*/
public class PlanRouteActivity extends AppCompatActivity {
    Exhibit closestExhibit;
    ArrayList<Exhibit> planned_items;
    Map<String, ZooDataItem> vInfo;
    Map<String, Trail> eInfo;
    Graph<String, IdentifiedWeightedEdge> g;
    String returnResult;
    public ListManager lm;
    public String currentLocationID;
    public Exhibit curExhibit;
    public Boolean detailed;
    public String FromExhibitId;
    public boolean isSkip = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_route);
        lm = new ListManager(this);
        planned_items = ExhibitListViewActivity.planned_items;

        Context context = getApplicationContext();
        ExhibitDatabase db = ExhibitDatabase.getSingleton(context);
        ExhibitDao exhibitDao = db.exhibitDao();
        List<Exhibit> exhibits = exhibitDao.getAll();

        TextView navigation = findViewById(R.id.plan_nav);
        navigation.setMovementMethod(new ScrollingMovementMethod());

        TextView nextStop = findViewById(R.id.nextStop);
        nextStop.setMovementMethod(new ScrollingMovementMethod());

        /**
         * Show alert when there is no animal in the list.
         * When user click "OK", finish this activity and return to ExhibitListViewActivity.
         * */
        if (exhibits.size() == 0){
            Utils.alertDialogShow(this,"You need to add a stop before planning");
        } else {
            // From ExhibitListViewActivity.class
            Bundle extras = getIntent().getExtras();
            currentLocationID = extras.getString("from");
            FromExhibitId = currentLocationID;
            detailed = extras.getBoolean("detailed");
            eInfo = lm.getTrailInfo();
            vInfo = lm.getExhibitInfo();
            //--------------
            Exhibit first_exhibit = null;
            for(Exhibit e: exhibits){
                if(e.getItemId().equals(currentLocationID)){  // could go previous only if is added into plan
                    first_exhibit = e;
                    break;
                }
            }
            if(first_exhibit!=null){
                planned_items.add(first_exhibit);
            }
            String first_cl_id=getIntent().getExtras().getString("from");
            String first_cl_name = null;
            for (Map.Entry<String, ZooDataItem> entry : vInfo.entrySet()) {
                String cur_id = entry.getKey();
                if (cur_id.equals(first_cl_id)) {
                    first_cl_name=entry.getValue().name;
                    break;
                }
            }
            if(first_cl_name != null) {
                for(Exhibit exhibit: exhibits){
                    if(exhibit.getName().equals(first_cl_name)){
                        exhibitDao.delete(exhibitDao.get(first_cl_name));
                        break;
                    }
                }

            }
            //--------------

            g = lm.getGraph();

            update(currentLocationID);
        }
    }

    public void clear() {
        TextView navigation = findViewById(R.id.plan_nav);
        navigation.setText("You've already arrived at your destination!");
    }
    public void prev_update(Exhibit curExhibit, Exhibit prevExhibit) {
        initializeView();
        String prevExhibitId=prevExhibit.getItemId();
        Context context = getApplicationContext();
        ExhibitDatabase db = ExhibitDatabase.getSingleton(context);
        ExhibitDao exhibitDao = db.exhibitDao();
        List<Exhibit> exhibits = exhibitDao.getAll();
        double minDist = Double.POSITIVE_INFINITY;
        double nextMinDist = Double.POSITIVE_INFINITY;
        Exhibit nextClosestExhibit = null;


        // find next nearest exhibit
        if (prevExhibit != null) {
            for (Exhibit exhibit: exhibits) {
                if(exhibit.getItemId().equals(prevExhibitId)){
                    continue;
                }
                DijkstraShortestPath d = new DijkstraShortestPath(g);
                double weight = d.getPathWeight(prevExhibit.getItemId(),exhibit.getItemId());
                if (weight < nextMinDist){
                    nextMinDist = weight;
                    nextClosestExhibit = exhibit;
                }
            }
        }
        TextView from = findViewById(R.id.plan_from);
        TextView to = findViewById(R.id.plan_to);
        TextView navigation = findViewById(R.id.plan_nav);

        from.setText(curExhibit.getName());
        to.setText(prevExhibit.getName());
        returnResult = curExhibit.getItemId();

        TextView nextView = findViewById(R.id.nextStop);
        if (nextClosestExhibit != null) {
            GraphPath<String, IdentifiedWeightedEdge> path2 = DijkstraShortestPath.
                    findPathBetween(g,prevExhibit.getItemId(),nextClosestExhibit.getItemId());
            int distance=0;
            for (IdentifiedWeightedEdge e : path2.getEdgeList()) {
                distance+=(int)g.getEdgeWeight(e);
            }
            nextView.setText("Closest next stop: " + nextClosestExhibit.getName()+"\n"+"Distance: "+distance+" ft");
        }
        else if (exhibits.size() == 1){
            nextView.setText("You are almost done your visit");
        }
        else{
            nextView.setText("You have finished your plan!");
        }


        GraphPath<String, IdentifiedWeightedEdge> path = DijkstraShortestPath.
                findPathBetween(g,curExhibit.getItemId(),prevExhibitId);

        int i = 1;
        for (IdentifiedWeightedEdge e : path.getEdgeList()) {

            // find the direction we go through each edge by comparing their distance from current location.
            ZooDataItem vnear;
            ZooDataItem vfar;
            ZooDataItem v1 = vInfo.get(g.getEdgeTarget(e).toString());
            ZooDataItem v2 = vInfo.get(g.getEdgeSource(e).toString());
            GraphPath<String, IdentifiedWeightedEdge> route1 = DijkstraShortestPath.findPathBetween(g, v1.id, prevExhibitId);
            GraphPath<String, IdentifiedWeightedEdge> route2 = DijkstraShortestPath.findPathBetween(g, v2.id, prevExhibitId);
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
            if (detailed) {
                message= "detailed version todo..."+ "\n"; //todo
            }
            else{ //simplified version
                message= i + ". Walk on " + eInfo.get(e.getId()).street + " " + (int)g.getEdgeWeight(e)
                        + " ft from " + vfar.name + " to "  + vnear.name + "\n";
            }

            String currentMessage = navigation.getText().toString();
            if (currentMessage.equals("You've already arrived at your destination!")){
                currentMessage = "";
            }
            navigation.setText(currentMessage + message);

            i++;
        }
        this.curExhibit=prevExhibit;
        closestExhibit=prevExhibit;
    }

    public void update(String lastClosestExhibitId) {
        initializeView();
        Context context = getApplicationContext();
        ExhibitDatabase db = ExhibitDatabase.getSingleton(context);
        ExhibitDao exhibitDao = db.exhibitDao();
        List<Exhibit> exhibits = exhibitDao.getAll();
        double minDist = Double.POSITIVE_INFINITY;
        double nextMinDist = Double.POSITIVE_INFINITY;
        Exhibit nextClosestExhibit=null;

        // find current nearest exhibit to navigate to
        for (Exhibit exhibit : exhibits) {
            DijkstraShortestPath d = new DijkstraShortestPath(g);
            double weight = d.getPathWeight(lastClosestExhibitId, exhibit.getItemId());
            if (weight < minDist) {
                minDist = weight;
                closestExhibit = exhibit;
            }
        }
        curExhibit=closestExhibit;
        // find next nearest exhibit
        if (closestExhibit != null) {
            for (Exhibit exhibit: exhibits) {
                if(exhibit.getItemId().equals(lastClosestExhibitId) || exhibit.getItemId().equals(closestExhibit.getItemId())){
                    continue;
                }
                DijkstraShortestPath d = new DijkstraShortestPath(g);
                double weight = d.getPathWeight(closestExhibit.getItemId(),exhibit.getItemId());
                if (weight < nextMinDist){
                    nextMinDist = weight;
                    nextClosestExhibit = exhibit;
                }
            }
        }
        TextView from = findViewById(R.id.plan_from);
        TextView to = findViewById(R.id.plan_to);
        TextView navigation = findViewById(R.id.plan_nav);
        if(!isSkip){
            from.setText(vInfo.get(lastClosestExhibitId).name);
            to.setText(closestExhibit.getName());
            if(vInfo.get(lastClosestExhibitId).name.equals(closestExhibit.getName()) && exhibits.size()==0){
                to.setText("Destination");
            }
        }

        returnResult = vInfo.get(lastClosestExhibitId).id;

        TextView nextView = findViewById(R.id.nextStop);
        if (nextClosestExhibit != null) {
            GraphPath<String, IdentifiedWeightedEdge> path2 = DijkstraShortestPath.
                    findPathBetween(g,closestExhibit.getItemId(),nextClosestExhibit.getItemId());
            int distance=0;
            for (IdentifiedWeightedEdge e : path2.getEdgeList()) {
                distance+=(int)g.getEdgeWeight(e);
            }
            nextView.setText("Closest next stop: " + nextClosestExhibit.getName()+"\n"+"Distance: "+distance+" ft");
        }
        else if (exhibits.size() == 1){
            nextView.setText("You are almost done your visit");
        }
        else{
            nextView.setText("You have finished your plan!");
        }

        GraphPath<String, IdentifiedWeightedEdge> path = DijkstraShortestPath.
                findPathBetween(g,lastClosestExhibitId,closestExhibit.getItemId());

        if(!isSkip){
            int i = 1;
            for (IdentifiedWeightedEdge e : path.getEdgeList()) {

                // find the direction we go through each edge by comparing their distance from current location.
                ZooDataItem vnear;
                ZooDataItem vfar;
                ZooDataItem v1 = vInfo.get(g.getEdgeTarget(e).toString());
                ZooDataItem v2 = vInfo.get(g.getEdgeSource(e).toString());
                GraphPath<String, IdentifiedWeightedEdge> route1 = DijkstraShortestPath.findPathBetween(g, lastClosestExhibitId, v1.id);
                GraphPath<String, IdentifiedWeightedEdge> route2 = DijkstraShortestPath.findPathBetween(g, lastClosestExhibitId, v2.id);
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
                    message= "detailed version todo..."+ "\n"; // TODO: Add Details
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
        isSkip=false;
    }


    public void initializeView() {
        Context context = getApplicationContext();
        ExhibitDatabase db = ExhibitDatabase.getSingleton(context);
        ExhibitDao exhibitDao = db.exhibitDao();
        TextView countView = findViewById(R.id.num_remaining);
        long theCount = 0;
        if (exhibitDao.getAll() != null) {
            theCount = exhibitDao.getAll().size();
        }
        if(theCount>2){
            countView.setText(String.valueOf(theCount)+" unvisited exhibitions remaining");
        }
        else if(theCount == 1 || theCount == 2){
            countView.setText(String.valueOf(theCount)+" unvisited exhibition remaining");
        }
        else{
            countView.setText("You have no unvisited exhibition remaining");
        }

    }
    public void onPrevClicked(View view) {
        if(planned_items.size()==0){
            Utils.alertDialogShow2(this,"You don't have a previous stop");
            return;
        }
        Context context = getApplicationContext();
        ExhibitDatabase db = ExhibitDatabase.getSingleton(context);
        ExhibitDao exhibitDao = db.exhibitDao();
        Exhibit prev_stop = planned_items.get(planned_items.size()-1);
        planned_items.remove(planned_items.size()-1);
        exhibitDao.insert(prev_stop);
        clear();
        prev_update(curExhibit,prev_stop);
    }

    public void onNextClicked(View view){
        Context context = getApplicationContext();
        ExhibitDatabase db = ExhibitDatabase.getSingleton(context);
        ExhibitDao exhibitDao = db.exhibitDao();
        String closestExhibitId = closestExhibit.getItemId();
        if(exhibitDao.getAll().size()==0){
            return;
        }
        planned_items.add(closestExhibit);
        exhibitDao.delete(closestExhibit);
        clear();
        for(int i=0;i<planned_items.size();i++){
            System.out.println(planned_items.get(i));
        }
        update(closestExhibitId);
        FromExhibitId=closestExhibit.getItemId();
    }

    public void onGoBackClicked(View view) {
        Intent intent = new Intent();
        intent.putExtra("MESSAGE", returnResult);
        setResult(2, intent);
        finish();
    }


    public void onSkipClicked(View view) {
        Context context = getApplicationContext();
        ExhibitDatabase db = ExhibitDatabase.getSingleton(context);
        ExhibitDao exhibitDao = db.exhibitDao();
        List<Exhibit> exhibits = exhibitDao.getAll();
        Exhibit nextClosestExhibit=null;
        double nextMinDist = Double.POSITIVE_INFINITY;
        if (closestExhibit != null) {
            for (Exhibit exhibit: exhibits) {
                if(exhibit.getItemId().equals(closestExhibit.getItemId())){
                    continue;
                }
                DijkstraShortestPath d = new DijkstraShortestPath(g);
                double weight = d.getPathWeight(closestExhibit.getItemId(),exhibit.getItemId());
                if (weight < nextMinDist){
                    nextMinDist = weight;
                    nextClosestExhibit = exhibit;
                }
            }
        }
        if(nextClosestExhibit==null){
            Utils.alertDialogShow2(this,"No item to skip");
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure to skip "+nextClosestExhibit.getName()+" ?");
            builder.setTitle("Skip Stop");
            builder.setCancelable(false);
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            Exhibit finalNextClosestExhibit = nextClosestExhibit;
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    exhibitDao.delete(finalNextClosestExhibit);
                    isSkip = true;
                    update(FromExhibitId);
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }

    }

    public void onMockLocation(View view) {
        Context context = view.getContext();
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.mock_location, null);


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText longitude = (EditText) promptsView
                .findViewById(R.id.longitude_input);
        final EditText latitude = (EditText) promptsView
                .findViewById(R.id.latitude_input);


        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text
                                String lon = longitude.getText().toString();
                                String lat = latitude.getText().toString();
                                returnResult = findNearestLocation(lat, lon);
                                checkOffRoute(returnResult);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void checkOffRoute(String nearestLocation) {
        TextView from = findViewById(R.id.plan_from);
        TextView to = findViewById(R.id.plan_to);
        String currentStopName = from.getText().toString();
        String nextStopName = to.getText().toString();
        String currentStopId = lm.getIdFromName(currentStopName);
        String nextStopId = lm.getIdFromName(nextStopName);
        GraphPath<String, IdentifiedWeightedEdge> path = DijkstraShortestPath.findPathBetween(g, currentStopId, nextStopId);
        var vertexList = path.getVertexList();

        // build dialog
        var builder = new AlertDialog.Builder(this);
        if (vertexList.contains(nearestLocation)) {
            builder.setMessage("You are still on right track!");
            builder.setCancelable(false)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        } else {
            builder.setMessage("You are off track! Replan?")
            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent();
                    intent.putExtra("MESSAGE", returnResult);
                    setResult(2, intent);
                    finish();
                }
            })
            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent();
                    intent.putExtra("MESSAGE", currentStopId);
                    setResult(2, intent);
                    finish();
                }
            });

        }

        AlertDialog alert = builder.create();
        alert.show();

    }

    public String findNearestLocation(String latitude, String longitude) {
        String result = "entrance_exit_gate";
        try {
            double lat = Double.parseDouble(latitude);
            double lon = Double.parseDouble(longitude);
            ListManager lm = new ListManager(this);
            result = lm.getNearestExhibit(lat, lon);

        } catch (Exception e) {
            Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
            LOGGER.log(Level.WARNING, e.getMessage());
        }
        return result;
    }
}