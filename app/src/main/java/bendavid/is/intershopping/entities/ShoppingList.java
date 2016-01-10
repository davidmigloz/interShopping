package bendavid.is.intershopping.entities;

import com.orm.SugarRecord;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Shopping list that contains the items to be bought in a supermarket.
 */
public class ShoppingList extends SugarRecord implements Serializable, Comparable<ShoppingList> {
    private Date date;
    private Supermarket supermarket;
    private long totalPrice;

    public ShoppingList() {
    }

    public ShoppingList(Date date, Supermarket supermarket) {
        this.date = date;
        this.supermarket = supermarket;
        totalPrice = 0L;
    }

    public Supermarket getSupermarket() {
        return supermarket;
    }

    public long getTotalPrice() {
        return totalPrice;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(date);
    }

    public void updateTotalPrice(){
        // Get items of the shopping list
        List<ListItem> listItems = ListItem.find(ListItem.class,
                "shopping_list = ?", this.getId().toString());
        long totalPrice = 0L;
        for(ListItem item : listItems){
            totalPrice += item.getPrice();
        }
        this.totalPrice = totalPrice;
    }

    @Override
    public int compareTo(ShoppingList another) {
        return this.getDate().compareTo(another.getDate());
    }
}