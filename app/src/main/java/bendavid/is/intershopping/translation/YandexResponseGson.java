package bendavid.is.intershopping.translation;

import java.util.List;

/**
 * Created by Benni on 12.01.2016.
 */
public class YandexResponseGson {

    // Example JSON: D/json: {"code":200,"lang":"en-de","text":["Sie gehen Weg"]}

    private int code;
    private String lang;
    private List<String> text;

    public YandexResponseGson() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public List<String> getText() {
        return text;
    }

    public void setText(List<String> text) {
        this.text = text;
    }
}
