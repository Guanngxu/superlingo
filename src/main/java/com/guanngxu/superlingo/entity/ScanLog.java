package com.guanngxu.superlingo.entity;

import java.sql.Timestamp;

public class ScanLog {

    private Integer id;            // 对应自增流水号
    private String barcode;        // 关联商品的外键
    private String ipAddress;      // 用户 IP
    private Timestamp scannedAt;   // 扫码时间

    public ScanLog() {}

    public ScanLog(String barcode, String ipAddress) {
        this.barcode = barcode;
        this.ipAddress = ipAddress;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Timestamp getScannedAt() {
        return scannedAt;
    }

    public void setScannedAt(Timestamp scannedAt) {
        this.scannedAt = scannedAt;
    }

    @Override
    public String toString() {
        return "ScanLog{" +
                "id=" + id +
                ", barcode='" + barcode + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", scannedAt=" + scannedAt +
                '}';
    }
}
