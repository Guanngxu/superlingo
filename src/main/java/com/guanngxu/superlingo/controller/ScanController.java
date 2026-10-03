package com.guanngxu.superlingo.controller;

import com.guanngxu.superlingo.common.ApiResponse;
import com.guanngxu.superlingo.entity.Product;
import com.guanngxu.superlingo.service.ScanService;
import com.guanngxu.superlingo.utils.ClientIpUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ScanController {

    private final ScanService scanService;

    public ScanController(ScanService scanService) {
        this.scanService = scanService;
    }

    /**
     * C 端扫码入口
     * 路径示例: GET /api/v1/scan/0028400199148
     */
    @GetMapping("/scan/{barcode}")
    public ResponseEntity<Map<String, Object>> scan(
            @PathVariable String barcode,
            HttpServletRequest request) {

        String ipAddress = ClientIpUtil.getClientIp(request);
        Product product = scanService.processScan(barcode, ipAddress);

        if (product != null) {
            return ResponseEntity.ok(ApiResponse.ok(product));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(404, "未找到商品信息"));
    }
}
