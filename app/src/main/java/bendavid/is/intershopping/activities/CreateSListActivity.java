package bendavid.is.intershopping.activities;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import bendavid.is.intershopping.R;
import bendavid.is.intershopping.entities.ListItem;
import bendavid.is.intershopping.entities.ShoppingList;
import bendavid.is.intershopping.entities.Supermarket;

public class CreateSListActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private Date date;
    private Supermarket supermarket;
    private List<String> newItems;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat sdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_shopping_list);

        // Action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        ab.setDisplayHomeAsUpEnabled(true);

        // Left menu
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        // Show form to add shopping list
        showSListAddForm();
    }

    private void showSListAddForm() {
        ListView addLV = (ListView) findViewById(R.id.addedlist);
        final EditText dateInput = (EditText) findViewById(R.id.datefield);
        final Spinner supermarketInput = (Spinner) findViewById(R.id.supermarkedspinner);
        final EditText itemInput = (EditText) findViewById(R.id.itemfield);
        Button addBtn = (Button) findViewById(R.id.addbtn);

        // Get supermarkets
        final List<Supermarket> smList = Supermarket.listAll(Supermarket.class);
        List<String> smNamesList = new ArrayList<String>();
        for (Supermarket sm : smList) {
            smNamesList.add(sm.toString());
        }

        // Supermarket spinner
        ArrayAdapter<String> supermarkedAdapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.list_item_simple, smNamesList);
        supermarkedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        supermarketInput.setAdapter(supermarkedAdapter);

        // Select supermarket
        supermarketInput.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                supermarket = smList.get(position);
                supermarketInput.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // List of new items
        newItems = new ArrayList<String>();
        final ArrayAdapter<String> addAdapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.list_item_simple, newItems);
        addLV.setAdapter(addAdapter);

        // Add new item
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = itemInput.getText().toString();
                if (!text.equals("")) {
                    newItems.add(text);
                    addAdapter.notifyDataSetChanged();
                    itemInput.setText("");
                }
            }
        });

        sdf = new SimpleDateFormat("dd/mm/yyyy");
        sdf.setLenient(false);
        Calendar newCalendar = Calendar.getInstance();

        datePickerDialog = new DatePickerDialog(this, new OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                dateInput.setText(sdf.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        dateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }


        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get date

                try {
                    //if not valid, it will throw ParseException
                    date = sdf.parse(dateInput.getText().toString());
                } catch (ParseException e) {
                    Toast.makeText(getApplicationContext(),
                            "Date Format invalid! (dd/mm/yyyy)",
                            Toast.LENGTH_SHORT).show();
                }

                if (date != null && supermarket != null && newItems.size() > 0) {
                    ShoppingList newSL = new ShoppingList(date, supermarket);
                    newSL.save();
                    for (String item : newItems) {
                        new ListItem(item, newSL).save();
                    }
                    Toast.makeText(getApplicationContext(),
                            "New Shopping List saved.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CreateSListActivity.this, InterShoppingActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Fill the inputs", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
     *
     * @param item selected action
     * @return
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
}