package com.example.newbeemall.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.newbeemall.R;
import com.example.newbeemall.util.Constants;
import com.example.newbeemall.util.HttpUtil;
import com.example.newbeemall.util.ToastUtil;
import com.example.newbeemall.util.TokenManager;
import org.json.JSONObject;

public class AddressEditActivity extends AppCompatActivity {

    private EditText etName, etPhone, etDetail;
    private TextView tvProvince, tvCity, tvRegion;
    private Switch switchDefault;
    private Button btnSave;
    private int addressId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_edit);

        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etDetail = findViewById(R.id.et_detail);
        tvProvince = findViewById(R.id.tv_province);
        tvCity = findViewById(R.id.tv_city);
        tvRegion = findViewById(R.id.tv_region);
        switchDefault = findViewById(R.id.switch_default);
        btnSave = findViewById(R.id.btn_save);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        // 编辑模式
        addressId = getIntent().getIntExtra("addressId", 0);
        if (addressId > 0) {
            etName.setText(getIntent().getStringExtra("userName"));
            etPhone.setText(getIntent().getStringExtra("userPhone"));
            etDetail.setText(getIntent().getStringExtra("detailAddress"));
            tvProvince.setText(getIntent().getStringExtra("provinceName"));
            tvCity.setText(getIntent().getStringExtra("cityName"));
            tvRegion.setText(getIntent().getStringExtra("regionName"));
        }

        // 简化的省市区选择（点击弹出输入）
        tvProvince.setOnClickListener(v -> showInputDialog("省份", tvProvince));
        tvCity.setOnClickListener(v -> showInputDialog("城市", tvCity));
        tvRegion.setOnClickListener(v -> showInputDialog("区/县", tvRegion));

        btnSave.setOnClickListener(v -> saveAddress());
    }

    private void showInputDialog(String title, TextView target) {
        EditText input = new EditText(this);
        input.setHint("请输入" + title);
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("选择" + title)
                .setView(input)
                .setPositiveButton("确定", (dialog, which) -> {
                    String value = input.getText().toString().trim();
                    if (!value.isEmpty()) target.setText(value);
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void saveAddress() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String detail = etDetail.getText().toString().trim();
        String province = tvProvince.getText().toString().trim();
        String city = tvCity.getText().toString().trim();
        String region = tvRegion.getText().toString().trim();
        String defaultFlag = switchDefault.isChecked() ? "1" : "0";

        if (name.isEmpty()) { ToastUtil.show(this, "请输入收件人"); return; }
        if (phone.isEmpty()) { ToastUtil.show(this, "请输入手机号"); return; }
        if (detail.isEmpty()) { ToastUtil.show(this, "请输入详细地址"); return; }

        new Thread(() -> {
            try {
                String token = TokenManager.getToken(this);
                String json = "{\"userName\":\"" + name + "\",\"userPhone\":\"" + phone + "\"," +
                        "\"provinceName\":\"" + province + "\",\"cityName\":\"" + city + "\"," +
                        "\"regionName\":\"" + region + "\",\"detailAddress\":\"" + detail + "\"," +
                        "\"defaultFlag\":\"" + defaultFlag + "\"}";
                String result;
                if (addressId > 0) {
                    json = "{\"addressId\":" + addressId + "," + json.substring(1);
                    result = HttpUtil.put(Constants.API_ADDRESS_UPDATE, json, token);
                } else {
                    result = HttpUtil.post(Constants.API_ADDRESS_ADD, json, token);
                }
                JSONObject obj = new JSONObject(result);
                int code = obj.optInt("resultCode");
                runOnUiThread(() -> {
                    if (code == 200) {
                        ToastUtil.show(this, "保存成功");
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        ToastUtil.show(this, obj.optString("message"));
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> ToastUtil.show(this, "网络错误"));
            }
        }).start();
    }
}
