package bendavid.is.intershopping.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import bendavid.is.intershopping.R;
import bendavid.is.intershopping.models.entities.ListItem;
import bendavid.is.intershopping.models.entities.ShoppingList;
import bendavid.is.intershopping.utils.OnChangeShoppingListDetailsListener;
import bendavid.is.intershopping.utils.ShoppingListDetailClickListener;
import bendavid.is.intershopping.utils.SimpleItemTouchHelperCallback;
import bendavid.is.intershopping.views.adapters.ShoppingListDetailAdapter;

public class ShoppingListDetailActivity extends AppCompatActivity
        implements ShoppingListDetailClickListener, OnChangeShoppingListDetailsListener {
    public static final int DATA_NO_CHANGED = 0;
    public static final int DATA_CHANGED = 1;

    private DrawerLayout mDrawerLayout;

    private ShoppingList shoppingList;
    // Shopping list card
    private boolean changed;
    private TextView boughtItems;
    private TextView totalPrice;
    // Items list
    private RecyclerView recyclerView;
    // Modal
    private RelativeLayout modal;
    private EditText price;
    private Spinner priceType;
    private EditText quantity;
    private TextView type;
    private Button ok;
    private Button skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_list_detail);

        // Action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        ab.setDisplayHomeAsUpEnabled(true);

        // Left menu
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        // Get shopping list
        Long shoppingListID = (Long) getIntent().getExtras().getSerializable("shopping-list-id");
        shoppingList = ShoppingList.findById(ShoppingList.class, shoppingListID);
        ab.setTitle(shoppingList.toString());

        // Show shoopping list details
        TextView itemName = (TextView) findViewById(R.id.item_name);
        TextView supermarketName = (TextView) findViewById(R.id.supermarket_name);
        boughtItems = (TextView) findViewById(R.id.bought_items);
        totalPrice = (TextView) findViewById(R.id.total_price);
        itemName.setText(shoppingList.toString());
        supermarketName.setText(shoppingList.getSupermarket().toString());
        updateListItemsInfo();
        changed = false;

        // Show shopping list items list
        showListItems();

        // Prepare modal windows
        prepareModal();
    }

    /**
     * Update the number of items bought and the total price.
     */
    private void updateListItemsInfo() {
        shoppingList.updateItemsInfo();
        shoppingList.save();
        // Number of items bought
        boughtItems.setText(this.getString(R.string.num_bought_total,
                shoppingList.getNumItemsBought(), shoppingList.getNumItems()));
        // Total price
        NumberFormat nf = new DecimalFormat("#,###.##€");
        totalPrice.setText(nf.format(shoppingList.getTotalPrice()));
    }

    /**
     * Show a list with the items of the shopping list.
     */
    private void showListItems() {
        // Config recyclerView
        recyclerView = (RecyclerView) findViewById(R.id.items_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        // Get items of the shopping list
        List<ListItem> listItems = ListItem.find(ListItem.class,
                "shopping_list = ?", shoppingList.getId().toString());

        // Set adapter
        ShoppingListDetailAdapter adapter = new ShoppingListDetailAdapter(this, listItems, this, this);
        recyclerView.setAdapter(adapter);

        // Swipe and drag
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter,
                ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.RIGHT);
        ItemTouchHelper supermarketTouchHelper = new ItemTouchHelper(callback);
        supermarketTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void prepareModal() {
        modal = (RelativeLayout) findViewById(R.id.price_modal);
        price = (EditText) findViewById(R.id.price);
        priceType = (Spinner) findViewById(R.id.price_type);
        quantity = (EditText) findViewById(R.id.quantity);
        type = (TextView) findViewById(R.id.label3);
        ok = (Button) findViewById(R.id.ok_button);
        skip = (Button) findViewById(R.id.skip_button);

        // Fill spinner
        List<ListItem.PriceType> priceTypesList = new ArrayList<>();
        priceTypesList.add(ListItem.PriceType.MONEY_UNIT);
        priceTypesList.add(ListItem.PriceType.MONEY_KILO);
        priceTypesList.add(ListItem.PriceType.MONEY_GRAM);
        ArrayAdapter<ListItem.PriceType> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, priceTypesList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        priceType.setAdapter(dataAdapter);
        // Spinner listener
        priceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                ListItem.PriceType item = (ListItem.PriceType) arg0.getItemAtPosition(arg2);
                if (item != null) {
                    type.setText(item.toString().split("/")[1]); // Set units
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * Open a modal to enter the price and quantity.
     */
    private void openModal(final ListItem listItem) {
        // Buttons listeners
        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Get data
                float pri = Float.parseFloat(String.valueOf(price.getText()));
                ListItem.PriceType typ = (ListItem.PriceType) priceType.getSelectedItem();
                int qua = Integer.parseInt(String.valueOf(quantity.getText()));
                // Save it
                listItem.buy(pri, typ, qua);
                listItem.save();
                closeModal();
            }
        });
        skip.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Save it
                listItem.buy(0, ListItem.PriceType.MONEY_UNIT, 0);
                listItem.save();
                closeModal();
            }
        });
        modal.setVisibility(View.VISIBLE);
    }

    private void closeModal() {
        recyclerView.getAdapter().notifyDataSetChanged();
        updateListItemsInfo();
        modal.setVisibility(View.GONE);
    }

    @Override
    public void onClick(ListItem listItem) {
        changed = true;
        if (!listItem.isPurchased()) {
            openModal(listItem);
        } else {
            listItem.changeStatus(false);
            listItem.save();
            updateListItemsInfo();
        }
    }

    @Override
    public void onChangeShoppingListDetails() {
        updateListItemsInfo();
    }

    /**
     * Trigger when an action of the action bar is selected.
     *
     * @param item selected action
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        RelativeLayout modal = (RelativeLayout) findViewById(R.id.price_modal);
        if (modal.isShown()) {
            modal.setVisibility(View.GONE);
        } else {
            Intent intent = new Intent();
            if (changed) {
                setResult(DATA_CHANGED, intent);
            } else {
                setResult(DATA_NO_CHANGED, intent);
            }
            finish();
        }
    }

    /**
     * Setup left menu.
     */
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }
}
