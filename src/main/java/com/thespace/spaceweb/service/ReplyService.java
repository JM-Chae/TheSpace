package com.thespace.spaceweb.service;

import com.thespace.spaceweb.domain.Board;
import com.thespace.spaceweb.domain.Reply;
import com.thespace.spaceweb.domain.User;
import com.thespace.spaceweb.dto.ReplyDTOs;
import com.thespace.spaceweb.dto.page.PageReqDTO;
import com.thespace.spaceweb.dto.page.PageResDTO;
import com.thespace.spaceweb.exception.PostNotFound;
import com.thespace.spaceweb.exception.ReplyNotFound;
import com.thespace.spaceweb.repository.BoardRepository;
import com.thespace.spaceweb.repository.ReplyRepository;
import com.thespace.spaceweb.repository.getList.GetListReply;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReplyService {

    private final BoardRepository boardRepository;
    private final ReplyRepository replyRepository;
    private final GetListReply getListReply;
    private final ModelMapper modelMapper;

    @Transactional
    public void register(Long bno, ReplyDTOs.Register replyRegisterDTO, Authentication authentication) {
        Board board = boardRepository.findById(bno).orElseThrow(PostNotFound::new);
        Long replyCount = board.getRCount() + 1L;
        board.setRCount(replyCount);
        boardRepository.save(board);

        User user = (User) authentication.getPrincipal();

        if (!Objects.equals(replyRegisterDTO.path(), bno.toString() + '/')) {
            Reply R = replyRepository.findById(Long.valueOf(replyRegisterDTO.path().split("/")[1]))
                .orElseThrow(
                    ReplyNotFound::new);
            Long isNested = R.getIsNested() + 1L;
            R.setIsNested(isNested);
            replyRepository.save(R);
        }
        Reply reply = modelMapper.map(replyRegisterDTO, Reply.class);
        reply.setBoard(board);
        reply.setUser(user);

        replyRepository.save(reply);
    }

    @Transactional
    public void delete(Long bno, Long rno, Authentication authentication) {
        if (!replyRepository.existsById(rno)) {
            throw new ReplyNotFound();
        }
        Board board = boardRepository.findById(bno).orElseThrow(PostNotFound::new);
        Reply reply = replyRepository.findById(rno).orElseThrow(ReplyNotFound::new);
        if(reply.getPath().split("/").length > 1){
            Reply rootReply = replyRepository.findById((Long.valueOf(reply.getPath().split("/")[1]))).orElseThrow(ReplyNotFound::new);
            rootReply.setIsNested(rootReply.getIsNested() - 1L);
            replyRepository.save(rootReply);
        }
        User user = (User) authentication.getPrincipal();

        if (user.getUuid().equals(reply.getUser().getUuid())) {
        replyRepository.deleteById(rno);
        Long replyCount = board.getRCount() - 1L;
        board.setRCount(replyCount);
        boardRepository.save(board);
        }
    }

    public PageResDTO<ReplyDTOs.Info> getListReply(Long bno) {
        if (!boardRepository.existsById(bno)) {
            throw new PostNotFound();
        }

        PageReqDTO pageReqDTO = new PageReqDTO(1, 0, "", "", "", "");

        Pageable pageable = pageReqDTO.getPageable("rno");

        Page<ReplyDTOs.Info> list = getListReply.getListReply(bno, pageable);

        return PageResDTO.<ReplyDTOs.Info>PageResDTO()
            .pageReqDTO(pageReqDTO)
            .dtoList(list.getContent())
            .total((int) list.getTotalElements())
            .build();
    }

    public PageResDTO<ReplyDTOs.Info> getListNestedReply(Long rno, Long bno) {
        if (!boardRepository.existsById(bno)) {
            throw new PostNotFound();
        }

        PageReqDTO pageReqDTO = new PageReqDTO(1, 0, "", "", "", "");

        Pageable pageable = pageReqDTO.getPageable("rno");

        Page<ReplyDTOs.Info> list = getListReply.getListNestedReply(rno, bno, pageable);

        return PageResDTO.<ReplyDTOs.Info>PageResDTO()
            .pageReqDTO(pageReqDTO)
            .dtoList(list.getContent())
            .total((int) list.getTotalElements())
            .build();
    }
}
