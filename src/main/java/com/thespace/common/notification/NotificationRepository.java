package com.thespace.common.notification;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends CrudRepository<Notification, Long> {
    @Query(value = "SELECT * FROM notification WHERE friendship_id = :fid", nativeQuery = true)
    Optional<Notification> findByFriendshipId(@Param("fid") Long fid);
}
