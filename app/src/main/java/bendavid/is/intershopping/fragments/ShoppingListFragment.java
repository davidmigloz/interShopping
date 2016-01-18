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

import java.util.Collections;
import java.util.List;

import bendavid.is.intershopping.R;
import bendavid.is.intershopping.activities.CreateSListActivity;
import bendavid.is.intershopping.activities.InterShoppingActivity;
import bendavid.is.intershopping.activities.ShoppingListDetailActivity;
import bendavid.is.intershopping.models.entities.ShoppingList;
import bendavid.is.intershopping.utils.ShoppingListClickListener;
import bendavid.is.intershopping.utils.SimpleItemTouchHelperCallback;
import bendavid.is.intershopping.views.adapters.ShoppingListsAdapter;

/**
 * Show a list with all the shopping lists stored in the data base.
 */
public class ShoppingListFragment extends Fragment implements ShoppingListClickListener {

    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Config view
        View view = inflater.inflate(R.layout.list_shopping_lists, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.list_shopping_lists);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        // Get Shopping lists
        List<ShoppingList> shoppingLists = ShoppingList.listAll(ShoppingList.class);
        Collections.sort(shoppingLists, Collections.reverseOrder());

        // Set adapter
        ShoppingListsAdapter adapter = new ShoppingListsAdapter(shoppingLists, this);
        recyclerView.setAdapter(adapter);

        // Swipe and drag
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter,
                ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.RIGHT);
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
                // Action: create shopping list
                Intent intent = new Intent(getActivity(), CreateSListActivity.class);
                startActivity(intent);
            }
        });
        mainActivity.fab.show();

    }

    @Override
    public void onClick(ShoppingList shoppingList) {
        Intent i = new Intent(getActivity(), ShoppingListDetailActivity.class);
        i.putExtra("shopping-list-id", shoppingList.getId());
        startActivityForResult(i, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == ShoppingListDetailActivity.DATA_CHANGED) {
            // Get Shopping lists
            List<ShoppingList> shoppingLists = ShoppingList.listAll(ShoppingList.class);
            Collections.sort(shoppingLists, Collections.reverseOrder());
            ShoppingListsAdapter adapter = (ShoppingListsAdapter) recyclerView.getAdapter();
            adapter.updateData(shoppingLists);
        }
    }
}