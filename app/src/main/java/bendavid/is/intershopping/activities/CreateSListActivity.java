package bendavid.is.intershopping.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bendavid.is.intershopping.R;
import bendavid.is.intershopping.entities.ListItem;
import bendavid.is.intershopping.entities.ShoppingList;
import bendavid.is.intershopping.entities.Supermarket;

/**
 * Created by Benni on 28.11.2015.
 */
public class CreateSListActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBar ab;
    private Button addBtn;
    private EditText addDate;
    private EditText addItem;

    private ListView addLV;
    private ArrayList<String> addItemList, supermarkedList;
    private ArrayAdapter<String> addAdapter, supermarkedAdapter;
    private Spinner supermarkedSpinner;
    private List<Supermarket> smList;
    private int pos;
    private boolean supermarkedSelected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_shopping_list);

        // Action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        ab.setDisplayHomeAsUpEnabled(true);

        // Left menu
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        // Show the data of the shopping list
        showSListAddForm();
    }

    private void showSListAddForm() {
        addBtn = (Button) findViewById(R.id.addbtn);
        addLV = (ListView) findViewById(R.id.addedlist);
        addDate = (EditText) findViewById(R.id.datefield);
        addItem = (EditText) findViewById(R.id.itemfield);
        supermarkedSpinner = (Spinner) findViewById(R.id.supermarkedspinner);
        supermarkedSelected = false;

        supermarkedList = new ArrayList<String>();
        smList = Supermarket.listAll(Supermarket.class);
        for (Supermarket sm : smList) {
            supermarkedList.add(sm.toString());
        }

        supermarkedAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.add_list_item, supermarkedList);
        supermarkedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        supermarkedSpinner.setAdapter(supermarkedAdapter);

        supermarkedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pos = position;
                supermarkedSpinner.setSelection(position);
                supermarkedSelected = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                supermarkedSelected = false;
            }
        });

        addItemList = new ArrayList<String>();
        addAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.add_list_item, addItemList);
        addLV.setAdapter(addAdapter);


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = addItem.getText().toString();
                if (!text.equals("")) {
                    addItemList.add(text);
                    addAdapter.notifyDataSetChanged();
                    addItem.setText("");
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date date = dateIsValid();
                if (supermarkedSelected && !addItemList.isEmpty() && date != null) {
                    ShoppingList nL = new ShoppingList(date, smList.get(pos));
                    nL.save();
                    for (String sml : supermarkedList) {
                        new ListItem(sml, nL).save();
                    }
                    Toast.makeText(getApplicationContext(), "New Shopping List saved.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Add at least one Item!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private Date dateIsValid() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
        sdf.setLenient(false);
        Date date;
        try {
            //if not valid, it will throw ParseException
            date = sdf.parse(addDate.getText().toString());
            System.out.println(date);
        } catch (ParseException e) {

            Toast.makeText(getApplicationContext(), "Date Format invalid! (dd/mm/yyyy): " + addDate.getText().toString(), Toast.LENGTH_SHORT).show();
            return null;
        }
        return date;
    }

    /**
     * @param navigationView
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