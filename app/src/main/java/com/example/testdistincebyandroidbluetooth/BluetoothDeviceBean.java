package com.example.testdistincebyandroidbluetooth;

/**
 * @author xin
 * @datetime 21/03/2020 21:55
 * @description
 */
public class BluetoothDeviceBean {
    private String name;
    private String mac;
    private double distince;
    private int rssi;

    public BluetoothDeviceBean(String name, String mac, double distince, int rssi) {
        this.name = name;
        this.mac = mac;
        this.distince = distince;
        this.rssi = rssi;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public double getDistince() {
        return distince;
    }

    public void setDistince(double distince) {
        this.distince = distince;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    @Override
    public String toString() {
        return "BluetoothDeviceBean{" +
                "name='" + name + '\'' +
                ", mac='" + mac + '\'' +
                ", distince=" + distince +
                ", rssi=" + rssi +
                '}';
    }
}
