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
import com.example.newbeemall.model.OrderItem;
import com.example.newbeemall.util.Constants;
import java.util.List;

public class OrderGoodsAdapter extends RecyclerView.Adapter<OrderGoodsAdapter.ViewHolder> {

    private List<OrderItem.GoodsItem> goodsList;
    private Context context;

    public OrderGoodsAdapter(List<OrderItem.GoodsItem> goodsList, Context context) {
        this.goodsList = goodsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_goods, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderItem.GoodsItem item = goodsList.get(position);
        holder.tvName.setText(item.getGoodsName());
        holder.tvPrice.setText("¥" + item.getSellingPrice() + ".");
        holder.tvCount.setText("x" + item.getGoodsCount());

        String imgUrl = item.getGoodsCoverImg();
        if (imgUrl != null && !imgUrl.startsWith("http")) imgUrl = Constants.IMG_BASE_URL + imgUrl;
        Glide.with(context).load(imgUrl).into(holder.ivGoods);
    }

    @Override
    public int getItemCount() { return goodsList.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivGoods;
        TextView tvName, tvPrice, tvCount;

        ViewHolder(View v) {
            super(v);
            ivGoods = v.findViewById(R.id.iv_goods);
            tvName = v.findViewById(R.id.tv_name);
            tvPrice = v.findViewById(R.id.tv_price);
            tvCount = v.findViewById(R.id.tv_count);
        }
    }
}
