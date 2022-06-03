package com.example.zooseeker;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

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
