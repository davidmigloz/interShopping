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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import bendavid.is.intershopping.R;
import bendavid.is.intershopping.activities.ShoppingListDetailActivity;
import bendavid.is.intershopping.entities.ShoppingList;

public class ShoppingListFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView rv = (RecyclerView) inflater.inflate(
                R.layout.fragment_list, container, false);
        setupRecyclerView(rv);
        return rv;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        // Get Shopping lists
        List<ShoppingList> shoppingLists = ShoppingList.listAll(ShoppingList.class);

        // LinearLayoutManager provides a similar implementation to a ListView
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(new ShoppingListRecyclerViewAdapter(getActivity(),
                shoppingLists));
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
                    .inflate(R.layout.list_item, parent, false);
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