package com.thespace.config;

import com.thespace.common.fcm.FcmService;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@WebFilter
@Component
public class SessionListener implements HttpSessionListener {

    private static FcmService fcmService;

    @Autowired
    public void setFcmService(FcmService fcmService) {
        SessionListener.fcmService = fcmService;
    }

    public void sessionDestroyed(final HttpSessionEvent se) {
        String sessionId = se.getSession().getId();
        log.info("Session destroyed, sessionId: {}", sessionId);

        if (fcmService != null) {
            fcmService.deleteTokenBySessionId(sessionId);
        } else { log.error("FCM service is null"); }
    }
}
