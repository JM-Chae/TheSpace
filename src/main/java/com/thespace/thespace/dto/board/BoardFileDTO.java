package com.thespace.thespace.dto.board;

import lombok.Builder;

@Builder
public record BoardFileDTO(String fileId,
                           String fileName,
                           boolean imageChk,
                           int ord) {

}

//    public String getLink()
//      {
//        if(imageChk)
//          {
//            return "s_" + fileId + "_" + fileName;
//          }else{
//          return fileId + "_" + fileName;
//        }
//      }

