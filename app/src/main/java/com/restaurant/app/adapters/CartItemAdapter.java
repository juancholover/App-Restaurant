package com.restaurant.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.restaurant.app.R;
import com.restaurant.app.models.CartItem;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder> {
    private List<CartItem> cartItems;
    private OnCartItemChangeListener listener;
    private NumberFormat currencyFormat;

    public interface OnCartItemChangeListener {
        void onQuantityChanged(CartItem item, int newQuantity);
        void onItemRemoved(CartItem item);
    }

    public CartItemAdapter(List<CartItem> cartItems, OnCartItemChangeListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "PE"));
    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart_item, parent, false);
        return new CartItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.bind(cartItem);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    class CartItemViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewName, textViewPrice, textViewQuantity, textViewSubtotal;
        private ImageView imageViewItem;
        private ImageButton buttonDecrease, buttonIncrease, buttonRemove;

        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewItemName);
            textViewPrice = itemView.findViewById(R.id.textViewItemPrice);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
            textViewSubtotal = itemView.findViewById(R.id.textViewSubtotal);
            imageViewItem = itemView.findViewById(R.id.imageViewItem);
            buttonDecrease = itemView.findViewById(R.id.buttonDecrease);
            buttonIncrease = itemView.findViewById(R.id.buttonIncrease);
            buttonRemove = itemView.findViewById(R.id.buttonRemove);
        }

        public void bind(CartItem cartItem) {
            textViewName.setText(cartItem.getMenuItem().getName());
            textViewPrice.setText(currencyFormat.format(cartItem.getMenuItem().getPrice()));
            textViewQuantity.setText(String.valueOf(cartItem.getQuantity()));
            textViewSubtotal.setText(currencyFormat.format(cartItem.getSubtotal()));

            buttonDecrease.setOnClickListener(v -> {
                if (listener != null && cartItem.getQuantity() > 1) {
                    listener.onQuantityChanged(cartItem, cartItem.getQuantity() - 1);
                }
            });

            buttonIncrease.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onQuantityChanged(cartItem, cartItem.getQuantity() + 1);
                }
            });

            buttonRemove.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemRemoved(cartItem);
                }
            });
        }
    }
}