package org.greenflow.equipment.output.persistent;

import org.greenflow.equipment.model.constant.EquipmentStatus;
import org.greenflow.equipment.model.entity.Equipment;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentRepository extends MongoRepository<Equipment, String> {

    List<Equipment> findAllByWarehouseId(Long warehouseId);

    List<Equipment> findAllByLeasedBy(String leasedBy);

    List<Equipment> findByWarehouseIdInAndStatus(
            List<Long> warehouseIds,
            EquipmentStatus status,
            Sort sort
    );

}
