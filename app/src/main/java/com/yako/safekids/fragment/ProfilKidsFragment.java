package com.yako.safekids.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.safekids.android.R;
import com.yako.safekids.activity.ProfilSetting;
import com.yako.safekids.adapter.KidsItemAdapter;
import com.yako.safekids.model.Kids;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProfilKidsFragment extends Fragment {

    private KidsItemAdapter kidsAdapter;
    private SharedPreferences prefs;
    Gson gson = new Gson();
    private List<Kids> allKids = new ArrayList<>();
    private String langage;

    public ProfilKidsFragment() {
        // Required empty public constructor
    }

    public static ProfilKidsFragment newInstance() {
        ProfilKidsFragment fragment = new ProfilKidsFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout =  inflater.inflate(R.layout.fragment_profil_kids, container, false);

        ((ProfilSetting) getActivity()).profilKidsFragment = this;

        prefs = getContext().getSharedPreferences("com.yako.safekids", Context.MODE_PRIVATE);
        langage = prefs.getString("Langage", "ar");

        RecyclerView rv_kids = (RecyclerView) layout.findViewById(R.id.rv_kids);
        GridLayoutManager gll = new GridLayoutManager(getContext(), 3);
        rv_kids.setLayoutManager(gll);
        getListKids();
        kidsAdapter = new KidsItemAdapter(getContext(), allKids);
        rv_kids.setAdapter(kidsAdapter);

        ImageView img_no_kids = (ImageView) layout.findViewById(R.id.img_no_kids);
        if (allKids.size() == 0) {
            img_no_kids.setVisibility(View.VISIBLE);
        } else {
            img_no_kids.setVisibility(View.GONE);
        }
        return layout;
    }

    private void getListKids() {
        allKids.clear();
        String jsonKids = prefs.getString("MesKids", null);
        if (jsonKids != null) {
            Kids[] kidItems = gson.fromJson(jsonKids, Kids[].class);
            allKids = Arrays.asList(kidItems);
            allKids = new ArrayList(allKids);
        }

        if (langage.equals("ar")) {
            ((ProfilSetting) getActivity()).txtNbKids.setText("عدد الابناء: " + allKids.size());
        } else {
            ((ProfilSetting) getActivity()).txtNbKids.setText(getActivity().getResources().getString(R.string.number_kids)+" " + allKids.size());
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (kidsAdapter != null) {
            kidsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            boolean kidsUpdate = data.getBooleanExtra("KidsUpdate", false);
            boolean kidsDelete = data.getBooleanExtra("KidsDelete", false);
            if (kidsDelete) {
                getListKids();
                kidsAdapter.listItems.clear();
                kidsAdapter.listItems.addAll(allKids);

                kidsAdapter.notifyDataSetChanged();
            } else if (kidsUpdate) {
                getListKids();
                kidsAdapter.listItems.clear();
                kidsAdapter.listItems.addAll(allKids);

                kidsAdapter.notifyDataSetChanged();
            }
        }
    }
}
