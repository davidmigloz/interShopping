package bendavid.is.intershopping.entities;

import com.orm.SugarRecord;


/**
 * Item of a shopping list.
 */
public class ListItem extends SugarRecord {
    private String name;
    private String translation;
    private long price;
    private PriceType priceType;
    private ShoppingList shoppingList;
    private boolean purchased;

    public ListItem() {
    }

    public ListItem(String name, ShoppingList shoppingList) {
        this.name = name;
        this.shoppingList = shoppingList;
        this.purchased = false;
    }

    public ListItem(String name, String translation, ShoppingList shoppingList) {
        this.name = name;
        this.translation = translation;
        this.shoppingList = shoppingList;
        this.purchased = false;
    }

    public ListItem(String name, long price, PriceType priceType, ShoppingList shoppingList) {
        this.name = name;
        this.price = price;
        this.priceType = priceType;
        this.shoppingList = shoppingList;
    }

    public enum PriceType {
        MONEY_UNIT, MONEY_KILO
    }

    public String getName() {
        return name;
    }

    public String getTranslation() {
        return translation;
    }

    public long getPrice() {
        return price;
    }

    public PriceType getPriceType() {
        return priceType;
    }

    public boolean isPurchased() {
        return this.purchased;
    }

    public void changeStatus(boolean purchased) {
        this.purchased = purchased;
    }
}