package bendavid.is.intershopping.database;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import bendavid.is.intershopping.entities.AppConfig;
import bendavid.is.intershopping.entities.ListItem;
import bendavid.is.intershopping.entities.ShoppingList;
import bendavid.is.intershopping.entities.Supermarket;

public final class InitializeDatabase {
    private static InitializeDatabase instance = null;

    public InitializeDatabase() {
    }

    public static void initialize() {
        if (instance == null) {
            instance = new InitializeDatabase();
            prepareSampleData();
        }
    }

    public static void prepareSampleData() {
        // Delete old data
        ListItem.deleteAll(ListItem.class);
        ShoppingList.deleteAll(ShoppingList.class);
        Supermarket.deleteAll(Supermarket.class);
        // Insert data
        insertData(5);
    }

    public static void insertData(int numSL) {
        Random random = new Random();
        String[] supermarketsNames = {"Auchan", "Biedronka", "Carrefour", "Żabka"};
        String[] locations = {"Górczewska 124", "Grzybowska 12/14", "Puławska 39",  "Kobielska 6"};
        String[] notes = {"Tel +48 22 334 86 44", "Tel +48 22 205 33 00",
                "Tel +48 22 584 90 50", "Tel +48 61 856 37 00"};

        String[] itemsNames = {"Ketchup", "Potatoes", "Garlic", "Bread", "Pizza", "Pasta",
                "Milk", "Turkey"};
        String[] translations = {"Ketchup", "Ziemniaki", "Czosnek", "Chleb", "Pizza", "Makaron",
                        "Mleko", "Turcja"};

        List<Supermarket> supermarketsList = new ArrayList<>();
        List<ShoppingList> shoppingLists = new ArrayList<>();
        List<Date> dates = new ArrayList<>();

        // Create Config
        AppConfig c = new AppConfig("Polish",true);
        c.save();

        // Create supermarkets
        for (int i = 0; i < supermarketsNames.length; i++) {
            Supermarket s = new Supermarket(supermarketsNames[i], locations[i], notes[i]);
            s.save();
            supermarketsList.add(s);
        }

        // Create random dates
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (int i = 0; i < numSL; i++) {
            int day = randInt(1, 28);
            int month = randInt(1, 12);
            int year = randInt(2013, 2015);
            Date date;
            try {
                date = sdf.parse(day + "/" + month + "/" + year);
            } catch (ParseException e) {
                e.printStackTrace();
                return;
            }
            dates.add(date);
        }

        // Create shopping lists
        for (int i = 0; i < numSL; i++) {
            // Get random supermarket
            Supermarket s = supermarketsList.get(random.nextInt(supermarketsList.size()));
            ShoppingList sl = new ShoppingList(dates.get(i), s);
            sl.save();
            shoppingLists.add(sl);
        }

        // Add items to the shopping lists
        for (ShoppingList sl : shoppingLists) {
            int nItems = randInt(3,6);
            // Items purchased
            for (int i = 0; i < nItems; i++) {
                int r = random.nextInt(itemsNames.length);
                ListItem it = new ListItem(itemsNames[r], translations[r], sl);
                it.buy(randInt(1, 10), ListItem.PriceType.MONEY_UNIT, randInt(1, 5));
                it.save();
            }
            // Items to purchase
            nItems = randInt(3,6);
            for (int i = 0; i < nItems; i++) {
                int r = random.nextInt(itemsNames.length);
                ListItem it = new ListItem(itemsNames[r], translations[r], sl);
                it.save();
            }
            sl.updateItemsInfo();
            sl.save();
        }
    }

    /**
     * Returns a pseudo-random number between min and max, inclusive.
     */
    private static int randInt(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }
}
