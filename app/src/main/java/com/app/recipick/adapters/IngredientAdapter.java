package com.app.recipick.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.app.recipick.data.Ingredient.Ingredient;
import java.util.ArrayList;
import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {

    private List<Ingredient> allIngredients = new ArrayList<>();
    private List<Ingredient> displayedIngredients = new ArrayList<>();

    public void setIngredients(List<Ingredient> ingredients) {
        this.allIngredients = ingredients;
        this.displayedIngredients = new ArrayList<>(ingredients);
        notifyDataSetChanged();
    }

    public void filter(String query) {
        displayedIngredients.clear();
        if (query.isEmpty()) {
            displayedIngredients.addAll(allIngredients);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Ingredient ingredient : allIngredients) {
                if (ingredient.getName().toLowerCase().contains(lowerCaseQuery)) {
                    displayedIngredients.add(ingredient);
                }
            }
        }
        notifyDataSetChanged();
    }

    public List<Ingredient> getSelectedIngredients() {
        List<Ingredient> selected = new ArrayList<>();
        for (Ingredient ingredient : allIngredients) {
            if (ingredient.isSelected()) {
                selected.add(ingredient);
            }
        }
        return selected;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_multiple_choice, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ingredient currentItem = displayedIngredients.get(position);
        holder.checkedTextView.setText(currentItem.getName());

        holder.checkedTextView.setChecked(currentItem.isSelected());

        holder.checkedTextView.setOnClickListener(v -> {
            // Αντιστρέφουμε την κατάσταση επιλογής με το κλικ
            boolean isNowChecked = !holder.checkedTextView.isChecked();
            holder.checkedTextView.setChecked(isNowChecked);
            currentItem.selected = isNowChecked;
        });
    }

    @Override
    public int getItemCount() {
        return displayedIngredients.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckedTextView checkedTextView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkedTextView = (CheckedTextView) itemView.findViewById(android.R.id.text1);
        }
    }
}