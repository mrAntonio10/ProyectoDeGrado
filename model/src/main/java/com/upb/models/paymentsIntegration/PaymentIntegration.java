package com.upb.models.paymentsIntegration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.upb.models.enterprise.Enterprise;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "PAYMENT_INTEGRATION")
public class PaymentIntegration implements Serializable {
    @Id
    @Column(name = "ID", length = 36)
    @UuidGenerator
    private String  id;

    @Column(name = "USERNAME", nullable = false)
    private String user;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "API_KEY", nullable = false)
    private String apiKey;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ENTERPRISE", referencedColumnName = "ID", nullable=false)
    public Enterprise enterprise;
}
