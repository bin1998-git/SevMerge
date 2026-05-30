package com.example.SevMerge.board;

import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.member.Member;
import lombok.Builder;
import lombok.Data;

public class BoardRequest {

    @Data
    public static class SaveBoardDTO {
        private String title;
        private String content;
        private Integer viewCount;
        private Member member;
        private BoardType boardType;

        public SaveBoardDTO(String title, String content, Integer viewCount, Member member, BoardType boardType) {
            this.title = title;
            this.content = content;
            this.viewCount = viewCount;
            this.member = member;
            this.boardType = boardType;
        }

        @Builder


        public void validate() {
            if(title == null || title.trim().isEmpty()) {
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

        @Builder
        public UpdateBoardDTO(String title, String content) {
            this.title = title;
            this.content = content;
        }

        public void validate() {
            if(title == null || title.trim().isEmpty()) {
                throw new BadRequestException("제목 입력은 필수 입니다.");
            }

            if (content == null || content.trim().isEmpty()) {
                throw new BadRequestException("본문 입력은 필수입니다.");
            }
        }
    }

    @Data
    public static class DeleteDTO {
        private Boolean isActive;;

        public DeleteDTO(Boolean isActive) {
            this.isActive = isActive;
        }
    }
}
