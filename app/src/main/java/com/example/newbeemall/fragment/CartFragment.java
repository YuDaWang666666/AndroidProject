package com.example.newbeemall.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.newbeemall.R;
import com.example.newbeemall.activity.LoginActivity;
import com.example.newbeemall.activity.MainActivity;
import com.example.newbeemall.activity.OrderConfirmActivity;
import com.example.newbeemall.model.CartItem;
import com.example.newbeemall.util.Constants;
import com.example.newbeemall.util.HttpUtil;
import com.example.newbeemall.util.ToastUtil;
import com.example.newbeemall.util.TokenManager;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    private RecyclerView rvCart;
    private TextView tvTotal;
    private Button btnCheckout;
    private CheckBox cbSelectAll;
    private LinearLayout layoutEmpty;
    private Button btnGoShopping;
    private CartAdapter adapter;
    private List<CartItem> cartList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvCart = view.findViewById(R.id.rv_cart);
        tvTotal = view.findViewById(R.id.tv_total);
        btnCheckout = view.findViewById(R.id.btn_checkout);
        cbSelectAll = view.findViewById(R.id.cb_select_all);
        layoutEmpty = view.findViewById(R.id.layout_empty);
        btnGoShopping = view.findViewById(R.id.btn_go_shopping);

        rvCart.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CartAdapter();
        rvCart.setAdapter(adapter);

        cbSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (CartItem item : cartList) {
                item.setSelected(isChecked);
            }
            adapter.notifyDataSetChanged();
            updateTotal();
        });

        btnCheckout.setOnClickListener(v -> {
            if (!TokenManager.isLoggedIn(getContext())) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
                return;
            }
            startActivity(new Intent(getActivity(), OrderConfirmActivity.class));
        });

        btnGoShopping.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).switchToHome();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getContext() == null) return;
        if (TokenManager.isLoggedIn(getContext())) {
            loadCartData();
        } else {
            cartList.clear();
            adapter.notifyDataSetChanged();
            updateTotal();
            layoutEmpty.setVisibility(View.VISIBLE);
            rvCart.setVisibility(View.GONE);
        }
    }

    private void loadCartData() {
        if (getContext() == null) return;
        new Thread(() -> {
            try {
                String token = TokenManager.getToken(getContext());
                String result = HttpUtil.get(Constants.API_CART_LIST, token);
                JSONObject obj = new JSONObject(result);
                JSONArray data = obj.optJSONArray("data");
                cartList.clear();
                if (data != null) {
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject item = data.getJSONObject(i);
                        CartItem cart = new CartItem();
                        cart.setCartItemId(item.optInt("cartItemId"));
                        cart.setGoodsId(item.optInt("goodsId"));
                        cart.setGoodsCount(item.optInt("goodsCount"));
                        cart.setGoodsName(item.optString("goodsName"));
                        cart.setGoodsCoverImg(item.optString("goodsCoverImg"));
                        cart.setSellingPrice(item.optInt("sellingPrice"));
                        cartList.add(cart);
                    }
                }
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        adapter.notifyDataSetChanged();
                        updateTotal();
                        updateSelectAllState();
                        layoutEmpty.setVisibility(cartList.isEmpty() ? View.VISIBLE : View.GONE);
                        rvCart.setVisibility(cartList.isEmpty() ? View.GONE : View.VISIBLE);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void updateTotal() {
        int total = 0;
        for (CartItem item : cartList) {
            if (item.isSelected()) {
                total += item.getSellingPrice() * item.getGoodsCount();
            }
        }
        tvTotal.setText("¥" + String.format("%.2f", total / 100.0));
    }

    private void updateSelectAllState() {
        boolean allSelected = !cartList.isEmpty();
        for (CartItem item : cartList) {
            if (!item.isSelected()) {
                allSelected = false;
                break;
            }
        }
        cbSelectAll.setChecked(allSelected);
    }

    private void deleteCartItem(int cartItemId) {
        new Thread(() -> {
            try {
                String token = TokenManager.getToken(getContext());
                HttpUtil.delete(Constants.API_CART_DELETE + "/" + cartItemId, token);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(this::loadCartData);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void updateCartCount(int cartItemId, int count) {
        new Thread(() -> {
            try {
                String token = TokenManager.getToken(getContext());
                String json = "{\"cartItemId\":" + cartItemId + ",\"goodsCount\":" + count + "}";
                HttpUtil.put(Constants.API_CART_UPDATE, json, token);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            CartItem item = cartList.get(position);

            String imgUrl = item.getGoodsCoverImg();
            if (imgUrl != null && !imgUrl.startsWith("http")) imgUrl = Constants.IMG_BASE_URL + imgUrl;
            Glide.with(holder.itemView).load(imgUrl).into(holder.ivGoods);
            holder.tvName.setText(item.getGoodsName());
            holder.tvPrice.setText("¥" + item.getSellingPrice());
            holder.tvCount.setText(String.valueOf(item.getGoodsCount()));
            holder.cbSelect.setChecked(item.isSelected());

            holder.cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
                item.setSelected(isChecked);
                updateTotal();
                updateSelectAllState();
            });

            holder.btnMinus.setOnClickListener(v -> {
                if (item.getGoodsCount() > 1) {
                    item.setGoodsCount(item.getGoodsCount() - 1);
                    holder.tvCount.setText(String.valueOf(item.getGoodsCount()));
                    updateCartCount(item.getCartItemId(), item.getGoodsCount());
                    updateTotal();
                }
            });

            holder.btnPlus.setOnClickListener(v -> {
                item.setGoodsCount(item.getGoodsCount() + 1);
                holder.tvCount.setText(String.valueOf(item.getGoodsCount()));
                updateCartCount(item.getCartItemId(), item.getGoodsCount());
                updateTotal();
            });

            holder.btnDelete.setOnClickListener(v -> deleteCartItem(item.getCartItemId()));
        }

        @Override
        public int getItemCount() { return cartList.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            CheckBox cbSelect;
            ImageView ivGoods;
            TextView tvName, tvPrice, tvCount;
            Button btnMinus, btnPlus;
            ImageView btnDelete;

            ViewHolder(View v) {
                super(v);
                cbSelect = v.findViewById(R.id.cb_select);
                ivGoods = v.findViewById(R.id.iv_goods);
                tvName = v.findViewById(R.id.tv_name);
                tvPrice = v.findViewById(R.id.tv_price);
                tvCount = v.findViewById(R.id.tv_count);
                btnMinus = v.findViewById(R.id.btn_minus);
                btnPlus = v.findViewById(R.id.btn_plus);
                btnDelete = v.findViewById(R.id.btn_delete);
            }
        }
    }
}
