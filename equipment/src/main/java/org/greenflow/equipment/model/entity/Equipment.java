package org.greenflow.equipment.model.entity;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.greenflow.equipment.model.constant.LeasingStatus;
import org.greenflow.equipment.model.entity.types.Lawnmower;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.math.BigDecimal;

@Document(collection = "equipment")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Lawnmower.class, name = "lawnmower")}
)
public abstract class Equipment {

    @Id
    String id;

    LeasingStatus status = LeasingStatus.AVAILABLE;

    String leasedBy;

    @NotNull
    BigDecimal dailyLeasingPrice;

    Long warehouseId;

    @NotBlank
    String name;

    String description;

}
