package org.greenflow.equipment.model.entity;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.greenflow.equipment.model.entity.types.Fertilizer;
import org.greenflow.equipment.model.entity.types.Lawnmower;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Document(collection = "equipment")
@Getter
@Setter(onParam_ = @NonNull)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Fertilizer.class, name = "fertilizer"),
        @JsonSubTypes.Type(value = Lawnmower.class, name = "lawnmower")}
)
public abstract class Equipment {

    @Id
    String id;

    @DBRef
    Warehouse warehouse;

    String name;

    String description;

}
