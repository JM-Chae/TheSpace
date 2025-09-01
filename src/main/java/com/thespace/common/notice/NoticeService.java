package com.thespace.common.notice;

import com.thespace.common.fcm.FcmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final FcmService fcmService;

    public void sendNotice(NoticeDTO dto) {
        //save notice
        Notice notice = new Notice(dto.title(), dto.body(), dto.url(), dto.topic());
        noticeRepository.save(notice);

        //push notification
        fcmService.sendNotice(dto);
    }

}
