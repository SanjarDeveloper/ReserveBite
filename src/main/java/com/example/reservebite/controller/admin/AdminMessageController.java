package com.example.reservebite.controller.admin;

import com.example.reservebite.entity.Message;
import com.example.reservebite.entity.Users;
import com.example.reservebite.entity.enums.MessageStatus;
import com.example.reservebite.service.MessageService;
import com.example.reservebite.service.UsersService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin/messages")
@PreAuthorize("hasRole('ADMIN')")
public class AdminMessageController {

    private final MessageService messageService;
    private final UsersService usersService;

    public AdminMessageController(MessageService messageService, UsersService usersService) {
        this.messageService = messageService;
        this.usersService = usersService;
    }

    @GetMapping
    public String showAdminMessages(Model model, Authentication authentication) {
        Users admin = usersService.getUserByUsername(authentication.getName());
        model.addAttribute("users", messageService.getDistinctSenders(admin));
        model.addAttribute("unreadCount", messageService.getUnreadMessageCount(admin));
        return "admin/messages/list";
    }

    @GetMapping("/{userId}")
    public String viewConversation(@PathVariable Long userId, Model model, Authentication authentication) {
        Users admin = usersService.getUserByUsername(authentication.getName());
        Users user = usersService.getUserById(userId);
        List<Message> messages = messageService.getConversation(admin, user);
        messages.forEach(message -> {
            if (message.getRecipient().getId().equals(admin.getId()) && message.getStatus() == MessageStatus.UNREAD) {
                messageService.markMessageAsRead(message.getId());
            }
        });
        model.addAttribute("messages", messages);
        model.addAttribute("currentUser", admin);
        model.addAttribute("otherUser", user);
        model.addAttribute("unreadCount", messageService.getUnreadMessageCount(admin));
        return "admin/messages/conversation";
    }

    @PostMapping("/{userId}/send")
    public String sendMessage(@PathVariable Long userId,
                              @RequestParam String content,
                              @RequestParam(required = false) MultipartFile image,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) throws IOException {
        Users admin = usersService.getUserByUsername(authentication.getName());
        Users recipient = usersService.getUserById(userId);
        messageService.sendMessage(admin, recipient, content, image);
        redirectAttributes.addFlashAttribute("success", "Message sent successfully");
        return "redirect:/admin/messages/" + userId;
    }
}