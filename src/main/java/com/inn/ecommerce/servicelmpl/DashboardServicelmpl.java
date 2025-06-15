package com.inn.ecommerce.servicelmpl;

import com.inn.ecommerce.dao.BillDao;
import com.inn.ecommerce.dao.CategoryDao;
import com.inn.ecommerce.dao.ProductDao;
import com.inn.ecommerce.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardServicelmpl implements DashboardService {

    @Autowired
    private BillDao billDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private CategoryDao categoryDao;

    @Override
    public ResponseEntity<Map<String, Object>> getDashboardDetails() {
        Map<String, Object> response = new HashMap<>();

        try {
            long totalBills = billDao.count();
            double totalRevenue = billDao.findAll().stream().mapToDouble(b -> b.getTotal()).sum();
            long totalProducts = productDao.count();
            long totalCategories = categoryDao.count();

            response.put("Bill", totalBills);
            response.put("Revenue", totalRevenue);
            response.put("Product", totalProducts);  // <-- ici modifié
            response.put("Category", totalCategories);  // <-- ici modifié

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Erreur lors de la récupération des données : " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

}
