package com.example.zooseeker;

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
    private List<ZooDataItem> animalItems = Collections.emptyList();
    private ExhibitDao exhibitDao;

    public void setAnimalItems(List<ZooDataItem> newAnimalItems){
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
        private ZooDataItem animalItem;
        private Button addButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.animal_item);
            addButton = itemView.findViewById(R.id.add_animal);
        }

        public ZooDataItem getAnimalItem() {return animalItem;}

        /**
        * If animal is already in user's list, display "DONE!" instead of "ADD" button.
        * If animal not already in list, the user sees "ADD",
        * after user click "ADD", add the animal to the list and then display "DONE!"
        * */
        public void setAnimalItem(ZooDataItem animalItem) {
            this.animalItem = animalItem;
            this.textView.setText(animalItem.name);

            ExhibitDatabase db = ExhibitDatabase.getSingleton(textView.getContext());
            ExhibitDao exhibitDao = db.exhibitDao();

            if (exhibitDao.get(animalItem.name) != null) {
                addButton.setText("Done!");
                itemView.findViewById(R.id.add_animal).setBackgroundColor(Color.GRAY);
            }

            addButton.setOnClickListener(view -> {
                Exhibit theExhibit = exhibitDao.get(animalItem.name);
                if (theExhibit == null) {
                    theExhibit = new Exhibit(animalItem.id, animalItem.name);
                    exhibitDao.insert(theExhibit);
                }
                addButton.setText("Done!");
                itemView.findViewById(R.id.add_animal).setBackgroundColor(Color.GRAY);
            });
        }
    }
}
