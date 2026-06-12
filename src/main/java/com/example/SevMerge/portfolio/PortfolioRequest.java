package com.example.SevMerge.portfolio;

import com.example.SevMerge.core.exception.BadRequestException;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

public class PortfolioRequest {

    @Data
    @NoArgsConstructor
    public static class SaveDTO {

        private Long expertId; // expert의 memberId
        private String title; // 제목
        private String description; // 설명
        private MultipartFile imageUrl; // 이미지 링크
        private String projectUrl; // 프로젝트 링크

        public void validate () {

            if(title == null || title.trim().isEmpty()) {
                throw new BadRequestException("제목 입력은 필수 입니다.");
            }
            if(description == null || description.trim().isEmpty()) {
                throw new BadRequestException("설명란을 입력해 주세요.");
            }

        }

    }

    @Data
    @NoArgsConstructor
    public static class UpdateDTO {

        private Long id; // 포폴 아이디
        private String title;
        private String description;
        private MultipartFile imageUrl;
        private String projectUrl;

        public void validate () {

            if(title == null || title.trim().isEmpty()) {
                throw new BadRequestException("제목 입력은 필수 입니다.");
            }
            if(description == null || description.trim().isEmpty()) {
                throw new BadRequestException("설명란을 입력해 주세요.");
            }
        }
    }

}
