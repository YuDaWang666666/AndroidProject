package com.example.newbeemall.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.newbeemall.R;
import com.example.newbeemall.activity.DetailActivity;
import com.example.newbeemall.activity.LoginActivity;
import com.example.newbeemall.activity.MainActivity;
import com.example.newbeemall.activity.SearchActivity;
import com.example.newbeemall.adapter.GoodsAdapter;
import com.example.newbeemall.model.GoodsInfo;
import com.example.newbeemall.util.Constants;
import com.example.newbeemall.util.HttpUtil;
import com.example.newbeemall.util.TokenManager;
import com.example.newbeemall.view.MyGridView;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private Banner banner;
    private MyGridView gvNav, gvNew, gvHot, gvRecommend;
    private TextView tvSearch;
    private ImageView ivLogin;
    private ScrollView scrollView;
    private LinearLayout headerLayout;

    private List<GoodsInfo> newGoodsList = new ArrayList<>();
    private List<GoodsInfo> hotGoodsList = new ArrayList<>();
    private List<GoodsInfo> recommendList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        banner = view.findViewById(R.id.banner);
        gvNav = view.findViewById(R.id.gv_nav);
        gvNew = view.findViewById(R.id.gv_new);
        gvHot = view.findViewById(R.id.gv_hot);
        gvRecommend = view.findViewById(R.id.gv_recommend);
        tvSearch = view.findViewById(R.id.tv_search);
        ivLogin = view.findViewById(R.id.iv_login);
        scrollView = view.findViewById(R.id.scroll_view);
        headerLayout = view.findViewById(R.id.header_layout);

        // 三道杠菜单 - 跳转分类页
        ImageView ivMenu = view.findViewById(R.id.iv_menu);
        ivMenu.setOnClickListener(v -> ((MainActivity) getActivity()).switchToCategory());

        // 搜索栏点击
        tvSearch.setOnClickListener(v -> startActivity(new Intent(getActivity(), SearchActivity.class)));

        // 登录按钮
        ivLogin.setOnClickListener(v -> startActivity(new Intent(getActivity(), LoginActivity.class)));

        // 导航栏 - 分类跳转
        int[] navIcons = {
            R.drawable.nav_1, R.drawable.nav_2, R.drawable.nav_3, R.drawable.nav_4,
            R.drawable.nav_5, R.drawable.nav_6, R.drawable.nav_7, R.drawable.nav_8
        };
        String[] navNames = {"新蜂超市", "新蜂服饰", "母婴生活", "图书音像", "充值缴费", "生鲜到家", "家具家装", "更多分类"};
        List<NavGridItem> navItems = new ArrayList<>();
        for (int i = 0; i < navIcons.length; i++) {
            navItems.add(new NavGridItem(navIcons[i], navNames[i]));
        }
        NavGridAdapter navAdapter = new NavGridAdapter(navItems);
        gvNav.setAdapter(navAdapter);
        gvNav.setOnItemClickListener((parent, v, position, id) -> {
            if (position == 7) {
                // 跳转分类
                ((MainActivity) getActivity()).switchToCategory();
            }
        });

        // 设置滑动时标题栏变色
        scrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY > 200) {
                headerLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
            } else if (scrollY > 100) {
                headerLayout.setBackgroundColor(Color.parseColor("#80FFFFFF"));
            } else {
                headerLayout.setBackgroundColor(Color.TRANSPARENT);
            }
        });

        loadIndexInfos();
    }

    @Override
    public void onResume() {
        super.onResume();
        banner.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        banner.stop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        banner.destroy();
    }

    /**
     * 一次性加载首页所有数据（轮播图 + 新品 + 热门 + 推荐）
     * API: GET /mallapi/api/v1/index-infos
     * 响应: {resultCode:200, data:{carousels:[], newGoodses:[], hotGoodses:[], recommendGoodses:[]}}
     */
    private void loadIndexInfos() {
        new Thread(() -> {
            try {
                String result = HttpUtil.get(Constants.API_INDEX_INFOS);
                JSONObject obj = new JSONObject(result);
                JSONObject data = obj.optJSONObject("data");
                if (data == null) return;

                // 1. 解析轮播图
                List<String> imageUrls = new ArrayList<>();
                JSONArray carousels = data.optJSONArray("carousels");
                if (carousels != null) {
                    for (int i = 0; i < carousels.length(); i++) {
                        JSONObject item = carousels.getJSONObject(i);
                        String url = item.optString("carouselUrl");
                        if (url != null && !url.isEmpty()) {
                            if (!url.startsWith("http")) url = Constants.IMG_BASE_URL + url;
                            imageUrls.add(url);
                        }
                    }
                }

                // 2. 解析新品上线
                List<GoodsInfo> newGoods = parseGoodsList(data.optJSONArray("newGoodses"));

                // 3. 解析热门商品
                List<GoodsInfo> hotGoods = parseGoodsList(data.optJSONArray("hotGoodses"));

                // 4. 解析最新推荐
                List<GoodsInfo> recommendGoods = parseGoodsList(data.optJSONArray("recommendGoodses"));

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // 设置轮播图
                        if (imageUrls.isEmpty()) {
                            imageUrls.add("https://via.placeholder.com/800x400/1BAEFE/FFFFFF?text=NewBeeMall");
                        }
                        banner.setAdapter(new BannerImageAdapter<String>(imageUrls) {
                            @Override
                            public void onBindView(BannerImageHolder holder, String data, int position, int size) {
                                Glide.with(holder.itemView).load(data)
                                    .placeholder(R.drawable.bg_search_bar)
                                    .into(holder.imageView);
                            }
                        });
                        banner.start();

                        // 设置商品网格
                        setupGoodsGrid(gvNew, newGoods);
                        setupGoodsGrid(gvHot, hotGoods);
                        setupGoodsGrid(gvRecommend, recommendGoods);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private List<GoodsInfo> parseGoodsList(JSONArray jsonArray) {
        List<GoodsInfo> list = new ArrayList<>();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    JSONObject item = jsonArray.getJSONObject(i);
                    GoodsInfo goods = new GoodsInfo();
                    goods.setGoodsId(item.optInt("goodsId"));
                    goods.setGoodsName(item.optString("goodsName"));
                    goods.setGoodsCoverImg(item.optString("goodsCoverImg"));
                    goods.setGoodsIntro(item.optString("goodsIntro"));
                    goods.setSellingPrice(item.optInt("sellingPrice"));
                    goods.setTag(item.optInt("tag"));
                    list.add(goods);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    private void setupGoodsGrid(GridView gridView, List<GoodsInfo> list) {
        GoodsSimpleAdapter adapter = new GoodsSimpleAdapter(list);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener((parent, v, position, id) -> {
            Intent intent = new Intent(getActivity(), DetailActivity.class);
            intent.putExtra("goodsId", list.get(position).getGoodsId());
            startActivity(intent);
        });
    }

    // 导航项数据
    static class NavGridItem {
        int iconRes;
        String name;
        NavGridItem(int iconRes, String name) { this.iconRes = iconRes; this.name = name; }
    }

    // 导航适配器
    class NavGridAdapter extends android.widget.BaseAdapter {
        List<NavGridItem> items;
        NavGridAdapter(List<NavGridItem> items) { this.items = items; }
        @Override public int getCount() { return items.size(); }
        @Override public Object getItem(int pos) { return items.get(pos); }
        @Override public long getItemId(int pos) { return pos; }
        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_nav_grid, parent, false);
            }
            ImageView iv = convertView.findViewById(R.id.iv_icon);
            TextView tv = convertView.findViewById(R.id.tv_name);
            iv.setImageResource(items.get(pos).iconRes);
            tv.setText(items.get(pos).name);
            return convertView;
        }
    }

    // 商品简要适配器
    class GoodsSimpleAdapter extends android.widget.BaseAdapter {
        List<GoodsInfo> list;
        GoodsSimpleAdapter(List<GoodsInfo> list) { this.list = list; }
        @Override public int getCount() { return list.size(); }
        @Override public Object getItem(int pos) { return list.get(pos); }
        @Override public long getItemId(int pos) { return pos; }
        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_goods_grid, parent, false);
            }
            ImageView iv = convertView.findViewById(R.id.iv_goods);
            TextView tvName = convertView.findViewById(R.id.tv_name);
            TextView tvPrice = convertView.findViewById(R.id.tv_price);

            GoodsInfo goods = list.get(pos);
            String imgUrl = goods.getGoodsCoverImg();
            if (imgUrl != null && !imgUrl.startsWith("http")) imgUrl = Constants.IMG_BASE_URL + imgUrl;
            Glide.with(convertView).load(imgUrl).into(iv);
            tvName.setText(goods.getGoodsName());
            tvPrice.setText("¥" + goods.getSellingPrice());
            return convertView;
        }
    }
}
