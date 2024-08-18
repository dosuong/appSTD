package com.example.qldrl.FCM;

public class DeviceInfo {
    private String deviceId, token, tkId;
    public DeviceInfo(String deviceId, String token, String tkId) {
        this.deviceId = deviceId;
        this.token = token;
        this.tkId = tkId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
