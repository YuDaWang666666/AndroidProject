package com.example.newbeemall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.newbeemall.R;
import com.example.newbeemall.util.AppExecutors;
import com.example.newbeemall.util.Constants;
import com.example.newbeemall.util.HttpUtil;
import com.example.newbeemall.util.ToastUtil;
import com.example.newbeemall.util.TokenManager;
import org.json.JSONObject;

public class PayActivity extends AppCompatActivity {

    private TextView tvOrderNo;
    private RadioGroup rgPayType;
    private Button btnPay;
    private String orderNo;
    private AppExecutors.LifecycleTask lifecycleTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        lifecycleTask = AppExecutors.getInstance().createLifecycleTask();
        orderNo = getIntent().getStringExtra("orderNo");
        tvOrderNo = findViewById(R.id.tv_order_no);
        rgPayType = findViewById(R.id.rg_pay_type);
        btnPay = findViewById(R.id.btn_pay);

        tvOrderNo.setText("订单号: " + orderNo);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        btnPay.setOnClickListener(v -> doPay());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (lifecycleTask != null) lifecycleTask.cancel();
    }

    private void doPay() {
        int payType = rgPayType.getCheckedRadioButtonId() == R.id.rb_alipay ? 1 : 2;

        lifecycleTask.submit(() -> {
            String token = TokenManager.getToken(this);
            String url = Constants.API_ORDER_PAY + "?orderNo=" + orderNo + "&payType=" + payType;
            String result = HttpUtil.get(url, token);
            JSONObject obj = new JSONObject(result);
            return new Object[]{obj.optInt("resultCode"), obj.optString("message")};
        }, (result) -> {
            Object[] arr = (Object[]) result;
            int code = (int) arr[0];
            String msg = (String) arr[1];
            if (code == 200) {
                ToastUtil.show(PayActivity.this, "支付成功");
                Intent intent = new Intent(PayActivity.this, OrderListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {
                ToastUtil.show(PayActivity.this, msg);
            }
        }, () -> {
            ToastUtil.show(PayActivity.this, "支付失败，请检查网络");
        });
    }
}
