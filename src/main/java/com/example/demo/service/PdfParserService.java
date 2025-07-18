package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.entity.BankStatement;
import com.example.demo.entity.Transaction;
import com.example.demo.mapper.BankStatementMapper;
import com.example.demo.mapper.TransactionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class PdfParserService {

    private final BankStatementMapper bankStatementMapper;
    private final TransactionMapper transactionMapper;

    /**
     * 解析银行对账单
     *
     * @param pdfStream PDF 流
     *
     * @return {@code BankStatement }
     *
     * @throws IOException io异常
     */
    @Transactional(rollbackFor = Exception.class)
    public BankStatement parseBankStatement(InputStream pdfStream) throws IOException {
        BankStatement statement = new BankStatement();
        List<Transaction> transactions = new ArrayList<>();

        try (PDDocument document = PDDocument.load(pdfStream)) {
            PDFTextStripper stripper = new PDFTextStripper();

            // 逐页解析
            for (int page = 0; page < document.getNumberOfPages(); ++page) {
                stripper.setStartPage(page + 1);
                stripper.setEndPage(page + 1);
                String pageText = stripper.getText(document);

                // 第一页解析基本信息
                if (page == 0) {
                    parseHeaderInfo(pageText, statement);
                }

                // 解析交易数据
                parseTransactions(pageText, transactions);
            }
        }

        statement.setTransactions(transactions);
        calculateBalances(statement);
        saveBankStatement(statement);
        return statement;
    }


    public void saveBankStatement(BankStatement bankStatement) {
        // 保存主表信息
        bankStatementMapper.insert(bankStatement);

        // 保存关联的交易记录
        if (bankStatement.getTransactions() != null && !bankStatement.getTransactions().isEmpty()) {
            for (Transaction transaction : bankStatement.getTransactions()) {
                transaction.setStatementId(bankStatement.getId());
                transactionMapper.insert(transaction);
            }
        }
    }

    public BankStatement getBankStatementWithTransactions(Long id) {
        BankStatement statement = bankStatementMapper.selectById(id);
        if (statement != null) {
            QueryWrapper<Transaction> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("statement_id", id);
            List<Transaction> transactions = transactionMapper.selectList(queryWrapper);
            statement.setTransactions(transactions);
        }
        return statement;
    }


    /**
     * 解析银行流水单的头部信息并填充到 BankStatement 对象中。
     * 头部信息包括：客户姓名、日期范围、卡/账号、币种、流水单号等。
     *
     * @param text 包含银行流水单头部信息的文本
     * @param statement 需要被填充的 BankStatement 对象
     * @throws IllegalArgumentException 如果输入文本不符合预期的头部格式
     */
    private void parseHeaderInfo(String text, BankStatement statement) {
        // 使用正则表达式提取基本信息
        Pattern pattern = Pattern.compile(
                "客户姓名：(.*?)\\s+日期范围：(.*?)—(.*?)\\s+" +
                        "卡/账号：(.*?)\\s+币种：(.*?)\\s+流水单号：(.*?)\\s+"
        );

        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            statement.setCustomerName(matcher.group(1).trim());
            statement.setStartDate(parseDate(matcher.group(2).trim()));
            statement.setEndDate(parseDate(matcher.group(3).trim()));
            statement.setAccountNumber(matcher.group(4).trim());
            statement.setCurrency(matcher.group(5).trim());
            statement.setStatementNumber(matcher.group(6).trim());
        }
    }

    /**
     * 从银行流水单文本中解析交易记录并添加到交易列表中。
     * 每条交易记录包含：交易日期、币种、交易类型、业务摘要、
     * 发生额、余额、对方户名和对方账号等信息。
     *
     * @param text 包含交易记录的文本
     * @param transactions 用于存储解析后的交易对象的列表
     */
    private void parseTransactions(String text, List<Transaction> transactions) {
        // 匹配交易表格行
        Pattern pattern = Pattern.compile(
                "(\\d{4}-\\d{2}-\\d{2})\\s+" +  // 日期
                        "(\\S+)\\s+" +                  // 币种
                        "(\\S+)\\s+" +                  // 钞汇
                        "([^*]+?)\\s+" +                // 业务摘要(排除*号)
                        "([+-]?\\d+\\.\\d{2})\\s+" +    // 发生额
                        "(\\d+\\.\\d{2})\\s+" +         // 余额
                        "([^*]+?)\\s+" +                // 对方户名
                        "(\\S+)"                         // 对方账号
        );

        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            Transaction transaction = new Transaction();
            transaction.setTransactionDate(parseDate(matcher.group(1)));
            transaction.setCurrency(matcher.group(2));
            transaction.setTransactionType(matcher.group(3));
            transaction.setBusinessSummary(matcher.group(4).trim());
            transaction.setAmount(new BigDecimal(matcher.group(5)));
            transaction.setBalance(new BigDecimal(matcher.group(6)));
            transaction.setCounterpartyName(matcher.group(7).trim());
            transaction.setCounterpartyAccount(matcher.group(8));

            transactions.add(transaction);
        }
    }

    /**
     * 将格式为 "yyyy-MM-dd" 的日期字符串解析为 Date 对象。
     *
     * @param dateStr 要解析的日期字符串，格式应为 "yyyy-MM-dd"
     * @return 解析后的 Date 对象，如果解析失败则返回 null
     * @throws ParseException 如果日期字符串无法被解析（已在方法内部处理）
     */
    private Date parseDate(String dateStr) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (ParseException e) {
            log.error("日期解析错误: {}", dateStr, e);
            return null;
        }
    }

    /**
     * 根据银行对账单中的交易记录计算期初余额和期末余额
     * 如果对账单中有交易记录，则将最后一笔交易的余额设为期初余额，第一笔交易的余额设为期末余额
     * 此方法的目的是自动更新对账单的期初和期末余额字段，以确保它们反映对账单上交易数据的最新状态
     * 
     * @param statement 银行对账单对象，包含交易记录列表和期初、期末余额字段
     */
    private void calculateBalances(BankStatement statement) {
        // 检查交易记录列表是否为空，如果不为空，则执行余额计算
        if (!statement.getTransactions().isEmpty()) {
            // 将交易记录列表中最后一笔交易的余额设为期初余额
            statement.setOpeningBalance(statement.getTransactions()
                                                .get(statement.getTransactions().size() - 1).getBalance());
            // 将交易记录列表中第一笔交易的余额设为期末余额
            statement.setClosingBalance(statement.getTransactions()
                                                .get(0).getBalance());
        }
    }
}