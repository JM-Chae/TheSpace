package com.thespace.spaceweb.friendship;

import com.thespace.spaceweb.user.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    Optional<Friendship> findByFromAndTo(User to, User from);
}
