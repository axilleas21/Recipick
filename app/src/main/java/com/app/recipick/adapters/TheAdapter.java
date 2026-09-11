package com.app.recipick.adapters;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.app.recipick.R;
import com.app.recipick.data.Recipe.Recipe;
import java.util.List;

public class TheAdapter extends RecyclerView.Adapter<TheAdapter.ViewHolder> {

    private List<Recipe> recipes;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Recipe recipe);
    }

    public TheAdapter(List<Recipe> recipes, OnItemClickListener listener) {
        this.recipes = recipes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.txtRecipeName.setText(recipe.getName());
        holder.txtRecipeDescription.setText(recipe.getDescription());

        // --- ΛΟΓΙΚΗ ΧΡΩΜΑΤΩΝ ΒΑΣΕΙ ΠΟΣΟΣΤΟΥ ---
        if (recipe.matchPercentage == 100) {
            // 100%: Σκούρο Πράσινο (Έχει όλα τα υλικά)
            holder.txtRecipeDescription.setTextColor(Color.parseColor("#2E7D32"));
            holder.txtRecipeDescription.setTypeface(null, Typeface.BOLD);
        } else if (recipe.matchPercentage >= 75) {
            // 75%-99%: Ανοιχτό Πράσινο
            holder.txtRecipeDescription.setTextColor(Color.parseColor("#689F38"));
            holder.txtRecipeDescription.setTypeface(null, Typeface.NORMAL);
        } else if (recipe.matchPercentage >= 60) {
            // 60%-74%: Πορτοκαλί
            holder.txtRecipeDescription.setTextColor(Color.parseColor("#F57C00"));
            holder.txtRecipeDescription.setTypeface(null, Typeface.NORMAL);
        } else {
            // 50%-59%: Κόκκινο
            holder.txtRecipeDescription.setTextColor(Color.parseColor("#D32F2F"));
            holder.txtRecipeDescription.setTypeface(null, Typeface.NORMAL);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(recipe));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtRecipeName;
        TextView txtRecipeDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtRecipeName = itemView.findViewById(R.id.txtRecipeName);
            txtRecipeDescription = itemView.findViewById(R.id.txtRecipeDescription);
        }
    }
}