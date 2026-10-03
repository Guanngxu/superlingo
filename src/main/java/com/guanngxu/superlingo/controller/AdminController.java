package com.guanngxu.superlingo.controller;

import com.guanngxu.superlingo.common.ApiResponse;
import com.guanngxu.superlingo.dao.ProductDao;
import com.guanngxu.superlingo.dao.ScanLogDao;
import com.guanngxu.superlingo.dto.HotProduct;
import com.guanngxu.superlingo.entity.Product;
import com.guanngxu.superlingo.entity.ScanLog;
import com.guanngxu.superlingo.service.AdminAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminAuthService authService;
    private final ProductDao productDao;
    private final ScanLogDao scanLogDao;

    public AdminController(AdminAuthService authService, ProductDao productDao, ScanLogDao scanLogDao) {
        this.authService = authService;
        this.productDao = productDao;
        this.scanLogDao = scanLogDao;
    }

    /**
     * 管理员登录
     * POST /api/v1/admin/login  body: {"username": "...", "password": "..."}
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        if (username == null || username.isBlank() || password == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "缺少用户名或密码"));
        }

        String token = authService.login(username, password);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(401, "用户名或密码错误"));
        }

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        return ResponseEntity.ok(ApiResponse.ok(data));
    }

    /**
     * 商品列表（后台浏览所有已入库商品）
     * GET /api/v1/admin/products
     */
    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> listProducts() {
        return ResponseEntity.ok(ApiResponse.ok(productDao.findAll()));
    }

    /**
     * 人工修正商品信息（修正后 source 置为 MANUAL，后续扫码以本地数据为准）
     * POST /api/v1/admin/products/{barcode}
     * body: {"nameZh": "...", "nameEn": "...", "imageUrl": "..."}
     */
    @PostMapping("/products/{barcode}")
    public ResponseEntity<Map<String, Object>> updateProduct(
            @PathVariable String barcode,
            @RequestBody Map<String, String> body) {

        Product existing = productDao.findByBarcode(barcode);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "商品不存在，请先扫码入库"));
        }

        existing.setNameZh(body.getOrDefault("nameZh", existing.getNameZh()));
        existing.setNameEn(body.getOrDefault("nameEn", existing.getNameEn()));
        existing.setImageUrl(body.getOrDefault("imageUrl", existing.getImageUrl()));
        existing.setSource("MANUAL");

        productDao.saveOrUpdate(existing);
        return ResponseEntity.ok(ApiResponse.ok(existing));
    }

    /**
     * 热门商品排行榜
     * GET /api/v1/admin/stats/hot?limit=20
     */
    @GetMapping("/stats/hot")
    public ResponseEntity<Map<String, Object>> hotProducts(@RequestParam(defaultValue = "20") int limit) {
        List<HotProduct> hot = scanLogDao.findHotProducts(limit);
        return ResponseEntity.ok(ApiResponse.ok(hot));
    }

    /**
     * 某商品的扫码流水（每次扫码的 IP + 时间戳）
     * GET /api/v1/admin/logs/{barcode}
     */
    @GetMapping("/logs/{barcode}")
    public ResponseEntity<Map<String, Object>> scanLogs(@PathVariable String barcode) {
        List<ScanLog> logs = scanLogDao.findByBarcode(barcode);
        return ResponseEntity.ok(ApiResponse.ok(logs));
    }
}
