package com.thespace.thespace.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageReqDTO
  {
    @Builder.Default
    private int page = 1;

    @Builder.Default
    private int size = 10;

    private String type;

    private String keyword;

    public String[] getTypes()
      {
        if(type==null || type.isEmpty())
          {
            return null;
          }
        return type.split("");
      }

    public Pageable getPageable(String...props)
      {
          if((Arrays.toString(props)).contains("rno"))
            {
              return PageRequest.of(this.page - 1, this.size, Sort.by(props).ascending());
            }
        return PageRequest.of(this.page - 1, this.size, Sort.by(props).descending());
      }

    private String link;

    public String getLink()
      {
        if(link == null)
          {
            StringBuilder builder = new StringBuilder();
            builder.append("page="+this.page);
            builder.append("&size="+this.size);
            if(type != null && type.length() > 0)
              {
                builder.append("&type="+type);
              }
            if(keyword != null && keyword.length() > 0)
              {
                builder.append("&keyword=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8));
              }
            link = builder.toString();
          }
        return link;
      }
  }
