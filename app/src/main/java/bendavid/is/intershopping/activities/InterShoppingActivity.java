package bendavid.is.intershopping.activities;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import bendavid.is.intershopping.R;
import bendavid.is.intershopping.database.InitializeDatabase;
import bendavid.is.intershopping.database.SItem;
import bendavid.is.intershopping.database.SList;
import bendavid.is.intershopping.database.SListItem;
import bendavid.is.intershopping.fragments.ShoppingListFragment;
import bendavid.is.intershopping.fragments.SupermarketListFragment;


public class InterShoppingActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inter_shopping);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),
                        "Action button pressed", Toast.LENGTH_SHORT).show();
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        // Adding some example entities if DB does not exist.
        if (SList.count(SList.class, null, null) == 0) {
            List<SItem> newSItems = new ArrayList<SItem>();
            newSItems.add(new SItem("Ketchup", "Ketchup"));
            newSItems.add(new SItem("Potatoes"));
            newSItems.add(new SItem("Garlic"));
            newSItems.add(new SItem("Bread"));
            newSItems.add(new SItem("Pizza"));
            newSItems.add(new SItem("Pasta"));
            newSItems.add(new SItem("Milk"));
            newSItems.add(new SItem("Turkey"));
            for (SItem sItem : newSItems) {
                sItem.save();
            }

            List<SList> newSLists = new ArrayList<SList>();
            newSLists.add(new SList("08/11/2015", "Biedronka", 40.3));
            newSLists.add(new SList("25/11/2015", "Auchan", 20.0));
            newSLists.add(new SList("23/11/2015", "Carrefour Express", 50.9));
            newSLists.add(new SList("21/11/2015", "Biedronka", 9.5));
            newSLists.add(new SList("17/11/2015", "Auchan", 88.8));
            newSLists.add(new SList("09/11/2015", "Carrefour Market", 74.4));
            newSLists.add(new SList("08/11/2015", "Carrefour Express", 40.3));
            newSLists.add(new SList("25/11/2015", "Auchan", 20.0));
            newSLists.add(new SList("23/11/2015", "Biedronka", 50.9));
            newSLists.add(new SList("21/11/2015", "Auchan", 9.5));
            newSLists.add(new SList("17/11/2015", "Carrefour Express", 88.8));
            newSLists.add(new SList("09/11/2015", "just any supermarked", 74.4));
            for (SList sList : newSLists) {
                sList.save();
            }

            List<SListItem> newSListItems = new ArrayList<SListItem>();
            newSListItems.add(new SListItem(7.7, "perPiece", newSLists.get(0), newSItems.get(0)));
            newSListItems.add(new SListItem(8.8, "perPiece", newSLists.get(1), newSItems.get(1)));
            newSListItems.add(new SListItem(4.4, "perPiece", newSLists.get(2), newSItems.get(2)));
            newSListItems.add(new SListItem(5.5, "perPiece", newSLists.get(3), newSItems.get(3)));
            for (SListItem newSListItem : newSListItems) {
                newSListItem.save();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new ShoppingListFragment(), "ShoppingListEntity");
        adapter.addFragment(new SupermarketListFragment(), "Supermarkets");
        viewPager.setAdapter(adapter);
    }

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