package com.example.SevMerge.ai;

import lombok.RequiredArgsConstructor;
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
    public ProjectAiDraftDTO generateDraft(@RequestBody ProjectAiRequestDTO requestDTO) {

        return projectAiService.generateDraft(requestDTO.userInput());
    }


    @PostMapping("/ask")
    public ChatResponseDTO ask(@RequestBody ChatRequestDTO requestDTO) {
        String answer = projectAiService.ask(requestDTO.question());
        return new ChatResponseDTO(answer);
    }
}
