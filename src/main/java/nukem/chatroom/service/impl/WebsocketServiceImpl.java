package nukem.chatroom.service.impl;

import lombok.RequiredArgsConstructor;
import nukem.chatroom.enums.headers.EventType;
import nukem.chatroom.enums.headers.Header;
import nukem.chatroom.service.WebsocketService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class WebsocketServiceImpl implements WebsocketService {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void notifyWebsocketSubscribers(final String destination, final Object payload, final EventType event) {
        messagingTemplate.convertAndSend(destination, payload,
                Collections.singletonMap(Header.EVENT_TYPE.getValue(), event.getValue()));
    }
}
