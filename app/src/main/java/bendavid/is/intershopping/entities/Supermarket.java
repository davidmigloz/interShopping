package bendavid.is.intershopping.entities;

import com.orm.SugarRecord;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Supermarket where the items of a list are going to be bought.
 */
public class Supermarket extends SugarRecord {
    private String name;
    private double coord_lat;
    private double coord_lon;
    private String notes;

    public Supermarket() {
    }

    public Supermarket(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
