package org.greenflow.equipment.output.persistent;

import org.greenflow.equipment.model.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    @Query(value = "SELECT * " +
            "FROM warehouses " +
            "WHERE ST_DWithin(cast(location as geography), " +
            "cast(ST_SetSRID(ST_MakePoint(:longitude, :latitude),4326) as geography), :radius)",
            nativeQuery = true)
    List<Warehouse> findWithinRadius(@Param("latitude") double latitude, @Param("longitude") double longitude,
                                     @Param("radius") double radiusInMeters);

}