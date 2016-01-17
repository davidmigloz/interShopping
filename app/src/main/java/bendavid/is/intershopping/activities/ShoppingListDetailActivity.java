package bendavid.is.intershopping.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import bendavid.is.intershopping.R;
import bendavid.is.intershopping.entities.ListItem;
import bendavid.is.intershopping.entities.ShoppingList;

public class ShoppingListDetailActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBar ab;
    private ShoppingList shoppingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shoopping_list_detail);

        // Action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ab = getSupportActionBar();
        assert ab != null;
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        ab.setDisplayHomeAsUpEnabled(true);

        // Left menu
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        // Show the data of the shopping list
        showShoppingListData();
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
            this.finish();
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

    /**
     * Show the data info of the shopping list and its items.
     */
    private void showShoppingListData() {
        // Get id of the shopping list clicked
        Long shoppingListID = (Long) getIntent().getExtras().getSerializable("shopping-list-id");
        // Get the shopping list
        shoppingList = ShoppingList.findById(ShoppingList.class, shoppingListID);
        // Set action bar title
        ab.setTitle(shoppingList.toString());
        // Show shopping list details
        showSLDetails();
        // Show items
        RecyclerView rv = (RecyclerView) findViewById(R.id.recyclerview);
        setupRecyclerView(rv, shoppingListID);
    }

    /**
     * Show a card with the details of the shopping list.
     */
    private void showSLDetails() {
        ImageView icon = (ImageView) findViewById(R.id.item_icon);
        TextView itemName = (TextView) findViewById(R.id.item_name);
        TextView supermarketName = (TextView) findViewById(R.id.supermarket_name);
        TextView boughtItems = (TextView) findViewById(R.id.bought_items);
        TextView totalPrice = (TextView) findViewById(R.id.total_price);
        // ShoppingLists's icon
        Drawable chart_icon = ContextCompat.getDrawable(this, R.drawable.ic_shopping_cart);
        icon.setImageDrawable(chart_icon);
        // ShoppingLists name
        itemName.setText(shoppingList.toString());
        // Supermarket
        supermarketName.setText(shoppingList.getSupermarket().toString());
        // Number of items bought
        boughtItems.setText(this.getString(R.string.num_bought_total,
                shoppingList.getNumItemsBought(), shoppingList.getNumItems()));
        // Total price
        NumberFormat nf = new DecimalFormat("#,###.##€");
        totalPrice.setText(nf.format(shoppingList.getTotalPrice()));
    }

    /**
     * Show the items in the recyclerView.
     */
    private void setupRecyclerView(RecyclerView recyclerView, Long shoppingListID) {
        // Get items of the shopping list
        List<ListItem> listItems = ListItem.find(ListItem.class,
                "shopping_list = ?", shoppingListID.toString());
        // LinearLayoutManager provides a similar implementation to a ListView
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        final ItemListRecyclerViewAdapter adapter = new ItemListRecyclerViewAdapter(this,
                listItems, shoppingList, (RelativeLayout) findViewById(R.id.price_modal),
                (TextView) findViewById(R.id.bought_items), (TextView) findViewById(R.id.total_price));
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

    public static class ItemListRecyclerViewAdapter
            extends RecyclerView.Adapter<ItemListRecyclerViewAdapter.ViewHolder> {
        private Context context;
        private List<ListItem> itemslist;
        private ShoppingList sl;
        private RelativeLayout modal = null;
        private TextView boughtItems;
        private TextView totalPrice;


        public ItemListRecyclerViewAdapter(Context context, List<ListItem> items,
                                           ShoppingList sl, RelativeLayout modal,
                                           TextView boughtItems, TextView totalPrice) {
            this.context = context;
            this.itemslist = items;
            this.sl = sl;
            this.modal = modal;
            this.boughtItems = boughtItems;
            this.totalPrice = totalPrice;
        }

        /**
         * Number of items in the list.
         */
        @Override
        public int getItemCount() {
            return itemslist == null ? 0 : itemslist.size();
        }

        /**
         * Gets the ViewHolder used for the item at given position.
         */
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_shopping_list_detail, parent, false);
            return new ViewHolder(view);
        }

        /**
         * It's called when views need to be created from given ViewHolder.
         */
        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
            // Item
            final ListItem li = itemslist.get(position);
            // Icon
            Drawable icon;
            if (li.isPurchased()) {
                // If it's already bought
                icon = ContextCompat.getDrawable(context, R.drawable.ic_check_box_1);
                viewHolder.itemName.setPaintFlags(viewHolder.itemName.getPaintFlags()
                        | Paint.STRIKE_THRU_TEXT_FLAG); // Cross text
                viewHolder.cartIcon.setVisibility(View.GONE);
                viewHolder.itemPrice.setText(context.getString(R.string.price_eur, li.getTotalPrice()));
                viewHolder.itemPrice.setVisibility(View.VISIBLE); // Show price
            } else {
                // If it's not bought
                icon = ContextCompat.getDrawable(context, R.drawable.ic_check_box_0);
            }
            viewHolder.checkIcon.setImageDrawable(icon);
            // Item name
            viewHolder.itemName.setText(context.getString(R.string.name_translation,
                    itemslist.get(position).getName(), itemslist.get(position).getTranslation()));
            // Listener
            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                /**
                 * When you touch the item, it is marked as bought.
                 */
                @Override
                public void onClick(View v) {
                    if (li.isPurchased()) {
                        // Reset it
                        li.changeStatus(false);
                        Drawable icon = ContextCompat.getDrawable(context, R.drawable.ic_check_box_0);
                        viewHolder.checkIcon.setImageDrawable(icon);
                        viewHolder.itemName.setPaintFlags(viewHolder.itemName.getPaintFlags()
                                & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        viewHolder.itemPrice.setVisibility(View.GONE); // Hide price
                        viewHolder.cartIcon.setVisibility(View.VISIBLE); // Show cart
                        li.save();
                        updateSLInfo();
                    } else {
                        // Purchase it
                        openModalPrice(li);
                    }
                }

                /**
                 * Open a modal to enter the price and quantity.
                 */
                private void openModalPrice(final ListItem li) {
                    final EditText price = (EditText) modal.findViewById(R.id.price);
                    final Spinner priceType = (Spinner) modal.findViewById(R.id.price_type);
                    final EditText quantity = (EditText) modal.findViewById(R.id.quantity);
                    final TextView type = (TextView) modal.findViewById(R.id.label3);
                    Button ok = (Button) modal.findViewById(R.id.ok_button);
                    Button skip = (Button) modal.findViewById(R.id.skip_button);

                    // Fill spinner
                    List<ListItem.PriceType> priceTypesList = new ArrayList<>();
                    priceTypesList.add(ListItem.PriceType.MONEY_UNIT);
                    priceTypesList.add(ListItem.PriceType.MONEY_KILO);
                    priceTypesList.add(ListItem.PriceType.MONEY_GRAM);
                    ArrayAdapter<ListItem.PriceType> dataAdapter = new ArrayAdapter<>(context,
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
                    //Buttons listeners
                    ok.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            // Get data
                            float pri = Float.parseFloat(String.valueOf(price.getText()));
                            ListItem.PriceType typ = (ListItem.PriceType) priceType.getSelectedItem();
                            int qua = Integer.parseInt(String.valueOf(quantity.getText()));
                            // Save it
                            li.buy(pri, typ, qua);
                            updateView();
                            modal.setVisibility(View.GONE);
                        }
                    });
                    skip.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            // Save it
                            li.buy(0, ListItem.PriceType.MONEY_UNIT, 0);
                            updateView();
                            modal.setVisibility(View.GONE);
                        }
                    });
                    modal.setVisibility(View.VISIBLE);
                }

                /**
                 * Change the icon to bought and update the shopping list info.
                 */
                private void updateView() {
                    Drawable checkIcon = ContextCompat.getDrawable(context, R.drawable.ic_check_box_1);
                    viewHolder.checkIcon.setImageDrawable(checkIcon);
                    viewHolder.itemName.setPaintFlags(viewHolder.itemName.getPaintFlags() |
                            Paint.STRIKE_THRU_TEXT_FLAG);
                    viewHolder.cartIcon.setVisibility(View.GONE);
                    viewHolder.itemPrice.setText(context.getString(R.string.price_eur, li.getTotalPrice()));
                    viewHolder.itemPrice.setVisibility(View.VISIBLE);
                    li.save();
                    updateSLInfo();
                }
            });
        }

        private void updateSLInfo() {
            sl.updateItemsInfo();
            sl.save();
            // Number of items bought
            boughtItems.setText(context.getString(R.string.num_bought_total,
                    sl.getNumItemsBought(), sl.getNumItems()));
            // Total price
            NumberFormat nf = new DecimalFormat("#,###.##€");
            totalPrice.setText(nf.format(sl.getTotalPrice()));
        }

        /**
         * When an item is removed, delete it from database.
         */
        public void onItemDismiss(int position) {
            itemslist.get(position).delete();
            itemslist.remove(position);
            notifyItemRemoved(position);
            updateSLInfo();
        }

        /**
         * When an item is moved, save position.
         */
        public void onItemMove(int fromPosition, int toPosition) {
            ListItem prev = itemslist.remove(fromPosition);
            itemslist.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
            notifyItemMoved(fromPosition, toPosition);
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public ImageView checkIcon;
            public TextView itemName;
            public ImageView cartIcon;
            public TextView itemPrice;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                checkIcon = (ImageView) view.findViewById(R.id.item_icon);
                itemName = (TextView) view.findViewById(R.id.item_name);
                cartIcon = (ImageView) view.findViewById(R.id.add_cart);
                itemPrice = (TextView) view.findViewById(R.id.item_price);
            }
        }
    }
}
