package bendavid.is.intershopping.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import bendavid.is.intershopping.R;
import bendavid.is.intershopping.database.InitializeDatabase;
import bendavid.is.intershopping.entities.AppConfig;
import bendavid.is.intershopping.entities.ListItem;
import bendavid.is.intershopping.entities.ShoppingList;
import bendavid.is.intershopping.fragments.ShoppingListFragment;
import bendavid.is.intershopping.fragments.SupermarketListFragment;
import bendavid.is.intershopping.translation.Languages;
import bendavid.is.intershopping.translation.YandexTranslator;

public class InterShoppingActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    public FloatingActionButton fab;

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

    private void translateUntranslated() {


        // Check connection
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        final boolean[] translated = {true};
        if (networkInfo != null && networkInfo.isConnected()) {

            //we are connected to a network
            class backgroundTranslation extends AsyncTask<Void, Void, Void> {
                @Override
                protected Void doInBackground(Void... params) {
//                    List<ListItem> listItems = ListItem.find(ListItem.class, "translated = ?", "false");
                    List<ListItem> listItems = ListItem.listAll(ListItem.class);
                    for (ListItem listItem : listItems) {
                        if (!listItem.isTranslated()) {
                            try {
                                Languages language = new Languages(AppConfig.first(AppConfig.class).getLanguage());
                                YandexTranslator translator = new YandexTranslator(language.getCode());
                                listItem.setTranslation(translator.translate(listItem.getName()));
                                listItem.save();
                            } catch (Exception e) {
                                e.printStackTrace();
                                translated[0] = false;
                                Toast.makeText(getApplicationContext(), "Translation Error!", Toast.LENGTH_SHORT).show();
                                return null;
                            }
                        }
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    if (translated[0])
                        Toast.makeText(getApplicationContext(), "...Translation successful.", Toast.LENGTH_SHORT).show();
                    super.onPostExecute(result);
                }
            }
            Toast.makeText(getApplicationContext(),
                    "The automatic translation takes a while...", Toast.LENGTH_SHORT).show();
            new backgroundTranslation().execute();

        } else {
            Toast.makeText(getApplicationContext(),
                    "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
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

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
}