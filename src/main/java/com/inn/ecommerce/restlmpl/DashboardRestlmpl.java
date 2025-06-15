package com.inn.ecommerce.restlmpl;

import com.inn.ecommerce.rest.DashboardRest;
import com.inn.ecommerce.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DashboardRestlmpl implements DashboardRest {

    @Autowired
    private DashboardService dashboardService;

    @Override
    public ResponseEntity<Map<String, Object>> getDashboardDetails() {
        return dashboardService.getDashboardDetails();
    }
}
