package com.thespace.thespace.service;

import com.thespace.thespace.domain.Board;
import com.thespace.thespace.domain.Category;
import com.thespace.thespace.domain.User;
import com.thespace.thespace.dto.BoardDTOs;
import com.thespace.thespace.dto.page.PageReqDTO;
import com.thespace.thespace.dto.page.PageResDTO;
import com.thespace.thespace.exception.PostNotFound;
import com.thespace.thespace.repository.BoardRepository;
import com.thespace.thespace.repository.CategoryRepository;
import com.thespace.thespace.repository.getList.GetListBoard;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final CategoryRepository categoryRepository;
    private final GetListBoard getListBoard;
    private final UserService userService;
    private final UserRoleService userRoleService;

    public Board postBoard(BoardDTOs.Post postDTO, Authentication authentication) {
        Category category = categoryRepository.findById(postDTO.categoryId()).orElseThrow();
        User user = (User) authentication.getPrincipal();

        Board board = Board.builder()
            .title(postDTO.title())
            .content(postDTO.content())
            .user(user)
            .category(category)
            .path(category.getPath())
            .build();

        if (postDTO.fileNames() != null) {
            postDTO.fileNames().forEach(fileName -> {
                String[] array = fileName.split("_", 2);
                board.addFile(array[0], array[1]);
            });
        }
        return board;
    }

    public BoardDTOs.Info readBoard(Board board) {
        Long categoryId = board.getCategory().getCategoryId();

        List<String> fileNames = board.getFileSet().stream().sorted().map(boardFile ->
            boardFile.getFileId() + "_" + boardFile.getFileName()).toList();

        return BoardDTOs.Info.builder()
            .bno(board.getBno())
            .title(board.getTitle())
            .content(board.getContent())
            .writer(board.getUser().getName())
            .writerUuid(board.getUser().getUuid())
            .path(board.getPath())
            .categoryId(categoryId)
            .vote(board.getVote())
            .viewCount(board.getViewCount())
            .modDate(board.getModDate())
            .createDate(board.getCreateDate())
            .rCount(board.getRCount())
            .fileNames(fileNames)
            .build();
    }

    public Long post(BoardDTOs.Post postDTO, Authentication authentication) {
        Board board = postBoard(postDTO, authentication);
        return boardRepository.save(board).getBno();
    }

    @Transactional
    public BoardDTOs.Info read(Long bno) {
        Optional<Board> result = boardRepository.findByIdWithFiles(bno);
        Board board = result.orElseThrow(PostNotFound::new);
        board.setViewCount(board.getViewCount() + 1L);
        boardRepository.save(board);

        return readBoard(board);
    }

    public void modify(BoardDTOs.Modify modifyDTO) {

        Long bno = modifyDTO.bno();
        Optional<Board> result = boardRepository.findById(bno);
        Board board = result.orElseThrow(PostNotFound::new);

        if (board.getUser().getId().equals(modifyDTO.writer())) {
            board.change(
                modifyDTO.title(), modifyDTO.content(),
                categoryRepository.findById(modifyDTO.categoryId()).orElseThrow()
            );

            if (modifyDTO.fileNames() != null) {
                modifyDTO.fileNames().forEach(fileName -> {
                    String[] array = fileName.split("_", 2);
                    board.addFile(array[0], array[1]);
                });
            }

            boardRepository.save(board);
        }
    }

    public void delete(Long bno, Authentication authentication) {
        if (boardRepository.existsById(bno)) {
            Board board = boardRepository.findById(bno).orElseThrow(PostNotFound::new);
            User user = (User) authentication.getPrincipal();
            if (user.getUuid().equals(board.getUser().getUuid())) {
                boardRepository.deleteById(bno);
            }
            return;
        }

        throw new PostNotFound();
    }

    public void delete(Long bno, Authentication authentication, String communityName) {
        if (boardRepository.existsById(bno)) {
            User user = (User) authentication.getPrincipal();
            if (userService.findUserRoles(user.getId())
                .contains(userRoleService.findRoleId("ADMIN_" + communityName))) {
                boardRepository.deleteById(bno);
            }
            return;
        }

        throw new PostNotFound();
    }

    public PageResDTO<BoardDTOs.Info> list(PageReqDTO pageReqDTO) {
        String[] types = pageReqDTO.getTypes();
        String keyword = pageReqDTO.keyword();
        String path = pageReqDTO.path();
        String category = pageReqDTO.category();

        Pageable pageable = pageReqDTO.getPageable("bno");

        Page<BoardDTOs.Info> list = getListBoard.getList(types, keyword, pageable, path, category);

        return PageResDTO.<BoardDTOs.Info>PageResDTO()
            .pageReqDTO(pageReqDTO)
            .dtoList(list.getContent())
            .total((int) list.getTotalElements())
            .build();
    }
}



