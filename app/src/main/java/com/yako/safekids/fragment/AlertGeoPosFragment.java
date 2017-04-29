package com.yako.safekids.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.safekids.android.R;
import com.yako.safekids.activity.NormalAlert;
import com.yako.safekids.model.AlertModel;

public class AlertGeoPosFragment extends Fragment {

    private SharedPreferences prefs;
    private String langage;

    public AlertGeoPosFragment() {
        // Required empty public constructor
    }

    public static AlertGeoPosFragment newInstance() {
        AlertGeoPosFragment fragment = new AlertGeoPosFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        prefs = getContext().getSharedPreferences("com.yako.safekids", Context.MODE_PRIVATE);
        langage = prefs.getString("Langage", "ar");

        View layout;
        if (langage.equals("ar")) {
            layout =  inflater.inflate(R.layout.fragment_alert_geo, container, false);
        } else {
            layout =  inflater.inflate(R.layout.fragment_alert_geo_en, container, false);
        }

        AlertModel alertModel = ((NormalAlert) getActivity()).alertModel;

        TextView tv_location = (TextView) layout.findViewById(R.id.tv_location);
        TextView tv_geo = (TextView) layout.findViewById(R.id.tv_geo);

        tv_location.setText(alertModel.getLocationName());

        tv_geo.setText(String.format("%.2f", alertModel.getLatitude()) + " ° " + String.format("%.2f", alertModel.getLongitude()));

        return layout;
    }

}
