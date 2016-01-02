package bendavid.is.intershopping.activities;

import android.content.Context;
import android.content.Intent;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import bendavid.is.intershopping.R;
import bendavid.is.intershopping.entities.ListItem;
import bendavid.is.intershopping.entities.ShoppingList;

public class ShoppingListDetailActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBar ab;

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
     * Show the data of the activity (items).
     */
    private void showShoppingListData() {
        // Get id of the shopping list clicked
        Long shoppingListID = (Long) getIntent().getExtras().getSerializable("shopping-list-id");
        // Get the shopping list
        ShoppingList shoppingList = ShoppingList.findById(ShoppingList.class, shoppingListID);
        // Set action bar title
        ab.setTitle(shoppingList.toString());
        // Show items
        RecyclerView rv = (RecyclerView)findViewById(R.id.recyclerview);
        setupRecyclerView(rv, shoppingListID);
    }

    /**
     * Inflate xml of the menu of the action bar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_settings, menu);
        return true;
    }

    /**
     * Trigger when an action of the action bar is selected.
     * @param item selected action
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, InterShoppingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.action_settings:
                Toast.makeText(this,
                        "Settings not available at the moment", Toast.LENGTH_SHORT).show();
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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
     * Show the items in the recyclerView.
     */
    private void setupRecyclerView(RecyclerView recyclerView, Long shoppingListID) {
        // Get items of the shopping list
        List<ListItem> listItems = ListItem.find(ListItem.class,
                "shopping_list = ?", shoppingListID.toString());

        // LinearLayoutManager provides a similar implementation to a ListView
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(new ItemListRecyclerViewAdapter(this,
                listItems));
    }

    public static class ItemListRecyclerViewAdapter
            extends RecyclerView.Adapter<ItemListRecyclerViewAdapter.ViewHolder> {

        private Context context;
        private List<ListItem> itemslist;

        public ItemListRecyclerViewAdapter(Context context, List<ListItem> items) {
            this.context = context;
            itemslist = items;
        }

        /**
         * Number of items in the list.
         */
        @Override
        public int getItemCount() {
            return itemslist.size();
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
            if(li.isPurchased()) {
                icon = ContextCompat.getDrawable(context, R.drawable.ic_check_circle);
            } else {
                icon = ContextCompat.getDrawable(context, R.drawable.ic_add_circle_outline);
            }
            viewHolder.icon.setImageDrawable(icon);
            // Item name
            viewHolder.item_name.setText(itemslist.get(position).getName());
            // Listener
            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                /**
                 * When you touch the item, it is marked as bought.
                 */
                @Override
                public void onClick(View v) {
                    // Change Icon
                    Drawable icon;
                    if(li.isPurchased()) {
                        li.changeStatus(false);
                        icon = ContextCompat.getDrawable(context, R.drawable.ic_add_circle_outline);
                    } else{
                        li.changeStatus(true);
                        icon = ContextCompat.getDrawable(context, R.drawable.ic_check_circle);
                    }
                    viewHolder.icon.setImageDrawable(icon);
                    li.save();
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
