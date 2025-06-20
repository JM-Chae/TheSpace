package com.thespace.common.page;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PageResDTO<E>
  {
    private int page;
    private int size;
    private int total;

    private int start;
    private int end;

    private boolean prev;
    private boolean next;

    private List<E> dtoList;

    @Builder(builderMethodName = "PageResDTO")
    public PageResDTO(PageReqDTO pageReqDTO, List<E> dtoList, int total)
      {
        if (total <= 0)
          {
            return;
          }
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
  }
