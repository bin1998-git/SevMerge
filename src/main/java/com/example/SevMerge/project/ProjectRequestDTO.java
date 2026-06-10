package com.example.SevMerge.project;

import com.example.SevMerge.core.exception.BadRequestException;
import lombok.Data;

import java.sql.Timestamp;


@Data
public class ProjectRequestDTO {
    @Data
    public static class SaveDTO {
        private String title;
        private String category;
        private String description;
        private Integer budgetMin;
        private Integer budgetMax;
        private String deadline;
        private Boolean isPrivate; // 비공개 입찰여부
        private String bidFilter;

        public void validate() {
            if (title == null || title.isBlank()) {
                throw new BadRequestException("제목을 입력해주세요");
            }
            if (title.length() <= 4) {
                throw new BadRequestException("제목은 4글자 이상이어야 합니다");
            }
            if (description == null || description.isBlank()) {
                throw new BadRequestException("설명을 입력해주세요");
            }
            if (budgetMin == null || budgetMin <= 0) {
                throw new BadRequestException("최소 예산을 입력해주세요");
            }
            if (budgetMax == null || budgetMax <= 0) {
                throw new BadRequestException("최대 예산을 입력해주세요");
            }
            if (budgetMin > budgetMax) {
                throw new BadRequestException("최소 예산이 최대 예산보다 클 수 없습니다");
            }
            if (deadline == null) {
                throw new BadRequestException("마감일을 입력해주세요");
            }
        }
    }

    @Data
    public static class UpdateDTO {
        private String title;
        private String description;
        private Integer budgetMin;
        private Integer budgetMax;
        private String deadline;
        private String bidFilter;

        public void validate() {
            if (title == null || title.isBlank()) {
                throw new BadRequestException("제목은 필수 입력입니다");
            }

            if (budgetMin != null && budgetMax != null && budgetMin > budgetMax) {
                throw new BadRequestException("최대예산은 최소예산보다 작을수 없습니다");
            }
        }
    }


}







