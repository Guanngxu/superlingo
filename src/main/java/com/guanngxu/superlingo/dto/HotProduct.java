package com.guanngxu.superlingo.dto;

/**
 * 热门商品排行榜条目。
 */
public class HotProduct {

    private String barcode;
    private String nameZh;
    private String nameEn;
    private long scanCount;

    public HotProduct(String barcode, String nameZh, String nameEn, long scanCount) {
        this.barcode = barcode;
        this.nameZh = nameZh;
        this.nameEn = nameEn;
        this.scanCount = scanCount;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getNameZh() {
        return nameZh;
    }

    public String getNameEn() {
        return nameEn;
    }

    public long getScanCount() {
        return scanCount;
    }
}
