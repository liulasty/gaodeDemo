package com.example.demo.dto.page;

import com.baomidou.mybatisplus.core.metadata.IPage;

public class PageQuery<T> {
    // 基础分页参数
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String orderBy;

    // 扩展查询参数
    private T params;

    // 是否需要进行count查询(默认true)
    private Boolean searchCount = true;

    // 是否合理化分页参数(默认true)
    // 例如pageNum<1时设为1，pageNum>totalPages时设为totalPages
    private Boolean reasonable = true;

    // getters and setters
    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public T getParams() {
        return params;
    }

    public void setParams(T params) {
        this.params = params;
    }

    public Boolean getSearchCount() {
        return searchCount;
    }

    public void setSearchCount(Boolean searchCount) {
        this.searchCount = searchCount;
    }

    public Boolean getReasonable() {
        return reasonable;
    }

    public void setReasonable(Boolean reasonable) {
        this.reasonable = reasonable;
    }
}
