package bendavid.is.intershopping.activities;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import bendavid.is.intershopping.R;
import bendavid.is.intershopping.entities.AppConfig;
import bendavid.is.intershopping.entities.Languages;
import bendavid.is.intershopping.entities.ListItem;
import bendavid.is.intershopping.entities.ShoppingList;
import bendavid.is.intershopping.entities.Supermarket;


/**
 * Create new shopping list.
 */
public class SettingsActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private List<String> newItems;

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
        ab.setTitle("New Shopping List");

        // Left menu
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        // Show form to add shopping list
        showSettings();
    }

    private void showSettings() {
        final Spinner selectLanguageSpinner = (Spinner) findViewById(R.id.selectLanguageSpinner);
        // Get Config - language

//        final Languages language = new Languages(AppConfig.first(AppConfig.class).getLanguage());
        final Languages language = new Languages("Polish");

        // Supermarket spinner
        ArrayAdapter<String> languageAdapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.list_item_simple, language.getLanguageList());
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectLanguageSpinner.setAdapter(languageAdapter);
//        selectLanguageSpinner.setSelection(language.getLanguageList().indexOf(language.getLanguage()));
        int pos = 0;
        for (String search : language.getLanguageList()) {
            if (search.equalsIgnoreCase(language.getLanguage())) {
                selectLanguageSpinner.setSelection(pos);
                Log.d("pos: ", String.valueOf(pos));
                break;
            }
            pos++;
        }
        Log.d(language.getLanguage(), String.valueOf(pos));


        // Select supermarket
        selectLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                language.setLanguage(language.getLanguageList().get(position));
                selectLanguageSpinner.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * Inflate xml of the menu of the action bar (done / discart).
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.done_discart, menu);
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
                Intent intent = new Intent(this, InterShoppingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.action_menu_done:
                // todo
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