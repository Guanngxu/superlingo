package com.guanngxu.superlingo.entity;

public class Product {

    private String barcode; // 条形码
    private String nameZh; // 中文名称
    private String nameEn; // 英文名称
    private String imageUrl; // 图片
    private String source; // 来源：AUTO 表示自动填写
    private String quantity; // 重量信息（包含单位）
    private String brands; // 品牌
    private String categories; // 类别
    private String countries; // 可出售国家
    private String origins; // 原料来源
    private String ingredients; // 成分及占比
    private Integer novaGroup; // 加工程度：1、未加工或低度加工的食品，2、加工过的烹饪原料，3、加工食品，4、超加工食品

    public Product() {}

    public Product(String barcode, String nameZh, String nameEn, String imageUrl, String source, String quantity, String brands, String categories, String countries, String origins, String ingredients, Integer novaGroup) {
        this.barcode = barcode;
        this.nameZh = nameZh;
        this.nameEn = nameEn;
        this.imageUrl = imageUrl;
        this.source = source;
        this.quantity = quantity;
        this.brands = brands;
        this.categories = categories;
        this.countries = countries;
        this.origins = origins;
        this.ingredients = ingredients;
        this.novaGroup = novaGroup;
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

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getBrands() {
        return brands;
    }

    public void setBrands(String brands) {
        this.brands = brands;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getCountries() {
        return countries;
    }

    public void setCountries(String countries) {
        this.countries = countries;
    }

    public String getOrigins() {
        return origins;
    }

    public void setOrigins(String origins) {
        this.origins = origins;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public Integer getNovaGroup() {
        return novaGroup;
    }

    public void setNovaGroup(Integer novaGroup) {
        this.novaGroup = novaGroup;
    }

    @Override
    public String toString() {
        return "Product{" +
                "barcode='" + barcode + '\'' +
                ", nameZh='" + nameZh + '\'' +
                ", nameEn='" + nameEn + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", source='" + source + '\'' +
                ", quantity='" + quantity + '\'' +
                ", brands='" + brands + '\'' +
                ", categories='" + categories + '\'' +
                ", countries='" + countries + '\'' +
                ", origins='" + origins + '\'' +
                ", ingredients='" + ingredients + '\'' +
                ", novaGroup=" + novaGroup +
                '}';
    }
}
