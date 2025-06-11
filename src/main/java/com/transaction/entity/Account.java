package com.transaction.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
public class Account {

    @Id
    private Long id;
    private String name;
    private String currency;
    private BigDecimal balance;

}
