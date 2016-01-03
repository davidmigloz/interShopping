package bendavid.is.intershopping.fragments;

import android.content.Context;
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
import android.widget.Toast;

import java.util.List;

import bendavid.is.intershopping.R;
import bendavid.is.intershopping.activities.InterShoppingActivity;
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
        recyclerView.setAdapter(new SupermarketRecyclerViewAdapter(getActivity(),
                supermarkets));
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
        InterShoppingActivity mainActivity = (InterShoppingActivity)getActivity();
        mainActivity.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Action: add new supermarket
                //Intent intent = new Intent(getActivity(), AddSupermarketActivity.class);
                //startActivity(intent);
                Toast.makeText(v.getContext(),
                        "Add Supermarket Pressed", Toast.LENGTH_SHORT).show();
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
            return supermarkets.size();
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
        public void onBindViewHolder(final ViewHolder viewHolder, int position) {
            // Supermarket's icon
            Drawable icon = ContextCompat.getDrawable(context, R.drawable.ic_calendar_24);
            viewHolder.icon.setImageDrawable(icon);
            // Supermarket name
            viewHolder.item_name.setText(supermarkets.get(position).toString());

            // Listener
            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(),
                            "SupermarketEntity Pressed", Toast.LENGTH_SHORT).show();
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
