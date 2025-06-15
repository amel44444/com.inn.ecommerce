package com.inn.ecommerce.servicelmpl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inn.ecommerce.POJO.Bill;
import com.inn.ecommerce.POJO.BillRequest;
import com.inn.ecommerce.POJO.ProductInBill;
import com.inn.ecommerce.constents.EcommerceConstants;
import com.inn.ecommerce.dao.BillDao;
import com.inn.ecommerce.service.BillService;
import com.inn.ecommerce.utils.EcommerceUtils;
import com.inn.ecommerce.utils.PDFGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

@Slf4j
@Service
public class BillServicelmpl implements BillService {

    @Autowired
    private BillDao billDao;

    @Override
    public ResponseEntity<String> generateReport(BillRequest billRequest) {
        log.info("Inside generateReport {}", billRequest);
        try {
            String uuid = "ecom-" + UUID.randomUUID();
            Bill bill = new Bill();

            bill.setUuid(uuid);
            bill.setEmail(billRequest.getEmail());
            bill.setContactNumber(billRequest.getContactNumber());
            bill.setPaymentMethod(billRequest.getPaymentMethod());
            bill.setTotal(billRequest.getTotal());

            // Sérialiser la liste des produits en JSON pour l'enregistrer dans productDetail
            ObjectMapper objectMapper = new ObjectMapper();

            List<ProductInBill> products = billRequest.getProducts();
            if (products == null) {
                products = new ArrayList<>();
            }
            /*List<ProductInBill> products = billRequest.getProducts();
            if (products == null || products.isEmpty()) {
                return EcommerceUtils.getResponseEntity("Erreur : la liste des produits est vide.", HttpStatus.BAD_REQUEST);
            }*/
            /*List<ProductInBill> products = billRequest.getProducts();

            if (products == null || products.isEmpty()) {
                log.warn("La liste des produits est vide ou nulle !");
            } else {
                log.info("Produits reçus : {}", products);
            }*/


            String productDetailJson = objectMapper.writeValueAsString(products);
            log.info("productDetailJson : {}", productDetailJson);
            bill.setProductDetail(productDetailJson);

            bill.setCreatedBy(billRequest.getCreatedBy());

            billDao.save(bill);

            Map<String, Object> reportMap = new HashMap<>();
            reportMap.put("uuid", uuid);
            reportMap.put("email", billRequest.getEmail());
            reportMap.put("contactNumber", billRequest.getContactNumber());
            reportMap.put("paymentMethod", billRequest.getPaymentMethod());
            reportMap.put("total", billRequest.getTotal());
            reportMap.put("productDetail", productDetailJson);
            reportMap.put("createdBy", billRequest.getCreatedBy());

            String fileName = "Facture_" + uuid + ".pdf";
            String directory = System.getProperty("user.home") + "/Desktop/factures/";
            new File(directory).mkdirs();
            FileOutputStream fos = new FileOutputStream(directory + fileName);
            PDFGenerator.generateBillPDF(fos, reportMap);

            return EcommerceUtils.getResponseEntity("Facture générée :\nUUID: " + uuid, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Erreur dans generateReport", e);
            return EcommerceUtils.getResponseEntity(EcommerceConstants.SOMETHING_WENT_WRONG,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




    @Override
    public ResponseEntity<List<Bill>> getBills() {
        log.info("Inside getBills");
        try {
            boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
                    .getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(role -> role.equals("ROLE_admin"));

            if (isAdmin) {
                return new ResponseEntity<>(billDao.findAll(), HttpStatus.OK);
            } else {
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                return new ResponseEntity<>(billDao.findByCreatedBy(email), HttpStatus.OK);
            }

        } catch (Exception e) {
            log.error("Erreur dans getBills", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Override
    public ResponseEntity<byte[]> getPdf(String uuid) {
        log.info("Inside getPdf {}", uuid);
        try {
            if (uuid == null || uuid.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            Optional<Bill> optionalBill = billDao.findByUuid(uuid);
            if (optionalBill.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            Bill bill = optionalBill.get();

            Map<String, Object> fullRequestMap = new HashMap<>();
            fullRequestMap.put("uuid", bill.getUuid());
            fullRequestMap.put("email", bill.getEmail());
            fullRequestMap.put("contactNumber", bill.getContactNumber());
            fullRequestMap.put("paymentMethod", bill.getPaymentMethod());
            fullRequestMap.put("total", bill.getTotal());
            fullRequestMap.put("productDetail", bill.getProductDetail());
            fullRequestMap.put("createdBy", bill.getCreatedBy());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PDFGenerator.generateBillPDF(baos, fullRequestMap);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename("Facture_" + uuid + ".pdf")
                    .build());
            headers.setContentType(MediaType.APPLICATION_PDF);

            return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Erreur dans getPdf", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @Override
    public ResponseEntity<String> deleteBill(Integer id) {
        log.info("Inside deleteBill id: {}", id);
        try {
            if (!billDao.existsById(id)) {
                return EcommerceUtils.getResponseEntity("Bill ID doesn't exist", HttpStatus.BAD_REQUEST);
            }

            billDao.deleteById(id);
            return EcommerceUtils.getResponseEntity("Bill deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Erreur dans deleteBill", e);
            return EcommerceUtils.getResponseEntity(EcommerceConstants.SOMETHING_WENT_WRONG,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
