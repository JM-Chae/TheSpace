package com.thespace.thespace.service;

import com.thespace.thespace.domain.Board;
import com.thespace.thespace.domain.Reply;
import com.thespace.thespace.dto.page.PageReqDTO;
import com.thespace.thespace.dto.page.PageResDTO;
import com.thespace.thespace.dto.reply.ReplyDTO;
import com.thespace.thespace.dto.reply.ReplyRegisterDTO;
import com.thespace.thespace.exception.PostNotFound;
import com.thespace.thespace.exception.ReplyNotFound;
import com.thespace.thespace.repository.BoardRepository;
import com.thespace.thespace.repository.ReplyRepository;
import com.thespace.thespace.repository.getList.GetListReply;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public void register(Long bno, ReplyRegisterDTO replyRegisterDTO) {
        Board board = boardRepository.findById(bno).orElseThrow(PostNotFound::new);
        Long replyCount = board.getRCount() + 1L;
        board.setRCount(replyCount);
        boardRepository.save(board);

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

        replyRepository.save(reply);
    }

    @Transactional
    public void delete(Long bno, Long rno) {
        if (!replyRepository.existsById(rno)) {
            throw new ReplyNotFound();
        }
        Board board = boardRepository.findById(bno).orElseThrow(PostNotFound::new);
        replyRepository.deleteById(rno);
        Long replyCount = board.getRCount() - 1L;
        board.setRCount(replyCount);
        boardRepository.save(board);
    }

    public PageResDTO<ReplyDTO> getListReply(Long bno) {
        if (!boardRepository.existsById(bno)) {
            throw new PostNotFound();
        }

        PageReqDTO pageReqDTO = new PageReqDTO(1, 0, "", "", "", "");

        Pageable pageable = pageReqDTO.getPageable("rno");

        Page<ReplyDTO> list = getListReply.getListReply(bno, pageable);

        return PageResDTO.<ReplyDTO>PageResDTO()
            .pageReqDTO(pageReqDTO)
            .dtoList(list.getContent())
            .total((int) list.getTotalElements())
            .build();
    }

    public PageResDTO<ReplyDTO> getListNestedReply(Long rno, Long bno) {
        if (!boardRepository.existsById(bno)) {
            throw new PostNotFound();
        }

        PageReqDTO pageReqDTO = new PageReqDTO(1, 0, "", "", "", "");

        Pageable pageable = pageReqDTO.getPageable("rno");

        Page<ReplyDTO> list = getListReply.getListNestedReply(rno, bno, pageable);

        return PageResDTO.<ReplyDTO>PageResDTO()
            .pageReqDTO(pageReqDTO)
            .dtoList(list.getContent())
            .total((int) list.getTotalElements())
            .build();
    }
}
