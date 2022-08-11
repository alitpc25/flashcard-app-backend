package com.project.flashcardApp.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class NotificationController {
	  @MessageMapping("/chat")
	  @SendTo("/topic/messages")
	  public NotificationResponse notify(NotificationMessage message) throws Exception {
	    return new NotificationResponse(message.getUserId(), message.getFriendId(),HtmlUtils.htmlEscape(message.getText()), message.getSentAt());
	  }
}
