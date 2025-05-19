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

@TypeAlias("fertilizer")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Fertilizer extends Equipment {

    String brand;

    String model;

    String serialNumber;

    String nutrientContent;

    Double weight;

    String color;

    Boolean organic;

    Boolean slowRelease;

    Boolean waterSoluble;

    Boolean granular;
}
