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
        private Member member;
        private BoardType boardType;

        public SaveBoardDTO(String title, String content, Member member, BoardType boardType) {
            this.title = title;
            this.content = content;
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

}
