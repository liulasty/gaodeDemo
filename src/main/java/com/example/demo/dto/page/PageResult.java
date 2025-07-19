package com.example.demo.dto.page;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.List;

/**
 * @author Administrator
 */
@Data
public class PageResult<T> {
    private Integer pageNum;
    private Integer pageSize;
    private Long total;
    private Integer totalPages;
    private List<T> list;

    // 扩展字段
    private Boolean hasNextPage;
    private Boolean hasPreviousPage;
    private Boolean isFirstPage;
    private Boolean isLastPage;

    public PageResult(IPage<?> page, List<T> list) {
        this.pageNum = (int) page.getCurrent();
        this.pageSize = (int) page.getSize();
        this.total = page.getTotal();
        this.totalPages = (int) page.getPages();
        this.list = list;

        // 计算扩展字段
        this.hasNextPage = pageNum < totalPages;
        this.hasPreviousPage = pageNum > 1;
        this.isFirstPage = pageNum == 1;
        this.isLastPage = pageNum.equals(totalPages);
    }


}
