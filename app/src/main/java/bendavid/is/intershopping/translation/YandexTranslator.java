package bendavid.is.intershopping.translation;

import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import bendavid.is.intershopping.entities.AppConfig;

/**
 * Created by Benni on 12.01.2016.
 */
public class YandexTranslator {


    private String aimLanguageCode;

    public YandexTranslator(String aimLanguageCode) {
        if (aimLanguageCode.length() == 2)
            this.aimLanguageCode = aimLanguageCode.toLowerCase();
        else
            this.aimLanguageCode = "en";
    }

    public String translate(String textToTranslate) throws Exception {
        String json = "", translatedText = "";
        textToTranslate = URLEncoder.encode(textToTranslate, "utf-8");
        URL url = new URL("https://translate.yandex.net/api/v1.5/tr.json/translate?" +
                "key=" + "trnsl.1.1.20160107T140340Z.3a9a6b4696483460.12c87164fd285e07c39ff4fde63b5c98feef6dd4" +
                "&text=" + textToTranslate +
                "&lang=" + aimLanguageCode);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String nextLine;
            while ((nextLine = reader.readLine()) != null) {
                sb.append(nextLine);
            }
            json = sb.toString();
            Log.d("json", json);

            Gson gson = new Gson();
            YandexResponseGson yandexResponseGson = gson.fromJson(json, YandexResponseGson.class);
            Log.d("YandexResponseGson:", yandexResponseGson.getText().get(0));

            if (yandexResponseGson.getCode() == 200 || yandexResponseGson.getCode() == 201) {
                translatedText = yandexResponseGson.getText().get(0); // yandex
            } else
                throw new ConnectException();
        } finally {
            urlConnection.disconnect();
        }

        return translatedText;
    }
}
