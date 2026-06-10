package com.example.newbeemall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.newbeemall.R;
import com.example.newbeemall.adapter.OrderGoodsAdapter;
import com.example.newbeemall.model.OrderItem;
import com.example.newbeemall.util.Constants;
import com.example.newbeemall.util.HttpUtil;
import com.example.newbeemall.util.ToastUtil;
import com.example.newbeemall.util.TokenManager;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailActivity extends AppCompatActivity {

    private TextView tvOrderNo, tvStatus, tvTotalPrice, tvCreateTime;
    private RecyclerView rvGoods;
    private Button btnAction;
    private String orderNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        orderNo = getIntent().getStringExtra("orderNo");

        tvOrderNo = findViewById(R.id.tv_order_no);
        tvStatus = findViewById(R.id.tv_status);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        tvCreateTime = findViewById(R.id.tv_create_time);
        rvGoods = findViewById(R.id.rv_goods);
        btnAction = findViewById(R.id.btn_action);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        rvGoods.setLayoutManager(new LinearLayoutManager(this));

        loadOrderDetail();
    }

    private void loadOrderDetail() {
        new Thread(() -> {
            try {
                String token = TokenManager.getToken(this);
                String result = HttpUtil.get(Constants.API_ORDER_DETAIL + "/" + orderNo, token);
                JSONObject obj = new JSONObject(result);
                JSONObject data = obj.optJSONObject("data");
                if (data != null) {
                    String orderNo = data.optString("orderNo");
                    int status = data.optInt("orderStatus");
                    int totalPrice = data.optInt("totalPrice");
                    String createTime = data.optString("createTime");

                    List<OrderItem.GoodsItem> goodsItems = new ArrayList<>();
                    JSONArray items = data.optJSONArray("newBeeMallOrderItemVOS");
                    if (items != null) {
                        for (int i = 0; i < items.length(); i++) {
                            JSONObject g = items.getJSONObject(i);
                            OrderItem.GoodsItem gi = new OrderItem.GoodsItem();
                            gi.setGoodsId(g.optInt("goodsId"));
                            gi.setGoodsCount(g.optInt("goodsCount"));
                            gi.setGoodsName(g.optString("goodsName"));
                            gi.setGoodsCoverImg(g.optString("goodsCoverImg"));
                            gi.setSellingPrice(g.optInt("sellingPrice"));
                            goodsItems.add(gi);
                        }
                    }

                    OrderItem order = new OrderItem();
                    order.setOrderNo(orderNo);
                    order.setOrderStatus(status);
                    order.setTotalPrice(totalPrice);
                    order.setCreateTime(createTime);

                    runOnUiThread(() -> {
                        tvOrderNo.setText("订单号: " + orderNo);
                        tvStatus.setText(order.getStatusText());
                        tvTotalPrice.setText("¥" + (totalPrice / 100.0));
                        tvCreateTime.setText(createTime);

                        OrderGoodsAdapter adapter = new OrderGoodsAdapter(goodsItems, this);
                        rvGoods.setAdapter(adapter);

                        if (status == 0) {
                            btnAction.setText("去付款");
                            btnAction.setOnClickListener(v -> {
                                Intent payIntent = new Intent(OrderDetailActivity.this, PayActivity.class);
                                payIntent.putExtra("orderNo", orderNo);
                                startActivity(payIntent);
                                finish();
                            });
                        } else if (status == 3) {
                            btnAction.setText("确认收货");
                            btnAction.setOnClickListener(v -> confirmReceive());
                        } else {
                            btnAction.setVisibility(android.view.View.GONE);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void confirmReceive() {
        new Thread(() -> {
            try {
                String token = TokenManager.getToken(this);
                String result = HttpUtil.put(Constants.API_ORDER_FINISH + "/" + orderNo, "{}", token);
                JSONObject obj = new JSONObject(result);
                int code = obj.optInt("resultCode");
                runOnUiThread(() -> {
                    if (code == 200) {
                        ToastUtil.show(this, "已确认收货");
                        finish();
                    } else {
                        ToastUtil.show(this, obj.optString("message"));
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
