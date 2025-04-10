package com.example.reservebite.service;

import com.example.reservebite.entity.Message;
import com.example.reservebite.entity.Users;
import com.example.reservebite.entity.enums.MessageStatus;
import com.example.reservebite.repository.MessageRepository;
import com.example.reservebite.repository.UsersRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UsersRepository usersRepository;

    public MessageService(MessageRepository messageRepository, UsersRepository usersRepository) {
        this.messageRepository = messageRepository;
        this.usersRepository = usersRepository;
    }

    @Transactional
    public Message sendMessage(Users sender, Users recipient, String content, MultipartFile image) throws IOException {
        Message message = new Message();
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setContent(content);
        message.setStatus(MessageStatus.UNREAD);

        if (image != null && !image.isEmpty()) {
            message.setImage(image.getBytes());
        }

        return messageRepository.save(message);
    }

    @Transactional
    public List<Message> getConversation(Users user1, Users user2) {
        List<Message> messages = messageRepository.findBySenderAndRecipientOrRecipientAndSenderOrderByCreatedAtAsc(
                user1, user2, user1, user2);
        // Encode images to Base64 for display
        messages.forEach(this::encodeImage);
        return messages;
    }

    @Transactional
    public List<Message> getUnreadMessages(Users user) {
        List<Message> messages = messageRepository.findByRecipientAndStatusOrderByCreatedAtDesc(user, MessageStatus.UNREAD);
        messages.forEach(this::encodeImage);
        return messages;
    }

    @Transactional
    public void markMessageAsRead(Long messageId) {
        Message message = getMessageById(messageId);
        message.setStatus(MessageStatus.READ);
        messageRepository.save(message);
    }

    @Transactional
    public void markMessagesAsRead(Users user) {
        messageRepository.markMessagesAsRead(user);
    }

    @Transactional
    public List<Message> getAllMessages() {
        List<Message> messages = messageRepository.findAllByOrderByCreatedAtDesc();
        messages.forEach(this::encodeImage);
        return messages;
    }

    @Transactional
    public List<Users> getDistinctSenders(Users recipient) {
        return messageRepository.findDistinctSendersByRecipient(recipient);
    }

    @Transactional
    public Message getMessageById(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found"));
        encodeImage(message);
        return message;
    }

    @Transactional
    public void sendReply(Long messageId, String replyContent, MultipartFile image) throws IOException {
        Message original = getMessageById(messageId);
        Users admin = usersRepository.findAdminUser()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin user not found"));

        Message reply = new Message();
        reply.setContent(replyContent);
        reply.setSender(admin);
        reply.setRecipient(original.getSender());
        reply.setParentMessage(original);
        reply.setStatus(MessageStatus.UNREAD);

        if (image != null && !image.isEmpty()) {
            reply.setImage(image.getBytes());
        }

        messageRepository.save(reply);
    }

    @Transactional
    public long getUnreadMessageCount(Users recipient) {
        return messageRepository.countByRecipientAndStatus(recipient, MessageStatus.UNREAD);
    }

    // Utility method to encode image to Base64
    private void encodeImage(Message message) {
        if (message.getImage() != null) {
            String base64Image = Base64.getEncoder().encodeToString(message.getImage());
            message.setBase64Image(base64Image); // Assuming you add this field to the Message entity
        }
    }
}