package com.example.SevMerge.ai;

import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.member.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/project/ai")
@RequiredArgsConstructor
public class ProjectAiController {

    private final ProjectAiService projectAiService;

    @PostMapping("/draft")
    public ResponseEntity<?> generateDraft(@RequestBody ProjectAiRequestDTO requestDTO,
                                           HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) {
            return ResponseEntity.status(401).body(null);
        }
        try {
            ProjectAiDraftDTO result = projectAiService.generateDraft(requestDTO.userInput());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }


    @PostMapping("/ask")
    public ResponseEntity<?> ask(@RequestBody ChatRequestDTO requestDTO,
                                 HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) {
            return ResponseEntity.status(401).body(null);
        }
        try {
            String answer = projectAiService.ask(requestDTO.question());
            return ResponseEntity.ok(new ChatResponseDTO(answer));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
