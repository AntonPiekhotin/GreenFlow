package org.greenflow.equipment.ouput.persistent;

import org.greenflow.equipment.model.entity.Equipment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipmentRepository extends MongoRepository<Equipment, String> {
}
