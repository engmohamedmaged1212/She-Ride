package com.project.shedrive.Driver;

import com.project.shedrive.User.Status;
import com.project.shedrive.User.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "drivers")
public class Driver {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id")
    private User user; // the same person in the database

    @Column(name = "national_id_front_url", length = 500)
    private String nationalIdFrontUrl;

    @Column(name = "national_id_back_url", length = 500)
    private String nationalIdBackUrl;

    @Column(name = "driver_license_url", length = 500)
    private String driverLicenseUrl;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "id_verification_status", nullable = false)
    private Status VerificationStatus;

    @Column(name = "id_verified_at")
    private LocalDateTime idVerifiedAt;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "is_online", nullable = false)
    private Boolean isOnline = false;

    @Column(name = "current_latitude", precision = 10, scale = 8)
    private BigDecimal currentLatitude;

    @Column(name = "current_longitude", precision = 11, scale = 8)
    private BigDecimal currentLongitude;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "total_trips", nullable = false)
    private Integer totalTrips;

    @ColumnDefault("0.00")
    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "id_verified_by")
    private User idVerifiedBy; // admin who verify

}