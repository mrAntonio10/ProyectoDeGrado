package com.upb.models.operation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "OPERATION")
public class Operation implements Serializable {
    @Id
    @Column(name = "ID", length = 36)
    @UuidGenerator
    private String  id;

    @Column(name = "NAME", length = 60,nullable = false)
    private String name;

    @Column(name = "DESCRIPTION", length = 150,nullable = false)
    private String description;

   @Column(name = "STATE")
   private Boolean state;

}
