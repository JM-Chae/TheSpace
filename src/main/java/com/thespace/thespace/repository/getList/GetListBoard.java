package com.thespace.thespace.repository.getList;

import com.thespace.thespace.dto.BoardDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetListBoard
  {
    Page<BoardDTO> getList(String[] types, String keyword, Pageable pageable, String path, String category);
  }
