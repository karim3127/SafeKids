package com.yako.safekids.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.safekids.android.R;
import com.yako.safekids.activity.About;
import com.yako.safekids.activity.CallUs;
import com.yako.safekids.activity.KidsList;
import com.yako.safekids.activity.Notice;
import com.yako.safekids.activity.Notification;
import com.yako.safekids.activity.PhoneNumberList;
import com.yako.safekids.activity.ProfilSetting;
import com.yako.safekids.activity.Setting;
import com.yako.safekids.activity.SplashScreen;
import com.yako.safekids.util.ConnectionDetector;

import de.hdodenhof.circleimageview.CircleImageView;


public class NavigationDrawerFragment extends Fragment {

    static SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    static DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .showImageOnLoading(R.drawable.ic_white)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    public static final String PREF_FILE_NAME = "testPref";
    public static final String KEY_USER_LEARNED_DRAWER = "user_learned_drawer";

    private boolean mUserLearnedDrawer;
    private boolean mFromSavedInstanceState;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private View containerView;

    ConnectionDetector connectionDetector;

    static CircleImageView imgUser;
    static TextView txtName;
    RelativeLayout rlSetting, rlSafKids, rlNotif, rlNumTel, rlNotice, rlCallus, rlAbout, rlShare;
    String langage;

    public NavigationDrawerFragment() {
        // Required empty public constructor
    }

    public static NavigationDrawerFragment newInstance() {
        NavigationDrawerFragment fragment = new NavigationDrawerFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        prefs = getContext().getSharedPreferences("com.yako.safekids", Context.MODE_PRIVATE);
        editor = prefs.edit();

        langage = prefs.getString("Langage", "ar");

        if (langage.equals("ar")) {
            return inflater.inflate(R.layout.fragment_home_drawer, container, false);
        } else {
            return inflater.inflate(R.layout.fragment_home_drawer_en, container, false);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserLearnedDrawer = Boolean.valueOf(readFromPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, "false"));

        if (savedInstanceState != null) {
            mFromSavedInstanceState = true;
        }
    }

