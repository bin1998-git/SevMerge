package com.example.SevMerge.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProjectAiService {

    // Spring AI가 application-dev.yml 설정(spring.ai.google.genai...)을 읽어서
    // 자동으로 ChatClient.Builder를 빈으로 등록
    private final ChatClient chatClient;
    private final ExpertToolService expertToolService;

    // (이걸 "의존성 주입"이라고 부름 - Repository 주입받는 거랑 똑같은 원리)
    public ProjectAiService(ChatClient.Builder builder, ExpertToolService expertToolService) {
        this.expertToolService = expertToolService;

        this.chatClient = builder
                .defaultTools(expertToolService)
                .build();
    }


    // === 실제로 AI를 호출하는 메서드 ===
    // userInput: 사용자가 화면에서 입력한 자연어 설명
    //   예: "쇼핑몰 결제 기능 있는 웹사이트 만들고 싶어요. 예산은 300만원이요."
    //
    // 반환값: ProjectAiDraftDTO

    public ProjectAiDraftDTO generateDraft(String userInput) {
        String systemPrompt = """
                너는 IT 외주 프로젝트 등록을 돕는 어시스턴트야.
                사용자의 자연어 설명을 분석해서 프로젝트 등록 항목을 채워줘.
                
                규칙:
                1. category는 반드시 다음 중 하나여야 해: WEB, APP, UI_UX, DATA, VIDEO, ETC
                2. bidFilter는 반드시 다음 중 하나여야 해: ALL, CERTIFIED_ONLY, GENERAL_ONLY
                   (사용자가 특별히 언급하지 않으면 ALL로 설정해)
                3. budgetMin, budgetMax는 원(KRW) 단위 숫자로 추정해줘.
                   사용자가 예산을 언급하지 않으면 null로 둬.
                4. description은 사용자 입력을 바탕으로 자연스럽게 다듬은 프로젝트 설명으로 작성해줘 (3~5문장).
                5. title은 핵심을 담은 간결한 제목으로 작성해줘 (4글자 이상).
                6. 확실하지 않은 항목은 null로 남겨줘. 임의로 지어내지 마.
                """;

        // 여기서부터가 실제로 Gemini한테 메시지를 보내는 부분.
        // 메서드를 점(.)으로 이어붙이는 방식(메서드 체이닝)으로 작성되어 있음.
        return chatClient
                .prompt() // 1. 새로운 대화 요청을 하나 시작함
                .system(systemPrompt) // 2. 위에서 만든 지시사항을 AI한테 전달
                .user(userInput) // 3. 사용자가 입력한 실제 내용을 전달
                .call() // 4. 실제로 Gemini 서버에 요청을 보냄 (여기서 진짜 네트워크 통신 발생)
                .entity(ProjectAiDraftDTO.class); // 5. Gemini가 준 응답(JSON 텍스트)을 ProjectAiDraftDTO 객체로 자동 변환해서 반환
    }


    // 일반질문/ 이용방법 안내
    public String ask(String userQuestion) {

        String systemPrompt = """
                너는 SevMerge라는 IT 외주 역경매 플랫폼의 AI 안내 챗봇이야.
                SevMerge는 클라이언트가 프로젝트를 등록하면, 여러 업체(전문가)가 입찰(제안)하는 방식의 서비스야.

                사용자에게 다음과 같은 내용을 친절하게 안내해줘:
                - 프로젝트 등록 방법
                - 입찰 참여 방법
                - 인증된 전문가 뱃지 시스템
                - 비공개 입찰이 뭔지
                - 결제 및 정산 방식
                - 1:1 실시간 채팅 기능

                답변은 친근하고 간결하게, 2~4문장 정도로 해줘.
                모르는 내용이나 SevMerge와 무관한 질문이면 "죄송해요, 그 부분은 잘 모르겠어요"라고 답해줘.
                """;

        return chatClient.prompt()
                .system(systemPrompt)
                .user(userQuestion)
                .call()
                .content();

    }
}
