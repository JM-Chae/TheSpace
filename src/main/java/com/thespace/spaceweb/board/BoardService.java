package com.thespace.spaceweb.board;

import com.thespace.common.exception.Exceptions.PostNotFound;
import com.thespace.common.getList.GetListBoard;
import com.thespace.common.page.PageReqDTO;
import com.thespace.common.page.PageResDTO;
import com.thespace.spaceweb.category.Category;
import com.thespace.spaceweb.category.CategoryRepository;
import com.thespace.spaceweb.user.User;
import com.thespace.spaceweb.user.UserRoleService;
import com.thespace.spaceweb.user.UserService;
import jakarta.transaction.Transactional;
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
            .community(category.getCommunity())
            .build();

        if (postDTO.fileNames() != null) {
            postDTO.fileNames().forEach(fileName -> {
                String[] array = fileName.split("_", 2);
                board.addFile(array[0], array[1]);
            });
        }
        return board;
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

        return BoardDTOs.Info.fromEntity(board);
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

    public void deleteByAdmin(Long bno, Authentication authentication) {
        if (boardRepository.existsById(bno)) {
            User user = (User) authentication.getPrincipal();
            String communityName = boardRepository.findById(bno).orElseThrow(PostNotFound::new).getCommunity().getName();

            if (userService.findUserRoles(user.getId())
                .contains(userRoleService.findRoleIdByName("ADMIN_" + communityName))) {
                boardRepository.deleteById(bno);
            }
            return;
        }

        throw new PostNotFound();
    }

    public PageResDTO<BoardDTOs.Info> list(PageReqDTO pageReqDTO) {
        String[] types = pageReqDTO.getTypes();
        String keyword = pageReqDTO.keyword();
        Long communityId = pageReqDTO.communityId();
        Long categoryId = pageReqDTO.categoryId();

        Pageable pageable = pageReqDTO.getPageable("bno");

        Page<BoardDTOs.Info> list = getListBoard.getList(types, keyword, pageable, communityId, categoryId);

        return PageResDTO.<BoardDTOs.Info>PageResDTO()
            .pageReqDTO(pageReqDTO)
            .dtoList(list.getContent())
            .total((int) list.getTotalElements())
            .build();
    }
}



