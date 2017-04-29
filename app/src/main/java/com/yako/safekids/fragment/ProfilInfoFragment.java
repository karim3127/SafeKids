package com.yako.safekids.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.safekids.android.R;
import com.yako.safekids.activity.ProfilSetting;

public class ProfilInfoFragment extends Fragment {

    private SharedPreferences prefs;

    EditText et_city, et_phone, et_car_type, et_car_color, et_car_num;
    private String langage;

    public ProfilInfoFragment() {
        // Required empty public constructor
    }

    public static ProfilInfoFragment newInstance() {
        ProfilInfoFragment fragment = new ProfilInfoFragment();
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
            layout =  inflater.inflate(R.layout.fragment_profil_info, container, false);
        } else {
            layout =  inflater.inflate(R.layout.fragment_profil_info_en, container, false);
        }

        ((ProfilSetting) getActivity()).profilInfoFragment = this;

        et_city = (EditText) layout.findViewById(R.id.et_city);
        et_city.setText(prefs.getString("city", ""));

        et_phone = (EditText) layout.findViewById(R.id.et_phone);
        et_phone.setText("+" + prefs.getString("codeCountry", "") + " " + prefs.getString("phoneNumber", ""));

        et_car_type = (EditText) layout.findViewById(R.id.et_car_type);
        et_car_type.setText(prefs.getString("carType", ""));

        et_car_color = (EditText) layout.findViewById(R.id.et_car_color);
        et_car_color.setText(prefs.getString("carColor", ""));

        et_car_num = (EditText) layout.findViewById(R.id.et_car_num);
        et_car_num.setText(prefs.getString("carNumber", ""));

        return layout;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            boolean userUpdate = data.getBooleanExtra("UserUpdate", false);
            if (userUpdate) {
                et_city.setText(prefs.getString("city", ""));
                et_phone.setText("+" + prefs.getString("codeCountry", "") + " " + prefs.getString("phoneNumber", ""));
                et_car_type.setText(prefs.getString("carType", ""));
                et_car_color.setText(prefs.getString("carColor", ""));
                et_car_num.setText(prefs.getString("carNumber", ""));
            }
        }
    }
}
