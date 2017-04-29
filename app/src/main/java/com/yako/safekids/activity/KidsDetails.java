package com.yako.safekids.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.commit451.nativestackblur.NativeStackBlur;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.rey.material.widget.Switch;
import com.safekids.android.R;
import com.yako.safekids.model.Kids;
import com.yako.safekids.model.KidsPlaningModel;
import com.yako.safekids.util.ConnectionDetector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class KidsDetails extends AppCompatActivity {

    static Kids currentKids;

    DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .showImageOnLoading(R.drawable.ic_white)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.imgBack)
    ImageView imgBack;

    @BindView(R.id.imgUser)
    CircleImageView imgUser;

    @BindView(R.id.tv_name)
    TextView tv_name;

    @BindView(R.id.tv_age)
    TextView tv_age;

    @BindView(R.id.tv_nb_alarme)
    TextView tv_nb_alarme;

    @BindView(R.id.imgMore)
    ImageView imgMore;

    @BindView(R.id.imgEdit)
    ImageView imgEdit;

    @BindView(R.id.rlPlaning)
    RelativeLayout rlPlaning;

    @BindView(R.id.rlOthers)
    RelativeLayout rlOthers;

    @BindView(R.id.switch_status)
    Switch switch_status;

    boolean isChanged = false;
    boolean isDeleted = false;
    private ConnectionDetector connectionDetector;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    Gson gson = new Gson();
    private List<Kids> allKids;
    private String langage;

    public static void start(Context context, Kids kids) {
        currentKids = kids;
        if(currentKids != null) {
            Intent intent = new Intent(context, KidsDetails.class);
            ((Activity) context).startActivityForResult(intent, 1);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = this.getSharedPreferences("com.yako.safekids", Context.MODE_PRIVATE);
        langage = prefs.getString("Langage", "ar");

        if (langage.equals("ar")) {
            setContentView(R.layout.activity_kids_details);
        } else if(langage.equals("he")){
            setContentView(R.layout.activity_kids_detail_he);
        }else {
            setContentView(R.layout.activity_kids_details_en);
        }

        ButterKnife.bind(this);

        connectionDetector = new ConnectionDetector(this);
        prefs = this.getSharedPreferences("com.yako.safekids", Context.MODE_PRIVATE);
        editor = prefs.edit();

        getListKids();

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

        if (currentKids.getPhoto() != null && !currentKids.equals("")) {
            ImageLoader.getInstance().loadImage(currentKids.getPhoto(), options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    imgUser.setImageBitmap(loadedImage);
                    Bitmap bm = NativeStackBlur.process(loadedImage, getResources().getInteger(R.integer.blur_radius));
                    imgBack.setImageBitmap(bm);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
        } else {
            imgUser.setImageResource(R.drawable.ic_avatar_kids);
            Bitmap bm = NativeStackBlur.process(BitmapFactory.decodeResource(getResources(), R.drawable.bg_profil_setting), getResources().getInteger(R.integer.blur_radius));
            imgBack.setImageBitmap(bm);
        }
        tv_name.setText(currentKids.getName());

        if (langage.equals("ar")) {
            String age;
            if (currentKids.getAge() == 1) {
                age = "سنة";
            } else if (currentKids.getAge() == 2) {
                age = "سنتان";
            } else if (currentKids.getAge() < 11) {
                age = currentKids.getAge() +  " سنوات" ;
            } else {
                age = currentKids.getAge() +  " سنة" ;
            }
            tv_age.setText("العمر: " + age);
        } else {
            String age;
            if (currentKids.getAge() == 1) {
                age = "1 "+getResources().getString(R.string.one_year_normal_alert);
            } else {
                age = currentKids.getAge() + " "+getResources().getString(R.string.nombre_years_normal_alert);
            }
            tv_age.setText(getResources().getString(R.string.age_normal_alerte)+" " + age);
        }

        boolean isActif = false;
        for (int i = 0; i < MainActivity.listStatus.size(); i++) {
            if (MainActivity.listStatus.get(i).equals(currentKids.getObjectId())) {
                isActif = true;
            }
        }
        switch_status.setChecked(isActif);
        switch_status.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                /*if (checked) {
                    MainActivity.listStatus.add(currentKids.getObjectId());

                    String jsonStatus = gson.toJson(MainActivity.listStatus);
                    editor.putString("MesKidsStatus", jsonStatus);
                    editor.commit();
                } else {
                    MainActivity.listStatus.remove(currentKids.getObjectId());

                    String jsonStatus = gson.toJson(MainActivity.listStatus);
                    editor.putString("MesKidsStatus", jsonStatus);
                    editor.commit();
                }*/
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getListPlaning();
    }

    private void getListPlaning() {

        List<KidsPlaningModel> allPlaning = new ArrayList<>();
        List<KidsPlaningModel> listPlaning = new ArrayList<>();

        String json = prefs.getString("KidsPlaning", null);
        if (json != null) {
            KidsPlaningModel[] items = gson.fromJson(json, KidsPlaningModel[].class);
            allPlaning = Arrays.asList(items);
            allPlaning = new ArrayList(allPlaning);
        }
        for (int i = 0; i < allPlaning.size(); i++) {
            if (allPlaning.get(i).getKids().getObjectId().equals(KidsDetails.currentKids.getObjectId())) {
                listPlaning.add(allPlaning.get(i));
            }
        }

        if (listPlaning.size() == 0) {
            if (langage.equals("ar")) {
                tv_nb_alarme.setText("لا يوجد تنبيهات");
            } else {
                tv_nb_alarme.setText(getResources().getString(R.string.no_alerte));
            }
        } else {
            if (langage.equals("ar")) {
                if (listPlaning.size() == 1) {
                    tv_nb_alarme.setText("تنبيه واحد");
                } else if (listPlaning.size() == 2) {
                    tv_nb_alarme.setText("تنبيهان");
                } else {
                    tv_nb_alarme.setText(listPlaning.size() + " تنبيه");
                }

            } else {
                if (listPlaning.size() == 1) {
                    tv_nb_alarme.setText("1 "+getResources().getString(R.string.one_alerte));
                } else {
                    tv_nb_alarme.setText(listPlaning.size() + " "+getResources().getString(R.string.nombre_alertes));
                }
            }
        }
    }

    @OnClick(R.id.imgMore)
    public void showMore() {
        PopupMenu popup = new PopupMenu(this, imgMore);
        //Inflating the Popup using xml file
        if (langage.equals("ar")) {
            popup.getMenuInflater().inflate(R.menu.menu_more_kids, popup.getMenu());
        } else {
            popup.getMenuInflater().inflate(R.menu.menu_more_kids_en, popup.getMenu());
        }

        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();

                if(id == R.id.action_delete) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(KidsDetails.this);
                    String positiveBtnText = "";
                    String negativeBtnText = "";

                    if (langage.equals("ar")) {
                        builder.setMessage(" الرجاء تأكيد الحذف ؟");
                        positiveBtnText = "حذف";
                        negativeBtnText = "إلغاء";
                    } else {
                        builder.setMessage(getResources().getString(R.string.confirme_delete_kids));
                        positiveBtnText = getResources().getString(R.string.Valid_delete_kids);
                        negativeBtnText = getResources().getString(R.string.Cancel_delete_kids);
                    }

                    builder.setPositiveButton(positiveBtnText, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //do your work here
                            if (connectionDetector.isConnectingToInternet()) {
                                Backendless.Persistence.of(Kids.class).remove(currentKids, new AsyncCallback<Long>() {
                                    @Override
                                    public void handleResponse(Long aLong) {
                                        sauvegardeDelete();
                                        isDeleted = true;
                                        onBackPressed();
                                    }

                                    @Override
                                    public void handleFault(BackendlessFault backendlessFault) {

                                    }
                                });
                            } else {
                                Toast.makeText(KidsDetails.this,getResources().getString(R.string.problem_connexion), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.setNegativeButton(negativeBtnText, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                    alert.getWindow().getAttributes();

                    Button btnNegatif = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                    btnNegatif.setTextColor(getResources().getColor(R.color.colorRed));

                    Button btnPositif = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                    btnPositif.setTextColor(getResources().getColor(R.color.colorPrimary));

                }

                return false;
            }
        });
    }

    @OnClick(R.id.imgEdit)
    public void editKids() {
        startActivityForResult(new Intent(this, KidsEdit.class), 1);
    }

    @OnClick(R.id.rlOthers)
    public void showOtherDetails() {
        startActivity(new Intent(this, KidsOtherDetails.class));
    }

    @OnClick(R.id.rlPlaning)
    public void showPlaning() {
        startActivity(new Intent(this, KidsPlaning.class));
    }

    private List<Kids> getListKids() {
        String jsonKids = prefs.getString("MesKids", null);
        Kids[] kidItems = gson.fromJson(jsonKids, Kids[].class);
        allKids = Arrays.asList(kidItems);
        allKids = new ArrayList(allKids);

        return allKids;
    }

    public void sauvegardeDelete() {
        for (int i = 0; i < allKids.size(); i++) {
            if (allKids.get(i).getObjectId().equals(currentKids.getObjectId())) {
                allKids.remove(i);
            }
        }
        String jsonCats = gson.toJson(allKids);
        editor.putString("MesKids", jsonCats);
        editor.commit();

        MainActivity.listStatus.remove(currentKids.getObjectId());

        String jsonStatus = gson.toJson(MainActivity.listStatus);
        editor.putString("MesKidsStatus", jsonStatus);
        editor.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            boolean userUpdate = data.getBooleanExtra("KidsUpdate", false);
            if (userUpdate) {

                isChanged = true;

                if (currentKids.getPhoto() != null && !currentKids.equals("")) {
                    ImageLoader.getInstance().loadImage(currentKids.getPhoto(), options, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            imgUser.setImageBitmap(loadedImage);
                            Bitmap bm = NativeStackBlur.process(loadedImage, getResources().getInteger(R.integer.blur_radius));
                            imgBack.setImageBitmap(bm);
                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {

                        }
                    });
                } else {
                    imgUser.setImageResource(R.drawable.ic_avatar_kids);
                    Bitmap bm = NativeStackBlur.process(BitmapFactory.decodeResource(getResources(), R.drawable.bg_profil_setting), getResources().getInteger(R.integer.blur_radius));
                    imgBack.setImageBitmap(bm);
                }
                tv_name.setText(currentKids.getName());
                String age;
                if (currentKids.getAge() == 1) {
                    age = getResources().getString(R.string.annee);
                } else if (currentKids.getAge() == 2) {
                    age = getResources().getString(R.string.two_yers);
                } else if (currentKids.getAge() < 11) {
                    age = currentKids.getAge() +  getResources().getString(R.string.years) ;
                } else {
                    age = currentKids.getAge() +  ""+getResources().getString(R.string.years_single) ;
                }
                tv_age.setText(getResources().getString(R.string.age_normal_alerte) + age);

            }
        }
    }

    @Override
    public void onBackPressed() {

        if (isDeleted) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("KidsDelete", true);
            setResult(Activity.RESULT_OK, returnIntent);
        } else if (isChanged) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("KidsUpdate", true);
            setResult(Activity.RESULT_OK, returnIntent);
        }

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        currentKids = null;
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
