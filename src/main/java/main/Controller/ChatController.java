package main.Controller;

import main.model.Message;
import main.model.Users;
import main.repository.MessageRepository;
import main.repository.UsersRepository;
import main.response.AddMessageResponse;
import main.response.MessageResponse;
import main.response.AuthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class ChatController {

    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    private static final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd:MM:yyyy");

    @GetMapping(path = "/api/auth")
    public AuthResponse auth() {
        AuthResponse response = new AuthResponse();
        String sessionId = getSessionId();
        Users user = userRepository.getBySessionId(sessionId);
        response.setResult(user != null);
        if (user != null) {
            response.setName(user.getName());
        }
        return response;
    }

//    public HashMap<String, Boolean> auth() {
//        HashMap<String, Boolean> response = new HashMap<>();
//        String sessionId = getSessionId();
//        Users user = userRepository.getBySessionId(sessionId);
//        response.put("result", user != null);
//        if (user != null) {
//            response.put("name", user.getName());
//        }
//        return response;
//    }

    @GetMapping(path = "/api/users")
    public List<String> GetUsers(HttpServletRequest request) {
        List<String> response = new ArrayList<>();
        Iterable<Users> allUsers = userRepository.findAll();
        for(Users user: allUsers) {
            response.add(user.getName());
        }
        return response;
    }

    @PostMapping(path = "/api/users")
    public  HashMap<String, Boolean> addUser(HttpServletRequest request) {
        String name = request.getParameter("name");
        String sessionId = getSessionId();

        Users user = new Users();
        user.setName(name);
        user.setSessionId(sessionId);
        userRepository.save(user);

        HashMap<String, Boolean> response = new HashMap<>();
        response.put("result", true);
        return response;
    }

    @GetMapping(path = "/api/messages")
    public Map<String, List<MessageResponse>> GetMessages(HttpServletRequest request) {
        List<MessageResponse> response = new ArrayList<>();
        MessageResponse messageResponse = new MessageResponse();
        Iterable<Message> allMessages = messageRepository.findAll();
        for(Message message : allMessages) {
            messageResponse.setSendTime(formatter.format(message.getSendTime()));
            messageResponse.setUserName(message.getUser().getName());
            messageResponse.setText(message.getText());
            response.add(messageResponse);
        }
        return Collections.singletonMap("messages", response);
    }

    @PostMapping(path = "/api/messages")
    public AddMessageResponse addMessage(HttpServletRequest request) {
        String messageText = request.getParameter("text");

        String sessionId = getSessionId();
        Users user = userRepository.getBySessionId(sessionId);

        Message message = new Message();
        message.setText(messageText);
        message.setUser(user);
        message.setSendTime(new Date());
        messageRepository.save(message);

        AddMessageResponse response = new AddMessageResponse();
        response.setResult(true);
        response.setSendTime(formatter.format(message.getSendTime()));
        return response;
    }

    private String getSessionId() {
        return RequestContextHolder.currentRequestAttributes().getSessionId();
    }
}
