package bendavid.is.intershopping.database;

import com.orm.SugarRecord;

/**
 * Created by Benni on 07.11.2015.
 */
public class InitializeDatabase extends SugarRecord<InitializeDatabase> {
    public int isInitialized;

    public InitializeDatabase() {
    }

    public InitializeDatabase(int isInitialized) {
        this.isInitialized = isInitialized;
    }
}
