package com.example.newbeemall.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.newbeemall.R;
import com.example.newbeemall.adapter.OrderAdapter;
import com.example.newbeemall.model.OrderItem;
import com.example.newbeemall.util.Constants;
import com.example.newbeemall.util.HttpUtil;
import com.example.newbeemall.util.TokenManager;
import com.google.android.material.tabs.TabLayout;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class OrderListActivity extends AppCompatActivity {

    private TabLayout tabStatus;
    private RecyclerView rvOrders;
    private OrderAdapter adapter;
    private List<OrderItem> orderList = new ArrayList<>();
    private int currentStatus = -1; // -1=全部

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        tabStatus = findViewById(R.id.tab_status);
        rvOrders = findViewById(R.id.rv_orders);
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        adapter = new OrderAdapter(orderList, this);
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        rvOrders.setAdapter(adapter);

        adapter.setOnItemClickListener(position -> {
            OrderItem order = orderList.get(position);
            Intent intent = new Intent(this, OrderDetailActivity.class);
            intent.putExtra("orderNo", order.getOrderNo());
            startActivity(intent);
        });

        // Tab
        String[] tabs = {"全部", "待付款", "待确认", "待发货", "已发货", "交易完成"};
        for (String tab : tabs) {
            tabStatus.addTab(tabStatus.newTab().setText(tab));
        }

        tabStatus.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: currentStatus = -1; break;
                    case 1: currentStatus = 0; break;
                    case 2: currentStatus = 1; break;
                    case 3: currentStatus = 2; break;
                    case 4: currentStatus = 3; break;
                    case 5: currentStatus = 4; break;
                }
                loadOrders();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        loadOrders();
    }

    private void loadOrders() {
        new Thread(() -> {
            try {
                String token = TokenManager.getToken(this);
                String url = Constants.API_ORDER_LIST + "?pageNumber=1";
                if (currentStatus >= 0) {
                    url += "&status=" + currentStatus;
                }
                String result = HttpUtil.get(url, token);
                JSONObject obj = new JSONObject(result);
                JSONObject data = obj.optJSONObject("data");
                JSONArray list = data != null ? data.optJSONArray("list") : null;
                orderList.clear();
                if (list != null) {
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject item = list.getJSONObject(i);
                        OrderItem order = new OrderItem();
                        order.setOrderId(item.optInt("orderId"));
                        order.setOrderNo(item.optString("orderNo"));
                        order.setTotalPrice(item.optInt("totalPrice"));
                        order.setOrderStatus(item.optInt("orderStatus"));
                        order.setCreateTime(item.optString("createTime"));
                        JSONArray items = item.optJSONArray("newBeeMallOrderItemVOS");
                        if (items != null) {
                            List<OrderItem.GoodsItem> goodsItems = new ArrayList<>();
                            for (int j = 0; j < items.length(); j++) {
                                JSONObject g = items.getJSONObject(j);
                                OrderItem.GoodsItem gi = new OrderItem.GoodsItem();
                                gi.setGoodsId(g.optInt("goodsId"));
                                gi.setGoodsCount(g.optInt("goodsCount"));
                                gi.setGoodsName(g.optString("goodsName"));
                                gi.setGoodsCoverImg(g.optString("goodsCoverImg"));
                                gi.setSellingPrice(g.optInt("sellingPrice"));
                                goodsItems.add(gi);
                            }
                            order.setNewBeeMallOrderItemVOS(goodsItems);
                        }
                        orderList.add(order);
                    }
                }
                runOnUiThread(() -> adapter.notifyDataSetChanged());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
