package com.example.newbeemall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.newbeemall.R;
import com.example.newbeemall.util.Constants;
import com.example.newbeemall.util.HttpUtil;
import com.example.newbeemall.util.ToastUtil;
import com.example.newbeemall.util.TokenManager;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private ImageView ivGoodsImage;
    private TextView tvName, tvIntro, tvPrice, tvOriginalPrice, tvCount;
    private Button btnAddCart, btnBuyNow;
    private int goodsId;
    private int sellingPrice;
    private String goodsName;
    private int count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        goodsId = getIntent().getIntExtra("goodsId", 0);

        ivGoodsImage = findViewById(R.id.iv_goods_image);
        tvName = findViewById(R.id.tv_goods_name);
        tvIntro = findViewById(R.id.tv_goods_intro);
        tvPrice = findViewById(R.id.tv_price);
        tvOriginalPrice = findViewById(R.id.tv_original_price);
        tvCount = findViewById(R.id.tv_count);
        btnAddCart = findViewById(R.id.btn_add_cart);
        btnBuyNow = findViewById(R.id.btn_buy_now);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_minus).setOnClickListener(v -> {
            if (count > 1) {
                count--;
                tvCount.setText(String.valueOf(count));
            }
        });
        findViewById(R.id.btn_plus).setOnClickListener(v -> {
            count++;
            tvCount.setText(String.valueOf(count));
        });

        btnAddCart.setOnClickListener(v -> addToCart());
        btnBuyNow.setOnClickListener(v -> {
            addToCart();
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("switchToCart", true);
            startActivity(intent);
        });

        findViewById(R.id.iv_cart).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("switchToCart", true);
            startActivity(intent);
        });

        loadGoodsDetail();
    }

    private void loadGoodsDetail() {
        new Thread(() -> {
            try {
                String token = TokenManager.getToken(this);
                String result = HttpUtil.get(Constants.API_GOODS_DETAIL + "/" + goodsId, token);
                JSONObject obj = new JSONObject(result);
                JSONObject data = obj.getJSONObject("data");

                goodsName = data.optString("goodsName");
                String intro = data.optString("goodsIntro");
                String coverImg = data.optString("goodsCoverImg");
                sellingPrice = data.optInt("sellingPrice");
                int originalPrice = data.optInt("originalPrice");
                String carousel = data.optString("goodsCarousel");

                runOnUiThread(() -> {
                    tvName.setText(goodsName);
                    tvIntro.setText(intro);
                    tvPrice.setText("¥" + sellingPrice);
                    tvOriginalPrice.setText("¥" + originalPrice);

                    String imgUrl = coverImg.startsWith("http") ? coverImg : Constants.IMG_BASE_URL + coverImg;
                    Glide.with(DetailActivity.this).load(imgUrl).into(ivGoodsImage);

                    // 轮播图
                    try {
                        JSONArray carouselArr = new JSONArray(carousel);
                        List<String> imageUrls = new ArrayList<>();
                        for (int i = 0; i < carouselArr.length(); i++) {
                            String url = carouselArr.getString(i);
                            if (!url.startsWith("http")) url = Constants.IMG_BASE_URL + url;
                            imageUrls.add(url);
                        }
                        // 简单实现：显示第一张图
                        if (!imageUrls.isEmpty()) {
                            Glide.with(DetailActivity.this).load(imageUrls.get(0)).into(ivGoodsImage);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void addToCart() {
        if (!TokenManager.isLoggedIn(this)) {
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        new Thread(() -> {
            try {
                String token = TokenManager.getToken(this);
                String json = "{\"goodsId\":" + goodsId + ",\"goodsCount\":" + count + "}";
                String result = HttpUtil.post(Constants.API_CART_ADD, json, token);
                JSONObject obj = new JSONObject(result);
                int code = obj.optInt("resultCode");
                runOnUiThread(() -> {
                    if (code == 200) {
                        ToastUtil.show(DetailActivity.this, "已加入购物车");
                    } else {
                        ToastUtil.show(DetailActivity.this, obj.optString("message"));
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> ToastUtil.show(DetailActivity.this, "操作失败"));
            }
        }).start();
    }
}
