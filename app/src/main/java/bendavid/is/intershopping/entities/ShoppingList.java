package bendavid.is.intershopping.entities;

import com.orm.SugarRecord;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Shopping list that contains the items to be bought in a supermarket.
 */
public class ShoppingList extends SugarRecord implements Serializable {
    private Date date;
    private Supermarket supermarked;
    private long total_price;

    public ShoppingList() {
    }

    public ShoppingList(Date date, Supermarket supermarked) {
        this.date = date;
        this.supermarked = supermarked;
        total_price = 0L;
    }

    public Supermarket getSupermarked() {
        return supermarked;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(date);
    }
}