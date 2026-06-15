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
import com.example.newbeemall.util.TokenManager;
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

    // 从首页跳转时，暂存要选中的分类 ID
    private static int pendingCategoryId = -1;

    /**
     * 首页调用此方法，设置要跳转到的分类 ID
     */
    public static void setPendingCategory(int categoryId) {
        pendingCategoryId = categoryId;
    }

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
                CategoryInfo selected = categoryList.get(position);
                loadGoodsByCategory(selected.getCategoryId(), selected.getCategoryName());
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
                android.util.Log.d("Category", "分类原始数据: " + result);
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
                            // 检查是否有从首页传入的待选分类
                            int targetPos = 0;
                            if (pendingCategoryId != -1) {
                                for (int i = 0; i < categoryList.size(); i++) {
                                    if (categoryList.get(i).getCategoryId() == pendingCategoryId) {
                                        targetPos = i;
                                        break;
                                    }
                                }
                                pendingCategoryId = -1; // 用完清除
                            }
                            lvCategory.setItemChecked(targetPos, true);
                            lvCategory.setSelection(targetPos);
                            CategoryInfo selected = categoryList.get(targetPos);
                            loadGoodsByCategory(selected.getCategoryId(), selected.getCategoryName());
                        }
                    });
                }
            } catch (Exception e) {
                android.util.Log.e("Category", "加载分类异常", e);
            }
        }).start();
    }

    /**
     * 根据分类加载商品（关键词映射方案）
     * 因为后端 /search?goodsCategoryId=xxx 不生效，所以用分类名映射到搜索关键词
     */
    private void loadGoodsByCategory(int categoryId, String categoryName) {
        android.util.Log.d("Category", ">>> loadGoodsByCategory 被调用, id=" + categoryId + ", name=" + categoryName);
        if (getActivity() == null) return;

        // 步骤1：根据分类名称映射到搜索关键词
        String keyword = "手机";  // 默认兜底关键词
        if (categoryName != null && !categoryName.isEmpty()) {
            String firstWord = categoryName.split("\\s+")[0];
            switch (firstWord) {
                case "家电": keyword = "手机"; break;
                case "女装": keyword = "T恤";  break;
                case "家具": keyword = "座椅"; break;
                case "运动": keyword = "耳机"; break;
                case "游戏": keyword = "小米"; break;
                case "美妆": keyword = "口红"; break;
                case "工具": keyword = "橡皮"; break;
                case "鞋靴": keyword = "苹果"; break;
                case "玩具": keyword = "MAC";  break;
                default:     keyword = "手机"; break;
            }
        }

        final String searchKeyword = keyword;
        android.util.Log.d("Category", "映射关键词: " + searchKeyword);

        // 步骤2：发起搜索请求
        new Thread(() -> {
            try {
                String encodedKeyword = java.net.URLEncoder.encode(searchKeyword, "UTF-8");
                String url = Constants.API_SEARCH + "?pageNumber=1&keyword=" + encodedKeyword + "&orderBy=";
                String token = TokenManager.getToken(getActivity());

                // 先尝试带 token 请求
                String result = HttpUtil.get(url, token);
                JSONObject obj = new JSONObject(result);
                int resultCode = obj.optInt("resultCode", -1);

                // token 无效（416），尝试不带 token
                if (resultCode == 416) {
                    android.util.Log.d("Category", "Token 无效，尝试不带 token");
                    result = HttpUtil.get(url);
                    obj = new JSONObject(result);
                    resultCode = obj.optInt("resultCode", -1);
                }

                android.util.Log.d("Category", "resultCode=" + resultCode);
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
                final int count = goodsList.size();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        goodsAdapter.notifyDataSetChanged();
                    android.widget.Toast.makeText(getActivity(), "加载了 " + count + " 件商品", android.widget.Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                android.util.Log.e("Category", "异常", e);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() ->
                        android.widget.Toast.makeText(getActivity(), "加载失败: " + e.getMessage(), android.widget.Toast.LENGTH_SHORT).show()
                    );
                }
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
