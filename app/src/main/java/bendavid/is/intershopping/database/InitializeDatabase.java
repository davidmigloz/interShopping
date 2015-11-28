package bendavid.is.intershopping.database;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import bendavid.is.intershopping.entities.ListItem;
import bendavid.is.intershopping.entities.ShoppingList;
import bendavid.is.intershopping.entities.Supermarket;

public final class InitializeDatabase {
    private static InitializeDatabase instance = null;

    public InitializeDatabase() {
    }

    public static void initialize() {
        if(instance == null) {
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
        insertData();
    }

    public static void insertData() {
        Random random = new Random();
        String[] supermarketsNames = {"Auchan", "Biedronka", "Carrefour market", "Carrefor express"};
        String[] itemsNames = {"Ketchup", "Potatoes", "Garlic", "Bread", "Pizza", "Pasta",
                "Milk", "Turkey"};

        List<Supermarket> supermarketsList = new ArrayList<Supermarket>();
        List<ShoppingList> shoppingLists = new ArrayList<ShoppingList>();

        // Create supermarkets
        for (String name : supermarketsNames) {
            Supermarket s = new Supermarket(name);
            s.save();
            supermarketsList.add(s);
        }

        // Create shopping lists
        for (int i = 0; i < 10; i++) {
            // Get random supermarket
            Supermarket s = supermarketsList.get(random.nextInt(supermarketsList.size()));
            ShoppingList sl = new ShoppingList(new Date(random.nextInt()), s);
            sl.save();
            shoppingLists.add(sl);
        }

        // Add items to the shopping lists
        for (ShoppingList sl : shoppingLists) {
            int nItems = 5 + random.nextInt(10); // Minimum 5 items
            for (int i = 0; i < nItems; i++) {
                ListItem it = new ListItem(itemsNames[random.nextInt(itemsNames.length)], sl);
                it.save();
            }
        }
    }
}
