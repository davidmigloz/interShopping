package bendavid.is.intershopping.entities;

import com.orm.SugarRecord;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Item of a shopping list.
 */
public class ListItem extends SugarRecord {
    private String name;
    private String translation;
    private long price;
    private PriceType priceType;
    private ShoppingList shoppingList;

    public ListItem() {
    }

    public ListItem(String name, ShoppingList shoppingList) {
        this.name = name;
        this.shoppingList = shoppingList;
    }

    private enum PriceType {
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
}