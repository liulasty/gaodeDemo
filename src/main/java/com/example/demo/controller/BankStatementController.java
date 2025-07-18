package com.example.demo.controller;

import com.example.demo.entity.BankStatement;
import com.example.demo.entity.Transaction;
import com.example.demo.service.PdfParserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import com.example.demo.dto.CommonResponse;

// BankStatementController.java
@RestController
@RequestMapping("/api/bank-statements")
@RequiredArgsConstructor
@Slf4j
public class BankStatementController {

    private final PdfParserService pdfParserService;

    @PostMapping("/upload")
    public CommonResponse<BankStatement> uploadStatement(@RequestParam("file") MultipartFile file) {
        try {
            BankStatement statement = pdfParserService.parseBankStatement(file.getInputStream());
            return new CommonResponse<>(200, "上传成功", statement);
        } catch (IOException e) {
            log.error("上传失败: " + e.getMessage());
            return new CommonResponse<>(500, "上传失败: " + e.getMessage(), null);
        }
    }

    @PostMapping("/analyze")
    public CommonResponse<Map<String, Object>> analyzeStatement(@RequestParam("file") MultipartFile file) {
        try {
            BankStatement statement = pdfParserService.parseBankStatement(file.getInputStream());
            Map<String, Object> analysis = analyzeTransactions(statement);
            return new CommonResponse<>(200, "分析成功", analysis);
        } catch (IOException e) {
            return new CommonResponse<>(500, "分析失败: " + e.getMessage(), null);
        }
    }

    private Map<String, Object> analyzeTransactions(BankStatement statement) {
        Map<String, Object> result = new HashMap<>();

        // 基本统计
        result.put("totalTransactions", statement.getTransactions().size());
        result.put("startDate", statement.getStartDate());
        result.put("endDate", statement.getEndDate());
        result.put("openingBalance", statement.getOpeningBalance());
        result.put("closingBalance", statement.getClosingBalance());

        // 收支统计
        BigDecimal totalIncome = statement.getTransactions().stream()
                .filter(t -> t.getAmount().compareTo(BigDecimal.ZERO) > 0)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = statement.getTransactions().stream()
                .filter(t -> t.getAmount().compareTo(BigDecimal.ZERO) < 0)
                .map(t -> t.getAmount().abs())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        result.put("totalIncome", totalIncome);
        result.put("totalExpense", totalExpense);
        result.put("netBalance", totalIncome.subtract(totalExpense));

        // 按业务类型分类
        Map<String, BigDecimal> byCategory = statement.getTransactions().stream()
                .filter(t -> t.getAmount().compareTo(BigDecimal.ZERO) < 0)
                .collect(Collectors.groupingBy(
                        t -> categorizeTransaction(t.getBusinessSummary()),
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                t -> t.getAmount().abs(),
                                BigDecimal::add)
                ));

        result.put("expenseByCategory", byCategory);

        return result;
    }

    private String categorizeTransaction(String summary) {
        if (summary.contains("美团") || summary.contains("饿了么")) {
            return "餐饮外卖";
        } else if (summary.contains("支付宝") || summary.contains("微信")) {
            return "电子支付";
        } else if (summary.contains("滴滴") || summary.contains("交通")) {
            return "交通出行";
        } else if (summary.contains("营销")) {
            return "营销返现";
        } else {
            return "其他";
        }
    }
}