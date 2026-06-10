package com.example.newbeemall.util;

public class Constants {
    // 新地址（实训环境内网）
    // public static final String BASE_URL = "http://172.21.3.8:28019";
    // 备用地址
    public static final String BASE_URL = "http://172.30.130.131:28019";
    public static final String API_BASE = BASE_URL + "/mallapi/api/v1";
    // 图片资源基础路径（静态资源也需要 /mallapi 前缀）
    public static final String IMG_BASE_URL = BASE_URL + "/mallapi";

    // 首页
    public static final String API_INDEX_INFOS = API_BASE + "/index-infos";

    // 用户
    public static final String API_LOGIN = API_BASE + "/user/login";
    public static final String API_REGISTER = API_BASE + "/user/register";

    // 分类 & 搜索
    public static final String API_CATEGORIES = API_BASE + "/categories";
    public static final String API_SEARCH = API_BASE + "/search";
    public static final String API_GOODS_DETAIL = API_BASE + "/goods/detail";

    // 购物车
    public static final String API_CART_LIST = API_BASE + "/shop-cart";
    public static final String API_CART_ADD = API_BASE + "/shop-cart";
    public static final String API_CART_UPDATE = API_BASE + "/shop-cart";
    public static final String API_CART_DELETE = API_BASE + "/shop-cart";

    // 订单
    public static final String API_ORDER_LIST = API_BASE + "/order";
    public static final String API_ORDER_CREATE = API_BASE + "/saveOrder";
    public static final String API_ORDER_DETAIL = API_BASE + "/order";
    public static final String API_ORDER_PAY = API_BASE + "/paySuccess";
    public static final String API_ORDER_FINISH = API_BASE + "/order/finish";

    // 地址
    public static final String API_ADDRESS_LIST = API_BASE + "/address";
    public static final String API_ADDRESS_ADD = API_BASE + "/address";
    public static final String API_ADDRESS_UPDATE = API_BASE + "/address";

    public static final String SP_NAME = "info";
}
