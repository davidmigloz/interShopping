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
    private float totalPrice;
    private int numItems;
    private int numItemsBought;

    public ShoppingList() {
    }

    public ShoppingList(Date date, Supermarket supermarket) {
        this.date = date;
        this.supermarket = supermarket;
        totalPrice = 0;
        numItems = 0;
        numItemsBought = 0;
    }

    public Supermarket getSupermarket() {
        return supermarket;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public Date getDate() {
        return date;
    }

    public int getNumItems() {
        return numItems;
    }

    public int getNumItemsBought() {
        return numItemsBought;
    }

    public void updateItemsInfo(){
        // Get items of the shopping list
        List<ListItem> listItems = ListItem.find(ListItem.class,
                "shopping_list = ?", this.getId().toString());
        numItems = 0;
        numItemsBought = 0;
        totalPrice = 0;
        for(ListItem item : listItems){
            numItems++;
            if(item.isPurchased()){
                numItemsBought++;
                totalPrice += item.getTotalPrice();
            }
        }
    }

    @Override
    public int compareTo(ShoppingList another) {
        return this.getDate().compareTo(another.getDate());
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(date);
    }

    public Supermarket getSupermarked() {
        return supermarket;
    }
}