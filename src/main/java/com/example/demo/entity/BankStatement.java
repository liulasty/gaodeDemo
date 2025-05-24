package com.example.demo.entity;

/*
 * Created with IntelliJ IDEA.
 * @Author: lz
 * @Date: 2025/04/14/23:52
 * @Description:
 */

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author lz
 */
@Data
@TableName("t_bank_statement")
public class BankStatement {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String customerName;
    private String accountNumber;
    private String currency;
    private Date startDate;
    private Date endDate;
    private String statementNumber;

    @TableField(exist = false)
    private List<Transaction> transactions;

    private BigDecimal openingBalance;
    private BigDecimal closingBalance;
}