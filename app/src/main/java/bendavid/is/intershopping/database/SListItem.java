package bendavid.is.intershopping.database;

import com.orm.SugarRecord;

/**
 * Created by Benni on 07.11.2015.
 */
public class SListItem extends SugarRecord<SListItem> {
    //    int SListItem_id;
    double price;
    String priceType;
    SList sList;
    SItem sItem;

    public SListItem() {
    }

    public SListItem(double price, String priceType, SList sList, SItem sItem) {
        this.price = price;
        this.priceType = priceType;
        this.sList = sList;
        this.sItem = sItem;
    }
}