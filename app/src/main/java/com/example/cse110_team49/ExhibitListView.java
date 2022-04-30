package com.example.cse110_team49;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class ExhibitListView extends AppCompatActivity {

    ArrayList<Exhibit> exhibitList;
    Exhibit            currentLocation;
    public RecyclerView recyclerView;
    private ExhibitListViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exhibit_list_view);

        viewModel = new ViewModelProvider(this)
                .get(ExhibitListViewModel.class);

        ExhibitAdapter adapter = new ExhibitAdapter();
        adapter.setHasStableIds(true);
        adapter.setOnDeleteButtonClickedHandler(viewModel::deleteExhibit);
        viewModel.getExhibits().observe(this, adapter::setExhibits);

        recyclerView = findViewById(R.id.exhibits);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}