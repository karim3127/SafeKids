package com.yako.safekids.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.safekids.android.R;
import com.yako.safekids.activity.KidsPlaning;
import com.yako.safekids.model.KidsPlaningModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by macbook on 12/05/15.
 */
public class PlaningItemAdapter extends RecyclerView.Adapter<PlaningItemAdapter.MyViewHolder> {

    private final LayoutInflater inflater;
    private final Typeface typeFace;
    private final DisplayImageOptions options;
    private final SharedPreferences prefs;
    private final String langage;
    public List<KidsPlaningModel> listItems = new ArrayList<KidsPlaningModel>();
    Context context;

    public PlaningItemAdapter(Context context, List<KidsPlaningModel> listItems) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.listItems = listItems;

        prefs = context.getSharedPreferences("com.yako.safekids", Context.MODE_PRIVATE);
        langage = prefs.getString("Langage", "ar");

        typeFace = Typeface.createFromAsset(context.getAssets(), "fonts/ElMessiri-Medium.ttf");
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
            v = inflater.inflate(R.layout.rv_planing_item, parent, false);
        } else {
            v = inflater.inflate(R.layout.rv_planing_item_en, parent, false);
        }
        MyViewHolder myHolder = new MyViewHolder(v);
        return myHolder;
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        KidsPlaningModel item = listItems.get(position);

        boolean am = false;
        if (item.getTimeMode().equals("am")) {
            if (langage.equals("ar")) {
                holder.txtDay.setText("ص");
            } else {
                holder.txtDay.setText("am");
            }
            am = true;
        } else {
            if (langage.equals("ar")) {
                holder.txtDay.setText("م");
            } else {
                holder.txtDay.setText("pm");
            }
        }

        String time = ((item.getHoure() > 9) ? item.getHoure() : "0" + item.getHoure()) + ":" + ((item.getMinute() > 9) ? item.getMinute() : "0" + item.getMinute());
        holder.txtTime.setText(time);

        if (am) {
            if (item.getHoure() >= 6) {
                holder.content.setBackgroundResource(R.drawable.bg_day);
            } else {
                holder.content.setBackgroundResource(R.drawable.bg_night);
            }
        } else {
            if (item.getHoure() <= 6) {
                holder.content.setBackgroundResource(R.drawable.bg_day);
            } else {
                holder.content.setBackgroundResource(R.drawable.bg_night);
            }
        }
        for (int i = 0; i < item.getDays().size(); i++) {
            switch (item.getDays().get(i)) {
                case 7 :
                    holder.txtJ1.setTextColor(Color.WHITE);
                    holder.txtJ1.setTypeface(typeFace);
                    //holder.txtJ1.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.text_normal_size));
                    break;
                case 6 :
                    holder.txtJ2.setTextColor(Color.WHITE);
                    holder.txtJ2.setTypeface(typeFace);
                    break;
                case 5 :
                    holder.txtJ3.setTextColor(Color.WHITE);
                    holder.txtJ3.setTypeface(typeFace);
                    break;
                case 4 :
                    holder.txtJ4.setTextColor(Color.WHITE);
                    holder.txtJ4.setTypeface(typeFace);
                    break;
                case 3 :
                    holder.txtJ5.setTextColor(Color.WHITE);
                    holder.txtJ5.setTypeface(typeFace);
                    break;
                case 2 :
                    holder.txtJ6.setTextColor(Color.WHITE);
                    holder.txtJ6.setTypeface(typeFace);
                    break;
                case 1 :
                    holder.txtJ7.setTextColor(Color.WHITE);
                    holder.txtJ7.setTypeface(typeFace);
                    break;

            }
        }

    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public KidsPlaningModel getItem(int position) {
        return listItems.get(position);
    }

    public void addItem(KidsPlaningModel item) {
        listItems.add(item);
        notifyItemInserted(getItemCount());
    }

    public void modifyItem(int position, KidsPlaningModel item) {
        listItems.set(position, item);
        notifyItemChanged(position);
    }

    public void deleteItem(int position) {
        listItems.remove(position);
        notifyItemRemoved(position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout content;

        TextView txtJ1, txtJ2, txtJ3, txtJ4, txtJ5, txtJ6, txtJ7;
        TextView txtDay, txtTime;
        public MyViewHolder(View itemView) {
            super(itemView);

            content = (RelativeLayout) itemView.findViewById(R.id.content);

            txtJ1 = (TextView) itemView.findViewById(R.id.txtJ1);
            txtJ2 = (TextView) itemView.findViewById(R.id.txtJ2);
            txtJ3 = (TextView) itemView.findViewById(R.id.txtJ3);
            txtJ4 = (TextView) itemView.findViewById(R.id.txtJ4);
            txtJ5 = (TextView) itemView.findViewById(R.id.txtJ5);
            txtJ6 = (TextView) itemView.findViewById(R.id.txtJ6);
            txtJ7 = (TextView) itemView.findViewById(R.id.txtJ7);

            txtDay = (TextView) itemView.findViewById(R.id.txtDay);
            txtTime = (TextView) itemView.findViewById(R.id.txtTime);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((KidsPlaning) context).showPopupPlaningUpdate(listItems.get(getPosition()), getPosition());
                }
            });

        }
    }

}
