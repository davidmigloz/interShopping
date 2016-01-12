package bendavid.is.intershopping.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bendavid.is.intershopping.R;
import bendavid.is.intershopping.activities.SupermarketDetailActivity;
import bendavid.is.intershopping.entities.ListItem;

public class ComparisonsFragment extends Fragment {
    private CardView card;
    private RelativeLayout help;
    private TextView helpText;
    private TextView supermarketText;
    private TextView dateText;
    private TextView priceText;
    private CardView chartCard;
    private BarChart chart;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.comparisons, container, false);

        ImageView searchButton = (ImageView) view.findViewById(R.id.search_icon);
        final EditText input = (EditText) view.findViewById(R.id.product_input);
        help = (RelativeLayout) view.findViewById(R.id.help);
        helpText = (TextView) view.findViewById(R.id.help_text);
        card = (CardView) view.findViewById(R.id.result);
        supermarketText = (TextView) view.findViewById(R.id.supermarket_name);
        dateText = (TextView) view.findViewById(R.id.date);
        priceText = (TextView) view.findViewById(R.id.total_price);
        chartCard = (CardView) view.findViewById(R.id.chart_card);
        chart = (BarChart) view.findViewById(R.id.chart);

        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String product = String.valueOf(input.getText());
                if (product != null && !product.equals("")) {
                    searchProduct(product);
                }
            }
        });
        return view;
    }

    private void searchProduct(String product) {
        Select selectSLInRange = Select.from(ListItem.class)
                .where(Condition.prop("name").eq(product),
                        Condition.prop("price").notEq(0))
                .orderBy("price");
        List<ListItem> result = selectSLInRange.list();

        if (result.size() > 0) {
            // close keyboard
            View view = this.getView();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            final ListItem lowest = result.get(0);
            supermarketText.setText(lowest.getShoppingList().getSupermarked().toString());
            dateText.setText(lowest.getShoppingList().toString());
            NumberFormat nf = new DecimalFormat("#,###.##" + lowest.getPriceType().toString());
            priceText.setText(nf.format(lowest.getPrice()));
            help.setVisibility(View.GONE);
            card.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent i = new Intent(getContext(), SupermarketDetailActivity.class);
                    i.putExtra("supermarket-id", lowest.getShoppingList().getSupermarked().getId());
                    getContext().startActivity(i);
                }
            });
            card.setVisibility(View.VISIBLE);
            drawChart(result, product);
        } else {
            helpText.setText(R.string.no_results);
            card.setVisibility(View.GONE);
            chartCard.setVisibility(View.GONE);
            help.setVisibility(View.VISIBLE);
        }
    }

    private void drawChart(List<ListItem> result, String product) {
        Map<String, Float> supermarketsAveragePrice = new HashMap<>();
        Map<String, Integer> supermarketNumItems = new HashMap<>();
        for (ListItem li : result) {
            String sm = li.getShoppingList().getSupermarked().toString();
            Float price = li.getPrice();
            if (supermarketsAveragePrice.containsKey(sm)) {
                Float sum = supermarketsAveragePrice.get(sm) + price;
                supermarketsAveragePrice.put(sm, sum);
                Integer numItems = supermarketNumItems.get(sm) + 1;
                supermarketNumItems.put(sm, numItems);
            } else {
                supermarketsAveragePrice.put(sm, price);
                supermarketNumItems.put(sm, 1);
            }
        }
        // Calculate average
        for (String sm : supermarketsAveragePrice.keySet()) {
            Float avg = supermarketsAveragePrice.get(sm) / supermarketNumItems.get(sm);
            supermarketsAveragePrice.put(sm, avg);
        }
        // Draw data
        prepareChart();
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        int i = 0;
        for (String sm : supermarketsAveragePrice.keySet()) {
            entries.add(new BarEntry(supermarketsAveragePrice.get(sm), i++));
            labels.add(sm);
        }
        BarDataSet dataset = new BarDataSet(entries, "Average price of " + product);
        dataset.setColors(ColorTemplate.COLORFUL_COLORS);
        BarData data = new BarData(labels, dataset);
        chart.setData(data);
        chartCard.setVisibility(View.VISIBLE);
    }

    private void prepareChart() {
        // Chart
        chart.clear();
        chart.setMaxVisibleValueCount(10);
        chart.setDrawGridBackground(false);
        chart.setDescription("");
        // X axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        // Y axis
        YAxisValueFormatter custom = new MyYAxisValueFormatter();
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(8, false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        // Legend
        Legend l = chart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);
    }

    /**
     * Custom format for the Y axis.
     */
    private class MyYAxisValueFormatter implements YAxisValueFormatter {
        private DecimalFormat mFormat;

        public MyYAxisValueFormatter() {
            mFormat = new DecimalFormat("###,###,###,##0.0€");
        }

        @Override
        public String getFormattedValue(float value, YAxis yAxis) {
            return mFormat.format(value);
        }
    }
}
