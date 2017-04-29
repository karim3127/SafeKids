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
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.safekids.android.R;
import com.yako.safekids.activity.KidsDetails;
import com.yako.safekids.activity.MainActivity;
import com.yako.safekids.model.Kids;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by macbook on 12/05/15.
 */
public class KidsListItemAdapter extends RecyclerView.Adapter<KidsListItemAdapter.MyViewHolder> {

    private final LayoutInflater inflater;
    private final Typeface typeFace;
    private final DisplayImageOptions options;
    private final SharedPreferences prefs;
    private final String langage;
    public List<Kids> listItems = new ArrayList<Kids>();
    Context context;

    public KidsListItemAdapter(Context context, List<Kids> listItems) {
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
            v = inflater.inflate(R.layout.rv_kids_list_item, parent, false);
        } else {
            v = inflater.inflate(R.layout.rv_kids_list_item_en, parent, false);
        }
        MyViewHolder myHolder = new MyViewHolder(v);
        return myHolder;
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Kids item = listItems.get(position);

        holder.txtName.setText(item.getName());

        if (item.getPhoto() != null && !item.getPhoto().equals("")) {
            ImageLoader.getInstance().displayImage(item.getPhoto(), holder.imgKids, options);
        } else {
            holder.imgKids.setImageResource(R.drawable.ic_avatar_kids);
        }

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
                if (MainActivity.listStatus.get(i).equals(item.getObjectId())) {
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
                age = "1 "+context.getResources().getString(R.string.years_single);
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

    public Kids getItem(int position) {
        return listItems.get(position);
    }

    public void addContact(Kids item) {
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

        public MyViewHolder(View itemView) {
            super(itemView);

            imgKids = (CircleImageView) itemView.findViewById(R.id.imgKids);
            imgState = (ImageView) itemView.findViewById(R.id.imgState);
            txtState = (TextView) itemView.findViewById(R.id.txtState);
            txtName = (TextView) itemView.findViewById(R.id.txtName);
            txtAge = (TextView) itemView.findViewById(R.id.txtAge);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    KidsDetails.start(context, listItems.get(getPosition()));
                }
            });
        }
    }

}
