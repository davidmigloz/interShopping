package bendavid.is.intershopping.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import bendavid.is.intershopping.R;
import bendavid.is.intershopping.entities.Supermarket;

public class SupermarketInfoFragment extends Fragment {
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = (View) inflater.inflate(
                R.layout.supermarket_detail_info, container, false);
        TextView notes = (TextView) view.findViewById(R.id.notes);
        TextView address = (TextView) view.findViewById(R.id.address);
        // Get id of the shopping list clicked
        Long supermarketID = getArguments().getLong("supermarket-id");
        // Get the supermarket
        Supermarket sm = Supermarket.findById(Supermarket.class, supermarketID);
        // Set text
        address.setText(sm.getAddress());
        notes.setText(sm.getNotes());
        return view;
    }
}
