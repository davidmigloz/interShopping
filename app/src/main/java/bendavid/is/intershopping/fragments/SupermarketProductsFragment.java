package bendavid.is.intershopping.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bendavid.is.intershopping.R;
import bendavid.is.intershopping.activities.ShoppingListDetailActivity;
import bendavid.is.intershopping.entities.ShoppingList;

/**
 * Show a list with all the shopping lists stored in the data base.
 */
public class SupermarketProductsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView rv = (RecyclerView) inflater.inflate(
                R.layout.list_shopping_lists, container, false);
        setupRecyclerView(rv);
        return rv;
    }

    /**
     * Get all the shopping lists and show them in the RecyclerView.
     */
    private void setupRecyclerView(RecyclerView recyclerView) {
        // Get ShoppingList-ID
        long supermarketID = getArguments().getLong("supermarket-id");
        // Get Shopping lists
        List<ShoppingList> allShoppingLists = ShoppingList.listAll(ShoppingList.class);
        List<ShoppingList> filteredShoppingLists = new ArrayList<>();
        Collections.sort(allShoppingLists, Collections.reverseOrder());

        for (ShoppingList shoppingList : allShoppingLists) {
            if (shoppingList.getSupermarked().getId() == supermarketID) {
                filteredShoppingLists.add(shoppingList);
            }
        }
        // LinearLayoutManager provides a similar implementation to a ListView
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        final ShoppingListRecyclerViewAdapter adapter = new ShoppingListRecyclerViewAdapter(
                getActivity(), filteredShoppingLists);
        recyclerView.setAdapter(adapter);
    }


    public static class ShoppingListRecyclerViewAdapter
            extends RecyclerView.Adapter<ShoppingListRecyclerViewAdapter.ViewHolder> {
        private Context context;
        private List<ShoppingList> shoppingLists;

        public ShoppingListRecyclerViewAdapter(Context context, List<ShoppingList> items) {
            this.context = context;
            shoppingLists = items;
        }

        /**
         * Number of shopping lists in the list.
         */
        @Override
        public int getItemCount() {
            return shoppingLists == null ? 0 : shoppingLists.size();
        }

        /**
         * Gets the ViewHolder used for the item at given position.
         */
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_shopping_lists_item, parent, false);
            return new ViewHolder(view);
        }

        /**
         * It's called when views need to be created from given ViewHolder.
         */
        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
            // ShoppingLists's icon
            Drawable icon = ContextCompat.getDrawable(context, R.drawable.ic_shopping_cart);
            viewHolder.icon.setImageDrawable(icon);
            // ShoppingLists name
            viewHolder.itemName.setText(shoppingLists.get(position).toString());
            // Supermarket
            viewHolder.supermarketName.setText(shoppingLists.get(position).getSupermarked().toString());
            // Number of items bought
            viewHolder.boughtItems.setText(shoppingLists.get(position).getNumItemsBought() +
                    "/" + shoppingLists.get(position).getNumItems());
            // Total price
            NumberFormat nf = new DecimalFormat("#,###.##€");
            viewHolder.totalPrice.setText(nf.format(shoppingLists.get(position).getTotalPrice()));
            // Listener: go to shopping list when item is pressed
            viewHolder.cv.setOnClickListener(new View.OnClickListener() {
                /**
                 * When you touch one shopping list, you move to the detail view.
                 */
                @Override
                public void onClick(View v) {
                    ShoppingList sl = shoppingLists.get(position);
                    Intent i = new Intent(context, ShoppingListDetailActivity.class);
                    i.putExtra("shopping-list-id", sl.getId());
                    context.startActivity(i);
                }
            });
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            final CardView cv;
            ImageView icon;
            TextView itemName;
            TextView supermarketName;
            TextView boughtItems;
            TextView totalPrice;

            public ViewHolder(View view) {
                super(view);
                cv = (CardView) itemView.findViewById(R.id.cv);
                icon = (ImageView) view.findViewById(R.id.item_icon);
                itemName = (TextView) view.findViewById(R.id.item_name);
                supermarketName = (TextView) view.findViewById(R.id.supermarket_name);
                boughtItems = (TextView) view.findViewById(R.id.bought_items);
                totalPrice = (TextView) view.findViewById(R.id.total_price);
            }
        }
    }
}