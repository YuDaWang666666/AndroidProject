package com.example.newbeemall.model;

import java.util.List;

public class OrderItem {
    private int orderId;
    private String orderNo;
    private int totalPrice;
    private int orderStatus;
    private String createTime;
    private List<GoodsItem> newBeeMallOrderItemVOS;

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public int getTotalPrice() { return totalPrice; }
    public void setTotalPrice(int totalPrice) { this.totalPrice = totalPrice; }
    public int getOrderStatus() { return orderStatus; }
    public void setOrderStatus(int orderStatus) { this.orderStatus = orderStatus; }
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
    public List<GoodsItem> getNewBeeMallOrderItemVOS() { return newBeeMallOrderItemVOS; }
    public void setNewBeeMallOrderItemVOS(List<GoodsItem> newBeeMallOrderItemVOS) { this.newBeeMallOrderItemVOS = newBeeMallOrderItemVOS; }

    public String getStatusText() {
        switch (orderStatus) {
            case 0: return "待付款";
            case 1: return "待确认";
            case 2: return "待发货";
            case 3: return "已发货";
            case 4: return "交易完成";
            default: return "未知";
        }
    }

    public static class GoodsItem {
        private int goodsId;
        private int goodsCount;
        private String goodsName;
        private String goodsCoverImg;
        private int sellingPrice;

        public int getGoodsId() { return goodsId; }
        public void setGoodsId(int goodsId) { this.goodsId = goodsId; }
        public int getGoodsCount() { return goodsCount; }
        public void setGoodsCount(int goodsCount) { this.goodsCount = goodsCount; }
        public String getGoodsName() { return goodsName; }
        public void setGoodsName(String goodsName) { this.goodsName = goodsName; }
        public String getGoodsCoverImg() { return goodsCoverImg; }
        public void setGoodsCoverImg(String goodsCoverImg) { this.goodsCoverImg = goodsCoverImg; }
        public int getSellingPrice() { return sellingPrice; }
        public void setSellingPrice(int sellingPrice) { this.sellingPrice = sellingPrice; }
    }
}
