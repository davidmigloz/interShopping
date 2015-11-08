package bendavid.is.intershopping.database;

import com.orm.SugarRecord;

/**
 * Created by Benni on 07.11.2015.
 */
public class SItem extends SugarRecord<SItem> {
    //    int item_id;
    String name, translation;

    public SItem() {
    }

    public SItem(String name) {
        this.name = name;
        this.translation = null;
    }

    public SItem(String name, String translation) {
        this.name = name;
        this.translation = translation;
    }
}