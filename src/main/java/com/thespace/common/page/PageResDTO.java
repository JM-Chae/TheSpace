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

    private final boolean prev;
    private final boolean next;

    private final int start;
    private int end;

    private final List<E> dtoList;

    @Builder(builderMethodName = "PageResDTO")
    public PageResDTO(PageReqDTO pageReqDTO, List<E> dtoList, int total)
      {
        this.page = pageReqDTO.page();
        this.size = pageReqDTO.size();

        this.total = total;
        this.dtoList = dtoList;

        this.end = (int)(Math.ceil(this.page/10.0)) * 10;
        this.start = this.end -9;

        int last = (int)(Math.ceil((total/(double)size)));

        this.end = Math.min(end, last);
        this.prev = this.start >1;
        this.next = total>this.end * this.size;
      }

    public static <E> PageResDTO<E> from(PageReqDTO pageReqDTO, Page<E> page) {
        return PageResDTO.<E>PageResDTO()
            .pageReqDTO(pageReqDTO)
            .dtoList(page.getContent())
            .total((int) page.getTotalElements())
            .build();
    }
  }
