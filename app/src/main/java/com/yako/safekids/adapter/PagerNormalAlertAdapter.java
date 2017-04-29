package com.yako.safekids.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.safekids.android.R;
import com.yako.safekids.fragment.AlertDetailsFragment;
import com.yako.safekids.fragment.AlertGeoPosFragment;

/**
 * Created by macbook on 14/05/15.
 */
public class PagerNormalAlertAdapter extends FragmentStatePagerAdapter {

    private final Context mContext;
    private final SharedPreferences prefs;
    private final String langage;
    String[] tabs = {"   الاحداثيات   ", "معلومات التنبيه"};
    String[] tabsEn ;//= {" Informations ", " coordinates "}

    public PagerNormalAlertAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
        tabsEn = new String[]{" "+mContext.getResources().getString(R.string.back_alerte_Informations) +" ", " " + mContext.getResources().getString(R.string.back_alerte_coordinates) + " "};
        prefs = mContext.getSharedPreferences("com.yako.safekids", Context.MODE_PRIVATE);
        langage = prefs.getString("Langage", "ar");
    }

    @Override
    public Fragment getItem(int position) {
        Fragment myFragment = new Fragment();
        if (langage.equals("ar")) {
            if(position == 0) {
                myFragment = AlertGeoPosFragment.newInstance();
            }else {
                myFragment = AlertDetailsFragment.newInstance();
            }
        } else {
            if(position == 0) {
                myFragment = AlertDetailsFragment.newInstance();
            }else {
                myFragment = AlertGeoPosFragment.newInstance();
            }
        }


        return myFragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (langage.equals("ar")) {
            return tabs[position];
        } else {
            return tabsEn[position];
        }
    }
}