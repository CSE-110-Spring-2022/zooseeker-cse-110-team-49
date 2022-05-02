package com.example.cse110_team49;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ExhibitAdapter extends RecyclerView.Adapter<ExhibitAdapter.ViewHolder> {

    private List<Exhibit> exhibits = Collections.emptyList();
    private Consumer<Exhibit> onDeleteButtonClicked;

    public void setExhibits(List<Exhibit> newExhibits) {
        this.exhibits.clear();
        this.exhibits = newExhibits;
        notifyDataSetChanged();
    }

    public void setOnDeleteButtonClickedHandler(Consumer<Exhibit> onDeleteButtonClicked) {
        this.onDeleteButtonClicked = onDeleteButtonClicked;
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
        holder.setTodoItem(exhibits.get(position));
    }

    @Override
    public int getItemCount() {
        return exhibits.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView;
        private Exhibit exhibit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.exhibit_name);

            itemView.findViewById(R.id.delete_btn).setOnClickListener(view -> {
                if (onDeleteButtonClicked == null) return;
                onDeleteButtonClicked.accept(exhibit);
            });
        }

        public Exhibit getExhibit() {
            return exhibit;
        }

        public void setTodoItem(Exhibit exhibit) {
            this.exhibit = exhibit;
            this.textView.setText(exhibit.getName());
        }
    }

}