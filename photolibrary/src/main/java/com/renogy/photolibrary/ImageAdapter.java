package com.renogy.photolibrary;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wyb
 * Date :2019/11/8 0008 16:09
 * Description: 查看图片的适配器
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {

    private Context context;
    private List<Uri> uriList;
    private OnItemClick onItemClick;

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public ImageAdapter(Context context, List<Uri> uriList) {
        this.context = context;
        this.uriList = uriList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.show_item_image, viewGroup));
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        if (uriList != null && uriList.size() > 0) {
            Glide.with(context).load(uriList.get(i)).into(myViewHolder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return uriList == null ? 0 : uriList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            if (onItemClick != null) {
                onItemClick.onItemClickListener(itemView, getAdapterPosition());
            }
        }
    }

    public interface OnItemClick {

        void onItemClickListener(View view, int position);
    }
}
