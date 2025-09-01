package com.thespace.common.fcm;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FcmTokenRepository extends JpaRepository<FcmToken, String> {

    List<FcmToken> findByUser_Id(String userId);
    List<FcmToken> findBySessionId(String sessionId);
    Optional<FcmToken> findByToken(String token);
}
