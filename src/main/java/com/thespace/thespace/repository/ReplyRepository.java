package com.thespace.thespace.repository;

import com.thespace.thespace.domain.Reply;
import com.thespace.thespace.repository.getList.GetListReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long>, GetListReply
  {
  }
