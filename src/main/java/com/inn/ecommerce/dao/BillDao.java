package com.inn.ecommerce.dao;

import com.inn.ecommerce.POJO.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BillDao extends JpaRepository<Bill, Integer> {
    List<Bill> findByCreatedBy(String createdBy);
    Optional<Bill> findByUuid(String uuid);

}
