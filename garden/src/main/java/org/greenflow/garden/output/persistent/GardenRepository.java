package org.greenflow.garden.output.persistent;

import org.greenflow.garden.model.entity.Garden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GardenRepository extends JpaRepository<Garden, Long> {

    List<Garden> findAllByOwnerId(String ownerId);
}