    @Override
    public void onViewCreated(View layout, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(layout, savedInstanceState);

        connectionDetector = new ConnectionDetector(getContext());

        imgUser = (CircleImageView) layout.findViewById(R.id.imgUser);
        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DrawerLayout) getActivity().findViewById(R.id.drawer_layout)).closeDrawer(getActivity().findViewById(R.id.fragment_navigation_drawer));
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getContext(), ProfilSetting.class));
                    }
                }, 300);
            }
        });
        if (prefs.getString("UserPhoto", "").equals("")) {
            imgUser.setImageResource(R.drawable.ic_avatar);
        } else {
            ImageLoader.getInstance().displayImage(prefs.getString("UserPhoto", ""), imgUser, options);
        }

        txtName = (TextView) layout.findViewById(R.id.txtName);
        txtName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DrawerLayout) getActivity().findViewById(R.id.drawer_layout)).closeDrawer(getActivity().findViewById(R.id.fragment_navigation_drawer));
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getContext(), ProfilSetting.class));
                    }
                }, 300);
            }
        });
        txtName.setText(prefs.getString("name", ""));

        TextView tv_logout = (TextView) layout.findViewById(R.id.tv_logout);
        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DrawerLayout) getActivity().findViewById(R.id.drawer_layout)).closeDrawer(getActivity().findViewById(R.id.fragment_navigation_drawer));

                Backendless.UserService.logout(new AsyncCallback<Void>() {
                    @Override
                    public void handleResponse(Void aVoid) {

                        String json = prefs.getString("Alert", "");

                        SharedPreferences.Editor editor = prefs.edit();

                        editor.clear();
                        editor.commit();

                        editor.putString("Langage", langage).commit();
                        editor.putString("Alert", json).commit();

                        Intent intent = new Intent(getActivity(), SplashScreen.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        getActivity().finish();
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {

                    }
                });
            }
        });

        rlSetting = (RelativeLayout) layout.findViewById(R.id.rlSetting);
        rlSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DrawerLayout) getActivity().findViewById(R.id.drawer_layout)).closeDrawer(getActivity().findViewById(R.id.fragment_navigation_drawer));
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getContext(), Setting.class));
                    }
                }, 300);
            }
        });

        rlSafKids = (RelativeLayout) layout.findViewById(R.id.rlSafKids);
        rlSafKids.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DrawerLayout) getActivity().findViewById(R.id.drawer_layout)).closeDrawer(getActivity().findViewById(R.id.fragment_navigation_drawer));
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getContext(), KidsList.class));
                    }
                }, 300);
            }
        });

        rlNotif = (RelativeLayout) layout.findViewById(R.id.rlNotif);
        rlNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DrawerLayout) getActivity().findViewById(R.id.drawer_layout)).closeDrawer(getActivity().findViewById(R.id.fragment_navigation_drawer));
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getContext(), Notification.class));
                    }
                }, 300);
            }
        });

        rlNumTel = (RelativeLayout) layout.findViewById(R.id.rlNumTel);
        rlNumTel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DrawerLayout) getActivity().findViewById(R.id.drawer_layout)).closeDrawer(getActivity().findViewById(R.id.fragment_navigation_drawer));
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getContext(), PhoneNumberList.class));
                    }
                }, 300);
            }
        });

        rlNotice = (RelativeLayout) layout.findViewById(R.id.rlNotice);
        rlNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DrawerLayout) getActivity().findViewById(R.id.drawer_layout)).closeDrawer(getActivity().findViewById(R.id.fragment_navigation_drawer));
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getContext(), Notice.class));
                    }
                }, 300);
            }
        });

        rlCallus = (RelativeLayout) layout.findViewById(R.id.rlCallus);
        rlCallus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DrawerLayout) getActivity().findViewById(R.id.drawer_layout)).closeDrawer(getActivity().findViewById(R.id.fragment_navigation_drawer));
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getContext(), CallUs.class));
                    }
                }, 300);
            }
        });

        rlAbout = (RelativeLayout) layout.findViewById(R.id.rlAbout);
        rlAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DrawerLayout) getActivity().findViewById(R.id.drawer_layout)).closeDrawer(getActivity().findViewById(R.id.fragment_navigation_drawer));
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getContext(), About.class));
                    }
                }, 300);
            }
        });

        rlShare = (RelativeLayout) layout.findViewById(R.id.rlShare);
        rlShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DrawerLayout) getActivity().findViewById(R.id.drawer_layout)).closeDrawer(getActivity().findViewById(R.id.fragment_navigation_drawer));
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String text = "Hi, I'm using Safe Kids, a new safety app. Try to install it, and be in security!!";
                        text += " https://play.google.com/store/apps/details?id=com.safekids.android";
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Safe Kids");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);
                        startActivity(Intent.createChooser(sharingIntent, ""+getActivity().getResources().getString(R.string.text_share)));
                    }
                }, 300);
            }
        });

    }

    public static void setUserInfo() {
        if (prefs.getString("UserPhoto", "").equals("")) {
            imgUser.setImageResource(R.drawable.ic_avatar);
        } else {
            ImageLoader.getInstance().displayImage(prefs.getString("UserPhoto", ""), imgUser, options);
        }
        txtName.setText(prefs.getString("name", ""));
    }

    public void setUp(int fragment_id, DrawerLayout dl, final Toolbar tb) {
        containerView = getActivity().findViewById(fragment_id);
        mDrawerLayout = dl;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), dl, tb, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {

                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(containerView);
        }
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }

    public static void saveToPreferences(Context c, String preferenceName, String defaultValue) {
        SharedPreferences sp = c.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(preferenceName, defaultValue);
        editor.apply();
    }

    public static String readFromPreferences(Context c, String preferenceName, String defaultValue) {
        SharedPreferences sp = c.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getString(preferenceName, defaultValue);
    }
}
