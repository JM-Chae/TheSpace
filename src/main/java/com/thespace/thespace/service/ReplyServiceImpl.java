package com.thespace.thespace.service;

import com.thespace.thespace.domain.Reply;
import com.thespace.thespace.dto.ReplyDTO;
import com.thespace.thespace.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReplyServiceImpl implements ReplyService
  {
    private ReplyRepository replyRepository;
    private ModelMapper modelMapper;

    @Autowired
    public ReplyServiceImpl(ReplyRepository replyRepository, ModelMapper modelMapper)
      {
        this.replyRepository = replyRepository;
        this.modelMapper = modelMapper;
      }

    public void register(ReplyDTO replyDTO)
      {
        Reply reply =  modelMapper.map(replyDTO, Reply.class);
        replyRepository.save(reply);
      }
  }
