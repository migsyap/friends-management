package com.sp.db.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SUBSCRIPTIONS")
public class Subscription {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "requestor")
    private String requestor;

    @Column(name = "target")
    private String target;

    @Column(name = "created_on")
    private Date createdOn;

    @Column(name = "modified_on")
    private Date modifiedOn;

    @Column(name = "blocked")
    private Boolean blocked;
}
