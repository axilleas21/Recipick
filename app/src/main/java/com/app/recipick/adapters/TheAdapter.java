package com.app.recipick.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

        holder.tvRecipeName.setText(recipe.name);
        holder.tvRecipeDesc.setText(recipe.desc);

        if (recipe.missingIngredients == 0) {
            holder.itemView.setAlpha(1.0f);
            holder.tvRecipeStatus.setText("Ready to cook!");
            holder.tvRecipeStatus.setTextColor(Color.parseColor("#4CAF50"));
        } else {
            holder.itemView.setAlpha(0.6f);
            holder.tvRecipeStatus.setText("Missing " + recipe.missingIngredients + " ingredient");
            holder.tvRecipeStatus.setTextColor(Color.parseColor("#FF9800"));
        }

        if (recipe.isFavorite) {
            holder.ivFavorite.setImageResource(R.drawable.baseline_favorite_24);
            holder.ivFavorite.setColorFilter(Color.parseColor("#E91E63")); // Κόκκινο
        } else {
            holder.ivFavorite.setImageResource(R.drawable.baseline_favorite_border_24);
            holder.ivFavorite.setColorFilter(Color.parseColor("#757575")); // Γκρι
        }


        holder.ivFavorite.setOnClickListener(v -> {
            recipe.isFavorite = !recipe.isFavorite;

            if (recipe.isFavorite) {
                holder.ivFavorite.setImageResource(R.drawable.baseline_favorite_24);
                holder.ivFavorite.setColorFilter(Color.parseColor("#E91E63"));
            } else {
                holder.ivFavorite.setImageResource(R.drawable.baseline_favorite_border_24);
                holder.ivFavorite.setColorFilter(Color.parseColor("#757575"));
            }

            new Thread(() -> {
                com.app.recipick.data.AppDatabase db = com.app.recipick.data.AppDatabase.getInstance(holder.itemView.getContext());
                db.recipeDao().updateFavorite(recipe.id, recipe.isFavorite);
            }).start();
        });

        holder.itemView.setOnClickListener(v -> listener.onItemClick(recipe));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvRecipeName;
        TextView tvRecipeDesc;
        TextView tvRecipeStatus;
        ImageView ivRecipeImage;
        ImageView ivFavorite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRecipeName = itemView.findViewById(R.id.tvRecipeName);
            tvRecipeDesc = itemView.findViewById(R.id.tvRecipeDesc);
            tvRecipeStatus = itemView.findViewById(R.id.tvRecipeStatus);
            ivRecipeImage = itemView.findViewById(R.id.ivRecipeImage);
            ivFavorite = itemView.findViewById(R.id.ivFavorite);
        }
    }
}