package bendavid.is.intershopping.models.entities;

import com.orm.SugarRecord;

/**
 * Item of a shopping list.
 */
public class AppConfig extends SugarRecord {
    private String language;
    private boolean chart;

    public AppConfig() {
    }

    public AppConfig(String language, boolean chart) {
        this.language = language;
        this.chart = chart;
    }

    public AppConfig(String language) {
        this.language = language;
        this.chart = true;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isChart() {
        return chart;
    }

    public void setChart(boolean chart) {
        this.chart = chart;
    }
}