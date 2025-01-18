package com.thespace.thespace.dto.page;

import java.util.Arrays;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public record PageReqDTO(int page,
                         int size,
                         String type,
                         String keyword,
                         String path,
                         String category) {

    public PageReqDTO(int page, int size, String type, String keyword, String path,
        String category) {
        this.page = page;
        this.size = size != 0 ? size : 1000000;
        this.type = type;
        this.keyword = keyword;
        this.path = path;
        this.category = category;
    }

    public String[] getTypes() {
        if (type == null || type.isEmpty()) {
            return null;
        }
        return type.split("");
    }

    public Pageable getPageable(String... props) {
        if ((Arrays.toString(props)).contains("rno")) {
            return PageRequest.of(this.page - 1, this.size, Sort.by(props).ascending());
        }
        return PageRequest.of(this.page - 1, this.size, Sort.by(props).descending());
    }

//    private String link;
//
//    public String getLink()
//      {
//        if(link == null)
//          {
//            StringBuilder builder = new StringBuilder();
//            builder.append("page="+this.page);
//            builder.append("&size="+this.size);
//            if(type != null && type.length() > 0)
//              {
//                builder.append("&type="+type);
//              }
//            if(keyword != null && keyword.length() > 0)
//              {
//                builder.append("&keyword=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8));
//              }
//            link = builder.toString();
//          }
//        return link;
//      }
}
