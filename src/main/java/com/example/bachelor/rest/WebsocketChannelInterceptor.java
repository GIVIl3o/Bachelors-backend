package com.example.bachelor.rest;

import com.example.bachelor.api.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@AllArgsConstructor
public class WebsocketChannelInterceptor implements ChannelInterceptor {

    private final UserService service;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        System.out.println("start");
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            var authorizationHeader = accessor.getNativeHeader("Authorization");
            if (authorizationHeader == null || authorizationHeader.isEmpty())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Authorization not provided");

            var token = authorizationHeader.get(0).split(" ")[1];

            var user = service.parseToken(token)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "token expired"));

            var authentication = new UsernamePasswordAuthenticationToken(user, null, List.of());

            accessor.setUser(authentication);
        }

        return message;
    }
}
