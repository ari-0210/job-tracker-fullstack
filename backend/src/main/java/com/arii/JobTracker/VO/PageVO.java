package com.arii.JobTracker.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "通用分页返回对象")
public class PageVO<T> {

    @Schema(description = "总记录数", example = "100")
    private Long totalElements;

    @Schema(description = "总页数", example = "10")
    private Integer totalPages;

    @Schema(description = "当前页码(从0开始)", example = "0")
    private Integer number;

    @Schema(description = "当前页的数量", example = "10")
    private Integer size;

    @Schema(description = "数据列表")
    private List<T> content;

    // learn;构造函数，直接将 Spring Data 的 Page 转换过来
    public <E> PageVO(org.springframework.data.domain.Page<E> page, List<T> content) {
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.number = page.getNumber();
        this.size = page.getSize();
        this.content = content;
    }
}