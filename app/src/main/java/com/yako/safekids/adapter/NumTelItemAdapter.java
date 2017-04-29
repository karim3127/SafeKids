package com.yako.safekids.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.safekids.android.R;
import com.yako.safekids.model.PhoneNumber;
import com.yako.safekids.util.ConnectionDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by macbook on 12/05/15.
 */
public class NumTelItemAdapter extends RecyclerView.Adapter<NumTelItemAdapter.MyViewHolder> {

    private final LayoutInflater inflater;
    private final Typeface typeFace;
    private final DisplayImageOptions options;
    private final SharedPreferences prefs;
    private final String langage;
    private final SharedPreferences.Editor editor;
    private final Gson gson;
    public List<PhoneNumber> listItems = new ArrayList<PhoneNumber>();
    Context context;
    ConnectionDetector connectionDetector;

    public NumTelItemAdapter(Context context, List<PhoneNumber> listItems) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.listItems = listItems;
        connectionDetector = new ConnectionDetector(context);
        prefs = context.getSharedPreferences("com.yako.safekids", Context.MODE_PRIVATE);
        editor = prefs.edit();
        gson = new Gson();
        langage = prefs.getString("Langage", "ar");

        typeFace = Typeface.createFromAsset(context.getAssets(), "fonts/helvetica_medium.ttf");
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .showImageOnLoading(R.drawable.ic_white)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (langage.equals("ar")) {
            v = inflater.inflate(R.layout.rv_numtel_item, parent, false);
        } else {
            v = inflater.inflate(R.layout.rv_numtel_item_en, parent, false);
        }
        MyViewHolder myHolder = new MyViewHolder(v);
        return myHolder;
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        PhoneNumber item = listItems.get(position);

        holder.tv_num.setText("+" + item.getCodeCountry() + " " + item.getNumero());
        String index;
        if (langage.equals("ar")) {
            switch (position) {
                case 0 :
                    index = "الأول";
                    break;
                case 1 :
                    index = "الثاني";
                    break;
                default :
                    index = "الثالث";
                    break;
            }
            holder.tv_index.setText(" الرقم " + index);

        } else {
            switch (position) {
                case 0 :
                    index = ""+context.getResources().getString(R.string.First);
                    break;
                case 1 :
                    index = ""+context.getResources().getString(R.string.Second);
                    break;
                default :
                    index = ""+context.getResources().getString(R.string.third);
                    break;
            }
            holder.tv_index.setText(index + " "+context.getResources().getString(R.string.number));

        }

    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public PhoneNumber getItem(int position) {
        return listItems.get(position);
    }

    public void addNumber(PhoneNumber item) {
        int pos = getItemCount();
        listItems.add(pos, item);
        notifyItemInserted(pos);
    }

    public void deleteContact(int position) {
        listItems.remove(position);
        notifyItemRemoved(position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_num;
        TextView tv_index;
        ImageView imgMore;

        public MyViewHolder(View itemView) {
            super(itemView);

            tv_index = (TextView) itemView.findViewById(R.id.tv_index);
            tv_num = (TextView) itemView.findViewById(R.id.tv_num);
            imgMore = (ImageView) itemView.findViewById(R.id.imgMore);
            imgMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popup = new PopupMenu(context, imgMore);
                    //Inflating the Popup using xml file
                    if (langage.equals("ar")) {
                        popup.getMenuInflater().inflate(R.menu.menu_more_phone, popup.getMenu());
                    } else {
                        popup.getMenuInflater().inflate(R.menu.menu_more_phone_en, popup.getMenu());
                    }
                    popup.show();
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();

                            if(id == R.id.action_delete) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage(context.getResources().getString(R.string.valide_delete));
                                builder.setPositiveButton(""+context.getResources().getString(R.string.Delete), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //do your work here
                                        if (connectionDetector.isConnectingToInternet()) {
                                            Backendless.Persistence.of(PhoneNumber.class).remove(listItems.get(getPosition()), new AsyncCallback<Long>() {
                                                @Override
                                                public void handleResponse(Long aLong) {
                                                    listItems.remove(getPosition());
                                                    notifyItemRemoved(getPosition());


                                                    String jsonPhone = gson.toJson(listItems);
                                                    editor.putString("MesPhones", jsonPhone);
                                                    editor.commit();
                                                }

                                                @Override
                                                public void handleFault(BackendlessFault backendlessFault) {

                                                }
                                            });
                                        } else {
                                            Toast.makeText(context,context.getResources().getString(R.string.problem_connexion), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                builder.setNegativeButton(""+context.getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                AlertDialog alert = builder.create();
                                alert.show();
                                alert.getWindow().getAttributes();

                                Button btnNegatif = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                                btnNegatif.setTextColor(context.getResources().getColor(R.color.colorRed));

                                Button btnPositif = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                                btnPositif.setTextColor(context.getResources().getColor(R.color.colorPrimary));

                            }

                            return false;
                        }
                    });
                }
            });
        }
    }

}
