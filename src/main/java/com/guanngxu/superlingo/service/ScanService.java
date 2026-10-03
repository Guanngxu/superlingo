package com.guanngxu.superlingo.service;

import com.guanngxu.superlingo.client.OpenFoodFactsClient;
import com.guanngxu.superlingo.dao.ProductDao;
import com.guanngxu.superlingo.dao.ScanLogDao;
import com.guanngxu.superlingo.entity.Product;
import com.guanngxu.superlingo.entity.ScanLog;
import org.springframework.stereotype.Service;

@Service
public class ScanService {

    private final ProductDao productDao;
    private final ScanLogDao scanLogDao;
    private final OpenFoodFactsClient offClient;

    public ScanService(ProductDao productDao, ScanLogDao scanLogDao, OpenFoodFactsClient offClient) {
        this.productDao = productDao;
        this.scanLogDao = scanLogDao;
        this.offClient = offClient;
    }

    /**
     * 核心业务：处理一次扫码
     * 1. L1 缓存：本地库命中则直接返回（人工修正过的数据优先，不再依赖外部接口）
     * 2. 缓存未命中：调用 Open Food Facts 抓取并「首扫落库」
     * 3. 成功解析后记录扫码流水（先落库再埋点，满足 scan_logs 的外键约束）
     */
    public Product processScan(String barcode, String ipAddress) {
        Product product = productDao.findByBarcode(barcode);
        if (product != null) {
            System.out.println("✅ [命中缓存] 本地库获取商品: " + product.getNameZh());
        } else {
            System.out.println("🌐 [触发抓取] 本地无数据，向 Open Food Facts 请求: " + barcode);
            product = offClient.fetch(barcode);
            if (product != null) {
                productDao.saveOrUpdate(product); // 首扫落库
            }
        }

        // 只有成功解析出商品信息才记录流水（scan_logs 外键依赖 products）
        if (product != null) {
            scanLogDao.insert(new ScanLog(barcode, ipAddress));
        }
        return product;
    }
}
