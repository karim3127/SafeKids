package com.yako.safekids.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.flyco.dialog.entity.DialogMenuItem;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.google.gson.Gson;
import com.rey.material.widget.RadioButton;
import com.safekids.android.R;
import com.yako.safekids.model.Kids;
import com.yako.safekids.util.ConnectionDetector;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NewKids extends AppCompatActivity {

    @BindView(R.id.img_new_img)
    ImageView img_new_img;

    @BindView(R.id.imgUser)
    CircleImageView imgUser;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.txtAddKids)
    TextView txtAddKids;

    @BindView(R.id.et_name)
    EditText et_name;

    @BindView(R.id.et_age)
    EditText et_age;

    @BindView(R.id.rb1)
    RadioButton rb1;

    @BindView(R.id.rb2)
    RadioButton rb2;

    @BindView(R.id.rlBlock)
    RelativeLayout rlBlock;

    private static int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 21;
    private static int PERMISSIONS_REQUEST_CAMERA = 22;

    File finalFile = null;
    protected static final int CAMERA_REQUEST = 0;
    protected static final int PICK_IMAGE = 1;
    int typeImage;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    Gson gson = new Gson();
    ConnectionDetector connectionDetector;
    private String currentUserId;
    BackendlessUser currentUser = null;
    private List<Kids> allKids = new ArrayList<>();
    private String langage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = this.getSharedPreferences("com.yako.safekids", Context.MODE_PRIVATE);
        langage = prefs.getString("Langage", "ar");

        if (langage.equals("ar")) {
            setContentView(R.layout.activity_new_kids);
        } else {
            setContentView(R.layout.activity_new_kids_en);
        }

        ButterKnife.bind(this);

        editor = prefs.edit();
        connectionDetector = new ConnectionDetector(this);
        currentUserId = prefs.getString("UserId", "");
        getCurrentUser(currentUserId);
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

        et_name.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            et_age.requestFocus();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
        et_age.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:

                            View viewFocus = getCurrentFocus();
                            if (viewFocus != null) {
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(viewFocus.getWindowToken(), 0);
                                et_name.clearFocus();
                            }

                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        rb2.setChecked(true);

        rb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    rb2.setChecked(false);
                }
            }
        });

        rb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    rb1.setChecked(false);
                }
            }
        });
    }

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

    @OnClick(R.id.rlBlock)
    public void blockView() {

    }

    @OnClick(R.id.txtAddKids)
    public void addKids() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (et_name.length() == 0) {
                    Toast.makeText(NewKids.this, ""+getResources().getString(R.string.add_name_kids_new), Toast.LENGTH_SHORT).show();
                } else if (et_age.length() == 0) {
                    Toast.makeText(NewKids.this,getResources().getString(R.string.add_age_kids) , Toast.LENGTH_SHORT).show();
                } else {
                    if (connectionDetector.isConnectingToInternet()) {
                        addQuery();
                    } else {
                        Toast.makeText(NewKids.this,getResources().getString(R.string.problem_connexion), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, 200);
    }

    public void addQuery() {
        final Kids kids = new Kids();

        kids.setName(et_name.getText().toString().trim());
        kids.setAge(Integer.valueOf(et_age.getText().toString().trim()));
        if (rb1.isChecked()) {
            kids.setSexe(""+getResources().getString(R.string.female));
        } else {
            kids.setSexe(""+getResources().getString(R.string.male));
        }

        if (currentUser != null) {
            rlBlock.setVisibility(View.VISIBLE);

            kids.setUser(currentUser);

            if (finalFile != null) {
                Backendless.Files.upload(finalFile, "ImageProfil" + new Date().getTime(), new AsyncCallback<BackendlessFile>() {
                    @Override
                    public void handleResponse(final BackendlessFile backendlessFile) {
                        kids.setPhoto(backendlessFile.getFileURL());

                        Backendless.Persistence.save(kids, new AsyncCallback<Kids>() {
                            public void handleResponse(Kids response) {
                                sauvegardeChange(response);

                                Intent returnIntent = new Intent();
                                returnIntent.putExtra("KidsNew", true);
                                setResult(Activity.RESULT_OK, returnIntent);
                                finish();
                            }

                            public void handleFault( BackendlessFault fault ) {
                                Toast.makeText(NewKids.this,getResources().getString(R.string.problem_connexion), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        Toast.makeText(NewKids.this,getResources().getString(R.string.problem_connexion), Toast.LENGTH_SHORT).show();
                        rlBlock.setVisibility(View.GONE);
                    }
                });
            } else {
                Backendless.Persistence.save(kids, new AsyncCallback<Kids>() {
                    public void handleResponse(Kids response) {
                        sauvegardeChange(response);

                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("KidsNew", true);
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }

                    public void handleFault( BackendlessFault fault ) {
                        Toast.makeText(NewKids.this, getResources().getString(R.string.problem_connexion), Toast.LENGTH_SHORT).show();
                        rlBlock.setVisibility(View.GONE);
                    }
                });
            }
        } else {
            Toast.makeText(NewKids.this, getResources().getString(R.string.problem_connexion), Toast.LENGTH_SHORT).show();
            rlBlock.setVisibility(View.GONE);
        }

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

                        if (ContextCompat.checkSelfPermission(NewKids.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            if (ContextCompat.checkSelfPermission(NewKids.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                    startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                                }

                            } else {
                                ActivityCompat.requestPermissions(NewKids.this, new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CAMERA);
                            }
                        } else {
                            ActivityCompat.requestPermissions(NewKids.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                        }

                        dialog.dismiss();
                        break;

                    case 1:
                        typeImage = 1;

                        if (ContextCompat.checkSelfPermission(NewKids.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                        } else {
                            ActivityCompat.requestPermissions(NewKids.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                        }
                        dialog.dismiss();
                        break;

                }
            }
        });
    }

    private List<Kids> getListKids() {
        String jsonKids = prefs.getString("MesKids", null);
        if (jsonKids != null) {
            Kids[] kidItems = gson.fromJson(jsonKids, Kids[].class);
            allKids = Arrays.asList(kidItems);
            allKids = new ArrayList(allKids);
        }
        return allKids;
    }

    public void sauvegardeChange(Kids kids) {
        allKids.add(kids);
        String jsonCats = gson.toJson(allKids);
        editor.putString("MesKids", jsonCats);
        editor.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (typeImage == 0) {
                    if (ContextCompat.checkSelfPermission(NewKids.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                        }
                    } else {
                        ActivityCompat.requestPermissions(NewKids.this, new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CAMERA);
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
