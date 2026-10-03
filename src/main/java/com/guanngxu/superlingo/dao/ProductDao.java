package com.guanngxu.superlingo.dao;

import com.guanngxu.superlingo.entity.Product;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductDao {

    private final JdbcTemplate jdbc;

    public ProductDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * 按条码查商品（L1 缓存查询）
     */
    public Product findByBarcode(String barcode) {
        String sql = "SELECT barcode, name_zh, name_en, image_url, source FROM products WHERE barcode = ?";
        List<Product> list = jdbc.query(sql, (rs, i) -> new Product(
                rs.getString("barcode"),
                rs.getString("name_zh"),
                rs.getString("name_en"),
                rs.getString("image_url"),
                rs.getString("source")
        ), barcode);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 查询全部商品（后台浏览用）
     */
    public List<Product> findAll() {
        String sql = "SELECT barcode, name_zh, name_en, image_url, source FROM products ORDER BY barcode";
        return jdbc.query(sql, (rs, i) -> new Product(
                rs.getString("barcode"),
                rs.getString("name_zh"),
                rs.getString("name_en"),
                rs.getString("image_url"),
                rs.getString("source")
        ));
    }

    /**
     * 有则更新，无则插入（PostgreSQL 的 ON CONFLICT 语法）
     */
    public void saveOrUpdate(Product p) {
        String sql = "INSERT INTO products (barcode, name_zh, name_en, image_url, source) " +
                "VALUES (?, ?, ?, ?, ?) " +
                "ON CONFLICT (barcode) DO UPDATE SET " +
                "name_zh = EXCLUDED.name_zh, " +
                "name_en = EXCLUDED.name_en, " +
                "image_url = EXCLUDED.image_url, " +
                "source = EXCLUDED.source";
        jdbc.update(sql, p.getBarcode(), p.getNameZh(), p.getNameEn(), p.getImageUrl(), p.getSource());
    }
}
