package com.thespace.thespace.service;

import com.thespace.thespace.domain.Board;
import com.thespace.thespace.domain.Category;
import com.thespace.thespace.domain.User;
import com.thespace.thespace.dto.board.BoardDTO;
import com.thespace.thespace.dto.board.BoardModifyDTO;
import com.thespace.thespace.dto.board.BoardPostDTO;
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

    public Board postBoard(BoardPostDTO boardPostDTO, Authentication authentication) {
        Category category = categoryRepository.findById(boardPostDTO.categoryId()).orElseThrow();
        User user = (User) authentication.getPrincipal();

        Board board = Board.builder()
            .title(boardPostDTO.title())
            .content(boardPostDTO.content())
            .user(user)
            .category(category)
            .path(category.getPath())
            .build();

        if (boardPostDTO.fileNames() != null) {
            boardPostDTO.fileNames().forEach(fileName -> {
                String[] array = fileName.split("_", 2);
                board.addFile(array[0], array[1]);
            });
        }
        return board;
    }

    public BoardDTO readBoard(Board board) {
        Long categoryId = board.getCategory().getCategoryId();

        List<String> fileNames = board.getFileSet().stream().sorted().map(boardFile ->
            boardFile.getFileId() + "_" + boardFile.getFileName()).toList();

        return BoardDTO.builder()
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

    public Long post(BoardPostDTO boardPostDTO, Authentication authentication) {
        Board board = postBoard(boardPostDTO, authentication);
        return boardRepository.save(board).getBno();
    }

    @Transactional
    public BoardDTO read(Long bno) {
        Optional<Board> result = boardRepository.findByIdWithFiles(bno);
        Board board = result.orElseThrow(PostNotFound::new);
        board.setViewCount(board.getViewCount() + 1L);
        boardRepository.save(board);

        return readBoard(board);
    }

    public void modify(BoardModifyDTO boardModifyDTO) {

        Long bno = boardModifyDTO.bno();
        Optional<Board> result = boardRepository.findById(bno);
        Board board = result.orElseThrow(PostNotFound::new);

        if (board.getUser().getId().equals(boardModifyDTO.writer())) {
            board.change(boardModifyDTO.title(), boardModifyDTO.content(),
                categoryRepository.findById(boardModifyDTO.categoryId()).orElseThrow()
            );

            if (boardModifyDTO.fileNames() != null) {
                boardModifyDTO.fileNames().forEach(fileName -> {
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

    public PageResDTO<BoardDTO> list(PageReqDTO pageReqDTO) {
        String[] types = pageReqDTO.getTypes();
        String keyword = pageReqDTO.keyword();
        String path = pageReqDTO.path();
        String category = pageReqDTO.category();

        Pageable pageable = pageReqDTO.getPageable("bno");

        Page<BoardDTO> list = getListBoard.getList(types, keyword, pageable, path, category);

        return PageResDTO.<BoardDTO>PageResDTO()
            .pageReqDTO(pageReqDTO)
            .dtoList(list.getContent())
            .total((int) list.getTotalElements())
            .build();
    }
}



