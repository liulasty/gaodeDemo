package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;


/**
 * @author lz
 */
@Data
@TableName("t_transaction")
public class Transaction {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long statementId;
    private Date transactionDate;
    private String currency;
    private String transactionType;
    private String businessSummary;
    private BigDecimal amount;
    private BigDecimal balance;
    private String counterpartyName;
    private String counterpartyAccount;
}