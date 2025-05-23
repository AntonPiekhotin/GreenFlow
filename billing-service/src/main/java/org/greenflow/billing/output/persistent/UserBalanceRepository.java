package org.greenflow.billing.output.persistent;

import org.greenflow.billing.model.entity.UserBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBalanceRepository extends JpaRepository<UserBalance, String> {
}
