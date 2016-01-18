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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bendavid.is.intershopping.R;
import bendavid.is.intershopping.activities.ShoppingListDetailActivity;
import bendavid.is.intershopping.models.entities.ShoppingList;
import bendavid.is.intershopping.utils.ShoppingListClickListener;
import bendavid.is.intershopping.utils.SimpleItemTouchHelperCallback;
import bendavid.is.intershopping.views.adapters.ShoppingListsAdapter;

/**
 * Show a list with all the shopping lists stored in the data base.
 */
public class SupermarketProductsFragment extends Fragment implements ShoppingListClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Config view
        View view = inflater.inflate(R.layout.list_shopping_lists, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list_shopping_lists);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        // Get ShoppingList-ID
        long supermarketID = getArguments().getLong("supermarket-id");
        // Get Shopping lists
        List<ShoppingList> shoppingLists = ShoppingList.listAll(ShoppingList.class);
        List<ShoppingList> filteredShoppingLists = new ArrayList<>();
        Collections.sort(shoppingLists, Collections.reverseOrder());

        for (ShoppingList shoppingList : shoppingLists) {
            if (shoppingList.getSupermarked().getId() == supermarketID) {
                filteredShoppingLists.add(shoppingList);
            }
        }

        // Set adapter
        ShoppingListsAdapter adapter = new ShoppingListsAdapter(filteredShoppingLists, this);
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
    public void onClick(ShoppingList shoppingList) {
        Intent i = new Intent(getActivity(), ShoppingListDetailActivity.class);
        i.putExtra("shopping-list-id", shoppingList.getId());
        startActivity(i);
    }
}