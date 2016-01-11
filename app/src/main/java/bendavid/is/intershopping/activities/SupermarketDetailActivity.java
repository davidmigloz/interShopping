package bendavid.is.intershopping.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import bendavid.is.intershopping.R;
import bendavid.is.intershopping.entities.Supermarket;
import bendavid.is.intershopping.fragments.SupermarketInfoFragment;
import bendavid.is.intershopping.fragments.SupermarketProductsFragment;

public class SupermarketDetailActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBar ab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats);

        // Action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ab = getSupportActionBar();
        assert ab != null;
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        ab.setDisplayHomeAsUpEnabled(true);
        //ab.setTitle("Stats");

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

        showSupermarkedInitial();

        // Tabs
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        assert viewPager != null;
        tabLayout.setupWithViewPager(viewPager);
    }

    private void showSupermarkedInitial() {
        // Get id of the shopping list clicked
        Long supermarketID = (Long) getIntent().getExtras().getSerializable("supermarket-id");
        // Get the shopping list
        Supermarket sm = Supermarket.findById(Supermarket.class, supermarketID);
        // Set action bar title
        ab.setTitle(sm.toString());
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

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
//        SupermarketInfoFragment smif = new SupermarketInfoFragment();
//        smif.setArguments(getIntent().getExtras());
//        getSupportFragmentManager().beginTransaction().add(
//                android.R.id.content, smif).commit();
//        adapter.addFragment(smif, "Info");
//        adapter.addFragment(new SupermarketInfoFragment(), "Info");
        Bundle args = new Bundle();
        args.putLong("supermarket-id", (long) getIntent().getExtras().getSerializable("supermarket-id"));
        SupermarketProductsFragment smppf = new SupermarketProductsFragment();
        SupermarketInfoFragment smif = new SupermarketInfoFragment();
        smif.setArguments(args);
        smppf.setArguments(args);
        long sid = (long) getIntent().getExtras().getSerializable("supermarket-id");
        Log.d("supermarket-id from", String.valueOf(sid));

        adapter.addFragment(smif, "Info");
        adapter.addFragment(smppf, "Shopping Lists");
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
