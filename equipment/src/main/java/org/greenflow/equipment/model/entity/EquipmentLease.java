package org.greenflow.equipment.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "equipment_leasing")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EquipmentLease {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false)
    String equipmentId;

    @Column(nullable = false)
    String lesseeId;

    @Column(nullable = false)
    LocalDateTime startDate;
    @Column(nullable = false)
    LocalDateTime endDate;

    @Column(nullable = false)
    BigDecimal dailyRate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    LeasingStatus status = LeasingStatus.ACTIVE;

    public enum LeasingStatus {
        ACTIVE,
        CLOSED
    }
}
