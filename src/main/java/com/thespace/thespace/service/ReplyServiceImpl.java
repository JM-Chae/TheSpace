package com.thespace.thespace.service;

import com.thespace.thespace.domain.Board;
import com.thespace.thespace.domain.Reply;
import com.thespace.thespace.dto.PageReqDTO;
import com.thespace.thespace.dto.PageResDTO;
import com.thespace.thespace.dto.ReplyDTO;
import com.thespace.thespace.exception.PostNotFound;
import com.thespace.thespace.exception.ReplyNotFound;
import com.thespace.thespace.repository.BoardRepository;
import com.thespace.thespace.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReplyServiceImpl implements ReplyService
  {
    private BoardRepository boardRepository;
    private ReplyRepository replyRepository;
    private ModelMapper modelMapper;

    @Autowired
    public ReplyServiceImpl(BoardRepository boardRepository, ReplyRepository replyRepository, ModelMapper modelMapper)
      {
        this.replyRepository = replyRepository;
        this.modelMapper = modelMapper;
        this.boardRepository = boardRepository;
      }

    public Long register(Long bno, ReplyDTO replyDTO)
      {
        Board board = boardRepository.findById(bno).orElseThrow(PostNotFound::new);

        replyDTO.setBno(bno);
        Reply reply =  modelMapper.map(replyDTO, Reply.class);
        Long rno = replyRepository.save(reply).getRno();

        return rno;
      }

    public void delete(Long rno)
      {
        if(!replyRepository.existsById(rno))
          {
            throw new ReplyNotFound();
          }
        replyRepository.deleteById(rno);

      }

    public PageResDTO<ReplyDTO> getListReply(Long bno, PageReqDTO pageReqDTO)
      {
        if (!boardRepository.existsById(bno))
          {
            throw new PostNotFound();
          }

        String[] types = pageReqDTO.getTypes();
        String keyword = pageReqDTO.getKeyword();

        pageReqDTO.setSize(30);

        Pageable pageable = pageReqDTO.getPageable("rno");


        Page<ReplyDTO> list = replyRepository.getListReply(types, keyword, pageable);

        PageResDTO<ReplyDTO> pageResDTO = PageResDTO.<ReplyDTO>PageResDTO()
            .pageReqDTO(pageReqDTO)
            .dtoList(list.getContent())
            .total((int) list.getTotalElements())
            .build();

        return pageResDTO;
      }
  }
