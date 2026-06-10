package com.example.newbeemall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.newbeemall.R;
import com.example.newbeemall.model.AddressInfo;
import com.example.newbeemall.util.AppExecutors;
import com.example.newbeemall.util.Constants;
import com.example.newbeemall.util.HttpUtil;
import com.example.newbeemall.util.ToastUtil;
import com.example.newbeemall.util.TokenManager;
import org.json.JSONArray;
import org.json.JSONObject;

public class OrderConfirmActivity extends AppCompatActivity {

    private static final int REQ_ADDRESS = 200;
    private LinearLayout layoutAddress;
    private TextView tvName, tvPhone, tvAddress;
    private Button btnSubmit;
    private int selectedAddressId = 0;
    private AppExecutors.LifecycleTask lifecycleTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);

        lifecycleTask = AppExecutors.getInstance().createLifecycleTask();

        layoutAddress = findViewById(R.id.layout_address);
        tvName = findViewById(R.id.tv_address_name);
        tvPhone = findViewById(R.id.tv_address_phone);
        tvAddress = findViewById(R.id.tv_address_detail);
        btnSubmit = findViewById(R.id.btn_submit_order);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        layoutAddress.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddressListActivity.class);
            intent.putExtra("selectMode", true);
            startActivityForResult(intent, REQ_ADDRESS);
        });

        btnSubmit.setOnClickListener(v -> createOrder());

        loadDefaultAddress();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (lifecycleTask != null) lifecycleTask.cancel();
    }

    private void loadDefaultAddress() {
        lifecycleTask.submit(() -> {
            String token = TokenManager.getToken(this);
            String result = HttpUtil.get(Constants.API_ADDRESS_LIST, token);
            JSONObject obj = new JSONObject(result);
            JSONArray data = obj.optJSONArray("data");
            if (data != null && data.length() > 0) {
                for (int i = 0; i < data.length(); i++) {
                    JSONObject item = data.getJSONObject(i);
                    if (item.optInt("defaultFlag") == 1) {
                        return new Object[]{
                            item.optInt("addressId"),
                            item.optString("userName"),
                            item.optString("userPhone"),
                            item.optString("provinceName") + item.optString("cityName") +
                            item.optString("regionName") + item.optString("detailAddress")
                        };
                    }
                }
            }
            return null;
        }, (result) -> {
            if (result != null) {
                Object[] arr = (Object[]) result;
                selectedAddressId = (int) arr[0];
                tvName.setText((String) arr[1]);
                tvPhone.setText((String) arr[2]);
                tvAddress.setText((String) arr[3]);
            }
        }, () -> {
            ToastUtil.show(this, "加载地址失败");
        });
    }

    private void createOrder() {
        if (selectedAddressId == 0) {
            ToastUtil.show(this, "请先选择收货地址");
            return;
        }

        String json = "{\"addressId\":" + selectedAddressId + ",\"cartItemIds\":[]}";
        AppExecutors.LifecycleTask task = AppExecutors.getInstance().createLifecycleTask();
        task.submit(() -> {
            String token = TokenManager.getToken(this);
            String result = HttpUtil.post(Constants.API_ORDER_CREATE, json, token);
            JSONObject obj = new JSONObject(result);
            return new Object[]{obj.optInt("resultCode"), obj.optString("data"), obj.optString("message")};
        }, (result) -> {
            Object[] arr = (Object[]) result;
            int code = (int) arr[0];
            String data = (String) arr[1];
            String msg = (String) arr[2];
            if (code == 200) {
                Intent intent = new Intent(this, PayActivity.class);
                intent.putExtra("orderNo", data);
                startActivity(intent);
                finish();
            } else {
                ToastUtil.show(this, msg);
            }
        }, () -> {
            ToastUtil.show(this, "下单失败，请检查网络");
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_ADDRESS && resultCode == RESULT_OK && data != null) {
            selectedAddressId = data.getIntExtra("addressId", 0);
            tvName.setText(data.getStringExtra("addressName"));
            tvPhone.setText(data.getStringExtra("addressPhone"));
            tvAddress.setText(data.getStringExtra("addressFull"));
        }
    }
}
