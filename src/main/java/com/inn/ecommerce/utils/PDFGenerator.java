package com.inn.ecommerce.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PDFGenerator {

    public static void generateBillPDF(OutputStream out, Map<String, Object> data) throws Exception {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, out);
        document.open();

        // Fonts
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 12);
        Font tableHeaderFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
        Font cellFont = new Font(Font.FontFamily.HELVETICA, 11);

        // Titre
        Paragraph title = new Paragraph("FACTURE", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));

        // Infos facture
        String uuid = getSafeString(data, "uuid");
        document.add(new Paragraph("UUID de la Facture: " + (uuid.isEmpty() ? "UUID manquant" : uuid), normalFont));
        document.add(new Paragraph("Email: " + getSafeString(data, "email"), normalFont));
        document.add(new Paragraph("Contact: " + getSafeString(data, "contactNumber"), normalFont));
        document.add(new Paragraph("Méthode de paiement: " + getSafeString(data, "paymentMethod"), normalFont));
        document.add(new Paragraph("Total: " + getSafeString(data, "total") + " MAD", normalFont));
        document.add(new Paragraph("Créé par: " + getSafeString(data, "createdBy"), normalFont));
        document.add(new Paragraph(" "));

        // Désérialisation des produits
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> products;
        try {
            String productDetailJson = (String) data.get("productDetail");
            products = objectMapper.readValue(productDetailJson, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            products = Collections.emptyList();
        }

        // Tableau des produits
        PdfPTable table = new PdfPTable(3); // Nom, Prix, Quantité
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);
        table.setWidths(new float[]{3f, 2f, 2f});

        // En-têtes
        String[] headers = {"Nom Produit", "Prix", "Quantité"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, tableHeaderFont));
            cell.setBackgroundColor(BaseColor.DARK_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        // Lignes de produits
        for (Map<String, Object> product : products) {
            table.addCell(new Phrase(getSafeString(product, "name"), cellFont));
            table.addCell(new Phrase(getSafeString(product, "price"), cellFont));
            table.addCell(new Phrase(getSafeString(product, "quantity"), cellFont));
        }

        document.add(table);
        document.close();
    }

    private static String getSafeString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : "";
    }
}
