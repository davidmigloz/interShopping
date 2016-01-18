package bendavid.is.intershopping.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import bendavid.is.intershopping.R;
import bendavid.is.intershopping.activities.CreateSupermarketActivity;
import bendavid.is.intershopping.activities.InterShoppingActivity;
import bendavid.is.intershopping.activities.SupermarketDetailActivity;
import bendavid.is.intershopping.models.entities.Supermarket;
import bendavid.is.intershopping.utils.SimpleItemTouchHelperCallback;
import bendavid.is.intershopping.utils.SupermarketClickListener;
import bendavid.is.intershopping.views.adapters.SupermarketsApdater;

/**
 * Show a list with all the supermarkets stored in the data base.
 */
public class SupermarketListFragment extends Fragment implements SupermarketClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Config view
        View view = inflater.inflate(R.layout.list_supermarkets, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list_supermarkets);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        // Get Supermarkets
        List<Supermarket> supermarkets = Supermarket.listAll(Supermarket.class);

        // Set adapter
        SupermarketsApdater adapter = new SupermarketsApdater(getActivity(), supermarkets, this);
        recyclerView.setAdapter(adapter);

        // Swipe and drag
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter,
                ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT);
        ItemTouchHelper supermarketTouchHelper = new ItemTouchHelper(callback);
        supermarketTouchHelper.attachToRecyclerView(recyclerView);

        return view;
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

    @Override
    public void onClick(Supermarket supermarket) {
        Intent intent = new Intent(getActivity(), SupermarketDetailActivity.class);
        intent.putExtra("supermarket-id", supermarket.getId());
        getActivity().startActivity(intent);
    }
}
