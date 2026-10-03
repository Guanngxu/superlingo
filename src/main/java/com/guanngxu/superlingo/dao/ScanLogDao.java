package com.guanngxu.superlingo.dao;

import com.guanngxu.superlingo.dto.HotProduct;
import com.guanngxu.superlingo.entity.ScanLog;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ScanLogDao {

    private final JdbcTemplate jdbc;

    public ScanLogDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * 静默记录一次扫码行为（id 与 scanned_at 由数据库自动生成）
     */
    public void insert(ScanLog log) {
        String sql = "INSERT INTO scan_logs (barcode, ip_address) VALUES (?, ?)";
        jdbc.update(sql, log.getBarcode(), log.getIpAddress());
    }

    /**
     * 查询某个条码的扫码流水（IP + 时间）
     */
    public List<ScanLog> findByBarcode(String barcode) {
        String sql = "SELECT id, barcode, ip_address, scanned_at FROM scan_logs WHERE barcode = ? ORDER BY scanned_at DESC";
        return jdbc.query(sql, (rs, i) -> {
            ScanLog log = new ScanLog(rs.getString("barcode"), rs.getString("ip_address"));
            log.setId(rs.getInt("id"));
            log.setScannedAt(rs.getTimestamp("scanned_at"));
            return log;
        }, barcode);
    }

    /**
     * 热门商品排行榜：按累计被扫次数倒序
     */
    public List<HotProduct> findHotProducts(int limit) {
        String sql = "SELECT l.barcode, p.name_zh, p.name_en, COUNT(*) AS scan_count " +
                "FROM scan_logs l " +
                "LEFT JOIN products p ON l.barcode = p.barcode " +
                "GROUP BY l.barcode, p.name_zh, p.name_en " +
                "ORDER BY scan_count DESC " +
                "LIMIT ?";
        return jdbc.query(sql, (rs, i) -> new HotProduct(
                rs.getString("barcode"),
                rs.getString("name_zh"),
                rs.getString("name_en"),
                rs.getLong("scan_count")
        ), limit);
    }
}
