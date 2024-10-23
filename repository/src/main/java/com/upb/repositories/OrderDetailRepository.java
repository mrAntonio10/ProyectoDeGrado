package com.upb.repositories;


import com.upb.models.detail.OrderDetail;
import com.upb.models.detail.dto.AllDetailInfoDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, String> {

    @Query("SELECT detail FROM OrderDetail detail " +
                "INNER JOIN FETCH detail.product p " +
                "INNER JOIN FETCH detail.document doc " +
                "INNER JOIN FETCH doc.salesUser user " +
            "WHERE doc.state <> 'DELETED' " +
                "AND p.state = true " +
                "AND doc.id =:idDoc " +
                "AND user.state <> 'DELETED'"
    )
    List<AllDetailInfoDto> getDetailAndDocumentInfo(@Param("idDoc") String idDocument);
}
