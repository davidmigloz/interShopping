package bendavid.is.intershopping.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import bendavid.is.intershopping.R;
import bendavid.is.intershopping.activities.CreateSListActivity;
import bendavid.is.intershopping.activities.InterShoppingActivity;
import bendavid.is.intershopping.activities.ShoppingListDetailActivity;
import bendavid.is.intershopping.entities.ShoppingList;

/**
 * Show a list with all the shopping lists stored in the data base.
 */
public class ShoppingListFragment extends Fragment {
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
        // Get Shopping lists
        List<ShoppingList> shoppingLists = ShoppingList.listAll(ShoppingList.class);

        // LinearLayoutManager provides a similar implementation to a ListView
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(new ShoppingListRecyclerViewAdapter(getActivity(),
                shoppingLists));
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
        // If it's visible, set the right action to the FloatingActionButton and show it
        InterShoppingActivity mainActivity = (InterShoppingActivity) getActivity();
        mainActivity.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Action: create shopping list
                Intent intent = new Intent(getActivity(), CreateSListActivity.class);
                startActivity(intent);
            }
        });
        mainActivity.fab.show();
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
            return shoppingLists.size();
        }

        /**
         * Gets the ViewHolder used for the item at given position.
         */
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_icon, parent, false);
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
            // Supermarket name
            viewHolder.item_name.setText(shoppingLists.get(position).toString());

            // Listener
            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
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
            public final View mView;
            public ImageView icon;
            public TextView item_name;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                icon = (ImageView) view.findViewById(R.id.letter_sm);
                item_name = (TextView) view.findViewById(R.id.item_name);
            }
        }
    }
}