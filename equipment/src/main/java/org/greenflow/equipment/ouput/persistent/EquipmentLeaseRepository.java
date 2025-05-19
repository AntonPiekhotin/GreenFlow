package org.greenflow.equipment.ouput.persistent;

import org.greenflow.equipment.model.entity.EquipmentLease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipmentLeaseRepository extends JpaRepository<EquipmentLease, String> {
}
