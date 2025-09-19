package com.upb.repositories;


import com.upb.models.branchOffice.BranchOffice;
import com.upb.models.branchOffice.dto.BranchOfficeDto;
import com.upb.models.branchOffice.dto.BranchOfficeStateDto;
import com.upb.models.document.Document;
import com.upb.models.document.dto.SalesUserDocumentDto;
import com.upb.models.enterprise.dto.EnterpriseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, String> {

    @Query("SELECT d FROM Document d " +
                "INNER JOIN FETCH d.salesUser u " +
            "WHERE d.state =:state " +
                "AND (:filter IS NULL) " +
                "AND u.id =:idUser " +
                "AND d.deliveryDate > :sDate " +
                "AND d.deliveryDate < :fDate"
    )
    Page<SalesUserDocumentDto> getSalesUserDocumetPageable(@Param("idUser") String idUser,
                                                           @Param("filter") String paymentMethod,
                                                           @Param("sDate") Long sDate,
                                                           @Param("fDate") Long fDate,
                                                           @Param("state") String state,
                                                           Pageable pageable);

    @Query("SELECT d FROM Document d " +
                "INNER JOIN FETCH d.salesUser u " +
            "WHERE d.state <> 'DELETED' " +
                "AND (:filter IS NULL OR UPPER(d.paymentMethod) LIKE :filter) " +
                "AND (:idUser IS NULL OR u.id =:idUser) " +
                "AND d.deliveryDate > :sDate " +
                "AND d.deliveryDate < :fDate"
    )
    Page<SalesUserDocumentDto> getManagementUserDocumetPageable(@Param("idUser") String idUser,
                                                           @Param("filter") String paymentMethod,
                                                           @Param("sDate") Long sDate,
                                                           @Param("fDate") Long fDate,
                                                           Pageable pageable);

    @Query("SELECT d FROM Document d " +
            "WHERE d.id =:id " +
                "AND d.state <> 'DELETED'"
    )
    Optional<Document> getDocumentByIdDocument(@Param("id") String id);
}
