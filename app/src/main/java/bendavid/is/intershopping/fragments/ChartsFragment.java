package bendavid.is.intershopping.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bendavid.is.intershopping.R;
import bendavid.is.intershopping.entities.ShoppingList;

import static java.util.Calendar.*;

public class ChartsFragment extends Fragment {
    private View view;
    private Calendar minDate;
    private Calendar maxDate;
    private BarChart chart1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(
                R.layout.charts, container, false);
        prepareDatePickers();
        prepareChart();
        Button go = (Button) view.findViewById(R.id.go_button);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawData();
            }
        });
        return view;
    }

    /**
     * Prepare all the stuf related with the date pickers.
     */
    private void prepareDatePickers() {
        final TextView minDateText = (TextView) view.findViewById(R.id.datefield1);
        final TextView maxDateText = (TextView) view.findViewById(R.id.datefield2);

        // Set default dates (1 year)
        minDate = getInstance();
        minDate.add(DAY_OF_YEAR, -365);
        maxDate = getInstance();
        final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setTimeZone(minDate.getTimeZone());
        minDateText.setText(sdf.format(minDate.getTime()));
        maxDateText.setText(sdf.format(maxDate.getTime()));

        // Min date date picker
        final DatePickerDialog minDatePicker = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        try {
                            minDate.setTime((sdf.parse(day + "/" + month + 1 + "/" + year)));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        minDateText.setText(sdf.format(minDate.getTime()));
                    }
                }, minDate.get(YEAR), minDate.get(MONTH), minDate.get(DAY_OF_MONTH));
        minDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minDatePicker.show();
            }
        });
        // Max date date picker
        final DatePickerDialog maxDatePicker = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        try {
                            maxDate.setTime((sdf.parse(day + "/" + month + 1 + "/" + year)));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        maxDateText.setText(sdf.format(maxDate.getTime()));
                    }
                }, maxDate.get(YEAR), maxDate.get(MONTH), maxDate.get(DAY_OF_MONTH));
        maxDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                maxDatePicker.show();
            }
        });
    }

    /**
     * Define the design of the chart.
     */
    private void prepareChart() {
        // Chart
        chart1 = (BarChart) view.findViewById(R.id.chart1);
        chart1.setMaxVisibleValueCount(60);
        chart1.setDrawGridBackground(false);
        chart1.setDescription("");
        chart1.setNoDataTextDescription("Select the range of dates...");
        // X axis
        XAxis xAxis = chart1.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        // Y axis
        YAxisValueFormatter custom = new MyYAxisValueFormatter();
        YAxis leftAxis = chart1.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        YAxis rightAxis = chart1.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(8, false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        // Legend
        Legend l = chart1.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);
        // Initialize chart without data
        ArrayList<BarEntry> entries = new ArrayList<>();
        BarDataSet dataset = new BarDataSet(entries, "" + maxDate.get(YEAR));
        ArrayList<String> labels = new ArrayList<>();
        BarData data = new BarData(labels, dataset);
        chart1.setData(data);
    }

    /**
     * Get the selected data and show it.
     */
    private void drawData() {
        // Get shopping list in the dates range
        Select selectSLInRange = Select.from(ShoppingList.class)
                .whereOr(Condition.prop("date").gt(minDate.getTime().getTime()),
                        Condition.prop("date").eq(minDate.getTime().getTime()))
                .and(Condition.prop("date").lt(maxDate.getTime().getTime()));
        List<ShoppingList> shoppingLists = selectSLInRange.list();

        // Show number of shopping lists retrieved
        Toast.makeText(getActivity(),
                shoppingLists.size() + " shopping lists", Toast.LENGTH_SHORT).show();

        // If there's one or more, draw the chart
        // Each year is painted in one color. All the shopping list of one month must be
        // merge to get the total spend money amount of that month
        if (shoppingLists.size() > 0) {
            String[] mMonths = new String[]{
                    "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
            };
            Collections.sort(shoppingLists);
            Map<String, List<BarEntry>> entrysYear = new HashMap<>();
            List<String> labels = new ArrayList<>();
            // First item
            Date date = shoppingLists.get(0).getDate();
            Calendar cal = getInstance();
            cal.setTime(date);
            int year = cal.get(YEAR);
            int month = cal.get(MONTH);
            entrysYear.put(year + "", new ArrayList<BarEntry>());
            labels.add(mMonths[month % 12]);

            float totalPrice = 0;
            int newYear;
            int newMonth;
            for (int i = 0; i < shoppingLists.size(); i++) {
                Date d = shoppingLists.get(i).getDate();
                Calendar c = getInstance();
                c.setTime(d);
                newYear = c.get(YEAR);
                newMonth = c.get(MONTH);

                if (newMonth == month && newYear == year) {
                    totalPrice += shoppingLists.get(i).getTotalPrice();
                } else {
                    // If not same year -> add new one
                    if (newYear != year) {
                        entrysYear.put(newYear + "", new ArrayList<BarEntry>());
                    }
                    // If not same month -> save previous month
                    BarEntry be = new BarEntry(totalPrice, labels.size() - 1);
                    entrysYear.get(year + "").add(be);
                    // Add new month
                    labels.add(mMonths[newMonth % 12]);
                    totalPrice = shoppingLists.get(i).getTotalPrice();
                }
                year = newYear;
                month = newMonth;
            }
            // Save last one
            BarEntry be = new BarEntry(totalPrice, labels.size() - 1);
            entrysYear.get(year + "").add(be);

            BarData data = new BarData(labels);
            int i = 0;
            for (String name : entrysYear.keySet()) {
                BarDataSet dataset = new BarDataSet(entrysYear.get(name), name);
                dataset.setColor(ColorTemplate.COLORFUL_COLORS[i % ColorTemplate.COLORFUL_COLORS.length]);
                dataset.setBarSpacePercent(35f);
                data.addDataSet(dataset);
                i++;
            }
            data.setValueTextSize(10f);
            chart1.setData(data);
            chart1.notifyDataSetChanged(); // let the chart know it's data changed
            chart1.invalidate(); // refresh
            chart1.animateXY(1000, 1000);
        }
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
