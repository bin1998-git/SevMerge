package com.example.SevMerge.board;

import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.member.Member;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

public class BoardRequest {

    @Data
    public static class SaveBoardDTO {
        private String title;
        private String content;
        private Integer viewCount;
        private BoardType boardType;
        private BoardInquiryScope inquiryScope;
        private MultipartFile attachmentFile; // 첨부파일

        public Board toEntity(Member member, String attachmentUrl, String attachmentName) {
            return Board.builder()
                    .title(title)
                    .inquiryScope(inquiryScope)
                    .content(content)
                    .boardType(boardType)
                    .viewCount(0)
                    .member(member)
                    .isActive(true)
                    .attachmentUrl(attachmentUrl)
                    .attachmentName(attachmentName)
                    .build();
        }

        public void validate() {
            if (title == null || title.trim().isEmpty()) {
                throw new BadRequestException("제목 입력은 필수 입니다.");
            }
            if (content == null || content.trim().isEmpty()) {
                throw new BadRequestException("본문 입력은 필수입니다.");
            }
        }
    }

    @Data
    public static class UpdateBoardDTO {
        private String title;
        private String content;
        private BoardInquiryScope inquiryScope;
        // 파일 업데이트 시 서비스에서 세팅
        private String attachmentUrl;
        private String attachmentName;

        public void validate() {
            if (title == null || title.trim().isEmpty()) {
                throw new BadRequestException("제목 입력은 필수 입니다.");
            }
            if (content == null || content.trim().isEmpty()) {
                throw new BadRequestException("본문 입력은 필수입니다.");
            }
        }
    }
}
