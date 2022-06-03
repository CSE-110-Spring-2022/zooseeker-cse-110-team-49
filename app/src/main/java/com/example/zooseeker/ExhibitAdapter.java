package com.example.zooseeker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class ExhibitAdapter extends RecyclerView.Adapter<ExhibitAdapter.ViewHolder> {

    private List<Exhibit> exhibits = Collections.emptyList();
    private Consumer<Exhibit> onDeleteButtonClicked;
    private String currentLocationID;

    private Graph<String, IdentifiedWeightedEdge> g;
    private Consumer<Exhibit> onNavigateButtonClicked;


    /**
    * Load graph from ExhibitListViewActivity
    * Used to compute the distance from current location
    */
    public void loadGraph(Graph<String, IdentifiedWeightedEdge> g) {
        this.g = g;
    }

    public void loadCurrentLocation(String currentLocationID){
        this.currentLocationID = currentLocationID;
    }

    public void setExhibits(List<Exhibit> newExhibits) {
        this.exhibits.clear();
        this.exhibits = newExhibits;
        notifyDataSetChanged();
    }

    public void setOnDeleteButtonClickedHandler(Consumer<Exhibit> onDeleteButtonClicked) {
        this.onDeleteButtonClicked = onDeleteButtonClicked;
    }

    public void setOnNavigateButtonClicked(Consumer<Exhibit> onNavigateButtonClicked) {
        this.onNavigateButtonClicked = onNavigateButtonClicked;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.exhibit, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setItem(exhibits.get(position));
    }

    @Override
    public int getItemCount() {
        return exhibits.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView;
        private Exhibit exhibit;
        private Button navigation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.exhibit_name);
            this.navigation = itemView.findViewById(R.id.navigation);

            itemView.findViewById(R.id.delete_btn).setOnClickListener(view -> {
                if (onDeleteButtonClicked == null) return;
                onDeleteButtonClicked.accept(exhibit);
            });

            itemView.findViewById(R.id.navigation).setOnClickListener(view -> {
                if (onNavigateButtonClicked == null) return;
                onNavigateButtonClicked.accept(exhibit);
            });
        }

        public Exhibit getExhibit() {
            return exhibit;
        }

        /**
        * Set distance on navigation button
        * */
        public void setItem(Exhibit exhibit) {
            this.exhibit = exhibit;
            DijkstraShortestPath d = new DijkstraShortestPath(g);
            double weight = d.getPathWeight(currentLocationID, exhibit.getItemId());
            this.navigation.setText("navigate\n" + String.valueOf((int)weight) + "ft");
            this.textView.setText(exhibit.getName());
        }
    }

}