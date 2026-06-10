package com.example.newbeemall.model;

public class AddressInfo {
    private int addressId;
    private String userName;
    private String userPhone;
    private String provinceName;
    private String cityName;
    private String regionName;
    private String detailAddress;
    private int defaultFlag;

    public int getAddressId() { return addressId; }
    public void setAddressId(int addressId) { this.addressId = addressId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getUserPhone() { return userPhone; }
    public void setUserPhone(String userPhone) { this.userPhone = userPhone; }
    public String getProvinceName() { return provinceName; }
    public void setProvinceName(String provinceName) { this.provinceName = provinceName; }
    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }
    public String getRegionName() { return regionName; }
    public void setRegionName(String regionName) { this.regionName = regionName; }
    public String getDetailAddress() { return detailAddress; }
    public void setDetailAddress(String detailAddress) { this.detailAddress = detailAddress; }
    public int getDefaultFlag() { return defaultFlag; }
    public void setDefaultFlag(int defaultFlag) { this.defaultFlag = defaultFlag; }

    public String getFullAddress() {
        return (provinceName != null ? provinceName : "") +
               (cityName != null ? cityName : "") +
               (regionName != null ? regionName : "") +
               (detailAddress != null ? detailAddress : "");
    }
}
