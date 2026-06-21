package com.example.SevMerge.portfolio.utile;


import com.example.SevMerge.portfolio.PortfolioRequest;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class PortfolioAiService {

    private final ChatClient chatClient;

    public PortfolioAiService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public boolean aiSaveValid(PortfolioRequest.SaveDTO saveDTO) {

        String prompt = """
                너는 사용자가 제출한 포트폴리오 URL이 실제로 존재하는 유효한 프로젝트 링크인지 판별하는 전문가야.
                반드시 제공된 URL의 활성화 여부와 콘텐츠를 확인하고 판단해줘.
                
                [주의 사항]
                - GitHub, 벨로그, 노션, 개인 웹사이트 등 프로젝트가 명확히 존재하면 true를 반환해.
                - 404 에러 페이지, 잘못된 도메인, 빈 페이지이면 false를 반환해.
                - 부연 설명은 절대 하지 말고, 오직 'true' 또는 'false' 단 한 단어만 소문자로 반환해.
                
                [예시]
                User: https://github.com/example/my-project -> AI: true
                User: https://github.com/error404page -> AI: false
                """;

        String result = chatClient.prompt()
                .system(prompt)
                .user(saveDTO.getProjectUrl())
                .call()
                .content();
        return result.equals("true");
    }


    public boolean aiUpdateValid(PortfolioRequest.UpdateDTO updateDTO) {

        String prompt = """
                너는 사용자가 제출한 포트폴리오 URL이 실제로 존재하는 유효한 프로젝트 링크인지 판별하는 전문가야.
                반드시 제공된 URL의 활성화 여부와 콘텐츠를 확인하고 판단해줘.
                
                [주의 사항]
                - GitHub, 벨로그, 노션, 개인 웹사이트 등 프로젝트가 명확히 존재하면 true를 반환해.
                - 404 에러 페이지, 잘못된 도메인, 빈 페이지이면 false를 반환해.
                - 부연 설명은 절대 하지 말고, 오직 'true' 또는 'false' 단 한 단어만 소문자로 반환해.
                
                [예시]
                User: https://github.com/example/my-project -> AI: true
                User: https://github.com/error404page -> AI: false
                """;

        String result = chatClient.prompt()
                .system(prompt)
                .user(updateDTO.getProjectUrl())
                .call()
                .content();
        return result.equals("true");
    }


}
