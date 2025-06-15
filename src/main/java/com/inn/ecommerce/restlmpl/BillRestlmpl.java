package com.inn.ecommerce.restlmpl;

import com.inn.ecommerce.POJO.BillRequest;
import com.inn.ecommerce.rest.BillRest;
import com.inn.ecommerce.service.BillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/bill")
public class BillRestlmpl implements BillRest {

    @Autowired
    private BillService billService;

    @Override
    public ResponseEntity<String> generateReport(@RequestBody BillRequest billRequest) {
        try {
            // ✅ Ajoute ce log pour voir ce que tu reçois
            System.out.println("Produits reçus : " + billRequest.getProducts());

            // Facultatif : log du body entier
            System.out.println("Requête complète : " + billRequest);

            return billService.generateReport(billRequest);
        } catch (Exception e) {
            log.error("Erreur lors de la génération du rapport : {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la génération du rapport.");
        }
    }

    @Override
    public ResponseEntity<?> getBills() {
        try {
            return billService.getBills();
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des factures : {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la récupération des factures.");
        }
    }

    @Override
    @GetMapping("/getPdf/{uuid}")

    public ResponseEntity<byte[]> getPdf(@PathVariable String uuid) {
        try {
            return billService.getPdf(uuid);
        } catch (Exception e) {
            log.error("Erreur lors de la génération du PDF : {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<String> deleteBill(@PathVariable Integer id) {
        try {
            return billService.deleteBill(id);
        } catch (Exception e) {
            log.error("Erreur lors de la suppression de la facture (ID: {}) : {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la suppression de la facture.");
        }
    }
}
