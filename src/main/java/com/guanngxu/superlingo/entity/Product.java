package com.guanngxu.superlingo.entity;

public class Product {

    private String barcode;
    private String nameZh;
    private String nameEn;
    private String imageUrl;
    private String source;

    public Product() {}

    public Product(String barcode, String nameZh, String nameEn, String imageUrl, String source) {
        this.barcode = barcode;
        this.nameZh = nameZh;
        this.nameEn = nameEn;
        this.imageUrl = imageUrl;
        this.source = source;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getNameZh() {
        return nameZh;
    }

    public void setNameZh(String nameZh) {
        this.nameZh = nameZh;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "Product{" +
                "barcode='" + barcode + '\'' +
                ", nameZh='" + nameZh + '\'' +
                ", nameEn='" + nameEn + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}
