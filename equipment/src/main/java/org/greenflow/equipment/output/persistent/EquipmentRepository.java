package org.greenflow.equipment.output.persistent;

import org.greenflow.equipment.model.entity.Equipment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentRepository extends MongoRepository<Equipment, String> {

    List<Equipment> findAllByWarehouseId(String warehouseId);

    List<Equipment> findAllByLeasedBy(String leasedBy);
}
