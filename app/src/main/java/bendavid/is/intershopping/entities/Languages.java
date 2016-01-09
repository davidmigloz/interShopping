package bendavid.is.intershopping.entities;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Benni on 07.01.2016.
 */
public class Languages {

    private static String allLanguages =// "Language\tCode\n" +
            "Albanian\tsq\n" +
                    "English\ten\n" +
                    "Arabic\tar\n" +
                    "Armenian\thy\n" +
                    "Azerbaijan\taz\n" +
                    "Afrikaans\taf\n" +
                    "Basque\teu\n" +
                    "Belarusian\tbe\n" +
                    "Bulgarian\tbg\n" +
                    "Bosnian\tbs\n" +
                    "Welsh\tcy\n" +
                    "Vietnamese\tvi\n" +
                    "Hungarian\thu\n" +
                    "Haitian (Creole)\tht\n" +
                    "Galician\tgl\n" +
                    "Dutch\tnl\n" +
                    "Greek\tel\n" +
                    "Georgian\tka\n" +
                    "Danish\tda\n" +
                    "Yiddish\the\n" +
                    "Indonesian\tid\n" +
                    "Irish\tga\n" +
                    "Italian\tit\n" +
                    "Icelandic\tis\n" +
                    "Spanish\tes\n" +
                    "Kazakh\tkk\n" +
                    "Catalan\tca\n" +
                    "Kyrgyz\tky\n" +
                    "Chinese\tzh\n" +
                    "Korean\tko\n" +
                    "Latin\tla\n" +
                    "Latvian\tlv\n" +
                    "Lithuanian\tlt\n" +
                    "Malagasy\tmg\n" +
                    "Malay\tms\n" +
                    "Maltese\tmt\n" +
                    "Macedonian\tmk\n" +
                    "Mongolian\tmn\n" +
                    "German\tde\n" +
                    "Norwegian\tno\n" +
                    "Persian\tfa\n" +
                    "Polish\tpl\n" +
                    "Portuguese\tpt\n" +
                    "Romanian\tro\n" +
                    "Russian\tru\n" +
                    "Serbian\tsr\n" +
                    "Slovakian\tsk\n" +
                    "Slovenian\tsl\n" +
                    "Swahili\tsw\n" +
                    "Tajik\ttg\n" +
                    "Thai\tth\n" +
                    "Tagalog\ttl\n" +
                    "Tatar\ttt\n" +
                    "Turkish\ttr\n" +
                    "Uzbek\tuz\n" +
                    "Ukrainian\tuk\n" +
                    "Finish\tfi\n" +
                    "French\tfr\n" +
                    "Croatian\thr\n" +
                    "Czech\tcs\n" +
                    "Swedish\tsv\n" +
                    "Estonian\tet\n" +
                    "Japanese\tja";
    private String language, code;
    private String[] languageList;

    public Languages(String language) {
        languageList = allLanguages.split("\n");
        setLanguage(language);
    }

    public void setLanguage(String language) {
        for (String l : languageList) {
            String[] cl = l.split("\t");
            if (cl[0].equals(language)) {
                this.language = cl[0];
                code = cl[1];
                Log.d(cl[0], cl[1]);
                return;
            }
        }
        // default
        this.language = "English";
        code = "en";
    }

    public String getLanguage() {
        return language;
    }

    public String getCode() {
        return code;
    }

    public ArrayList<String> getLanguageList() {
        ArrayList<String> onlyLanguages = new ArrayList<String>();
        for (String l : languageList) {
            String[] cl = l.split("\t");
            onlyLanguages.add(cl[0]);
        }
        return onlyLanguages;
    }
}
