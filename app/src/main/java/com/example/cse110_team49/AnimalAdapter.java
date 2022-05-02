package com.example.cse110_team49;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class AnimalAdapter extends RecyclerView.Adapter<AnimalAdapter.ViewHolder> {
    private List<ZooDataItem.VertexInfo> animalItems = Collections.emptyList();
    private ExhibitDao exhibitDao;

    public void setAnimalItems(List<ZooDataItem.VertexInfo> newAnimalItems){
        this.animalItems.clear();
        this.animalItems = newAnimalItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.animal_list_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setAnimalItem(animalItems.get(position));
    }

    @Override
    public int getItemCount() {
        return animalItems.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private ZooDataItem.VertexInfo animalItem;
        private Button addButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.animal_item);
            addButton = itemView.findViewById(R.id.add_animal);
        }

        public ZooDataItem.VertexInfo getAnimalItem() {return animalItem;}

        public void setAnimalItem(ZooDataItem.VertexInfo animalItem) {
            this.animalItem = animalItem;
            this.textView.setText(animalItem.name);
            addButton.setOnClickListener(view -> {
                Exhibit theExhibit = new Exhibit(animalItem.id, animalItem.name);
                Context context = view.getContext();
                ExhibitDatabase db = ExhibitDatabase.getSingleton(context);
                ExhibitDao exhibitDao = db.exhibitDao();
                exhibitDao.insert(theExhibit);
                addButton.setText("Done!");
                itemView.findViewById(R.id.add_animal).setBackgroundColor(Color.GRAY);
            });
        }
    }
}
