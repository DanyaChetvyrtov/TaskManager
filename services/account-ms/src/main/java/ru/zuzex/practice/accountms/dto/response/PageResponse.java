package ru.zuzex.practice.accountms.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({
        "content", "totalPages", "totalElements",
        "curPage", "pageSize"
})
public class PageResponse<T> {
    private List<T> content;
    private int totalPages;
    private long totalElements;
    private int curPage;
    private int pageSize;
}
