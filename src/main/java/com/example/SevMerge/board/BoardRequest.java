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
        private BoardType boardType;

        public Board toEntity(Member member) {
            return Board.builder()
                    .title(title)
                    .content(content)
                    .boardType(boardType)
                    .viewCount(0)
                    .member(member)
                    .isActive(true)
                    .build();
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
    public static class UpdateBoardDTO {
        private String title;
        private String content;

        public void validate() {
            if(title == null || title.trim().isEmpty()) {
                throw new BadRequestException("제목 입력은 필수 입니다.");
            }

            if (content == null || content.trim().isEmpty()) {
                throw new BadRequestException("본문 입력은 필수입니다.");
            }
        }
    }

}
