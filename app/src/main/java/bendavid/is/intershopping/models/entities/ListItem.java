package bendavid.is.intershopping.models.entities;

import com.orm.SugarRecord;


/**
 * Item of a shopping list.
 */
public class ListItem extends SugarRecord {
    private String name;
    private String translation;
    private float price;
    private PriceType priceType;
    private long quantity;
    private ShoppingList shoppingList;
    private boolean purchased, translated;

    public ListItem() {
    }

    public ListItem(String name, ShoppingList shoppingList) {
        this.name = name;
        this.shoppingList = shoppingList;
        this.purchased = false;
        this.translated = false;
        this.translation = "-";
    }

    public ListItem(String name, String translation, ShoppingList shoppingList) {
        this.name = name;
        this.translation = translation;
        this.shoppingList = shoppingList;
        this.purchased = false;
        this.translated = true;
    }

    public String getName() {
        return name;
    }

    public String getTranslation() {
        if (translation != null)
            return translation;
        else
            return "-";
    }

    public boolean isTranslated() {
        return translated;
    }

    public float getPrice() {
        return price;
    }

    public float getTotalPrice() {
        return price * quantity;
    }

    public PriceType getPriceType() {
        return priceType;
    }

    public ShoppingList getShoppingList() {
        return shoppingList;
    }

    public boolean isPurchased() {
        return purchased;
    }

    public void changeStatus(boolean purchased) {
        this.purchased = purchased;
    }

    public void buy(float price, PriceType priceType, long quantity) {
        this.price = price;
        this.priceType = priceType;
        this.quantity = quantity;
        this.purchased = true;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
        this.translated = true;
    }


    public enum PriceType {
        MONEY_UNIT("€/unit"),
        MONEY_KILO("€/kg"),
        MONEY_GRAM("€/g");

        private final String description;

        PriceType(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return this.description;
        }
    }
}