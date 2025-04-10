package com.example.reservebite.controller.user;

import com.example.reservebite.entity.Message;
import com.example.reservebite.entity.Users;
import com.example.reservebite.service.MessageService;
import com.example.reservebite.service.UsersService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/user/messages")
public class MessageController {

    private final MessageService messageService;
    private final UsersService usersService;

    public MessageController(MessageService messageService, UsersService usersService) {
        this.messageService = messageService;
        this.usersService = usersService;
    }

    @GetMapping
    public String showMessages(Model model, Authentication authentication) {
        Users currentUser = usersService.getUserByUsername(authentication.getName());
        Users admin = usersService.getUserByUsername("admin");

        model.addAttribute("messages", messageService.getConversation(currentUser, admin));
        model.addAttribute("unreadMessages", messageService.getUnreadMessages(currentUser));
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("admin", admin);

        return "user/messages/messages";
    }

    @PostMapping("/send")
    public String sendMessage(@RequestParam String content,
                              @RequestParam Long recipientId,
                              @RequestParam(required = false) MultipartFile image,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) throws IOException {
        Users sender = usersService.getUserByUsername(authentication.getName());
        Users recipient = usersService.getUserById(recipientId);

        messageService.sendMessage(sender, recipient, content, image);
        redirectAttributes.addFlashAttribute("success", "Message sent successfully");

        return "redirect:/user/messages";
    }
}