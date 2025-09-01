package com.thespace.spaceweb.reply;

import com.thespace.common.exception.Exceptions.HasChild;
import com.thespace.common.exception.Exceptions.NotRegisterUser;
import com.thespace.common.exception.Exceptions.PostNotFound;
import com.thespace.common.exception.Exceptions.ReplyNotFound;
import com.thespace.common.getList.GetListReply;
import com.thespace.common.page.PageReqDTO;
import com.thespace.common.page.PageResDTO;
import com.thespace.spaceweb.board.Board;
import com.thespace.spaceweb.board.BoardRepository;
import com.thespace.spaceweb.user.User;
import lombok.RequiredArgsConstructor;
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

    Reply dtoToEntity(ReplyDTOs.Register dto, User user, Board board) {
        return new Reply(dto.tagRno(), dto.parentRno(), dto.replyContent(), user, dto.tag(), board);
    }

    @Transactional
    public void register(Long bno, ReplyDTOs.Register replyRegisterDTO, Authentication authentication) {
        Board board = boardRepository.findById(bno).orElseThrow(PostNotFound::new);
        Long replyCount = board.getRCount() + 1L;
        board.setRCount(replyCount);
        boardRepository.save(board);

        User user = (User) authentication.getPrincipal();

        if (replyRegisterDTO.parentRno() != 0L) {
            Reply parent = replyRepository.findById(replyRegisterDTO.parentRno()).orElseThrow(ReplyNotFound::new);
            parent.setChildCount(parent.getChildCount() + 1L);
            replyRepository.save(parent);
        }

        if (replyRegisterDTO.tagRno() != 0L) {
            Reply taggedReply = replyRepository.findById(replyRegisterDTO.tagRno()).orElseThrow(ReplyNotFound::new);
            taggedReply.setTaggedCount(taggedReply.getTaggedCount() + 1L);
            replyRepository.save(taggedReply);
        }

        Reply reply = dtoToEntity(replyRegisterDTO, user, board);

        replyRepository.save(reply);
    }

    @Transactional
    public void delete(Long bno, Long rno, Authentication authentication) {

        Board board = boardRepository.findById(bno).orElseThrow(PostNotFound::new);
        Reply reply = replyRepository.findById(rno).orElseThrow(ReplyNotFound::new);
        User user = (User) authentication.getPrincipal();

        if (!user.getUuid().equals(reply.getUser().getUuid())) throw new NotRegisterUser();

        if (reply.getChildCount() > 0L) throw new HasChild();

        if(reply.getParentRno() > 0L) {
            Reply parent = replyRepository.findById(reply.getParentRno()).orElseThrow(ReplyNotFound::new);
            parent.setChildCount(parent.getChildCount() - 1L);
            replyRepository.save(parent);
        }

        if(reply.getTagRno() > 0L) {
            Reply taggedReply = replyRepository.findById(reply.getTagRno()).orElseThrow(ReplyNotFound::new);
            taggedReply.setTaggedCount(taggedReply.getTaggedCount() - 1L);
            replyRepository.save(taggedReply);
        }

        replyRepository.deleteById(rno);
        Long replyCount = board.getRCount() - 1L;
        board.setRCount(replyCount);
        boardRepository.save(board);
    }

    public PageResDTO<ReplyDTOs.Info> getListReply(Long bno) {
        if (!boardRepository.existsById(bno)) {
            throw new PostNotFound();
        }

        PageReqDTO pageReqDTO = new PageReqDTO(1, 0, "", "", 0L, 0L);

        Pageable pageable = pageReqDTO.getPageable("rno");

        Page<ReplyDTOs.Info> list = getListReply.getListReply(bno, pageable);

        return PageResDTO.from(pageReqDTO, list);
    }
}
