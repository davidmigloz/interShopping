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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bendavid.is.intershopping.R;
import bendavid.is.intershopping.activities.CreateSListActivity;
import bendavid.is.intershopping.activities.InterShoppingActivity;
import bendavid.is.intershopping.activities.ShoppingListDetailActivity;
import bendavid.is.intershopping.entities.ShoppingList;
import bendavid.is.intershopping.entities.Supermarket;

/**
 * Show a list with all the shopping lists stored in the data base.
 */
public class SupermarketPProductsFragment extends Fragment {

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
        List<ShoppingList> filteredShoppingLists = new ArrayList<ShoppingList>();
        Collections.sort(allShoppingLists, Collections.reverseOrder());
        for (ShoppingList shoppingList : allShoppingLists) {
            if (shoppingList.getSupermarked().getId() == supermarketID) {
                filteredShoppingLists.add(shoppingList);
            }
        }
//        List<ShoppingList> shoppingLists = ShoppingList.findWithQuery(ShoppingList.class, "slelect * from Supermarket where supermarket = ?",
//                List<ShoppingList> shoppingLists = ShoppingList.find(ShoppingList.class, "supermarket = ?",Supermarket.first(Supermarket.class).getId());
//                getIntent().getExtras().getSerializable("supermarket-id"));
//         LinearLayoutManager provides a similar implementation to a ListView
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        final ShoppingListRecyclerViewAdapter adapter = new ShoppingListRecyclerViewAdapter(
                getActivity(), filteredShoppingLists);
        recyclerView.setAdapter(adapter);
        // For swipe and drag
        ItemTouchHelper mIth = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                        ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        adapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                        return true;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        adapter.onItemDismiss(viewHolder.getAdapterPosition());
                    }
                });
        mIth.attachToRecyclerView(recyclerView);
    }

    @Override
    public void setUserVisibleHint(boolean visible) {
        // Set a hint to the system about whether this fragment's UI is currently visible
        super.setUserVisibleHint(visible);
        // call onResume() is there's any change
        if (visible && isResumed()) {
            onResume();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // If the fragment is not visible, don't do anything
        if (!getUserVisibleHint()) {
            return;
        }
        /*// If it's visible, set the right action to the FloatingActionButton and show it
        InterShoppingActivity mainActivity = (InterShoppingActivity) getActivity();
        mainActivity.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Action: create shopping list
                Intent intent = new Intent(getActivity(), CreateSListActivity.class);
                startActivity(intent);
            }
        });
        mainActivity.fab.show();*/
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
            viewHolder.boughtItems.setText("0/0");
            // Total price
            viewHolder.totalPrice.setText(shoppingLists.get(position).getTotalPrice() + "€");
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

        /**
         * When a shoppingLists is removed, delete it from database.
         */
        public void onItemDismiss(int position) {
            shoppingLists.get(position).delete();
            shoppingLists.remove(position);
            notifyItemRemoved(position);
        }

        /**
         * When a shoppingLists is moved, save position.
         */
        public void onItemMove(int fromPosition, int toPosition) {
            ShoppingList prev = shoppingLists.remove(fromPosition);
            shoppingLists.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
            notifyItemMoved(fromPosition, toPosition);
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
   /* private void setupRecyclerView(RecyclerView recyclerView) {
        // Get Shoppinglists
//        List<ShoppingList> shoppingLists = ShoppingList.find(ShoppingList.class, "supermarket = ?", getIntent().getExtras().getSerializable("supermarket-id"));

        // Get items
//        List<ListItem> itemList = ListItem.find(ListItem.class,);
//        Collections.sort(shoppingLists, Collections.reverseOrder());
//
//        // LinearLayoutManager provides a similar implementation to a ListView
//        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
//        recyclerView.setAdapter(new ShoppingListRecyclerViewAdapter(getActivity(),
//                shoppingLists));
        // Get Shopping lists
        List<ShoppingList> shoppingLists = ShoppingList.listAll(ShoppingList.class);
        Collections.sort(shoppingLists, Collections.reverseOrder());

        // LinearLayoutManager provides a similar implementation to a ListView
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(new ShoppingListRecyclerViewAdapter(getActivity(),
                shoppingLists));
    }*/