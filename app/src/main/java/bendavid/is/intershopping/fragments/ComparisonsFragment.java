package bendavid.is.intershopping.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import bendavid.is.intershopping.R;
import bendavid.is.intershopping.entities.ListItem;

public class ComparisonsFragment extends Fragment {
    private View view;
    private CardView card;
    private RelativeLayout noResults;
    private TextView supermarketText;
    private TextView dateText;
    private TextView priceText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(
                R.layout.comparisons, container, false);

        ImageView searchButton = (ImageView) view.findViewById(R.id.search_icon);
        final EditText input = (EditText) view.findViewById(R.id.product_input);
        noResults = (RelativeLayout) view.findViewById(R.id.no_results);
        card = (CardView) view.findViewById(R.id.result);
        supermarketText = (TextView) view.findViewById(R.id.supermarket_name);
        dateText = (TextView) view.findViewById(R.id.date);
        priceText = (TextView) view.findViewById(R.id.total_price);

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
            ListItem lowest = result.get(0);
            supermarketText.setText(lowest.getShoppingList().getSupermarked().toString());
            dateText.setText(lowest.getShoppingList().toString());
            NumberFormat nf = new DecimalFormat("#,###.##" + lowest.getPriceType().toString());
            priceText.setText(nf.format(lowest.getPrice()));
            noResults.setVisibility(View.GONE);
            card.setVisibility(View.VISIBLE);
            drawChart(result);
        } else {
            supermarketText.setText("No results");
            dateText.setText("");
            priceText.setText("");
            card.setVisibility(View.GONE);
            noResults.setVisibility(View.VISIBLE);
        }
    }

    private void drawChart(List<ListItem> result) {

    }


}
