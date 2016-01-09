package bendavid.is.intershopping.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.List;

import bendavid.is.intershopping.R;
import bendavid.is.intershopping.entities.AppConfig;
import bendavid.is.intershopping.entities.Languages;

/**
 * Create new shopping list.
 */
public class SettingsActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        // Action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Settings");

        // Left menu
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        // Show settings
        showSettings();
    }

    private void showSettings() {
        final Spinner selectLanguageSpinner = (Spinner) findViewById(R.id.selectLanguageSpinner);
        // Get Config - language
        final AppConfig appConfig = AppConfig.first(AppConfig.class);
        final Languages language = new Languages(appConfig.getLanguage());
//        final Languages language = new Languages("Polish");

        // Supermarket spinner
        ArrayAdapter<String> languageAdapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.list_item_simple, language.getLanguageList());
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectLanguageSpinner.setAdapter(languageAdapter);
        selectLanguageSpinner.setSelection(language.getLanguageList().indexOf(language.getLanguage()));

        // Select supermarket
        selectLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                language.setLanguage(language.getLanguageList().get(position));
                appConfig.setLanguage(language.getLanguageList().get(position));
                appConfig.save();
                selectLanguageSpinner.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
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
                Intent intent = new Intent(this, InterShoppingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
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
}