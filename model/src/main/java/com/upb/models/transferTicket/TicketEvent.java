package com.upb.models.transferTicket;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.upb.models.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(exclude = { "ticket", "user" })
@ToString(exclude = { "ticket", "user" })
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "TICKET_EVENT")
public class TicketEvent implements Serializable {

    @Id
    @Column(name = "ID")
    @UuidGenerator
    private String id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TICKET", referencedColumnName = "ID", nullable = false)
    private TransferTicket ticket;

    // Ej: ACCEPTED, REJECTED, PARTIALLY_DAMAGED → INMUTABLE
    @Column(name = "STATUS_CHANGE", nullable = false, length = 30)
    private String statusChange;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USER", referencedColumnName = "ID", nullable = false)
    private User user;

    @Column(name = "EVENT_DATE")
    private LocalDateTime date;

    @Column(name = "COMMENTS", length = 500)
    private String comments;
}
