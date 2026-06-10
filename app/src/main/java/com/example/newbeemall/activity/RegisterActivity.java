package com.example.newbeemall.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.example.newbeemall.R;
import com.example.newbeemall.util.Constants;
import com.example.newbeemall.util.HttpUtil;
import com.example.newbeemall.util.MD5Util;
import com.example.newbeemall.util.ToastUtil;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private EditText etPhone, etPassword, etConfirm;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);
        etConfirm = findViewById(R.id.et_confirm_password);
        btnRegister = findViewById(R.id.btn_register);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        btnRegister.setOnClickListener(v -> doRegister());
    }

    private void doRegister() {
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirm = etConfirm.getText().toString().trim();

        if (phone.isEmpty()) { ToastUtil.show(this, "请输入手机号"); return; }
        if (password.isEmpty()) { ToastUtil.show(this, "请输入密码"); return; }
        if (!password.equals(confirm)) { ToastUtil.show(this, "两次密码不一致"); return; }

        new Thread(() -> {
            try {
                String json = "{\"loginName\":\"" + phone + "\",\"password\":\"" + password + "\"}";
                String result = HttpUtil.post(Constants.API_REGISTER, json);
                JSONObject obj = new JSONObject(result);
                int code = obj.getInt("resultCode");
                String msg = obj.getString("message");
                runOnUiThread(() -> {
                    if (code == 200) {
                        ToastUtil.show(RegisterActivity.this, "注册成功，请登录");
                        finish();
                    } else {
                        ToastUtil.show(RegisterActivity.this, msg);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> ToastUtil.show(RegisterActivity.this, "网络错误"));
            }
        }).start();
    }
}
