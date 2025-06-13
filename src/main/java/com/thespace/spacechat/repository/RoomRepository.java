package com.thespace.spacechat.repository;

import com.thespace.spacechat.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<ChatRoom, Long> {

}
