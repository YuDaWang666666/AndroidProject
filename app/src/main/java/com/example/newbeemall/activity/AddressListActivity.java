package com.example.newbeemall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.newbeemall.R;
import com.example.newbeemall.adapter.AddressAdapter;
import com.example.newbeemall.model.AddressInfo;
import com.example.newbeemall.util.Constants;
import com.example.newbeemall.util.HttpUtil;
import com.example.newbeemall.util.TokenManager;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class AddressListActivity extends AppCompatActivity {

    private RecyclerView rvAddress;
    private AddressAdapter adapter;
    private List<AddressInfo> addressList = new ArrayList<>();
    private boolean selectMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);

        selectMode = getIntent().getBooleanExtra("selectMode", false);

        rvAddress = findViewById(R.id.rv_address);
        rvAddress.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AddressAdapter(addressList, this, selectMode);
        rvAddress.setAdapter(adapter);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_add).setOnClickListener(v -> {
            startActivityForResult(new Intent(this, AddressEditActivity.class), 100);
        });

        adapter.setOnItemClickListener(position -> {
            if (selectMode) {
                AddressInfo addr = addressList.get(position);
                Intent data = new Intent();
                data.putExtra("addressId", addr.getAddressId());
                data.putExtra("addressName", addr.getUserName());
                data.putExtra("addressPhone", addr.getUserPhone());
                data.putExtra("addressFull", addr.getFullAddress());
                setResult(RESULT_OK, data);
                finish();
            }
        });

        adapter.setOnDefaultClickListener(position -> {
            AddressInfo addr = addressList.get(position);
            setDefaultAddress(addr.getAddressId());
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAddresses();
    }

    private void loadAddresses() {
        new Thread(() -> {
            try {
                String token = TokenManager.getToken(this);
                String result = HttpUtil.get(Constants.API_ADDRESS_LIST, token);
                JSONObject obj = new JSONObject(result);
                JSONArray data = obj.optJSONArray("data");
                addressList.clear();
                if (data != null) {
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject item = data.getJSONObject(i);
                        AddressInfo addr = new AddressInfo();
                        addr.setAddressId(item.optInt("addressId"));
                        addr.setUserName(item.optString("userName"));
                        addr.setUserPhone(item.optString("userPhone"));
                        addr.setProvinceName(item.optString("provinceName"));
                        addr.setCityName(item.optString("cityName"));
                        addr.setRegionName(item.optString("regionName"));
                        addr.setDetailAddress(item.optString("detailAddress"));
                        addr.setDefaultFlag(item.optInt("defaultFlag"));
                        addressList.add(addr);
                    }
                }
                runOnUiThread(() -> adapter.notifyDataSetChanged());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void setDefaultAddress(int addressId) {
        new Thread(() -> {
            try {
                String token = TokenManager.getToken(this);
                // 先获取当前地址详情
                String detailResult = HttpUtil.get(Constants.API_ADDRESS_LIST + "/" + addressId, token);
                JSONObject detailObj = new JSONObject(detailResult);
                JSONObject addrData = detailObj.optJSONObject("data");
                if (addrData != null) {
                    // 用完整数据更新，设置 defaultFlag = "1"
                    String json = "{" +
                        "\"addressId\":" + addressId + "," +
                        "\"userName\":\"" + addrData.optString("userName") + "\"," +
                        "\"userPhone\":\"" + addrData.optString("userPhone") + "\"," +
                        "\"provinceName\":\"" + addrData.optString("provinceName") + "\"," +
                        "\"cityName\":\"" + addrData.optString("cityName") + "\"," +
                        "\"regionName\":\"" + addrData.optString("regionName") + "\"," +
                        "\"detailAddress\":\"" + addrData.optString("detailAddress") + "\"," +
                        "\"defaultFlag\":\"1\"" +
                        "}";
                    HttpUtil.put(Constants.API_ADDRESS_UPDATE, json, token);
                    runOnUiThread(this::loadAddresses);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            loadAddresses();
        }
    }
}
