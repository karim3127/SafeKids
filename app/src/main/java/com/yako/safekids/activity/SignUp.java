package com.yako.safekids.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.DeviceRegistration;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.flyco.dialog.entity.DialogMenuItem;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.makeramen.roundedimageview.RoundedImageView;
import com.wang.avi.AVLoadingIndicatorView;
import com.safekids.android.R;
import com.yako.safekids.adapter.CodeCountryPopUpItemAdapter;
import com.yako.safekids.model.BackendSetting;
import com.yako.safekids.model.Country;
import com.yako.safekids.util.ConnectionDetector;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignUp extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.img_new_img)
    ImageView img_new_img;

    @BindView(R.id.imgUser)
    CircleImageView imgUser;

    @BindView(R.id.et_fullname)
    EditText et_fullname;

    @BindView(R.id.et_login)
    EditText et_login;

    @BindView(R.id.et_pass)
    EditText et_pass;

    @BindView(R.id.et_city)
    EditText et_city;

    @BindView(R.id.et_phone)
    EditText et_phone;

    @BindView(R.id.et_car_type)
    EditText et_car_type;

    @BindView(R.id.et_car_color)
    EditText et_car_color;

    @BindView(R.id.et_car_num)
    EditText et_car_num;

    @BindView(R.id.txtSignup)
    TextView txtSignup;

    @BindView(R.id.avloadingIndicatorView)
    AVLoadingIndicatorView avloadingIndicatorView;

    @BindView(R.id.txtCodeContry)
    TextView txtCodeContry;

    @BindView(R.id.imgFlag)
    RoundedImageView imgFlag;

    private static int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 21;
    private static int PERMISSIONS_REQUEST_CAMERA = 22;

    File finalFile = null;
    protected static final int CAMERA_REQUEST = 0;
    protected static final int PICK_IMAGE = 1;
    int typeImage;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    ConnectionDetector connectionDetector;

    List<Country> mCountriesList = new ArrayList<Country>();
    public static String codeCountryTel;
    public static String codeCountryFlag;
    private AlertDialog.Builder builderCodeCountry;
    private CodeCountryPopUpItemAdapter ccPopUpItemAdapter;
    private String langage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences("com.yako.safekids", Context.MODE_PRIVATE);
        langage = prefs.getString("Langage", "ar");

        if (langage.equals("ar")) {
            setContentView(R.layout.activity_sign_up);
        } else {
            setContentView(R.layout.activity_sign_up_en);
        }

        ButterKnife.bind(this);

        editor = prefs.edit();

        connectionDetector = new ConnectionDetector(this);

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

        txtCodeContry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCountryDialog();
            }
        });

        imgFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCountryDialog();
            }
        });

        (new CountryAsyncTask(this)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        et_fullname.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            et_login.requestFocus();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
        et_login.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            et_pass.requestFocus();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
        et_pass.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            et_city.requestFocus();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
        et_city.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            et_phone.requestFocus();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
        et_phone.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            et_car_type.requestFocus();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
        et_car_type.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            et_car_color.requestFocus();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
        et_car_color.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            et_car_num.requestFocus();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
        et_car_num.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:

                            View viewFocus = getCurrentFocus();
                            if (viewFocus != null) {
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(viewFocus.getWindowToken(), 0);
                                et_fullname.clearFocus();
                            }

                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
    }

    @OnClick(R.id.txtSignup)
    public void signup() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (et_fullname.length() == 0) {
                    Toast.makeText(SignUp.this, ""+getResources().getString(R.string.add_full_name), Toast.LENGTH_SHORT).show();
                } else if (et_login.length() == 0) {
                    Toast.makeText(SignUp.this, ""+getResources().getString(R.string.add_user_name), Toast.LENGTH_SHORT).show();
                } else if (et_pass.length() == 0) {
                    Toast.makeText(SignUp.this, ""+getResources().getString(R.string.add_password_new), Toast.LENGTH_SHORT).show();
                } else if (et_city.length() == 0) {
                    Toast.makeText(SignUp.this, ""+getResources().getString(R.string.add_ville), Toast.LENGTH_SHORT).show();
                } else if (et_phone.length() == 0) {
                    Toast.makeText(SignUp.this, ""+getResources().getString(R.string.add_number_phone), Toast.LENGTH_SHORT).show();
                } else if (et_car_type.length() == 0) {
                    Toast.makeText(SignUp.this, ""+getResources().getString(R.string.add_number_cars), Toast.LENGTH_SHORT).show();
                } else if (et_car_color.length() == 0) {
                    Toast.makeText(SignUp.this, ""+getResources().getString(R.string.add_color_cars), Toast.LENGTH_SHORT).show();
                } else {
                    if (connectionDetector.isConnectingToInternet()) {
                        inscription();
                    } else {
                        Toast.makeText(SignUp.this, getResources().getString(R.string.problem_connexion), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, 200);

    }

    public void inscription() {

        avloadingIndicatorView.setVisibility(View.VISIBLE);
        txtSignup.setEnabled(false);

        final BackendlessUser user = new BackendlessUser();
        user.setProperty("name", et_fullname.getText().toString().trim());
        user.setProperty("username", et_login.getText().toString().trim());
        user.setProperty("city", et_city.getText().toString().trim());
        user.setProperty("phoneNumber", et_phone.getText().toString().trim());
        user.setProperty("codeCountry", codeCountryTel);
        user.setProperty("carType", et_car_type.getText().toString().trim());
        user.setProperty("carColor", et_car_color.getText().toString().trim());
        user.setProperty("carNumber", et_car_num.getText().toString().trim());
        user.setPassword(et_pass.getText().toString());

        if (finalFile != null) {
            Backendless.Files.upload(finalFile, "ImageProfil" + new Date().getTime(), new AsyncCallback<BackendlessFile>() {
                @Override
                public void handleResponse(final BackendlessFile backendlessFile) {
                    user.setProperty("photo", backendlessFile.getFileURL());

                    Backendless.UserService.register(user, new AsyncCallback<BackendlessUser>() {
                        public void handleResponse(final BackendlessUser registeredUser) {
                            // user has been registered and now can login

                            Backendless.UserService.setCurrentUser(registeredUser);
                            editor.putString("loginUser", et_login.getText().toString().trim());
                            editor.putString("passwordUser", et_pass.getText().toString().trim());

                            editor.putString("Login", "connecte");
                            editor.putString("UserId", registeredUser.getUserId());
                            editor.putString("name", (String) registeredUser.getProperty("name"));
                            editor.putString("username", (String) registeredUser.getProperty("username"));
                            editor.putString("city", (String) registeredUser.getProperty("city"));
                            editor.putString("phoneNumber", (String) registeredUser.getProperty("phoneNumber"));
                            editor.putString("codeCountry", (String) registeredUser.getProperty("codeCountry"));
                            editor.putString("carType", (String) registeredUser.getProperty("carType"));
                            editor.putString("carColor", (String) registeredUser.getProperty("carColor"));
                            editor.putString("carNumber", (String) registeredUser.getProperty("carNumber"));
                            editor.putString("UserPhoto", backendlessFile.getFileURL());

                            editor.commit();

                            Intent intent = new Intent(SignUp.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                            // make device token
                            registerPush(registeredUser);

                        }

                        public void handleFault(BackendlessFault fault) {
                            // an error has occurred, the error code can be retrieved with fault.getCode()
                            Toast.makeText(SignUp.this, ""+getResources().getString(R.string.problem_info), Toast.LENGTH_SHORT).show();
                            Log.e("Error", fault.toString());

                            avloadingIndicatorView.setVisibility(View.GONE);
                            txtSignup.setEnabled(true);

                        }
                    });
                }

                @Override
                public void handleFault(BackendlessFault backendlessFault) {

                    avloadingIndicatorView.setVisibility(View.GONE);
                    txtSignup.setEnabled(true);

                }
            });
        } else {
            Backendless.UserService.register(user, new AsyncCallback<BackendlessUser>() {
                public void handleResponse(final BackendlessUser registeredUser) {
                    // user has been registered and now can login

                    Backendless.UserService.setCurrentUser(registeredUser);

                    editor.putString("loginUser", et_login.getText().toString().trim());
                    editor.putString("passwordUser", et_pass.getText().toString().trim());

                    editor.putString("Login", "connecte");
                    editor.putString("UserId", registeredUser.getUserId()).apply();
                    editor.putString("name", (String) registeredUser.getProperty("name"));
                    editor.putString("username", (String) registeredUser.getProperty("username"));
                    editor.putString("city", (String) registeredUser.getProperty("city"));
                    editor.putString("phoneNumber", (String) registeredUser.getProperty("phoneNumber"));
                    editor.putString("codeCountry", (String) registeredUser.getProperty("codeCountry"));
                    editor.putString("carType", (String) registeredUser.getProperty("carType"));
                    editor.putString("carColor", (String) registeredUser.getProperty("carColor"));
                    editor.putString("carNumber", (String) registeredUser.getProperty("carNumber"));
                    editor.putString("UserPhoto", "").apply();

                    editor.commit();

                    Intent intent = new Intent(SignUp.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);


                    // make device token
                    registerPush(registeredUser);

                }

                public void handleFault(BackendlessFault fault) {
                    // an error has occurred, the error code can be retrieved with fault.getCode()
                    Toast.makeText(SignUp.this, getResources().getString(R.string.problem_info), Toast.LENGTH_SHORT).show();
                    Log.e("Error", fault.toString());

                    avloadingIndicatorView.setVisibility(View.GONE);
                    txtSignup.setEnabled(true);

                }
            });
        }
    }

    private void registerPush(final BackendlessUser registeredUser) {

        Backendless.Messaging.registerDevice(BackendSetting.GOOGLE_PROJECT_ID, BackendSetting.DEFAULT_CHANNEL, new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void response) {
                Backendless.Messaging.getDeviceRegistration(new AsyncCallback<DeviceRegistration>() {
                    @Override
                    public void handleResponse(DeviceRegistration deviceRegistration) {
                        try {
                            registeredUser.setProperty("deviceRegistration", deviceRegistration.getDeviceId());
                            editor.putString("UserDeviceToken", deviceRegistration.getDeviceId()).apply();
                            Backendless.UserService.update(registeredUser, new AsyncCallback<BackendlessUser>() {
                                @Override
                                public void handleResponse(BackendlessUser backendlessUser) {

                                }

                                @Override
                                public void handleFault(BackendlessFault backendlessFault) {
                                }
                            });

                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {

                    }
                });
            }

            @Override
            public void handleFault(BackendlessFault fault) {
            }
        });
    }

    @OnClick(R.id.img_new_img)
    public void chooseImage() {
        ArrayList<DialogMenuItem> listItems = new ArrayList<>();

        if (langage.equals("ar")) {
            listItems.add(new DialogMenuItem(" إستعمال الكاميرا ", R.drawable.ic_camera));
            listItems.add(new DialogMenuItem(" إختيار صورة من الذاكرة ", R.drawable.ic_gallery));
        } else {
            listItems.add(new DialogMenuItem(" "+getResources().getString(R.string.Use_camera)+" ", R.drawable.ic_camera));
            listItems.add(new DialogMenuItem(" "+getResources().getString(R.string.Choose_from_gallery)+" ", R.drawable.ic_gallery));
        }

        final ActionSheetDialog dialog = new ActionSheetDialog(this, listItems, null);
        dialog.isTitleShow(false).show();

        dialog.itemTextColor(getResources().getColor(R.color.colorGray));
        dialog.itemTextSize(getResources().getInteger(R.integer.option_menu_text_size));


        if (langage.equals("ar")) {
            dialog.cancelText(" إلغاء ");
        } else {
            dialog.cancelText(" "+getResources().getString(R.string.Cancel)+" ");
        }

        dialog.cancelText(getResources().getColor(R.color.colorRed));
        dialog.cancelTextSize(getResources().getInteger(R.integer.option_menu_text_size));

        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        typeImage = 0;

                        if (ContextCompat.checkSelfPermission(SignUp.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            if (ContextCompat.checkSelfPermission(SignUp.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                    startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                                }

                            } else {
                                ActivityCompat.requestPermissions(SignUp.this, new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CAMERA);
                            }
                        } else {
                            ActivityCompat.requestPermissions(SignUp.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                        }

                        dialog.dismiss();
                        break;

                    case 1:
                        typeImage = 1;

                        if (ContextCompat.checkSelfPermission(SignUp.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                        } else {
                            ActivityCompat.requestPermissions(SignUp.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                        }
                        dialog.dismiss();
                        break;

                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (typeImage == 0) {
                    if (ContextCompat.checkSelfPermission(SignUp.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                        }
                    } else {
                        ActivityCompat.requestPermissions(SignUp.this, new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CAMERA);
                    }
                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                }
            }
        } else if (PERMISSIONS_REQUEST_CAMERA == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_IMAGE) {
            if (resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                try {
                    InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                    Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);

                    imgUser.setImageBitmap(yourSelectedImage);

                    Uri fileUri = getImageUri(getApplicationContext(), yourSelectedImage);
                    finalFile = new File(getRealPathFromURI(fileUri));

                } catch (FileNotFoundException e) {

                }
            }
        } else {
            if (requestCode == CAMERA_REQUEST) {
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    try {
                        InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                        Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);

                        imgUser.setImageBitmap(yourSelectedImage);

                        Uri fileUri = getImageUri(getApplicationContext(), yourSelectedImage);
                        finalFile = new File(getRealPathFromURI(fileUri));

                    } catch (FileNotFoundException e) {

                    }
                }
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 80, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String[] projection = { MediaStore.MediaColumns.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_normal_blue, menu);
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

    public void showCountryDialog() {
        builderCodeCountry = new AlertDialog.Builder(this);
        builderCodeCountry.setCancelable(true);

        View dialogView = LayoutInflater.from(this).inflate(R.layout.layout_code_contry_popup, null);
        builderCodeCountry.setView(dialogView);

        AlertDialog alert = builderCodeCountry.create();
        alert.show();

        RecyclerView rvCc = (RecyclerView) dialogView.findViewById(R.id.rvListCc);
        ccPopUpItemAdapter = new CodeCountryPopUpItemAdapter(this, alert, mCountriesList, txtCodeContry, imgFlag);

        rvCc.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvCc.setAdapter(ccPopUpItemAdapter);
        rvCc.setItemViewCacheSize(400);
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
            txtCodeContry.setText("+" + codeCountryTel);

            try {
                int resourceId = getResources().getIdentifier(codeCountryFlag.replaceAll("١", "1").replaceAll("٢", "2").replaceAll("٣", "3")
                        .replaceAll("٤", "4").replaceAll("٥", "5").replaceAll("٦", "6").replaceAll("٧", "7")
                        .replaceAll("٨", "8").replaceAll("٩", "9").replaceAll("٠", "0"), "drawable", getPackageName());
                imgFlag.setImageResource(resourceId);
            }catch (Exception e){

            }
        }
    }
}
