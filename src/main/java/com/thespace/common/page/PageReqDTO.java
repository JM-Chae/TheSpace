package com.thespace.common.page;

import java.util.Arrays;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public record PageReqDTO(int page,
                         int size,
                         String type,
                         String keyword,
                         Long communityId,
                         Long categoryId) {

    public PageReqDTO(int page, int size, String type, String keyword, Long communityId, Long categoryId) {
        this.page = page != 0 ? page : 1;
        this.size = size != 0 ? size : 1000000;
        this.type = type;
        this.keyword = keyword != null ? keyword : "";
        this.communityId = communityId != null ? communityId : 0L;
        this.categoryId = categoryId != null ? categoryId : 0L;
    }

    public String[] getTypes() {
        if (type == null || type.isEmpty()) {
            return null;
        }
        return type.split("");
    }

    public Pageable getPageable(String... props) {
        if ((Arrays.toString(props)).contains("bno")) {
            return PageRequest.of(this.page - 1, this.size, Sort.by(props).descending());
        }
        return PageRequest.of(this.page - 1, this.size, Sort.by(props).ascending());
    }
}
