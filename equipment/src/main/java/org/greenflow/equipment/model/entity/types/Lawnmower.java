package org.greenflow.equipment.model.entity.types;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.greenflow.equipment.model.entity.Equipment;
import org.springframework.data.annotation.TypeAlias;

@TypeAlias("LAWNMOWER")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Lawnmower extends Equipment {

    String type;

    String brand;

    String model;

    String serialNumber;

    String fuelType;

    Double fuelCapacity;

    Double cuttingWidth;

    Double cuttingHeight;

    Double weight;

    String color;

    String engineType;

    Integer horsepower;

    Integer bladeCount;

    Boolean selfPropelled;

    Boolean mulchingCapability;

    Boolean baggingCapability;
}
