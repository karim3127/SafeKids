package com.yako.safekids.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.safekids.android.R;
import com.yako.safekids.adapter.PagerProfilAdapter;
import com.yako.safekids.fragment.ProfilInfoFragment;
import com.yako.safekids.fragment.ProfilKidsFragment;
import com.yako.safekids.view.tabs.SlidingTabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProfilSetting extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.pager)
    ViewPager mPager;

    @BindView(R.id.tabs)
    SlidingTabLayout mTabs;

    @BindView(R.id.imgUser)
    CircleImageView imgUser;

    @BindView(R.id.txtName)
    TextView txtName;

    @BindView(R.id.imgEdit)
    ImageView imgEdit;

    @BindView(R.id.txtNbKids)
    public TextView txtNbKids;

    public ProfilInfoFragment profilInfoFragment;
    public ProfilKidsFragment profilKidsFragment;

    private SharedPreferences prefs;

    DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .showImageOnLoading(R.drawable.ic_white)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();
    private String langage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = this.getSharedPreferences("com.yako.safekids", Context.MODE_PRIVATE);
        langage = prefs.getString("Langage", "ar");

        if (langage.equals("ar")) {
            setContentView(R.layout.activity_profil_setting);
        } else if(langage.equals("he")) {
            setContentView(R.layout.activity_profil_setting_he);
        }else {
            setContentView(R.layout.activity_profil_setting_en);
        }

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        if (langage.equals("ar") || langage.equals("he")) {
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        } else {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mPager.setOffscreenPageLimit(2);
        mTabs.setDistributeEvenly(true);

        mPager.setAdapter(new PagerProfilAdapter(getSupportFragmentManager(), this));
        if (langage.equals("ar")) {
            mPager.setCurrentItem(1);
        } else {
            mPager.setCurrentItem(0);
        }
        mTabs.setViewPager(mPager);

        if (prefs.getString("UserPhoto", "").equals("")) {
            imgUser.setImageResource(R.drawable.ic_avatar);
        } else {
            ImageLoader.getInstance().displayImage(prefs.getString("UserPhoto", ""), imgUser, options);
        }
        txtName.setText(prefs.getString("name", ""));
    }

    @OnClick(R.id.imgEdit)
    public void editUser() {
        startActivityForResult(new Intent(this, ProfilUpdate.class), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            boolean userUpdate = data.getBooleanExtra("UserUpdate", false);
            boolean kidsUpdate = data.getBooleanExtra("KidsUpdate", false);
            boolean kidsDelete = data.getBooleanExtra("KidsDelete", false);
            if (kidsDelete) {
                profilKidsFragment.onActivityResult(requestCode, resultCode, data);
            } else if (kidsUpdate) {
                profilKidsFragment.onActivityResult(requestCode, resultCode, data);
            }
            if (userUpdate) {

                if (prefs.getString("UserPhoto", "").equals("")) {
                    imgUser.setImageResource(R.drawable.ic_avatar);
                } else {
                    ImageLoader.getInstance().displayImage(prefs.getString("UserPhoto", ""), imgUser, options);
                }
                txtName.setText(prefs.getString("name", ""));

                profilInfoFragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_normal_white, menu);
        if (langage.equals("en")) {
            menu.findItem(R.id.action_back).setVisible(false);
            menu.findItem(R.id.action_back).setEnabled(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            if (langage.equals("en")) {
                onBackPressed();
                return true;
            }
        }

        if (id == R.id.action_back) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
