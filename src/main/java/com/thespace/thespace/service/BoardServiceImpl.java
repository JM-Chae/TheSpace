package com.thespace.thespace.service;


import com.thespace.thespace.domain.Board;
import com.thespace.thespace.domain.Category;
import com.thespace.thespace.dto.BoardDTO;
import com.thespace.thespace.dto.PageReqDTO;
import com.thespace.thespace.dto.PageResDTO;
import com.thespace.thespace.exception.PostNotFound;
import com.thespace.thespace.repository.BoardFileRepository;
import com.thespace.thespace.repository.BoardRepository;
import com.thespace.thespace.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private BoardFileRepository boardFileRepository;

    @Autowired
    public void setBoardRepository(BoardRepository boardRepository,  CategoryRepository categoryRepository, ModelMapper modelMapper, BoardFileRepository boardFileRepository)
      {
        this.boardRepository = boardRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
        this.boardFileRepository = boardFileRepository;
      }

    public Board dtoToEntity(BoardDTO boardDTO)
      {
        Category category = categoryRepository.findByCategoryName(boardDTO.getCategoryName());
          Board board = Board.builder()
              .bno(boardDTO.getBno())
              .title(boardDTO.getTitle())
              .content(boardDTO.getContent())
              .writer(boardDTO.getWriter())
              .writerUuid(boardDTO.getWriterUuid())
              .category(category)
              .path(category.getPath())
              .build();

          if(boardDTO.getFileNames() != null)
            {
              boardDTO.getFileNames().forEach(fileName ->{
                String[] array = fileName.split("_", 2);
                board.addFile(array[0], array[1]);
              });
            }
        return board;
      }

    public BoardDTO entityToDTO(Board board)
      {
        String categoryName = board.getCategory().getCategoryName();
        BoardDTO boardDTO = BoardDTO.builder()
            .bno(board.getBno())
            .title(board.getTitle())
            .content(board.getContent())
            .writer(board.getWriter())
            .writerUuid(board.getWriterUuid())
            .path(board.getPath())
            .categoryName(categoryName)
            .vote(board.getVote())
            .viewCount(board.getViewCount())
            .modDate(board.getModDate())
            .createDate(board.getCreateDate())
            .rCount(board.getRCount())
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

    @Transactional
    public BoardDTO read(Long bno)
      {
        Optional<Board> result = boardRepository.findByIdWithFiles(bno);
        Board board = result.orElseThrow(PostNotFound::new);
        board.setViewCount(board.getViewCount()+1L);
        boardRepository.save(board);
            BoardDTO boardDTO = entityToDTO(board);

            return boardDTO;
      }

    public void modify(BoardDTO boardDTO)
      {
        Long bno = boardDTO.getBno();
        Optional<Board> result = boardRepository.findById(bno);
        Board board = result.orElseThrow(PostNotFound::new);
        board.change(boardDTO.getTitle(), boardDTO.getContent(), categoryRepository.findByCategoryName(boardDTO.getCategoryName()));

        if(boardDTO.getFileNames() != null)
          {
            boardDTO.getFileNames().forEach(fileName ->{
              String[] array = fileName.split("_", 2);
              board.addFile(array[0], array[1]);
            });
          }

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
      String path = pageReqDTO.getPath();
      String category = pageReqDTO.getCategory();

      Pageable pageable = pageReqDTO.getPageable("bno");

      Page<BoardDTO> list = boardRepository.getList(types, keyword, pageable, path, category);

      PageResDTO<BoardDTO> pageResDTO = PageResDTO.<BoardDTO>PageResDTO()
          .pageReqDTO(pageReqDTO)
          .dtoList(list.getContent())
          .total((int) list.getTotalElements())
          .build();

      return pageResDTO;
    }

    public boolean writerCheck(Long bno, String userId)
      {
        return boardRepository.findById(bno).orElseThrow(PostNotFound::new).getWriter().equals(userId);
      }
  }
