package com.example.newbeemall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.newbeemall.R;
import com.example.newbeemall.model.GoodsInfo;
import com.example.newbeemall.util.Constants;
import java.util.List;

public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.ViewHolder> {

    private List<GoodsInfo> goodsList;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public GoodsAdapter(List<GoodsInfo> goodsList, Context context) {
        this.goodsList = goodsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goods_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GoodsInfo goods = goodsList.get(position);

        String imgUrl = goods.getGoodsCoverImg();
        if (imgUrl != null && !imgUrl.startsWith("http")) imgUrl = Constants.IMG_BASE_URL + imgUrl;
        Glide.with(context).load(imgUrl).into(holder.ivGoods);

        holder.tvName.setText(goods.getGoodsName());
        holder.tvPrice.setText("¥" + goods.getSellingPrice());
        holder.tvOriginalPrice.setText("¥" + goods.getOriginalPrice());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(position);
        });
    }

    @Override
    public int getItemCount() { return goodsList.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivGoods;
        TextView tvName, tvPrice, tvOriginalPrice;

        ViewHolder(View v) {
            super(v);
            ivGoods = v.findViewById(R.id.iv_goods);
            tvName = v.findViewById(R.id.tv_name);
            tvPrice = v.findViewById(R.id.tv_price);
            tvOriginalPrice = v.findViewById(R.id.tv_original_price);
        }
    }
}
