package com.example.newbeemall.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.newbeemall.R;
import com.example.newbeemall.model.GoodsInfo;
import com.example.newbeemall.util.Constants;
import com.example.newbeemall.util.HttpUtil;
import com.example.newbeemall.util.TokenManager;
import com.google.android.material.tabs.TabLayout;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText etSearch;
    private ImageView ivSearch;
    private TabLayout tabSort;
    private RecyclerView rvGoods;
    private SearchListAdapter adapter;
    private List<GoodsInfo> goodsList = new ArrayList<>();
    private String currentOrderBy = "";
    private String currentKeyword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        etSearch = findViewById(R.id.et_search);
        ivSearch = findViewById(R.id.iv_search);
        tabSort = findViewById(R.id.tab_sort);
        rvGoods = findViewById(R.id.rv_goods);

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        adapter = new SearchListAdapter(goodsList, this);
        rvGoods.setLayoutManager(new LinearLayoutManager(this));
        rvGoods.setAdapter(adapter);

        adapter.setOnItemClickListener(position -> {
            GoodsInfo goods = goodsList.get(position);
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("goodsId", goods.getGoodsId());
            startActivity(intent);
        });

        // 排序 Tab
        tabSort.addTab(tabSort.newTab().setText("推荐"));
        tabSort.addTab(tabSort.newTab().setText("新品"));
        tabSort.addTab(tabSort.newTab().setText("价格"));

        tabSort.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: currentOrderBy = ""; break;
                    case 1: currentOrderBy = "new"; break;
                    case 2: currentOrderBy = "price"; break;
                }
                doSearch();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        ivSearch.setOnClickListener(v -> {
            currentKeyword = etSearch.getText().toString().trim();
            doSearch();
        });

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                currentKeyword = etSearch.getText().toString().trim();
                doSearch();
                return true;
            }
            return false;
        });

        // 从其他页面传入的关键词或分类ID
        Intent intent = getIntent();
        if (intent != null) {
            String keyword = intent.getStringExtra("keyword");
            int categoryId = intent.getIntExtra("categoryId", 0);
            if (!TextUtils.isEmpty(keyword)) {
                currentKeyword = keyword;
                etSearch.setText(keyword);
            }
            if (categoryId > 0) {
                doSearchByCategory(categoryId);
                return;
            }
        }
        doSearch();
    }

    private void doSearch() {
        loadData(currentKeyword, currentOrderBy, 0);
    }

    private void doSearchByCategory(int categoryId) {
        String url = Constants.API_SEARCH + "?pageNumber=1&goodsCategoryId=" + categoryId + "&keyword=&orderBy=";
        loadDataFromUrl(url);
    }

    private void loadData(String keyword, String orderBy, int categoryId) {
        String url = Constants.API_SEARCH + "?pageNumber=1&keyword=" + keyword + "&orderBy=" + orderBy;
        if (categoryId > 0) {
            url += "&goodsCategoryId=" + categoryId;
        }
        loadDataFromUrl(url);
    }

    private void loadDataFromUrl(String url) {
        new Thread(() -> {
            try {
                String token = TokenManager.getToken(this);
                String result = HttpUtil.get(url, token);
                JSONObject obj = new JSONObject(result);
                JSONObject data = obj.getJSONObject("data");
                JSONArray list = data.getJSONArray("list");
                goodsList.clear();
                for (int i = 0; i < list.length(); i++) {
                    JSONObject item = list.getJSONObject(i);
                    GoodsInfo goods = new GoodsInfo();
                    goods.setGoodsId(item.optInt("goodsId"));
                    goods.setGoodsName(item.optString("goodsName"));
                    goods.setGoodsIntro(item.optString("goodsIntro"));
                    goods.setGoodsCoverImg(item.optString("goodsCoverImg"));
                    goods.setSellingPrice(item.optInt("sellingPrice"));
                    goods.setOriginalPrice(item.optInt("originalPrice"));
                    goodsList.add(goods);
                }
                runOnUiThread(() -> adapter.notifyDataSetChanged());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // 左图右文列表适配器
    static class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.ViewHolder> {
        private List<GoodsInfo> list;
        private Context context;
        private OnItemClickListener listener;

        public interface OnItemClickListener {
            void onItemClick(int position);
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
        }

        public SearchListAdapter(List<GoodsInfo> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goods_search, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            GoodsInfo goods = list.get(position);

            String imgUrl = goods.getGoodsCoverImg();
            if (imgUrl != null && !imgUrl.startsWith("http")) imgUrl = Constants.IMG_BASE_URL + imgUrl;
            Glide.with(context).load(imgUrl).into(holder.ivGoods);

            holder.tvName.setText(goods.getGoodsName());
            holder.tvIntro.setText(goods.getGoodsIntro());
            holder.tvPrice.setText("¥" + goods.getSellingPrice());

            holder.itemView.setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(position);
            });
        }

        @Override
        public int getItemCount() { return list.size(); }

        static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivGoods;
            TextView tvName, tvIntro, tvPrice;

            ViewHolder(View v) {
                super(v);
                ivGoods = v.findViewById(R.id.iv_goods);
                tvName = v.findViewById(R.id.tv_name);
                tvIntro = v.findViewById(R.id.tv_intro);
                tvPrice = v.findViewById(R.id.tv_price);
            }
        }
    }
}
