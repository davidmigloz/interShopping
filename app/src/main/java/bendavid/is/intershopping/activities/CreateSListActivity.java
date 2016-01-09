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
    private boolean submitted;

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
        ab.setTitle("New Shopping List");

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
        submitted = false;
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
                text = text.replace(System.getProperty("line.separator"), " ");
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
        if (submitted) {
            Toast.makeText(getApplicationContext(),
                    "The automatic translation takes a while...", Toast.LENGTH_SHORT).show();
        } else {
            if (date != null && supermarket != null && newItems.size() > 0 && !submitted) {
                submitted = true;
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //we are connected to a network

                    class backgroundTranslation extends AsyncTask<Void, Void, Void> {
                        @Override
                        protected Void doInBackground(Void... params) {
                            ShoppingList newSL = new ShoppingList(date, supermarket);
                            newSL.save();
                            String translatedText;
                            for (String item : newItems) {
                                try {
                                    translatedText = translate(item);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    translatedText = e.toString();
                                }
                                new ListItem(item, translatedText, newSL).save();
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void result) {
                            Toast.makeText(getApplicationContext(),
                                    "...New Shopping List saved.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CreateSListActivity.this, InterShoppingActivity.class);
                            startActivity(intent);
                            super.onPostExecute(result);
                        }
                    }
                    Toast.makeText(getApplicationContext(),
                            "The automatic translation takes a while...", Toast.LENGTH_SHORT).show();
                    new backgroundTranslation().execute();

                } else {
                    Toast.makeText(getApplicationContext(),
                            "No Internet Connection! Save without translation!", Toast.LENGTH_SHORT).show();
                    ShoppingList newSL = new ShoppingList(date, supermarket);
                    newSL.save();
                    for (String item : newItems) {
                        new ListItem(item, newSL).save();
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(),
                        "Fill the inputs", Toast.LENGTH_SHORT).show();


            }
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

    public String translate(String text) throws Exception {
        JSONObject jobj;
        String json = "", translatedText = "";
        // Hello%20World
        text = text.replace(System.getProperty("line.separator"), " ");
        text = URLEncoder.encode(text, "utf-8");
        String lang = "de";
//        URL url = new URL("http://api.mymemory.translated.net/get?q=" + text + "!&langpair=Autodetect|de");
        URL url = new URL("https://translate.yandex.net/api/v1.5/tr.json/translate?" +
                "key=" + "trnsl.1.1.20160107T140340Z.3a9a6b4696483460.12c87164fd285e07c39ff4fde63b5c98feef6dd4" +
                "&text=" + text +
                "&lang=" + lang);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String nextLine = "";
            while ((nextLine = reader.readLine()) != null) {
                sb.append(nextLine);
            }

            json = sb.toString();

            try {
                jobj = new JSONObject(json);
                int code = Integer.parseInt(jobj.getString("code")); // yandex
//                int code = Integer.parseInt(jobj.getString("responseStatus")); // mymemory
                if (code == 200 || code == 201) {
                    translatedText = jobj.getString("text");// yandex
                    translatedText = translatedText.substring(2, translatedText.length()-2);
                    Log.d(translatedText, translatedText);
//                    JSONObject jobj2 = new JSONObject(jobj.getString("responseData")); // mymemory
//                    translatedText = jobj2.getString("translatedText");
//                    Log.d(translatedText, translatedText);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        Log.d("json", json);
        return translatedText;
    }
}

//        String translated = null;
//        try {
//            String query = URLEncoder.encode(text, "UTF-8");
//            String langpair = URLEncoder.encode(srcLanguage.getLanguage() + "|" + dstLanguage.getLanguage(), "UTF-8");
//            String url = "http://mymemory.translated.net/api/get?q=" + query + "&langpair=" + langpair;
//            URLConnection urlConnection = url.openConnection();
//
//            HttpClient hc = new DefaultHttpClient();
//            HttpGet hg = new HttpGet(url);
//            HttpResponse hr = hc.execute(hg);
//            if (hr.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//                JSONObject response = new JSONObject(EntityUtils.toString(hr.getEntity()));
//                translated = response.getJSONObject("responseData").getString("translatedText");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return translated;


//        // Set the Client ID / Client Secret once per JVM. It is set statically and applies to all services
//        Translate.setClientId("bdintershopping");
//        Translate.setClientSecret("5h5S7KCTSFF53C4AQQBOlfQseTaHLax3Dmx52u8ejJ8");
////        Translate.setClientSecret("5h5S7KCTSFF53d4AQQBOlfQseTaHLax3Dmx52u8ejJ8");
//
//        String translatedText = "";
//        // Change Language to variable from settings.
////        translatedText = Translate.execute(text, Language.AUTO_DETECT, Language.POLISH);
//        translatedText = Translate.execute(text, Language.ENGLISH, Language.GERMAN);
//        return translatedText;
