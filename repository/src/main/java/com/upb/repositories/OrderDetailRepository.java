package com.upb.repositories;


import com.upb.models.detail.OrderDetail;
import com.upb.models.detail.dto.AllDetailInfoDto;
import com.upb.models.detail.dto.CharInfoDto;
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

    @Query("SELECT detail FROM OrderDetail detail " +
                "INNER JOIN FETCH detail.product p " +
                "INNER JOIN FETCH detail.document doc " +
                "INNER JOIN FETCH doc.branchOfficeInfo branch " +
            "WHERE doc.state <> 'DELETED' " +
                "AND p.state = true " +
                "AND doc.branchOfficeInfo.id IN :ids " +
                "AND doc.deliveryDate >=:first " +
                "AND doc.deliveryDate <=:last"
    )
    List<CharInfoDto> getCharInfoDataList(@Param("ids") List<String> idList,
                                          @Param("first") Long firstDay,
                                          @Param("last") Long lastDay);

    @Query("SELECT detail FROM OrderDetail detail " +
                "INNER JOIN FETCH detail.product p " +
                "INNER JOIN FETCH detail.document doc " +
                "INNER JOIN FETCH doc.branchOfficeInfo branch " +
            "WHERE doc.state <> 'DELETED' " +
                "AND p.state = true " +
                "AND doc.branchOfficeInfo.id  =:idBranchOffice " +
                "AND doc.deliveryDate >=:first " +
                "AND doc.deliveryDate <=:last"
    )
    List<CharInfoDto> getCharProductInfoDataList(@Param("idBranchOffice") String idBranchOffice,
                                          @Param("first") Long firstDay,
                                          @Param("last") Long lastDay);

}
