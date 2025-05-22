package org.greenflow.billing.output.persistent;

import org.greenflow.billing.model.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {

    List<Payment> findAllByUserId(String userId);
}
