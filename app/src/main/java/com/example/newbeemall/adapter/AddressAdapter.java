package com.example.newbeemall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.newbeemall.R;
import com.example.newbeemall.model.AddressInfo;
import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private List<AddressInfo> addressList;
    private Context context;
    private boolean selectMode;
    private OnItemClickListener clickListener;
    private OnDefaultClickListener defaultClickListener;

    public interface OnItemClickListener { void onItemClick(int position); }
    public interface OnDefaultClickListener { void onDefaultClick(int position); }

    public void setOnItemClickListener(OnItemClickListener listener) { this.clickListener = listener; }
    public void setOnDefaultClickListener(OnDefaultClickListener listener) { this.defaultClickListener = listener; }

    public AddressAdapter(List<AddressInfo> addressList, Context context, boolean selectMode) {
        this.addressList = addressList;
        this.context = context;
        this.selectMode = selectMode;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AddressInfo addr = addressList.get(position);

        holder.tvName.setText(addr.getUserName());
        holder.tvPhone.setText(addr.getUserPhone());
        holder.tvAddress.setText(addr.getFullAddress());
        holder.tvDefault.setVisibility(addr.getDefaultFlag() == 1 ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) clickListener.onItemClick(position);
        });

        holder.btnDefault.setOnClickListener(v -> {
            if (defaultClickListener != null) defaultClickListener.onDefaultClick(position);
        });

        holder.btnDefault.setText(addr.getDefaultFlag() == 1 ? "默认地址" : "设为默认");
    }

    @Override
    public int getItemCount() { return addressList.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone, tvAddress, tvDefault;
        Button btnDefault;

        ViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tv_name);
            tvPhone = v.findViewById(R.id.tv_phone);
            tvAddress = v.findViewById(R.id.tv_address);
            tvDefault = v.findViewById(R.id.tv_default);
            btnDefault = v.findViewById(R.id.btn_default);
        }
    }
}
