package com.thespace.thespace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardFileDTO
  {
    private String fileId;
    private String fileName;
    private boolean imageChk;
    private int ord;

//    public String getLink()
//      {
//        if(imageChk)
//          {
//            return "s_" + fileId + "_" + fileName;
//          }else{
//          return fileId + "_" + fileName;
//        }
//      }
  }
