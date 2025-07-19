package com.example.demo.dto.page;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;


/**
 * @author Administrator
 */
@Data
public class PageQuery<T> {
    // 基础分页参数
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String orderBy;
    private String order;

    // 扩展查询参数
    private T params;

    // 是否需要进行count查询(默认true)
    private Boolean searchCount = true;

    // 是否合理化分页参数(默认true)
    // 例如pageNum<1时设为1，pageNum>totalPages时设为totalPages
    private Boolean reasonable = true;


}
