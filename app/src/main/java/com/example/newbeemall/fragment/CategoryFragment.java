package com.example.newbeemall.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.newbeemall.R;
import com.example.newbeemall.activity.SearchActivity;
import com.example.newbeemall.model.CategoryInfo;
import com.example.newbeemall.model.GoodsInfo;
import com.example.newbeemall.util.Constants;
import com.example.newbeemall.util.HttpUtil;
import com.example.newbeemall.util.ToastUtil;
import com.example.newbeemall.view.MyGridView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment {

    private ListView lvCategory;
    private MyGridView gvGoods;
    private List<CategoryInfo> categoryList = new ArrayList<>();
    private List<String> categoryNameList = new ArrayList<>();
    private ArrayAdapter<String> categoryAdapter;
    private GoodsGridAdapter goodsAdapter;
    private List<GoodsInfo> goodsList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lvCategory = view.findViewById(R.id.lv_category);
        gvGoods = view.findViewById(R.id.gv_goods);

        // 搜索栏点击跳转搜索页
        TextView tvSearch = view.findViewById(R.id.tv_search);
        tvSearch.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
        });

        categoryAdapter = new ArrayAdapter<>(getContext(), R.layout.item_category, categoryNameList);
        lvCategory.setAdapter(categoryAdapter);

        goodsAdapter = new GoodsGridAdapter();
        gvGoods.setAdapter(goodsAdapter);

        lvCategory.setOnItemClickListener((parent, v, position, id) -> {
            if (position < categoryList.size()) {
                String categoryName = categoryList.get(position).getCategoryName();
                searchByCategory(categoryName);
            }
        });

        gvGoods.setOnItemClickListener((parent, v, position, id) -> {
            if (position < goodsList.size()) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                intent.putExtra("goodsId", goodsList.get(position).getGoodsId());
                startActivity(intent);
            }
        });

        loadCategories();
    }

    private void loadCategories() {
        if (getContext() == null) return;
        new Thread(() -> {
            try {
                String result = HttpUtil.get(Constants.API_CATEGORIES);
                JSONObject obj = new JSONObject(result);
                JSONArray data = obj.optJSONArray("data");
                categoryList.clear();
                categoryNameList.clear();
                if (data != null) {
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject item = data.getJSONObject(i);
                        CategoryInfo cat = new CategoryInfo();
                        cat.setCategoryId(item.optInt("categoryId"));
                        cat.setCategoryName(item.optString("categoryName"));
                        cat.setCategoryLevel(item.optInt("categoryLevel"));
                        categoryList.add(cat);
                        categoryNameList.add(cat.getCategoryName());
                    }
                }
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        categoryAdapter.notifyDataSetChanged();
                        if (!categoryList.isEmpty()) {
                            lvCategory.setItemChecked(0, true);
                            searchByCategory(categoryList.get(0).getCategoryName());
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void searchByCategory(String keyword) {
        if (getContext() == null) return;
        new Thread(() -> {
            try {
                String encodedKeyword = java.net.URLEncoder.encode(keyword, "UTF-8");
                String url = Constants.API_SEARCH + "?pageNumber=1&keyword=" + encodedKeyword + "&orderBy=";
                String result = HttpUtil.get(url);
                JSONObject obj = new JSONObject(result);
                JSONObject data = obj.optJSONObject("data");
                JSONArray list = data != null ? data.optJSONArray("list") : null;
                goodsList.clear();
                if (list != null) {
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject item = list.getJSONObject(i);
                        GoodsInfo goods = new GoodsInfo();
                        goods.setGoodsId(item.optInt("goodsId"));
                        goods.setGoodsName(item.optString("goodsName"));
                        goods.setGoodsCoverImg(item.optString("goodsCoverImg"));
                        goods.setSellingPrice(item.optInt("sellingPrice"));
                        goodsList.add(goods);
                    }
                }
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> goodsAdapter.notifyDataSetChanged());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    class GoodsGridAdapter extends android.widget.BaseAdapter {
        @Override public int getCount() { return goodsList.size(); }
        @Override public Object getItem(int pos) { return goodsList.get(pos); }
        @Override public long getItemId(int pos) { return pos; }
        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_goods_grid, parent, false);
            }
            ImageView iv = convertView.findViewById(R.id.iv_goods);
            TextView tvName = convertView.findViewById(R.id.tv_name);
            TextView tvPrice = convertView.findViewById(R.id.tv_price);

            GoodsInfo goods = goodsList.get(pos);
            String imgUrl = goods.getGoodsCoverImg();
            if (imgUrl != null && !imgUrl.startsWith("http")) imgUrl = Constants.IMG_BASE_URL + imgUrl;
            Glide.with(convertView).load(imgUrl).into(iv);
            tvName.setText(goods.getGoodsName());
            tvPrice.setText("¥" + goods.getSellingPrice());
            return convertView;
        }
    }
}
