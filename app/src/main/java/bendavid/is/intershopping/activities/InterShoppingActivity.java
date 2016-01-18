package bendavid.is.intershopping.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import bendavid.is.intershopping.R;
import bendavid.is.intershopping.fragments.ShoppingListFragment;
import bendavid.is.intershopping.fragments.SupermarketListFragment;
import bendavid.is.intershopping.network.BackgroundTranslation;
import bendavid.is.intershopping.utils.InitializeDatabase;
import bendavid.is.intershopping.views.adapters.TabsAdapter;

public class InterShoppingActivity extends AppCompatActivity {
    public FloatingActionButton fab;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inter_shopping);

        // Action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        // Left menu
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        // ViewPager
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        // Tabs
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        assert viewPager != null;
        tabLayout.setupWithViewPager(viewPager);

        // Floating action button
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();

        // Fill database with sample data
        InitializeDatabase.initialize();

        // Get
        Intent intent = getIntent();
        int tab = intent.getIntExtra("tab", 0);
        viewPager.setCurrentItem(tab);
    }

    /**
     * Inflate xml of the menu of the action bar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
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
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_stats:
                // Action: Stats
                Intent stats = new Intent(this, StatsActivity.class);
                startActivity(stats);
                break;
            case R.id.action_settings:
                // Action: Settings
                Intent settings = new Intent(this, SettingsActivity.class);
                startActivity(settings);
                break;
            case R.id.action_sync:
                // Action: Language Synchronization
                translateUntranslated();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        TabsAdapter adapter = new TabsAdapter(getSupportFragmentManager());
        adapter.addFragment(new ShoppingListFragment(), "ShoppingLists");
        adapter.addFragment(new SupermarketListFragment(), "Supermarkets");
        viewPager.setAdapter(adapter);
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

    private void translateUntranslated() {
        // Check connection
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            //w e are connected to a network
            Toast.makeText(getApplicationContext(),
                    "Translating...", Toast.LENGTH_SHORT).show();
            new BackgroundTranslation(this).execute();
        } else {
            Toast.makeText(getApplicationContext(),
                    "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }
    }
}