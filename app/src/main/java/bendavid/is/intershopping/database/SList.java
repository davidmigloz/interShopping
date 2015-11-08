package bendavid.is.intershopping.database;

import com.orm.SugarRecord;

/**
 * Created by Benni on 07.11.2015.
 */
public class SList extends SugarRecord<SList> {
    //    int Slist_id;
    public String date, supermarked;
    double total_price;

    public SList() {
    }

    public SList(String date, String supermarked, double total_price) {
        this.date = date;
        this.supermarked = supermarked;
        this.total_price = total_price;
    }
}