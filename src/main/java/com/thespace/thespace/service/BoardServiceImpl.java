package com.thespace.thespace.service;


import com.thespace.thespace.domain.Board;
import com.thespace.thespace.domain.Category;
import com.thespace.thespace.dto.BoardDTO;
import com.thespace.thespace.dto.PageReqDTO;
import com.thespace.thespace.dto.PageResDTO;
import com.thespace.thespace.exception.PostNotFound;
import com.thespace.thespace.repository.BoardRepository;
import com.thespace.thespace.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService
  {
    private BoardRepository boardRepository;
    private CategoryRepository categoryRepository;
    private ModelMapper modelMapper;

    @Autowired
    public void setBoardRepository(BoardRepository boardRepository,  CategoryRepository categoryRepository, ModelMapper modelMapper)
      {
        this.boardRepository = boardRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
      }

    public Board dtoToEntity(BoardDTO boardDTO)
      {
        Optional<Category> category = categoryRepository.findById(boardDTO.getCategoryId());
          Board board = Board.builder()
              .bno(boardDTO.getBno())
              .title(boardDTO.getTitle())
              .content(boardDTO.getContent())
              .writer(boardDTO.getWriter())
              .category(category.orElseThrow())
              .path(category.get().getPath())
              .build();

          if(boardDTO.getFileNames() != null)
            {
              boardDTO.getFileNames().forEach(fileName ->{
                String[] array = fileName.split("_");
                board.addFile(array[0], array[1]);
              });
            }
        return board;
      }

    public BoardDTO entityToDTO(Board board)
      {
        Long categoryId = board.getCategory().getCategoryId();
        BoardDTO boardDTO = BoardDTO.builder()
            .bno(board.getBno())
            .title(board.getTitle())
            .content(board.getContent())
            .writer(board.getWriter())
            .path(board.getPath())
            .categoryId(categoryId)
            .vote(board.getVote())
            .viewCount(board.getViewCount())
            .modDate(board.getModDate())
            .createDate(board.getCreateDate())
            .build();

        List<String> fileNames = board.getFileSet().stream().sorted().map(boardFile ->
            boardFile.getFileId()+"_"+boardFile.getFileName()).toList();

        boardDTO.setFileNames(fileNames);
        return boardDTO;
      }

    public Long post(BoardDTO boardDTO)
      {
//        Long categoryId = boardDTO.getCategoryId();
//        Optional<Category> category = categoryRepository.findById(categoryId);
//
//
//        Board board = Board.builder().category(category.orElseThrow())
//            .bno(boardDTO.getBno())
//            .title(boardDTO.getTitle())
//            .content(boardDTO.getContent())
//            .writer(boardDTO.getWriter())
//            .path(category.get().getCategoryName())
//            .build();
        Board board = dtoToEntity(boardDTO);
        Long bno = boardRepository.save(board).getBno();
        return bno;
      }

    public BoardDTO read(Long bno)
      {
        Optional<Board> result = boardRepository.findByIdWithFiles(bno);
        Board board = result.orElseThrow(PostNotFound::new);
            BoardDTO boardDTO = entityToDTO(board);
//            boardDTO.setBno(board.get().getBno());
//            boardDTO.setTitle(board.get().getTitle());
//            boardDTO.setContent(board.get().getContent());
//            boardDTO.setWriter(board.get().getWriter());
//            boardDTO.setCreateDate(board.get().getCreateDate());
//            boardDTO.setModDate(board.get().getModDate());
//            boardDTO.setViewCount(board.get().getViewCount());
//            boardDTO.setVote(board.get().getVote());
//            boardDTO.setPath(board.get().getPath());
//            boardDTO.setCategoryId(board.get().getCategory().getCategoryId());

            return boardDTO;
      }

    public void modify(BoardDTO boardDTO)
      {
        Optional<Board> result = boardRepository.findById(boardDTO.getBno());
        Board board = result.orElseThrow(PostNotFound::new);
        board.change(boardDTO.getTitle(), boardDTO.getContent());
        boardRepository.save(board);
      }

    public void delete(Long bno)
      {
        if(boardRepository.existsById(bno))
          {
            boardRepository.deleteById(bno);
            return;
          }
        throw new PostNotFound();
      }

    public PageResDTO<BoardDTO> list(PageReqDTO pageReqDTO)
    {
      String[] types = pageReqDTO.getTypes();
      String keyword = pageReqDTO.getKeyword();

      Pageable pageable = pageReqDTO.getPageable("bno");

      Page<BoardDTO> list = boardRepository.getList(types, keyword, pageable);

      PageResDTO<BoardDTO> pageResDTO = PageResDTO.<BoardDTO>PageResDTO()
          .pageReqDTO(pageReqDTO)
          .dtoList(list.getContent())
          .total((int) list.getTotalElements())
          .build();

      return pageResDTO;
    }
  }
