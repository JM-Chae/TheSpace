package com.thespace.common.page;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class PageResDTO<E>
  {
    private final int page;
    private final int size;
    private final int total;

    private final List<E> dtoList;

    @Builder(builderMethodName = "PageResDTO")
    public PageResDTO(PageReqDTO pageReqDTO, List<E> dtoList, int total)
      {
        this.page = pageReqDTO.page();
        this.size = pageReqDTO.size();

        this.total = total;
        this.dtoList = dtoList;
      }

    public static <E> PageResDTO<E> from(PageReqDTO pageReqDTO, Page<E> page) {
        return PageResDTO.<E>PageResDTO()
            .pageReqDTO(pageReqDTO)
            .dtoList(page.getContent())
            .total((int) page.getTotalElements())
            .build();
    }
  }
