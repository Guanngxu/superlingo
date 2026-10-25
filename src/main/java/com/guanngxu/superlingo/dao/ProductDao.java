package com.guanngxu.superlingo.dao;

import com.guanngxu.superlingo.entity.Product;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
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
        String sql = "SELECT * FROM products WHERE barcode = ?";
        List<Product> list = jdbc.query(sql, new BeanPropertyRowMapper<>(Product.class), barcode);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 查询全部商品（后台浏览用）
     */
    public List<Product> findAll() {
        String sql = "SELECT * FROM products ORDER BY barcode";
        return jdbc.query(sql, new BeanPropertyRowMapper<>(Product.class));
    }

    /**
     * 有则更新，无则插入（PostgreSQL 的 ON CONFLICT 语法）
     */
    public void saveOrUpdate(Product p) {
        String sql = "INSERT INTO products (barcode, name_zh, name_en, image_url, source, countries, " +
                "categories, quantity, nova_group, brands, ingredients, origins) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (barcode) DO UPDATE SET " +
                "name_zh = EXCLUDED.name_zh, " +
                "name_en = EXCLUDED.name_en, " +
                "image_url = EXCLUDED.image_url, " +
                "source = EXCLUDED.source, " +
                "countries = EXCLUDED.countries, " +
                "categories = EXCLUDED.categories, " +
                "quantity = EXCLUDED.quantity, " +
                "nova_group = EXCLUDED.nova_group, " +
                "brands = EXCLUDED.brands, " +
                "ingredients = EXCLUDED.ingredients, " +
                "origins = EXCLUDED.origins";
        jdbc.update(sql, p.getBarcode(), p.getNameZh(), p.getNameEn(), p.getImageUrl(), p.getSource(), p.getCountries(),
                p.getCategories(), p.getQuantity(), p.getNovaGroup(), p.getBrands(), p.getIngredients(), p.getIngredients());
    }
}
