package com.thespace.spacechat.room;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Room r JOIN r.members u WHERE r.roomId = :roomId AND u.uuid = :uuid")
    boolean existsUserByRoomIdAndUuid(@Param("roomId") Long rid, @Param("uuid") String uuid);
}
