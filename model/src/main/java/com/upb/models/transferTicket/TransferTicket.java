package com.upb.models.transferTicket;

import com.upb.models.branchOffice.BranchOffice;
import com.upb.models.enterprise.Enterprise;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(exclude = { "details", "events", "enterprise", "destination" })
@ToString(exclude = { "details", "events", "enterprise", "destination" })
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "TRANSFER_TICKET")
public class TransferTicket implements Serializable {

    @Id
    @Column(name = "ID")
    @UuidGenerator
    private String id;

    // INTERNAL_TRANSFER o EXTERNAL_SUPPLY
    @Column(name = "TRANSFER_TYPE", nullable = false, length = 30)
    private String transferType;

    // ID del BranchOffice o Supplier emisor
    @Column(name = "SOURCE_ID", nullable = false)
    private String sourceId;

    // nombre denormalizado para visualización rápida
    @Column(name = "SOURCE_NAME", length = 120)
    private String sourceName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_DESTINATION", referencedColumnName = "ID", nullable = false)
    private BranchOffice destination;

    @Column(name = "CURRENT_STATUS", nullable = false, length = 30)
    private String currentStatus;

    @Column(name = "DATE_CREATED")
    private LocalDateTime dateCreated;

    @Column(name = "COMMENTS", length = 500)
    private String comments;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<TransferDetail> details;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<TicketEvent> events;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ENTERPRISE", referencedColumnName = "ID", nullable = false)
    public Enterprise enterprise;
}
