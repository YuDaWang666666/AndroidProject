package com.example.newbeemall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.newbeemall.R;
import com.example.newbeemall.util.Constants;
import com.example.newbeemall.util.HttpUtil;
import com.example.newbeemall.util.MD5Util;
import com.example.newbeemall.util.ToastUtil;
import com.example.newbeemall.util.TokenManager;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText etPhone, etPassword;
    private Button btnLogin;
    private TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (TokenManager.isLoggedIn(this)) {
            finish();
            return;
        }

        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);

        btnLogin.setOnClickListener(v -> doLogin());
        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
    }

    private void doLogin() {
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (phone.isEmpty()) {
            ToastUtil.show(this, "请输入手机号");
            return;
        }
        if (password.isEmpty()) {
            ToastUtil.show(this, "请输入密码");
            return;
        }

        String md5Password = MD5Util.md5(password);

        new Thread(() -> {
            try {
                String json = "{\"loginName\":\"" + phone + "\",\"passwordMd5\":\"" + md5Password + "\"}";
                String result = HttpUtil.post(Constants.API_LOGIN, json);
                JSONObject obj = new JSONObject(result);
                int code = obj.getInt("resultCode");
                String msg = obj.getString("message");

                runOnUiThread(() -> {
                    if (code == 200) {
                        try {
                            String token = obj.getString("data");
                            TokenManager.saveToken(LoginActivity.this, token);
                            TokenManager.saveUserInfo(LoginActivity.this, phone, 0);
                            ToastUtil.show(LoginActivity.this, "登录成功");
                            setResult(RESULT_OK);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        ToastUtil.show(LoginActivity.this, msg);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> ToastUtil.show(LoginActivity.this, "网络错误"));
            }
        }).start();
    }
}
