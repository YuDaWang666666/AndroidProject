package com.example.newbeemall.model;

public class GoodsInfo {
    private int goodsId;
    private String goodsName;
    private String goodsIntro;
    private int goodsCategoryId;
    private String goodsCoverImg;
    private String goodsCarousel;
    private int sellingPrice;
    private int originalPrice;
    private int stockNum;
    private int tag;
    private int goodsSellStatus;
    private String createTime;

    public int getGoodsId() { return goodsId; }
    public void setGoodsId(int goodsId) { this.goodsId = goodsId; }
    public String getGoodsName() { return goodsName; }
    public void setGoodsName(String goodsName) { this.goodsName = goodsName; }
    public String getGoodsIntro() { return goodsIntro; }
    public void setGoodsIntro(String goodsIntro) { this.goodsIntro = goodsIntro; }
    public int getGoodsCategoryId() { return goodsCategoryId; }
    public void setGoodsCategoryId(int goodsCategoryId) { this.goodsCategoryId = goodsCategoryId; }
    public String getGoodsCoverImg() { return goodsCoverImg; }
    public void setGoodsCoverImg(String goodsCoverImg) { this.goodsCoverImg = goodsCoverImg; }
    public String getGoodsCarousel() { return goodsCarousel; }
    public void setGoodsCarousel(String goodsCarousel) { this.goodsCarousel = goodsCarousel; }
    public int getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(int sellingPrice) { this.sellingPrice = sellingPrice; }
    public int getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(int originalPrice) { this.originalPrice = originalPrice; }
    public int getStockNum() { return stockNum; }
    public void setStockNum(int stockNum) { this.stockNum = stockNum; }
    public int getTag() { return tag; }
    public void setTag(int tag) { this.tag = tag; }
    public int getGoodsSellStatus() { return goodsSellStatus; }
    public void setGoodsSellStatus(int goodsSellStatus) { this.goodsSellStatus = goodsSellStatus; }
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
}
