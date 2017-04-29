package com.yako.safekids.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.safekids.android.R;
import com.yako.safekids.activity.KidsDetails;
import com.yako.safekids.activity.MainActivity;
import com.yako.safekids.model.Kids;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by macbook on 12/05/15.
 */
public class KidsItemAdapter extends RecyclerView.Adapter<KidsItemAdapter.MyViewHolder> {

    private final LayoutInflater inflater;
    private final Typeface typeFace;
    private final DisplayImageOptions options;
    public List<Kids> listItems = new ArrayList<Kids>();
    Context context;

    public KidsItemAdapter(Context context, List<Kids> listItems) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.listItems = listItems;

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
        View v = inflater.inflate(R.layout.rv_kids_item, parent, false);
        MyViewHolder myHolder = new MyViewHolder(v);
        return myHolder;
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Kids item = listItems.get(position);

        holder.tv_name.setText(item.getName());

        if (item.getPhoto() != null && !item.getPhoto().equals("")) {
            ImageLoader.getInstance().displayImage(item.getPhoto(), holder.imgKids, options);
        } else {
            holder.imgKids.setImageResource(R.drawable.ic_avatar_kids);
        }

        boolean isActif = false;
        for (int i = 0; i < MainActivity.listStatus.size(); i++) {
            if (MainActivity.listStatus.get(i).equals(item.getObjectId())) {
                isActif = true;
            }
        }
        if (isActif) {
            holder.state.setBackgroundResource(R.drawable.circle_green);
        } else {
            holder.state.setBackgroundResource(R.drawable.circle_red);
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
        RoundedImageView imgKids;
        View state;
        TextView tv_name;


        public MyViewHolder(View itemView) {
            super(itemView);

            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            imgKids = (RoundedImageView) itemView.findViewById(R.id.imgKids);
            state = itemView.findViewById(R.id.state);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    KidsDetails.start(context, listItems.get(getPosition()));
                }
            });
        }
    }

}
