package bendavid.is.intershopping.network;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.List;

import bendavid.is.intershopping.models.entities.AppConfig;
import bendavid.is.intershopping.models.entities.ListItem;
import bendavid.is.intershopping.models.translation.Languages;
import bendavid.is.intershopping.models.translation.YandexTranslator;

public class BackgroundTranslation extends AsyncTask<Void, Void, Void> {
    private Context context;
    private boolean translated;

    public BackgroundTranslation(Context context) {
        this.context = context;
        this.translated = false;
    }

    @Override
    protected Void doInBackground(Void... params) {
        List<ListItem> listItems = ListItem.listAll(ListItem.class);
        for (ListItem listItem : listItems) {
            if (!listItem.isTranslated()) {
                try {
                    Languages language = new Languages(AppConfig.first(AppConfig.class).getLanguage());
                    YandexTranslator translator = new YandexTranslator(language.getCode());
                    listItem.setTranslation(translator.translate(listItem.getName()));
                    listItem.save();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        translated = true;
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (translated){
            Toast.makeText(context, "Translation successful!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Translation error!", Toast.LENGTH_SHORT).show();
        }
    }
}
