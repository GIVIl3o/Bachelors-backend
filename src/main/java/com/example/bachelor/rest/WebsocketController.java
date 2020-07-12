package com.example.bachelor.rest;

import com.example.bachelor.api.CommentInfo;
import com.example.bachelor.api.ProjectService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

@Controller
@Transactional
@AllArgsConstructor
public class WebsocketController {

    private final SimpMessagingTemplate template;
    private final ProjectService service;
    private final ObjectMapper objectMapper;

    @MessageMapping("/comment")
    public void addComment(CommentInfo comment) throws JsonProcessingException {
        var persistedComment = service.addComment(comment);

        template.convertAndSend("/comment/" + comment.getTaskId(), persistedComment);

        var payload = objectMapper.writeValueAsString(persistedComment);

        var notifications = service.addNotification(persistedComment.getTaskId(), payload);

        notifications.forEach(notification -> template.convertAndSend("/notification/" + notification.getUsername(), notification));
    }

    @MessageMapping("/notification/delete")
    public void deleteNotification(int notificationId) {
        service.deleteNotification(notificationId);
    }

}
