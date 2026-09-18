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

    private List<Ingredient> selectedForDeletion = new ArrayList<>();
    private boolean isMultiSelectMode = false;
    private OnMultiSelectListener multiSelectListener;

    public interface OnMultiSelectListener {
        void onSelectionChanged(boolean isMultiSelect, int selectedCount);
    }

    public void setOnMultiSelectListener(OnMultiSelectListener listener) {
        this.multiSelectListener = listener;
    }

    public List<Ingredient> getSelectedForDeletion() {
        return selectedForDeletion;
    }

    public boolean isMultiSelectMode() {
        return isMultiSelectMode;
    }

    public void clearSelection() {
        isMultiSelectMode = false;
        selectedForDeletion.clear();
        if (multiSelectListener != null) {
            multiSelectListener.onSelectionChanged(false, 0);
        }
        notifyDataSetChanged();
    }

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
            com.google.android.material.card.MaterialCardView card = (com.google.android.material.card.MaterialCardView) holder.itemView;

            // Αλλαγή χρώματος αν είναι επιλεγμένο για διαγραφή
            if (selectedForDeletion.contains(currentItem)) {
                card.setCardBackgroundColor(android.graphics.Color.parseColor("#FFCDD2"));
            } else {
                card.setCardBackgroundColor(android.graphics.Color.WHITE); // Λευκό
            }

            // Παρατεταμένο κλικ για να ξεκινήσει η επιλογή
            holder.itemView.setOnLongClickListener(v -> {
                if (!isMultiSelectMode) {
                    v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);
                    isMultiSelectMode = true;
                    selectedForDeletion.add(currentItem);

                    if (multiSelectListener != null) {
                        multiSelectListener.onSelectionChanged(true, selectedForDeletion.size());
                    }

                    notifyItemChanged(holder.getAdapterPosition());
                }
                return true;
            });

            // Απλό κλικ για να επιλέγει κι άλλα υλικά
            holder.itemView.setOnClickListener(v -> {
                if (isMultiSelectMode) {
                    if (selectedForDeletion.contains(currentItem)) {
                        selectedForDeletion.remove(currentItem);
                        if (selectedForDeletion.isEmpty()) { // Αν τα ξε-τίκαρε όλα, βγες από το mode
                            isMultiSelectMode = false;
                        }
                    } else {
                        selectedForDeletion.add(currentItem);
                    }

                    if (multiSelectListener != null) {
                        multiSelectListener.onSelectionChanged(isMultiSelectMode, selectedForDeletion.size());
                    }

                    notifyItemChanged(holder.getAdapterPosition());
                }
            });

        } else {
            CheckedTextView checkedView = (CheckedTextView) holder.textView;
            checkedView.setChecked(currentItem.isSelected());

            checkedView.setOnClickListener(v -> {
                boolean isNowChecked = !checkedView.isChecked();
                checkedView.setChecked(isNowChecked);

                currentItem.selected = isNowChecked;
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

    public void selectAll() {
        isMultiSelectMode = true;
        selectedForDeletion.clear();
        selectedForDeletion.addAll(displayedIngredients);
        notifyDataSetChanged();

        if (multiSelectListener != null) {
            multiSelectListener.onSelectionChanged(true, selectedForDeletion.size()); // Η σωστή μέθοδος του interface σου
        }
    }
}