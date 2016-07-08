package com.ashoksm.pinfinder.common;

public class AllCodeItem {
    private String pincode;
    private String officeName;

    public AllCodeItem(String pincode, String officeName) {
        this.pincode = pincode;
        this.officeName = officeName;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }
}
