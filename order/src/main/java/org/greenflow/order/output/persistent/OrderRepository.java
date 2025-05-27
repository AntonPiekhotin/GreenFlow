package org.greenflow.order.output.persistent;

import org.greenflow.order.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    List<Order> findAllByClientId(String clientId);

    List<Order> findAllByWorkerId(String workerId);

    @Query("SELECT o FROM Order o WHERE o.status = 'CREATED'")
    List<Order> findAllCreatedOrders();
}
