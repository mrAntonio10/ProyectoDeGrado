package com.upb.repositories;


import com.upb.models.enterprise.Enterprise;
import com.upb.models.enterprise.dto.EnterpriseDto;
import com.upb.models.enterprise.dto.EnterpriseStateDto;
import com.upb.models.paymentsIntegration.PaymentIntegration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentIntegrationRepository extends JpaRepository<PaymentIntegration, String> {
    @Query("SELECT p " +
            "FROM PaymentIntegration  p " +
                "WHERE p.enterprise.id=:idEnterprise"
    )
    Optional<PaymentIntegration> getPaymentIntegrationConfig(@Param("idEnterprise") String idEnterprise);
}
