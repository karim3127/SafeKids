package com.yako.safekids.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.rey.material.widget.CheckBox;
import com.safekids.android.R;
import com.yako.safekids.adapter.PlaningItemAdapter;
import com.yako.safekids.model.KidsPlaningModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class KidsPlaning extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.rv_planing)
    RecyclerView rv_planing;

    @BindView(R.id.img_no_planing)
    ImageView img_no_planing;

    private PlaningItemAdapter planAdapter;

    int heure = 1;
    int minute = 00;
    String day = "";

    List<KidsPlaningModel> allPlaning = new ArrayList<KidsPlaningModel>();
    List<KidsPlaningModel> listPlaning = new ArrayList<KidsPlaningModel>();
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    Gson gson = new Gson();
    private String langage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        day = ""+getResources().getString(R.string.string_morning);

        prefs = this.getSharedPreferences("com.yako.safekids", Context.MODE_PRIVATE);
        langage = prefs.getString("Langage", "ar");

        if (langage.equals("ar")) {
            setContentView(R.layout.activity_kids_planing);
        } else {
            setContentView(R.layout.activity_kids_planing_en);
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

        editor = prefs.edit();

        rv_planing.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        getListPlaning();

    }

    private void getListPlaning() {
        listPlaning.clear();
        allPlaning.clear();

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

        planAdapter = new PlaningItemAdapter(this, listPlaning);
        rv_planing.setAdapter(planAdapter);

        if (listPlaning.size() == 0) {
            img_no_planing.setVisibility(View.VISIBLE);
        } else {
            img_no_planing.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.fab)
    public void addPlaning() {
        showPopupPlaning();
    }

    public void showPopupPlaning() {
        heure = 0;
        minute = 0;
        day = (langage.equals("ar")) ? "صباح" :  ""+getResources().getString(R.string.string_morning);
        final List<Integer> listDays = new ArrayList<>();

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(true);

        View dialogView;
        if (langage.equals("ar")) {
            dialogView = LayoutInflater.from(this).inflate(R.layout.layout_new_planing_popup, null);
        } else {
            dialogView = LayoutInflater.from(this).inflate(R.layout.layout_new_planing_popup_en, null);
        }

        dialog.setView(dialogView);

        final AlertDialog alert = dialog.create();
        alert.show();

        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 420, getResources().getDisplayMetrics());
        alert.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, px);

        final EditText et_texto = (EditText) dialogView.findViewById(R.id.et_texto);

        TextView txtValide = (TextView) dialogView.findViewById(R.id.txtValide);
        txtValide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listDays.size() == 0) {
                    Toast.makeText(KidsPlaning.this, ""+getResources().getString(R.string.choose_dates), Toast.LENGTH_SHORT).show();
                } else {

                    String timeMode = (day.equals("صباح") || day.equals(getResources().getString(R.string.string_morning))) ? "am" : "pm";
                    KidsPlaningModel kidsPlaningModel = new KidsPlaningModel(KidsDetails.currentKids, timeMode, heure, minute, et_texto.getText().toString(), listDays);

                    planAdapter.addItem(kidsPlaningModel);
                    allPlaning.add(kidsPlaningModel);

                    img_no_planing.setVisibility(View.GONE);

                    String json = gson.toJson(allPlaning);
                    editor.putString("KidsPlaning", json);
                    editor.commit();

                    alert.dismiss();
                }
            }
        });
        TextView txtCancel = (TextView) dialogView.findViewById(R.id.txtCancel);
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
            }
        });

        final TextSwitcher txtSwitchH = (TextSwitcher) dialogView.findViewById(R.id.txtSwitcherH);
        final TextSwitcher txtSwitchM = (TextSwitcher) dialogView.findViewById(R.id.txtSwitcherM);
        final TextSwitcher txtSwitchD = (TextSwitcher) dialogView.findViewById(R.id.txtSwitcherD);

        ImageView imgUpH = (ImageView) dialogView.findViewById(R.id.imgUpH);
        imgUpH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (heure == 11) {
                    heure = 0;
                } else {
                    heure++;
                }
                txtSwitchH.setText((heure < 10) ? "0" + heure : "" + heure);
            }
        });

        ImageView imgDownH = (ImageView) dialogView.findViewById(R.id.imgDownH);
        imgDownH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (heure == 0) {
                    heure = 11;
                } else {
                    heure--;
                }
                txtSwitchH.setText((heure < 10) ? "0" + heure : "" + heure);
            }
        });

        ImageView imgUpM = (ImageView) dialogView.findViewById(R.id.imgUpM);
        imgUpM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (minute == 59) {
                    minute = 0;
                } else {
                    minute++;
                }
                txtSwitchM.setText((minute < 10) ? "0" + minute : "" + minute);
            }
        });

        ImageView imgDownM = (ImageView) dialogView.findViewById(R.id.imgDownM);
        imgDownM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (minute == 0) {
                    minute = 59;
                } else {
                    minute--;
                }
                txtSwitchM.setText((minute < 10) ? "0" + minute : "" + minute);
            }
        });

        ImageView imgUpD = (ImageView) dialogView.findViewById(R.id.imgUpD);
        imgUpD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (langage.equals("ar")) {
                    if (day.equals("صباح")) {
                        day = "مساء";
                    } else {
                        day = "صباح";
                    }
                } else {
                    if (day.equals(""+getResources().getString(R.string.string_morning))) {
                        day = ""+getResources().getString(R.string.Night);
                    } else {
                        day = ""+getResources().getString(R.string.string_morning);
                    }
                }

                txtSwitchD.setText(day);
            }
        });

        ImageView imgDownD = (ImageView) dialogView.findViewById(R.id.imgDownD);
        imgDownD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (langage.equals("ar")) {
                    if (day.equals("صباح")) {
                        day = "مساء";
                    } else {
                        day = "صباح";
                    }
                } else {
                    if (day.equals(getResources().getString(R.string.string_morning))) {
                        day = ""+getResources().getString(R.string.Night);
                    } else {
                        day = ""+getResources().getString(R.string.string_morning);
                    }
                }
                txtSwitchD.setText(day);
            }
        });

        final TextView txtJ1 = (TextView) dialogView.findViewById(R.id.txtJ1);
        txtJ1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtJ1.setSelected(!txtJ1.isSelected());
                int index = -1;
                for (int i = 0; i < listDays.size(); i++) {
                    if (listDays.get(i) == 7) {
                        index = i;
                    }
                }

                if (txtJ1.isSelected()) {
                    if (index == -1)
                        listDays.add(7);
                } else {
                    if (index > -1)
                        listDays.remove(index);
                }
            }
        });

        final TextView txtJ2 = (TextView) dialogView.findViewById(R.id.txtJ2);
        txtJ2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtJ2.setSelected(!txtJ2.isSelected());
                int index = -1;
                for (int i = 0; i < listDays.size(); i++) {
                    if (listDays.get(i) == 6) {
                        index = i;
                    }
                }

                if (txtJ2.isSelected()) {
                    if (index == -1)
                        listDays.add(6);
                } else {
                    if (index > -1)
                        listDays.remove(index);
                }
            }
        });

        final TextView txtJ3 = (TextView) dialogView.findViewById(R.id.txtJ3);
        txtJ3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtJ3.setSelected(!txtJ3.isSelected());
                int index = -1;
                for (int i = 0; i < listDays.size(); i++) {
                    if (listDays.get(i) == 5) {
                        index = i;
                    }
                }

                if (txtJ3.isSelected()) {
                    if (index == -1)
                        listDays.add(5);
                } else {
                    if (index > -1)
                        listDays.remove(index);
                }
            }
        });

        final TextView txtJ4 = (TextView) dialogView.findViewById(R.id.txtJ4);
        txtJ4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtJ4.setSelected(!txtJ4.isSelected());
                int index = -1;
                for (int i = 0; i < listDays.size(); i++) {
                    if (listDays.get(i) == 4) {
                        index = i;
                    }
                }

                if (txtJ4.isSelected()) {
                    if (index == -1)
                        listDays.add(4);
                } else {
                    if (index > -1)
                        listDays.remove(index);
                }
            }
        });

        final TextView txtJ5 = (TextView) dialogView.findViewById(R.id.txtJ5);
        txtJ5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtJ5.setSelected(!txtJ5.isSelected());
                int index = -1;
                for (int i = 0; i < listDays.size(); i++) {
                    if (listDays.get(i) == 3) {
                        index = i;
                    }
                }

                if (txtJ5.isSelected()) {
                    if (index == -1)
                        listDays.add(3);
                } else {
                    if (index > -1)
                        listDays.remove(index);
                }
            }
        });

        final TextView txtJ6 = (TextView) dialogView.findViewById(R.id.txtJ6);
        txtJ6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtJ6.setSelected(!txtJ6.isSelected());
                int index = -1;
                for (int i = 0; i < listDays.size(); i++) {
                    if (listDays.get(i) == 2) {
                        index = i;
                    }
                }

                if (txtJ6.isSelected()) {
                    if (index == -1)
                        listDays.add(2);
                } else {
                    if (index > -1)
                        listDays.remove(index);
                }
            }
        });

        final TextView txtJ7 = (TextView) dialogView.findViewById(R.id.txtJ7);
        txtJ7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtJ7.setSelected(!txtJ7.isSelected());
                int index = -1;
                for (int i = 0; i < listDays.size(); i++) {
                    if (listDays.get(i) == 1) {
                        index = i;
                    }
                }

                if (txtJ7.isSelected()) {
                    if (index == -1)
                        listDays.add(1);
                } else {
                    if (index > -1)
                        listDays.remove(index);
                }
            }
        });

        final CheckBox check_day = (CheckBox) dialogView.findViewById(R.id.check_day);
        check_day.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    txtJ1.setSelected(true);
                    txtJ2.setSelected(true);
                    txtJ3.setSelected(true);
                    txtJ4.setSelected(true);
                    txtJ5.setSelected(true);
                    txtJ6.setSelected(true);
                    txtJ7.setSelected(true);

                    listDays.clear();
                    for (int i = 1; i < 8; i++) {
                        listDays.add(i);
                    }
                } else {
                    txtJ1.setSelected(false);
                    txtJ2.setSelected(false);
                    txtJ3.setSelected(false);
                    txtJ4.setSelected(false);
                    txtJ5.setSelected(false);
                    txtJ6.setSelected(false);
                    txtJ7.setSelected(false);
                    listDays.clear();
                }
            }
        });

        TextView txtCheckAll = (TextView) dialogView.findViewById(R.id.txtCheckAll);
        txtCheckAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_day.setChecked(!check_day.isChecked());
            }
        });

        final CheckBox check_auto = (CheckBox) dialogView.findViewById(R.id.check_auto);
        check_auto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {

                } else {

                }
            }
        });

        TextView txtAuto = (TextView) dialogView.findViewById(R.id.txtAuto);
        txtAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_auto.setChecked(!check_auto.isChecked());
            }
        });

    }


    public void showPopupPlaningUpdate(final KidsPlaningModel planing, final int position) {
        heure = planing.getHoure();
        minute = planing.getMinute();
        if (langage.equals("ar")) {
            if (planing.getTimeMode().equals("am")) {
                day = "صباح";
            } else {
                day = "مساء";
            }
        } else {
            if (planing.getTimeMode().equals("am")) {
                day = ""+getResources().getString(R.string.string_morning);
            } else {
                day = ""+getResources().getString(R.string.Night);
            }
        }

        final List<Integer> listDays = new ArrayList<>();

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(true);

        View dialogView;
        if (langage.equals("ar")) {
            dialogView = LayoutInflater.from(this).inflate(R.layout.layout_new_planing_popup, null);
        } else {
            dialogView = LayoutInflater.from(this).inflate(R.layout.layout_new_planing_popup_en, null);
        }

        dialog.setView(dialogView);

        final AlertDialog alert = dialog.create();
        alert.show();

        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 420, getResources().getDisplayMetrics());
        alert.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, px);

        final EditText et_texto = (EditText) dialogView.findViewById(R.id.et_texto);
        et_texto.setText(planing.getTexto());

        TextView txtValide = (TextView) dialogView.findViewById(R.id.txtValide);
        if (langage.equals("ar")) {
            txtValide.setText("تعديل");
        } else {
            txtValide.setText(""+getResources().getString(R.string.Modify));
        }
        txtValide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listDays.size() == 0) {
                    Toast.makeText(KidsPlaning.this, ""+getResources().getString(R.string.days_of_alertes), Toast.LENGTH_SHORT).show();
                } else {

                    String timeMode = (day.equals("صباح") || day.equals(""+getResources().getString(R.string.string_morning))) ? "am" : "pm";

                    planing.setHoure(heure);
                    planing.setMinute(minute);
                    planing.setTimeMode(timeMode);
                    planing.setTexto(et_texto.getText().toString());
                    planing.setDays(listDays);

                    planAdapter.modifyItem(position, planing);

                    for (int i = 0; i < allPlaning.size(); i++) {
                        KidsPlaningModel p = allPlaning.get(i);
                        if (p.getMinute() == planing.getMinute() && p.getHoure() == planing.getHoure() && p.getKids().getObjectId().equals(planing.getKids().getObjectId()) && p.getTimeMode().equals(planing.getTimeMode())) {
                            allPlaning.set(i, planing);
                        }
                    }

                    img_no_planing.setVisibility(View.GONE);

                    String json = gson.toJson(allPlaning);
                    editor.putString("KidsPlaning", json);
                    editor.commit();

                    alert.dismiss();
                }
            }
        });
        TextView txtCancel = (TextView) dialogView.findViewById(R.id.txtCancel);
        if (langage.equals("ar")) {
            txtCancel.setText("حذف");
        } else {
            txtCancel.setText(""+getResources().getString(R.string.Delete));
        }
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
                deleteAlarme(position);
            }
        });

        final TextSwitcher txtSwitchH = (TextSwitcher) dialogView.findViewById(R.id.txtSwitcherH);
        final TextSwitcher txtSwitchM = (TextSwitcher) dialogView.findViewById(R.id.txtSwitcherM);
        final TextSwitcher txtSwitchD = (TextSwitcher) dialogView.findViewById(R.id.txtSwitcherD);

        ImageView imgUpH = (ImageView) dialogView.findViewById(R.id.imgUpH);
        imgUpH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (heure == 11) {
                    heure = 0;
                } else {
                    heure++;
                }
                txtSwitchH.setText((heure < 10) ? "0" + heure : "" + heure);
            }
        });

        ImageView imgDownH = (ImageView) dialogView.findViewById(R.id.imgDownH);
        imgDownH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (heure == 0) {
                    heure = 11;
                } else {
                    heure--;
                }
                txtSwitchH.setText((heure < 10) ? "0" + heure : "" + heure);
            }
        });

        txtSwitchH.setText((heure < 10) ? "0" + heure : "" + heure);

        ImageView imgUpM = (ImageView) dialogView.findViewById(R.id.imgUpM);
        imgUpM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (minute == 59) {
                    minute = 0;
                } else {
                    minute++;
                }
                txtSwitchM.setText((minute < 10) ? "0" + minute : "" + minute);
            }
        });

        ImageView imgDownM = (ImageView) dialogView.findViewById(R.id.imgDownM);
        imgDownM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (minute == 0) {
                    minute = 59;
                } else {
                    minute--;
                }
                txtSwitchM.setText((minute < 10) ? "0" + minute : "" + minute);
            }
        });

        txtSwitchM.setText((minute < 10) ? "0" + minute : "" + minute);

        ImageView imgUpD = (ImageView) dialogView.findViewById(R.id.imgUpD);
        imgUpD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (langage.equals("ar")) {
                    if (day.equals("صباح")) {
                        day = "مساء";
                    } else {
                        day = "صباح";
                    }
                } else {
                    if (day.equals(getResources().getString(R.string.string_morning))) {
                        day = getResources().getString(R.string.Night);
                    } else {
                        day = getResources().getString(R.string.string_morning);
                    }
                }

                txtSwitchD.setText(day);
            }
        });

        txtSwitchD.setText(day);

        ImageView imgDownD = (ImageView) dialogView.findViewById(R.id.imgDownD);
        imgDownD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (langage.equals("ar")) {
                    if (day.equals("صباح")) {
                        day = "مساء";
                    } else {
                        day = "صباح";
                    }
                } else {
                    if (day.equals(getResources().getString(R.string.string_morning))) {
                        day = getResources().getString(R.string.Night);
                    } else {
                        day = getResources().getString(R.string.string_morning);
                    }
                }
                txtSwitchD.setText(day);
            }
        });

        final TextView txtJ1 = (TextView) dialogView.findViewById(R.id.txtJ1);
        txtJ1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtJ1.setSelected(!txtJ1.isSelected());
                int index = -1;
                for (int i = 0; i < listDays.size(); i++) {
                    if (listDays.get(i) == 7) {
                        index = i;
                    }
                }

                if (txtJ1.isSelected()) {
                    if (index == -1)
                        listDays.add(7);
                } else {
                    if (index > -1)
                        listDays.remove(index);
                }
            }
        });

        final TextView txtJ2 = (TextView) dialogView.findViewById(R.id.txtJ2);
        txtJ2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtJ2.setSelected(!txtJ2.isSelected());
                int index = -1;
                for (int i = 0; i < listDays.size(); i++) {
                    if (listDays.get(i) == 6) {
                        index = i;
                    }
                }

                if (txtJ2.isSelected()) {
                    if (index == -1)
                        listDays.add(6);
                } else {
                    if (index > -1)
                        listDays.remove(index);
                }
            }
        });

        final TextView txtJ3 = (TextView) dialogView.findViewById(R.id.txtJ3);
        txtJ3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtJ3.setSelected(!txtJ3.isSelected());
                int index = -1;
                for (int i = 0; i < listDays.size(); i++) {
                    if (listDays.get(i) == 5) {
                        index = i;
                    }
                }

                if (txtJ3.isSelected()) {
                    if (index == -1)
                        listDays.add(5);
                } else {
                    if (index > -1)
                        listDays.remove(index);
                }
            }
        });

        final TextView txtJ4 = (TextView) dialogView.findViewById(R.id.txtJ4);
        txtJ4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtJ4.setSelected(!txtJ4.isSelected());
                int index = -1;
                for (int i = 0; i < listDays.size(); i++) {
                    if (listDays.get(i) == 4) {
                        index = i;
                    }
                }

                if (txtJ4.isSelected()) {
                    if (index == -1)
                        listDays.add(4);
                } else {
                    if (index > -1)
                        listDays.remove(index);
                }
            }
        });

        final TextView txtJ5 = (TextView) dialogView.findViewById(R.id.txtJ5);
        txtJ5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtJ5.setSelected(!txtJ5.isSelected());
                int index = -1;
                for (int i = 0; i < listDays.size(); i++) {
                    if (listDays.get(i) == 3) {
                        index = i;
                    }
                }

                if (txtJ5.isSelected()) {
                    if (index == -1)
                        listDays.add(3);
                } else {
                    if (index > -1)
                        listDays.remove(index);
                }
            }
        });

        final TextView txtJ6 = (TextView) dialogView.findViewById(R.id.txtJ6);
        txtJ6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtJ6.setSelected(!txtJ6.isSelected());
                int index = -1;
                for (int i = 0; i < listDays.size(); i++) {
                    if (listDays.get(i) == 2) {
                        index = i;
                    }
                }

                if (txtJ6.isSelected()) {
                    if (index == -1)
                        listDays.add(2);
                } else {
                    if (index > -1)
                        listDays.remove(index);
                }
            }
        });

        final TextView txtJ7 = (TextView) dialogView.findViewById(R.id.txtJ7);
        txtJ7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtJ7.setSelected(!txtJ7.isSelected());
                int index = -1;
                for (int i = 0; i < listDays.size(); i++) {
                    if (listDays.get(i) == 1) {
                        index = i;
                    }
                }

                if (txtJ7.isSelected()) {
                    if (index == -1)
                        listDays.add(1);
                } else {
                    if (index > -1)
                        listDays.remove(index);
                }
            }
        });

        final CheckBox check_day = (CheckBox) dialogView.findViewById(R.id.check_day);
        check_day.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    txtJ1.setSelected(true);
                    txtJ2.setSelected(true);
                    txtJ3.setSelected(true);
                    txtJ4.setSelected(true);
                    txtJ5.setSelected(true);
                    txtJ6.setSelected(true);
                    txtJ7.setSelected(true);

                    listDays.clear();
                    for (int i = 1; i < 8; i++) {
                        listDays.add(i);
                    }
                } else {
                    txtJ1.setSelected(false);
                    txtJ2.setSelected(false);
                    txtJ3.setSelected(false);
                    txtJ4.setSelected(false);
                    txtJ5.setSelected(false);
                    txtJ6.setSelected(false);
                    txtJ7.setSelected(false);
                    listDays.clear();
                }
            }
        });

        TextView txtCheckAll = (TextView) dialogView.findViewById(R.id.txtCheckAll);
        txtCheckAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_day.setChecked(!check_day.isChecked());
            }
        });

        final CheckBox check_auto = (CheckBox) dialogView.findViewById(R.id.check_auto);
        check_auto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {

                } else {

                }
            }
        });

        TextView txtAuto = (TextView) dialogView.findViewById(R.id.txtAuto);
        txtAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_auto.setChecked(!check_auto.isChecked());
            }
        });

        listDays.addAll(planing.getDays());
        for (int i = 0; i < planing.getDays().size(); i++) {
            switch (planing.getDays().get(i)) {
                case 7 :
                    txtJ1.setSelected(true);
                    break;
                case 6 :
                    txtJ2.setSelected(true);
                    break;
                case 5 :
                    txtJ3.setSelected(true);
                    break;
                case 4 :
                    txtJ4.setSelected(true);
                    break;
                case 3 :
                    txtJ5.setSelected(true);
                    break;
                case 2 :
                    txtJ6.setSelected(true);
                    break;
                case 1 :
                    txtJ7.setSelected(true);
                    break;

            }
        }
    }

    public void deleteAlarme(int position) {
        KidsPlaningModel kidsPlaningModel = listPlaning.get(position);

        for (int i = 0; i < allPlaning.size(); i++) {
            if (allPlaning.get(i).getHoure() == kidsPlaningModel.getHoure() && allPlaning.get(i).getMinute() == kidsPlaningModel.getMinute() && allPlaning.get(i).getDays() == kidsPlaningModel.getDays() && allPlaning.get(i).getTimeMode().equals(kidsPlaningModel.getTimeMode()) && allPlaning.get(i).getKids().getObjectId().equals(kidsPlaningModel.getKids().getObjectId())) {
                allPlaning.remove(i);
            }
        }
        planAdapter.deleteItem(position);

        if (planAdapter.getItemCount() == 0) {
            img_no_planing.setVisibility(View.VISIBLE);
        }

        String json = gson.toJson(allPlaning);
        editor.putString("KidsPlaning", json);
        editor.commit();

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
