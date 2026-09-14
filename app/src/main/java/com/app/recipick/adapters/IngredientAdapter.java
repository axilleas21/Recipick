package com.app.recipick.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.app.recipick.R;
import com.app.recipick.data.Ingredient.Ingredient;
import java.util.ArrayList;
import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {

    private List<Ingredient> allIngredients = new ArrayList<>();
    private List<Ingredient> displayedIngredients = new ArrayList<>();

    // μεταβλητή που ελέγχει αν είμαστε στα υλικα που εχουμε ή στην αναζήτηση για προσθηκη υλικων
    private boolean isPantryMode;

    // ΝΕΟ: Constructor
    public IngredientAdapter(boolean isPantryMode) {
        this.isPantryMode = isPantryMode;
    }

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

        if (isPantryMode) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pantry, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_multiple_choice, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ingredient currentItem = displayedIngredients.get(position);
        holder.textView.setText(currentItem.getName());

        if (isPantryMode) {
        } else {
            CheckedTextView checkedView = (CheckedTextView) holder.textView;
            checkedView.setChecked(currentItem.isSelected());

            android.content.Context context = holder.itemView.getContext();

            checkedView.setOnClickListener(v -> {
                boolean isNowChecked = !checkedView.isChecked();
                checkedView.setChecked(isNowChecked);
                currentItem.selected = isNowChecked;

                new Thread(() -> {
                    com.app.recipick.data.AppDatabase database = com.app.recipick.data.AppDatabase.getInstance(context);
                    int selectedValue = isNowChecked ? 1 : 0;
                    database.ingredientDao().updateSelection(currentItem.id, selectedValue);
                }).start();
            });
        }
    }

    @Override
    public int getItemCount() {
        return displayedIngredients.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tvIngredientName);
            if (textView == null) {
                textView = itemView.findViewById(android.R.id.text1);
            }
        }
    }

    public Ingredient getIngredientAt(int position) {
        return displayedIngredients.get(position);
    }

    public void removeIngredient(int position) {
        Ingredient item = displayedIngredients.get(position);
        allIngredients.remove(item);
        displayedIngredients.remove(position);
        notifyItemRemoved(position);
    }
}