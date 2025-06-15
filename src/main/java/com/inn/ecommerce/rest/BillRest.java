package com.inn.ecommerce.rest;

import com.inn.ecommerce.POJO.BillRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bill")
public interface BillRest {

    @PostMapping("/generateReport")
    ResponseEntity<String> generateReport(@RequestBody BillRequest billRequest);

    @GetMapping("/getBills")
    ResponseEntity<?> getBills();

    @GetMapping("/getPdf/{uuid}")
    ResponseEntity<byte[]> getPdf(@PathVariable String uuid);

    @DeleteMapping("/delete/{id}")
    ResponseEntity<String> deleteBill(@PathVariable Integer id);
}
