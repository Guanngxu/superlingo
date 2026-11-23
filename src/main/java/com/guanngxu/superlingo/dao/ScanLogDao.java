package com.guanngxu.superlingo.dao;

import com.guanngxu.superlingo.entity.ScanLog;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
}
