package com.project.shedrive.Admin;

import com.project.shedrive.Customer.Customer;
import com.project.shedrive.Driver.Driver;
import com.project.shedrive.User.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "admins")
public class Admin {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id", nullable = false)
    private User user;

    @Column(name = "permissions")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> permissions;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;


}