package bendavid.is.intershopping.entities;

import com.orm.SugarRecord;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Supermarket where the items of a list are going to be bought.
 */
public class Supermarket extends SugarRecord implements Serializable {
    private String name;
//    private String address;
    private double coord_lat;
    private double coord_lon;
    private String notes;

    public Supermarket() {
    }

    public Supermarket(String lName, String lAddress, String lNotes) {
        name = lName;
//        address = lAddress;
        notes = lNotes;
    }

    public Supermarket(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
