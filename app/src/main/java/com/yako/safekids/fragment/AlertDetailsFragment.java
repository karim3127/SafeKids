package com.yako.safekids.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.safekids.android.R;
import com.yako.safekids.activity.NormalAlert;
import com.yako.safekids.model.AlertModel;
import com.yako.safekids.service.MonitorService;

import java.util.Date;

public class AlertDetailsFragment extends Fragment {

    private SharedPreferences prefs;
    private String langage;

    public AlertDetailsFragment() {
        // Required empty public constructor
    }

    public static AlertDetailsFragment newInstance() {
        AlertDetailsFragment fragment = new AlertDetailsFragment();
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
            layout =  inflater.inflate(R.layout.fragment_alert_details, container, false);
        } else {
            layout =  inflater.inflate(R.layout.fragment_alert_details_en, container, false);
        }
        AlertModel alertModel = ((NormalAlert) getActivity()).alertModel;

        final TextView tv_timer = (TextView) layout.findViewById(R.id.tv_timer);
        final TextView tv_temp = (TextView) layout.findViewById(R.id.tv_temp);
        final TextView tv_oxygen = (TextView) layout.findViewById(R.id.tv_oxygen);

        tv_temp.setText(MonitorService.temperature);
        tv_oxygen.setText(MonitorService.humidity);

        Date now = new Date();
        Date alertDate = alertModel.getAlertDate();

        long diff = now.getTime() - alertDate.getTime();

        long timeRest = (alertModel.getExtraTime() * 1000 * 60) - diff;

        CountDownTimer countDownTimer = new CountDownTimer(timeRest, 1000) {
            public void onTick(long millisUntilFinished) {

                long timeout = millisUntilFinished / 1000;
                long minDiz = (timeout / 60) / 10;
                long minUnit = (timeout / 60) % 10;
                long secDiz = (timeout % 60) / 10;
                long secUnit = (timeout % 60) % 10;

                String diz = ((minDiz == 0) ? "0" : minDiz + "");
                String unit = ((minUnit == 0) ? "0" : minUnit + "");

                tv_timer.setText(diz + unit + ":" + secDiz + secUnit);
            }

            public void onFinish() {

            }
        };
        countDownTimer.start();

        return layout;
    }

}
