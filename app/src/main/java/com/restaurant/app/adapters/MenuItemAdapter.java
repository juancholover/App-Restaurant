package com.restaurant.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.restaurant.app.R;
import com.restaurant.app.models.MenuItem;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.MenuItemViewHolder> {
    private List<MenuItem> menuItems;
    private OnMenuItemClickListener listener;
    private NumberFormat currencyFormat;

    public interface OnMenuItemClickListener {
        void onMenuItemClick(MenuItem menuItem);
    }

    public MenuItemAdapter(List<MenuItem> menuItems, OnMenuItemClickListener listener) {
        this.menuItems = menuItems;
        this.listener = listener;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "PE"));
    }

    @NonNull
    @Override
    public MenuItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_menu_item, parent, false);
        return new MenuItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuItemViewHolder holder, int position) {
        MenuItem menuItem = menuItems.get(position);
        holder.bind(menuItem);
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    class MenuItemViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewName, textViewDescription, textViewPrice, textViewCategory;
        private ImageView imageViewMenuItem;

        public MenuItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewMenuItemName);
            textViewDescription = itemView.findViewById(R.id.textViewMenuItemDescription);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            textViewCategory = itemView.findViewById(R.id.textViewCategory);
            imageViewMenuItem = itemView.findViewById(R.id.imageViewMenuItem);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onMenuItemClick(menuItems.get(position));
                    }
                }
            });
        }

        public void bind(MenuItem menuItem) {
            textViewName.setText(menuItem.getName());
            textViewDescription.setText(menuItem.getDescription());
            textViewPrice.setText(currencyFormat.format(menuItem.getPrice()));
            textViewCategory.setText(menuItem.getCategory());

            // Asignar imagen según el nombre del plato
            if (menuItem.getName().contains("Pizza")) {
                imageViewMenuItem.setImageResource(R.drawable.ic_pizza_ita_background);
            } else if (menuItem.getName().contains("Sushi")) {
                imageViewMenuItem.setImageResource(R.drawable.ic_sushi_app_background);
            } else if (menuItem.getName().contains("Burger")) {
                imageViewMenuItem.setImageResource(R.drawable.ic_burguer_app_background);
            } else {
                imageViewMenuItem.setImageResource(R.drawable.ic_restaurant_placeholder); // tu imagen por defecto
            }
        }
    }
}