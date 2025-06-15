package com.inn.ecommerce.service;

import com.inn.ecommerce.POJO.Bill;
import com.inn.ecommerce.POJO.BillRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BillService {

    ResponseEntity<String> generateReport(BillRequest billRequest);

    ResponseEntity<byte[]> getPdf(String uuid);

    ResponseEntity<List<Bill>> getBills();

    ResponseEntity<String> deleteBill(Integer id);
}
