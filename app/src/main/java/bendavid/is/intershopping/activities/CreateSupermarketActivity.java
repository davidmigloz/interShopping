package bendavid.is.intershopping.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import bendavid.is.intershopping.R;
import bendavid.is.intershopping.entities.Supermarket;

/**
 * Create new shopping list.
 */
public class CreateSupermarketActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_supermarket);

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
        showSupermarketAddForm();
    }

    private void showSupermarketAddForm() {
        // nothing to show
    }

    private void addSupermarket() {
        final EditText newSmName = (EditText) findViewById(R.id.newname);
        final EditText newSmAdd = (EditText) findViewById(R.id.newaddress);
        final EditText newSmNote = (EditText) findViewById(R.id.newnote);

        if (isNotEmpty(newSmName) && isNotEmpty(newSmAdd) && isNotEmpty(newSmNote)) {

            Supermarket newSm = new Supermarket(newSmName.getText().toString(), newSmAdd.getText().toString(), newSmNote.getText().toString());
            newSm.save();

            Toast.makeText(getApplicationContext(),
                    "New Supermarket saved.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(CreateSupermarketActivity.this, InterShoppingActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(),
                    "Fill the inputs", Toast.LENGTH_SHORT).show();
        }
    }

    // is empty method checks if edittext is empty and promts with setError

    private boolean isNotEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() == 0) {
            etText.setError("This field cannot be empty!");
            return false;
        } else {
            return true;
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
                addSupermarket();
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