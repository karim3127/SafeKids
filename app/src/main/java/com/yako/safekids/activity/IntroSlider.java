package com.yako.safekids.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rey.material.widget.RadioButton;
import com.safekids.android.R;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class IntroSlider extends AppCompatActivity {

    @BindView(R.id.tvSkip)
    TextView tvSkip;

    @BindView(R.id.indicateur0)
    View indicateur0;

    @BindView(R.id.indicateur1)
    View indicateur1;

    @BindView(R.id.indicateur2)
    View indicateur2;

    @BindView(R.id.indicateur3)
    View indicateur3;

    @BindView(R.id.indicateur4)
    View indicateur4;

    @BindView(R.id.pager)
    ViewPager viewPager;

    private boolean isSliderAnimation = false;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private String langage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_slider);
        ButterKnife.bind(this);

        viewPager.setAdapter(new ViewPagerAdapter(R.array.icons, R.array.texts_view_pager));
        viewPager.setPageTransformer(true, new CustomPageTransformer());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0 :
                        indicateur0.setSelected(true);
                        indicateur1.setSelected(false);
                        indicateur2.setSelected(false);
                        indicateur3.setSelected(false);
                        indicateur4.setSelected(false);
                        break;
                    case 1 :
                        indicateur0.setSelected(false);
                        indicateur1.setSelected(true);
                        indicateur2.setSelected(false);
                        indicateur3.setSelected(false);
                        indicateur4.setSelected(false);
                        break;
                    case 2 :
                        indicateur0.setSelected(false);
                        indicateur1.setSelected(false);
                        indicateur2.setSelected(true);
                        indicateur3.setSelected(false);
                        indicateur4.setSelected(false);
                        break;
                    case 3 :
                        indicateur0.setSelected(false);
                        indicateur1.setSelected(false);
                        indicateur2.setSelected(false);
                        indicateur3.setSelected(true);
                        indicateur4.setSelected(false);
                        break;
                    case 4 :
                        indicateur0.setSelected(false);
                        indicateur1.setSelected(false);
                        indicateur2.setSelected(false);
                        indicateur3.setSelected(false);
                        indicateur4.setSelected(true);
                        break;

                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        indicateur0.setSelected(true);

        prefs = getSharedPreferences("com.yako.safekids", Context.MODE_PRIVATE);
        langage = prefs.getString("Langage", "en");
        editor = prefs.edit();

       // int number = 30;//٣٠
        //Toast.makeText(this,"30"+number,Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.tvSkip)
    public void skipClic() {
        startActivity(new Intent(IntroSlider.this, Connexion.class));
        finish();
    }

    @OnClick(R.id.change_langue)
    public void chanegClic(){
        // make alerte
        showPopupChooseLanguage();
    }

    private void showPopupChooseLanguage() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(true);

        View dialogView;
        if (langage.equals("ar")) {
            dialogView = LayoutInflater.from(this).inflate(R.layout.layout_choose_language_popup, null);
        } else {
            dialogView = LayoutInflater.from(this).inflate(R.layout.layout_choose_language_popup_en, null);
        }
        dialog.setView(dialogView);

        final AlertDialog alerteLanguage = dialog.create();
        alerteLanguage.show();

        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 420, getResources().getDisplayMetrics());
        alerteLanguage.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, px);

        final RadioButton rb1 = (RadioButton) dialogView.findViewById(R.id.rb1);
        final RadioButton rb2 = (RadioButton) dialogView.findViewById(R.id.rb2);
        final RadioButton rb3 = (RadioButton) dialogView.findViewById(R.id.rb3);

        if(langage.equals("ar"))
            rb1.setChecked(true);
        if(langage.equals("en"))
            rb2.setChecked(true);
        if(langage.equals("he"))
            rb3.setChecked(true);

        rb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    rb2.setChecked(false);
                    rb3.setChecked(false);
                }
            }
        });

        rb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    rb1.setChecked(false);
                    rb3.setChecked(false);
                }
            }
        });

        rb3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    rb1.setChecked(false);
                    rb2.setChecked(false);
                }
            }
        });

        TextView txtValide = (TextView) dialogView.findViewById(R.id.txtValide);
        txtValide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rb1.isChecked() && !langage.equals("ar")){
                    editor.putString("Langage", "ar");
                    editor.commit();
                    Locale myLocale = new Locale("ar");
                    Resources res = getResources();
                    DisplayMetrics dm = res.getDisplayMetrics();
                    Configuration conf = res.getConfiguration();
                    conf.locale = myLocale;
                    res.updateConfiguration(conf, dm);
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }else if(rb2.isChecked() && !langage.equals("en")){
                    editor.putString("Langage", "en");
                    editor.commit();
                    Locale myLocale = new Locale("en");
                    Resources res = getResources();
                    DisplayMetrics dm = res.getDisplayMetrics();
                    Configuration conf = res.getConfiguration();
                    conf.locale = myLocale;
                    res.updateConfiguration(conf, dm);
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }else if(rb3.isChecked() &&!langage.equals("he")){
                    editor.putString("Langage", "he");
                    editor.commit();
                    /**/
                    Locale myLocale = new Locale("he");
                    //Toast.makeText(getApplicationContext(),""+myLocale,Toast.LENGTH_LONG).show();
                    Resources res = getResources();
                    DisplayMetrics dm = res.getDisplayMetrics();
                    Configuration conf = res.getConfiguration();
                    conf.locale = myLocale;
                    res.updateConfiguration(conf, dm);
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
                alerteLanguage.dismiss();
            }
        });
        TextView txtCancel = (TextView) dialogView.findViewById(R.id.txtCancel);
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alerteLanguage.dismiss();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    public class ViewPagerAdapter extends PagerAdapter {

        private int iconResId, titleArrayResId;

        public ViewPagerAdapter(int iconResId, int titleArrayResId) {

            this.iconResId = iconResId;
            this.titleArrayResId = titleArrayResId;
        }

        @Override
        public int getCount() {
            return getResources().getIntArray(titleArrayResId).length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            Drawable icon = getResources().obtainTypedArray(iconResId).getDrawable(position);
            String title = getResources().getStringArray(titleArrayResId)[position];

            View itemView = getLayoutInflater().inflate(R.layout.viewpager_item, container, false);


            ImageView iconView = (ImageView) itemView.findViewById(R.id.landing_img_slide);
            TextView titleView = (TextView) itemView.findViewById(R.id.landing_txt_title);


            iconView.setImageDrawable(icon);
            titleView.setText(title);

            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((RelativeLayout) object);

        }
    }

    public class CustomPageTransformer implements ViewPager.PageTransformer {
        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            View imageView = view.findViewById(R.id.landing_img_slide);
            View contentView = view.findViewById(R.id.landing_txt_title);
            //View txt_title = view.findViewById(R.id.landing_txt_title);
            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left
            } else if (position <= 0) { // [-1,0]
                // This page is moving out to the left
                // Counteract the default swipe
                setTranslationX(view, pageWidth * -position);
                if (contentView != null) {
                    // But swipe the contentView
                    setTranslationX(contentView, pageWidth * position);
                    //setTranslationX(txt_title, pageWidth * position);
                    setAlpha(contentView, 1 + position);
                    //setAlpha(txt_title, 1 + position);
                }
                if (imageView != null) {
                    // Fade the image in
                    setAlpha(imageView, 1 + position);
                }
            } else if (position <= 1) { // (0,1]
                // This page is moving in from the right
                // Counteract the default swipe
                setTranslationX(view, pageWidth * -position);
                if (contentView != null) {
                    // But swipe the contentView
                    setTranslationX(contentView, pageWidth * position);
                    //setTranslationX(txt_title, pageWidth * position);
                    setAlpha(contentView, 1 - position);
                    //setAlpha(txt_title, 1 - position);
                }
                if (imageView != null) {
                    // Fade the image out
                    setAlpha(imageView, 1 - position);
                }
            }
        }
    }

    private void setAlpha(View view, float alpha) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && !isSliderAnimation) {
            view.setAlpha(alpha);
        }
    }

    private void setTranslationX(View view, float translationX) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && !isSliderAnimation) {
            view.setTranslationX(translationX);
        }
    }

}
