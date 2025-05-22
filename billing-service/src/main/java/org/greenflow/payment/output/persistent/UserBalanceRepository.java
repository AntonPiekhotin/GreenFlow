package org.greenflow.payment.output.persistent;

import org.greenflow.payment.model.entity.UserBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBalanceRepository extends JpaRepository<UserBalance, String> {
}
