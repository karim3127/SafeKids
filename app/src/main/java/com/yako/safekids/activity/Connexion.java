package com.yako.safekids.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.DeviceRegistration;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;
import com.google.gson.Gson;
import com.wang.avi.AVLoadingIndicatorView;
import com.safekids.android.R;
import com.yako.safekids.model.BackendSetting;
import com.yako.safekids.model.Kids;
import com.yako.safekids.model.PhoneNumber;
import com.yako.safekids.util.ConnectionDetector;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Connexion extends AppCompatActivity {

    private static AppCompatActivity activity_Dashbord;
    private Animation slideUp;

    @BindView(R.id.rlLogo)
    RelativeLayout rlLogo;

    @BindView(R.id.llSignin)
    LinearLayout llSignin;

    @BindView(R.id.et_login)
    EditText et_login;
    
    @BindView(R.id.et_pass)
    EditText et_pass;

    @BindView(R.id.avloadingIndicatorView)
    AVLoadingIndicatorView avloadingIndicatorView;

    @BindView(R.id.txtSignin)
    TextView txtSignin;

    @BindView(R.id.txtSignup)
    TextView txtSignup;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    Gson gson = new Gson();
    boolean isAnimationFinish = false;
    ConnectionDetector connectionDetector;
    private String langage;
    boolean finishPhones = false;
    boolean finishKids = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences("com.yako.safekids", Context.MODE_PRIVATE);
        langage = prefs.getString("Langage", "ar");

        if (langage.equals("ar")) {
            setContentView(R.layout.activity_connexion);
        } else {
            setContentView(R.layout.activity_connexion_en);
        }

        ButterKnife.bind(this);

        connectionDetector = new ConnectionDetector(this);

        editor = prefs.edit();

        rlLogo.setScaleX(0);
        rlLogo.setScaleY(0);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(rlLogo, "alpha", 0, 1),
                ObjectAnimator.ofFloat(rlLogo, "scaleX", 0, 1),
                ObjectAnimator.ofFloat(rlLogo, "scaleY", 0, 1)
        );
        animatorSet.setInterpolator(new OvershootInterpolator());
        animatorSet.setDuration(600);
        animatorSet.setStartDelay(600);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAnimationFinish = true;
            }
        });
        animatorSet.start();

        if (prefs.getString("Login", "").equals("connecte")) {
            getListKids();

            // check to register device
            //registerDevice(backendlessUser);
            verifierLogin();

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(Connexion.this, MainActivity.class));
                }
            }, 1600);

        } else {
            slideUp = AnimationUtils.loadAnimation(this, R.anim.up);

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

                                View viewFocus = getCurrentFocus();
                                if (viewFocus != null) {
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(viewFocus.getWindowToken(), 0);
                                    txtSignin.clearFocus();
                                }

                                return true;
                            default:
                                break;
                        }
                    }
                    return false;
                }
            });

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    llSignin.startAnimation(slideUp);
                    llSignin.setVisibility(View.VISIBLE);
                }
            }, 1200);
        }

    }

    private void verifierLogin() {

        try {
            Backendless.UserService.isValidLogin(new AsyncCallback<Boolean>() {
                @Override
                public void handleResponse(Boolean aBoolean) {
                    // Toast.makeText(getApplicationContext(), "yyyy" + aBoolean, Toast.LENGTH_SHORT).show();
                    if (aBoolean) {
                        Backendless.UserService.findById(prefs.getString("UserId", ""), new AsyncCallback<BackendlessUser>() {
                            @Override
                            public void handleResponse(BackendlessUser backendlessUser) {
                                // register Device to push
                                System.out.println("problem "+backendlessUser);
                                registerDevice(backendlessUser);
                                finish();
                            }

                            @Override
                            public void handleFault(BackendlessFault backendlessFault) {
                                System.out.println("problem "+backendlessFault);
                                finish();
                            }
                        });

                    } else {
                        loginUser();
                    }
                }

                @Override
                public void handleFault(BackendlessFault backendlessFault) {
                    //Toast.makeText(getApplicationContext(), "yyyyyy" + backendlessFault, Toast.LENGTH_SHORT).show();
                    loginUser();
                }
            });
        } catch (Exception e) {
            loginUser();
        }

    }

    private void loginUser() {

        Backendless.UserService.login(prefs.getString("loginUser", ""), prefs.getString("passwordUser", ""), new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser backendlessUser) {
                // register Device to push
                registerDevice(backendlessUser);
                finish();
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                //Toast.makeText(getApplicationContext(), "oooo" + backendlessFault, Toast.LENGTH_SHORT).show();
                System.out.println("problem "+backendlessFault);
                // ici metter if(backendlessFault.contain("Invalid login or")
                if (backendlessFault.toString().toLowerCase().contains("invalid login or") ||
                        backendlessFault.toString().toLowerCase().contains("user login cannot be null")) {
                    System.out.println("problem il est ou");
                    String json = prefs.getString("Alert", "");
                    SharedPreferences.Editor editor = prefs.edit();

                    editor.clear();
                    editor.apply();

                    editor.putString("Langage", langage).apply();
                    editor.putString("Alert", json).apply();

                    Intent intent = new Intent(Connexion.this, SplashScreen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    ActivityCompat.finishAffinity(Connexion.this);
                    
                }else{
                    System.out.println("problem il est ici");
                }
            }
        }, true);
    }

    @OnClick(R.id.txtSignin)
    public void signin() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (et_login.length() == 0) {
                    Toast.makeText(Connexion.this, getResources().getString(R.string.add_username), Toast.LENGTH_SHORT).show();
                } else if (et_pass.length() == 0) {
                    Toast.makeText(Connexion.this, ""+getResources().getString(R.string.add_password), Toast.LENGTH_SHORT).show();
                } else {
                    if (connectionDetector.isConnectingToInternet()) {
                        connexion();
                    } else {
                        Toast.makeText(Connexion.this,getResources().getString(R.string.problem_connexion), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, 200);

    }

    public void connexion() {
        avloadingIndicatorView.setVisibility(View.VISIBLE);
        txtSignin.setEnabled(false);
        txtSignup.setEnabled(false);

        Backendless.UserService.login(et_login.getText().toString().trim(), et_pass.getText().toString().trim(), new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser backendlessUser) {

                editor.putString("Login", "connecte");
                editor.putString("UserId", backendlessUser.getUserId());
                editor.putString("loginUser", et_login.getText().toString().trim());
                editor.putString("passwordUser", et_pass.getText().toString().trim());
                editor.putString("name", (String) backendlessUser.getProperty("name"));
                editor.putString("username", (String) backendlessUser.getProperty("username"));
                editor.putString("city", (String) backendlessUser.getProperty("city"));
                editor.putString("phoneNumber", (String) backendlessUser.getProperty("phoneNumber"));
                editor.putString("codeCountry", (String) backendlessUser.getProperty("codeCountry"));
                editor.putString("carType", (String) backendlessUser.getProperty("carType"));
                editor.putString("carColor", (String) backendlessUser.getProperty("carColor"));
                editor.putString("carNumber", (String) backendlessUser.getProperty("carNumber"));

                String imgUrl = (backendlessUser.getProperty("photo") != null) ? (String) backendlessUser.getProperty("photo") : "";
                editor.putString("UserPhoto", imgUrl);

                editor.commit();

                getListKids();
                getListPhones();

                // register Device to push
                registerDevice(backendlessUser);
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Toast.makeText(Connexion.this, getResources().getString(R.string.add_valid_info), Toast.LENGTH_SHORT).show();

                avloadingIndicatorView.setVisibility(View.GONE);
                txtSignin.setEnabled(true);
                txtSignup.setEnabled(true);
            }
        },true);
    }

    private void registerDevice(final BackendlessUser backendlessUser) {

        Backendless.Messaging.registerDevice(BackendSetting.GOOGLE_PROJECT_ID, BackendSetting.DEFAULT_CHANNEL, new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void response) {
                Backendless.Messaging.getDeviceRegistration(new AsyncCallback<DeviceRegistration>() {
                    @Override
                    public void handleResponse(DeviceRegistration deviceRegistration) {
                        try {
                            backendlessUser.setProperty("deviceRegistration", deviceRegistration.getDeviceId());
                            Backendless.UserService.update(backendlessUser, new AsyncCallback<BackendlessUser>() {
                                @Override
                                public void handleResponse(BackendlessUser backendlessUser) {
                                    System.out.println("problem 0 "+backendlessUser);
                                }

                                @Override
                                public void handleFault(BackendlessFault backendlessFault) {
                                    System.out.println("problem 1"+backendlessFault);
                                }
                            });
                        } catch (Exception e) {
                            System.out.println("problem 2 "+e);
                        }
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        System.out.println("problem 3"+backendlessFault);
                    }
                });
            }

            @Override
            public void handleFault(BackendlessFault fault) {
            }
        });
    }

    @OnClick(R.id.txtSignup)
    public void signup() {
        startActivity(new Intent(this, SignUp.class));
    }

    public void getListKids() {

        StringBuilder whereClause = new StringBuilder();
        whereClause.append("user.objectId = '" + prefs.getString("UserId", "") + "'");

        BackendlessDataQuery query = new BackendlessDataQuery();
        query.setWhereClause(whereClause.toString());

        QueryOptions queryOptions = new QueryOptions();
        queryOptions.setRelationsDepth(1);

        query.setQueryOptions(queryOptions);
        query.setPageSize(100);

        Backendless.Data.of(Kids.class).find(query, new AsyncCallback<BackendlessCollection<Kids>>() {
            @Override
            public void handleResponse(final BackendlessCollection<Kids> backendlessCollection) {
                if (Connexion.this != null) {
                    List<Kids> listkids = backendlessCollection.getCurrentPage();

                    String jsonKids = gson.toJson(listkids);
                    editor.putString("MesKids", jsonKids);
                    editor.commit();

                    finishKids = true;
                    if (finishKids && finishPhones) {
                        startActivity(new Intent(Connexion.this, MainActivity.class));
                        finish();
                    }
                }
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
            }
        });
    }

    public void getListPhones() {

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
                if (Connexion.this != null) {
                    List<PhoneNumber> listPhones = backendlessCollection.getCurrentPage();

                    String jsonPhone = gson.toJson(listPhones);
                    editor.putString("MesPhones", jsonPhone);
                    editor.commit();

                    finishPhones = true;
                    if (finishKids && finishPhones) {
                        startActivity(new Intent(Connexion.this, MainActivity.class));
                        finish();
                    }
                }
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {

            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
