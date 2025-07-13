package com.chatapp.service;

import com.chatapp.entity.Message;
import com.chatapp.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService {
    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Optional<List<Message>> getMessages(Integer channelId) {
        return messageRepository.findMessagesAtChannel(channelId);
    }

    public String getMessageDateAsString(Message message) {
        String date;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        LocalDateTime messageLocalDateTime = message.getCreatedAt()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        LocalDateTime startOfToday = LocalDateTime.now().toLocalDate().atStartOfDay();

        if (messageLocalDateTime.isAfter(startOfToday)) {
            date = messageLocalDateTime.format(timeFormatter);
        } else {
            date = messageLocalDateTime.format(dateFormatter) + " " + messageLocalDateTime.format(timeFormatter);
        }
        return date;
    }
}
