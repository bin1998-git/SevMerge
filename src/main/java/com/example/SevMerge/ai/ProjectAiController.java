package com.example.SevMerge.ai;

import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.member.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
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
            return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다."));
        }
        try {
            ProjectAiDraftDTO result = projectAiService.generateDraft(requestDTO.userInput());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("[AI] 프로젝트 초안 생성 실패 - memberId={}, error={}", sessionUser.getId(), e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("message", "AI 초안 생성에 실패했습니다. 잠시 후 다시 시도해 주세요."));
        }
    }


    @PostMapping("/ask")
    public ResponseEntity<?> ask(@RequestBody ChatRequestDTO requestDTO,
                                 HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
        if (sessionUser == null) {
            return ResponseEntity.status(401).body(Map.of("message", "로그인이 필요합니다."));
        }
        try {
            String answer = projectAiService.ask(requestDTO.question());
            return ResponseEntity.ok(new ChatResponseDTO(answer));
        } catch (Exception e) {
            log.error("[AI] AI 질문 응답 실패 - memberId={}, error={}", sessionUser.getId(), e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("message", "AI 응답에 실패했습니다. 잠시 후 다시 시도해 주세요."));
        }
    }
}
