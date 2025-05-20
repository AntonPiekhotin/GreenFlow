package org.greenflow.equipment.output.persistent;

import org.greenflow.equipment.model.entity.EquipmentLease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentLeaseRepository extends JpaRepository<EquipmentLease, Long> {
    List<EquipmentLease> findAllByLesseeId(String lesseeId);
}
