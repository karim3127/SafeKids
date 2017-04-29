package com.yako.safekids.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.safekids.android.R;
import com.yako.safekids.activity.MainActivity;
import com.yako.safekids.activity.Notification;
import com.yako.safekids.model.AlertModel;
import com.yako.safekids.model.Kids;
import com.yako.safekids.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by macbook on 12/05/15.
 */
public class AlertListItemAdapter extends RecyclerView.Adapter<AlertListItemAdapter.MyViewHolder> {

    private final LayoutInflater inflater;
    private final Typeface typeFace;
    private final DisplayImageOptions options;
    private final SharedPreferences prefs;
    private final String langage;
    public List<AlertModel> listItems = new ArrayList<AlertModel>();
    Context context;

    public AlertListItemAdapter(Context context, List<AlertModel> listItems) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.listItems = listItems;
        prefs = context.getSharedPreferences("com.yako.safekids", Context.MODE_PRIVATE);
        langage = prefs.getString("Langage", "ar");

        typeFace = Typeface.createFromAsset(context.getAssets(), "fonts/helvetica_medium.ttf");
        options = new DisplayImageOptions.Builder()
                .showImageOnFail(R.drawable.ic_avatar_kids)
                .showImageOnLoading(R.drawable.ic_avatar_kids)
                .showImageForEmptyUri(R.drawable.ic_avatar_kids)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .showImageOnLoading(R.drawable.ic_white)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new FadeInBitmapDisplayer(10)).build();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (langage.equals("ar")) {
            v = inflater.inflate(R.layout.rv_alarme_list_item, parent, false);
        } else {
            v = inflater.inflate(R.layout.rv_alarme_list_item_en, parent, false);
        }
        MyViewHolder myHolder = new MyViewHolder(v);
        return myHolder;
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        AlertModel alert = listItems.get(position);
        Kids item = alert.getListKids().get(0);

        holder.txtName.setText(item.getName());

        if (item.getPhoto() != null && !item.getPhoto().equals("")) {
            ImageLoader.getInstance().displayImage(item.getPhoto(), holder.imgKids, options);
        } else {
            holder.imgKids.setImageResource(R.drawable.ic_avatar_kids);
        }
        if (langage.equals("ar")) {
            holder.txtDate.setText(TimeUtils.millisToLongDHMS(alert.getAlertDate().getTime()));
            //holder.txtDate.setText(TimeUtils.dateToString(alert.getAlertDate().getTime()));
        }else{
            holder.txtDate.setText(TimeUtils.dateToString(alert.getAlertDate().getTime()).replaceAll("١", "1").replaceAll("٢", "2").replaceAll("٣", "3")
                    .replaceAll("٤", "4").replaceAll("٥", "5").replaceAll("٦", "6").replaceAll("٧", "7")
                    .replaceAll("٨", "8").replaceAll("٩", "9").replaceAll("٠", "0"));
        }

        String retard;
        if (langage.equals("ar")) {
            retard = "بعد " + alert.getExtraTime() + " دق";
        } else {
            retard = context.getResources().getString(R.string.after)+" " + alert.getExtraTime() + " "+context.getResources().getString(R.string.min);
        }
        holder.txtRetard.setText(retard);

        if (langage.equals("ar")) {
            String age;
            if (item.getAge() == 1) {
                age = "سنة";
            } else if (item.getAge() == 2) {
                age = "سنتان";
            } else if (item.getAge() < 11) {
                age = item.getAge() +  " سنوات" ;
            } else {
                age = item.getAge() +  " سنة" ;
            }
            holder.txtAge.setText(age);


            boolean isActif = false;
            for (int i = 0; i < MainActivity.listStatus.size(); i++) {
                if (MainActivity.listStatus.get(i).equals(item.getObjectId()) && (position == listItems.size() - 1)) {
                    isActif = true;
                }
            }
            if (isActif) {
                holder.imgState.setImageResource(R.drawable.ic_state_enable);
                holder.txtState.setText("فعال");
                holder.txtState.setTextColor(Color.parseColor("#5bba47"));
            } else {
                holder.imgState.setImageResource(R.drawable.ic_state_disable);
                holder.txtState.setText("غير فعال");
                holder.txtState.setTextColor(context.getResources().getColor(R.color.colorRed));
            }

        } else {
            String age;
            if (item.getAge() == 1) {
                age = "1 "+context.getResources().getString(R.string.one_year_normal_alert);
            } else {
                age = item.getAge() + " "+context.getResources().getString(R.string.years);
            }
            holder.txtAge.setText(age);

            boolean isActif = false;
            for (int i = 0; i < MainActivity.listStatus.size(); i++) {
                if (MainActivity.listStatus.get(i).equals(item.getObjectId())) {
                    isActif = true;
                }
            }
            if (isActif) {
                holder.imgState.setImageResource(R.drawable.ic_state_enable);
                holder.txtState.setText(context.getResources().getString(R.string.Enable));
                holder.txtState.setTextColor(Color.parseColor("#5bba47"));
            } else {
                holder.imgState.setImageResource(R.drawable.ic_state_disable);
                holder.txtState.setText(context.getResources().getString(R.string.Desable));
                holder.txtState.setTextColor(context.getResources().getColor(R.color.colorRed));
            }

        }
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public AlertModel getItem(int position) {
        return listItems.get(position);
    }

    public void addContact(AlertModel item) {
        int pos = getItemCount();
        listItems.add(pos, item);
        notifyItemInserted(pos);
    }

    public void deleteContact(int position) {
        listItems.remove(position);
        notifyItemRemoved(position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView imgKids;
        ImageView imgState;
        TextView txtState;
        TextView txtName;
        TextView txtAge;
        TextView txtRetard;
        TextView txtDate;

        public MyViewHolder(View itemView) {
            super(itemView);

            imgKids = (CircleImageView) itemView.findViewById(R.id.imgKids);
            imgState = (ImageView) itemView.findViewById(R.id.imgState);
            txtState = (TextView) itemView.findViewById(R.id.txtState);
            txtName = (TextView) itemView.findViewById(R.id.txtName);
            txtAge = (TextView) itemView.findViewById(R.id.txtAge);
            txtRetard = (TextView) itemView.findViewById(R.id.txtRetard);
            txtDate = (TextView) itemView.findViewById(R.id.txtdate);
            imgState.setVisibility(View.GONE);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    String positiveBtnText = "";
                    String negativeBtnText = "";

                    if (langage.equals("ar")) {
                        builder.setMessage(" الرجاء تأكيد الحذف ؟");
                        positiveBtnText = "حذف";
                        negativeBtnText = "تراجع";
                    } else {
                        builder.setMessage(""+context.getResources().getString(R.string.valide_delete));
                        positiveBtnText = ""+context.getResources().getString(R.string.Delete);
                        negativeBtnText = ""+context.getResources().getString(R.string.Cancel);
                    }

                    builder.setPositiveButton(positiveBtnText, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((Notification) context).deleteAlert(getPosition());
                            dialog.dismiss();
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
                    btnNegatif.setTextColor(context.getResources().getColor(R.color.colorRed));

                    Button btnPositif = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                    btnPositif.setTextColor(context.getResources().getColor(R.color.colorPrimary));

                }
            });

        }
    }

}
