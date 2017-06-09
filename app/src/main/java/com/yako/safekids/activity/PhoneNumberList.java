package com.yako.safekids.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;
import com.safekids.android.R;
import com.yako.safekids.adapter.CcPopUpItemAdapter;
import com.yako.safekids.adapter.NumTelItemAdapter;
import com.yako.safekids.model.Country;
import com.yako.safekids.model.PhoneNumber;
import com.yako.safekids.util.ConnectionDetector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PhoneNumberList extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.fab)
    com.getbase.floatingactionbutton.FloatingActionButton fab;

    @BindView(R.id.img_no_phone)
    ImageView img_no_phone;

    @BindView(R.id.rv_number)
    RecyclerView rv_number;

    private NumTelItemAdapter numAdapter;
    List<Country> mCountriesList = new ArrayList<Country>();
    public static String codeCountryTel;
    public static String codeCountryFlag;

    ConnectionDetector connectionDetector;
    private String currentUserId;
    BackendlessUser currentUser = null;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    Gson gson = new Gson();

    List<PhoneNumber> listPhones = new ArrayList<>();
    private String langage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = this.getSharedPreferences("com.yako.safekids", Context.MODE_PRIVATE);
        langage = prefs.getString("Langage", "ar");

        if (langage.equals("ar")) {
            setContentView(R.layout.activity_phone_number);
        } else {
            setContentView(R.layout.activity_phone_number_en);
        }

        ButterKnife.bind(this);

        editor = prefs.edit();
        connectionDetector = new ConnectionDetector(this);
        currentUserId = prefs.getString("UserId", "");
        getCurrentUser(currentUserId);

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

        getListPhones();

        rv_number.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        numAdapter = new NumTelItemAdapter(PhoneNumberList.this, listPhones);
        rv_number.setAdapter(numAdapter);

        if (listPhones.size() == 0) {
            img_no_phone.setVisibility(View.VISIBLE);
        } else {
            img_no_phone.setVisibility(View.GONE);
        }

        (new CountryAsyncTask(this)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /*public void getListPhones() {

        StringBuilder whereClause = new StringBuilder();
        whereClause.append("user.objectId = '" + prefs.getString("UserId", "") + "'");

        BackendlessDataQuery query = new BackendlessDataQuery();
        query.setWhereClause(whereClause.toString());

        QueryOptions queryOptions = new QueryOptions();
        queryOptions.setRelationsDepth(1);

        query.setQueryOptions(queryOptions);
        query.setPageSize(100);

        Backendless.Data.of(PhoneNumber.class).find(query, new AsyncCallback<BackendlessCollection<PhoneNumber>>() {
            @Override
            public void handleResponse(final BackendlessCollection<PhoneNumber> backendlessCollection) {
                List<PhoneNumber> list = backendlessCollection.getCurrentPage();

                for (int i = 0; i < list.size(); i++) {
                    numAdapter.addNumber(list.get(i));
                }

                if (listPhone.size() == 0) {
                    img_no_phone.setVisibility(View.VISIBLE);
                } else {
                    img_no_phone.setVisibility(View.GONE);
                }

            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Log.e("error", backendlessFault.getMessage());

                if (listPhone.size() == 0) {
                    img_no_phone.setVisibility(View.VISIBLE);
                } else {
                    img_no_phone.setVisibility(View.GONE);
                }
            }
        });
    }*/

    private void getCurrentUser(String currentUserId) {
        Backendless.UserService.findById(currentUserId, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser backendlessUser) {
                currentUser = backendlessUser;
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {

            }
        });
    }

    @OnClick(R.id.fab)
    public void addNumbre() {
        if (numAdapter.listItems.size() < 3) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setCancelable(true);

            View dialogView;

            if (langage.equals("ar")) {
                dialogView = LayoutInflater.from(this).inflate(R.layout.layout_new_numbre_popup, null);
            } else {
                dialogView = LayoutInflater.from(this).inflate(R.layout.layout_new_numbre_popup_en, null);
            }

            dialog.setView(dialogView);

            final AlertDialog alert = dialog.create();
            alert.show();

            int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 420, getResources().getDisplayMetrics());
            alert.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, px);

            TextView txtCodeContry = (TextView) dialogView.findViewById(R.id.txtCodeContry);
            RoundedImageView imgFlag = (RoundedImageView) dialogView.findViewById(R.id.imgFlag);
            final EditText txtPhoneNumber = (EditText) dialogView.findViewById(R.id.txtPhoneNumber);

            txtCodeContry.setText("+" + codeCountryTel);
            int resourceId = getResources().getIdentifier(codeCountryFlag.replaceAll( "١","1").replaceAll( "٢","2").replaceAll( "٣","3")
                    .replaceAll( "٤","4").replaceAll( "٥","5").replaceAll( "٦","6").replaceAll( "٧","7")
                    .replaceAll( "٨","8").replaceAll( "٩","9").replaceAll( "٠","0"), "drawable", getPackageName());
            imgFlag.setImageResource(resourceId);


            TextView txtValide = (TextView) dialogView.findViewById(R.id.txtValide);
            txtValide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(txtPhoneNumber == null)
                        return;

                    if (txtPhoneNumber.getText().toString().trim().length() == 0) {

                        Toast.makeText(PhoneNumberList.this,getResources().getString(R.string.emty_number_phone), Toast.LENGTH_SHORT).show();
                    } else {

                        if (connectionDetector.isConnectingToInternet()) {

                            if (currentUser != null) {

                                PhoneNumber phoneNumber = new PhoneNumber();
                                phoneNumber.setNumero(txtPhoneNumber.getText().toString().trim());
                                phoneNumber.setCodeCountry(codeCountryTel);
                                phoneNumber.setUser(currentUser);

                                Backendless.Persistence.save(phoneNumber, new AsyncCallback<PhoneNumber>() {
                                    public void handleResponse(PhoneNumber response) {
                                        numAdapter.addNumber(response);

                                        if (listPhones.size() == 0) {
                                            img_no_phone.setVisibility(View.VISIBLE);
                                        } else {
                                            img_no_phone.setVisibility(View.GONE);
                                        }

                                        String jsonPhone = gson.toJson(numAdapter.listItems);
                                        editor.putString("MesPhones", jsonPhone);
                                        editor.commit();
                                    }

                                    public void handleFault(BackendlessFault fault) {
                                        Toast.makeText(PhoneNumberList.this, getResources().getString(R.string.problem_connexion), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } else {
                                Toast.makeText(PhoneNumberList.this,getResources().getString(R.string.problem_connexion), Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(PhoneNumberList.this,getResources().getString(R.string.problem_connexion), Toast.LENGTH_SHORT).show();
                        }
                    }
                    alert.dismiss();
                }
            });
            RecyclerView rvCat = (RecyclerView) dialogView.findViewById(R.id.rvListCC);
            CcPopUpItemAdapter ccPopUpItemAdapter = new CcPopUpItemAdapter(this, mCountriesList, txtCodeContry, imgFlag);

            rvCat.setLayoutManager(new LinearLayoutManager(this));
            rvCat.setAdapter(ccPopUpItemAdapter);
            rvCat.setItemViewCacheSize(50);
        } else {
            Toast.makeText(PhoneNumberList.this, getResources().getString(R.string.max_numbers_phone), Toast.LENGTH_SHORT).show();
        }
    }

    private void getListPhones() {
        listPhones.clear();
        String jsonPhones = prefs.getString("MesPhones", null);
        if (jsonPhones != null) {
            PhoneNumber[] phoneItems = gson.fromJson(jsonPhones, PhoneNumber[].class);
            listPhones = Arrays.asList(phoneItems);
            listPhones = new ArrayList(listPhones);
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

    public static String getLocalCountry(Context context){
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCode = tm.getSimCountryIso();
        if(countryCode.equals("")){
            String networkCountry = tm.getNetworkCountryIso();
            if (networkCountry != null && networkCountry.length() == 2) {
                countryCode = networkCountry.toLowerCase(Locale.US);
            } else {
                countryCode = context.getResources().getConfiguration().locale.getCountry();
            }
        }
        return countryCode;
    }

    protected class CountryAsyncTask extends AsyncTask<Void, Void, ArrayList<Country>> {

        private Context mContext;

        public CountryAsyncTask(Context context) {
            mContext = context;
        }

        @Override
        protected ArrayList<Country> doInBackground(Void... params) {
            String country = getLocalCountry(mContext);

            ArrayList<Country> data = new ArrayList<Country>(233);
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(mContext.getApplicationContext().getAssets().open("countries.dat"), "UTF-8"));

                // do reading, usually loop until end of file reading
                String line;
                int i = 0;
                while ((line = reader.readLine()) != null) {
                    //process line
                    Country c = new Country(mContext, line, i);
                    mCountriesList.add(c);

                    if (c.getCountryISO().equals(country)) {
                        codeCountryTel = c.getCountryCode() + "";
                        codeCountryFlag = String.format("f%03d", i);

                    }

                    i++;
                }
            } catch (IOException e) {
                //log the exception
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        //log the exception
                    }
                }
            }

            return data;
        }

        @Override
        protected void onPostExecute(ArrayList<Country> data) {

        }
    }

}
