package com.example.cse110_team49;

import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.List;

public class ExhibitListViewModel extends AndroidViewModel {

    private LiveData<List<Exhibit>> exhibits;
    private final ExhibitDao exhibitDao;

    public ExhibitListViewModel(@NonNull Application application) {
        super(application);
        Context context = application.getApplicationContext();
        ExhibitDatabase db = ExhibitDatabase.getSingleton(context);
        exhibitDao = db.exhibitDao();
    }

    public LiveData<List<Exhibit>> getExhibits() {
        if (exhibits == null) {
            loadUsers();
        }
        return exhibits;
    }

    public void deleteExhibit(Exhibit exhibit) {
        exhibitDao.delete(exhibit);
    }

    private void loadUsers() {
        exhibits = exhibitDao.getAllLive();
    }
}
