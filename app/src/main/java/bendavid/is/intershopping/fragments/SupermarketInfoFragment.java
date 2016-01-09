package bendavid.is.intershopping.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bendavid.is.intershopping.R;

public class SupermarketInfoFragment extends Fragment {
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = (View) inflater.inflate(
                R.layout.charts, container, false);

        return view;
    }
}
