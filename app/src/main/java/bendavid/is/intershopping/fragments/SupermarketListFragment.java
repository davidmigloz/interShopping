package bendavid.is.intershopping.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import bendavid.is.intershopping.R;
import bendavid.is.intershopping.activities.CreateSupermarketActivity;
import bendavid.is.intershopping.activities.InterShoppingActivity;
import bendavid.is.intershopping.activities.SupermarketDetailActivity;
import bendavid.is.intershopping.entities.ShoppingList;
import bendavid.is.intershopping.entities.Supermarket;

/**
 * Show a list with all the supermarkets stored in the data base.
 */
public class SupermarketListFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView rv = (RecyclerView) inflater.inflate(
                R.layout.list_supermarkets, container, false);
        setupRecyclerView(rv);
        return rv;
    }

    /**
     * Get all the supermarkets and show them in the RecyclerView.
     */
    private void setupRecyclerView(RecyclerView recyclerView) {
        // Get Supermarkets
        List<Supermarket> supermarkets = Supermarket.listAll(Supermarket.class);
        // LinearLayoutManager provides a similar implementation to a ListView
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        final SupermarketRecyclerViewAdapter adapter = new SupermarketRecyclerViewAdapter(
                getActivity(), supermarkets);
        recyclerView.setAdapter(adapter);
        // For swipe and drag
        ItemTouchHelper mIth = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                        ItemTouchHelper.LEFT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        adapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                        return true;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        adapter.onItemDismiss(getActivity(), viewHolder.getAdapterPosition());
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
        // If it's visible, set the right action to the FloatingActionButton and show it
        InterShoppingActivity mainActivity = (InterShoppingActivity) getActivity();
        mainActivity.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Action: add new supermarket
                Intent intent = new Intent(getActivity(), CreateSupermarketActivity.class);
                startActivity(intent);
            }
        });
        mainActivity.fab.show();
    }

    public static class SupermarketRecyclerViewAdapter
            extends RecyclerView.Adapter<SupermarketRecyclerViewAdapter.ViewHolder> {
        private Context context;
        private List<Supermarket> supermarkets;

        public SupermarketRecyclerViewAdapter(Context context, List<Supermarket> items) {
            this.context = context;
            supermarkets = items;
        }

        /**
         * Number of supermarkets in the list.
         */
        @Override
        public int getItemCount() {
            return supermarkets == null ? 0 : supermarkets.size();
        }

        /**
         * Gets the ViewHolder used for the item at given position.
         */
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_supermarkets_item, parent, false);
            return new ViewHolder(view);
        }

        /**
         * It's called when views need to be created from given ViewHolder.
         */
        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
            // Supermarket letter
            viewHolder.letter.setText(supermarkets.get(position).toString().substring(0, 1).toUpperCase());
            // Supermarket name
            viewHolder.supermarketName.setText(supermarkets.get(position).toString());
            // Location
            viewHolder.location.setText(supermarkets.get(position).getAddress());
            // Listener
            viewHolder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Supermarket sm = supermarkets.get(position);
                    Intent i = new Intent(context, SupermarketDetailActivity.class);
                    i.putExtra("supermarket-id", sm.getId());
                    context.startActivity(i);
                }
            });
        }

        /**
         * When a supermarket is removed, delete it from database.
         */
        public void onItemDismiss(FragmentActivity activity, int position) {
            if (ShoppingList.find(ShoppingList.class, "Supermarket = ?", String.valueOf(supermarkets.get(position).getId())).isEmpty()) {
                supermarkets.get(position).delete();
                supermarkets.remove(position);
                notifyItemRemoved(position);
            } else {
                Toast.makeText(activity, "Cannot delete Supermarket - Delete first relating Shoppinglists.", Toast.LENGTH_SHORT).show();
            }
        }

        /**
         * When a supermarket is moved, save position.
         */
        public void onItemMove(int fromPosition, int toPosition) {
            Supermarket prev = supermarkets.remove(fromPosition);
            supermarkets.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
            notifyItemMoved(fromPosition, toPosition);
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private CardView card;
            public TextView letter;
            public TextView supermarketName;
            public TextView location;

            public ViewHolder(View view) {
                super(view);
                card = (CardView) view.findViewById(R.id.cv);
                letter = (TextView) view.findViewById(R.id.letter_sm);
                supermarketName = (TextView) view.findViewById(R.id.supermarket_name);
                location = (TextView) view.findViewById(R.id.location);
            }
        }
    }
}
