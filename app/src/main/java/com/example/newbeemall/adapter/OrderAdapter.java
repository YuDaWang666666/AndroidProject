package com.example.newbeemall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.newbeemall.R;
import com.example.newbeemall.model.OrderItem;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private List<OrderItem> orderList;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public OrderAdapter(List<OrderItem> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderItem order = orderList.get(position);

        // 订单时间
        holder.tvOrderTime.setText("订单时间：" + order.getCreateTime());
        // 订单状态
        holder.tvStatus.setText(order.getStatusText());
        // 订单总价
        holder.tvTotalPrice.setText("¥" + (order.getTotalPrice() / 100.0));

        // 嵌套的商品列表
        if (order.getNewBeeMallOrderItemVOS() != null && !order.getNewBeeMallOrderItemVOS().isEmpty()) {
            OrderGoodsAdapter goodsAdapter = new OrderGoodsAdapter(order.getNewBeeMallOrderItemVOS(), context);
            holder.rvGoods.setLayoutManager(new LinearLayoutManager(context));
            holder.rvGoods.setAdapter(goodsAdapter);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(position);
        });
    }

    @Override
    public int getItemCount() { return orderList.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderTime, tvStatus, tvTotalPrice;
        RecyclerView rvGoods;

        ViewHolder(View v) {
            super(v);
            tvOrderTime = v.findViewById(R.id.tv_order_time);
            tvStatus = v.findViewById(R.id.tv_status);
            tvTotalPrice = v.findViewById(R.id.tv_total_price);
            rvGoods = v.findViewById(R.id.rv_goods);
        }
    }
}
