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
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import bendavid.is.intershopping.R;
import bendavid.is.intershopping.entities.ListItem;
import bendavid.is.intershopping.entities.ShoppingList;
import bendavid.is.intershopping.entities.Supermarket;

/**
 * Create new shopping list.
 */
public class CreateSListActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private Date date;
    private Supermarket supermarket;
    private List<String> newItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_shopping_list);

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

        // Show form to add shopping list
        showSListAddForm();
    }

    private void showSListAddForm() {
        final TextView dateInput = (TextView) findViewById(R.id.datefield);
        final Spinner supermarketInput = (Spinner) findViewById(R.id.supermarkedspinner);
        final EditText itemInput = (EditText) findViewById(R.id.itemfield);
        Button addBtn = (Button) findViewById(R.id.addbtn);
        ListView addLV = (ListView) findViewById(R.id.addedlist);

        // Get supermarkets
        final List<Supermarket> smList = Supermarket.listAll(Supermarket.class);
        List<String> smNamesList = new ArrayList<>();
        for (Supermarket sm : smList) {
            smNamesList.add(sm.toString());
        }

        // Supermarket spinner
        ArrayAdapter<String> supermarkedAdapter = new ArrayAdapter<>(getApplicationContext(),
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
        newItems = new ArrayList<>();
        final ArrayAdapter<String> addAdapter = new ArrayAdapter<>(getApplicationContext(),
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

        // Date
        Calendar newCalendar = Calendar.getInstance();
        final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        // Set today's date as default
        date = new Date();
        dateInput.setText(sdf.format(date));

        // Date picker
        final DatePickerDialog datePickerDialog = new DatePickerDialog(this, new OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                try {
                    date = sdf.parse(day + "/" + month + 1 + "/" + year);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                dateInput.setText(sdf.format(date));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        dateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });
    }

    private void addSList() {
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
                addSList();
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