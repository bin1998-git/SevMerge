package com.example.SevMerge.bid;

import com.example.SevMerge.core.exception.BadRequestException;
import lombok.Data;

public class BidRequestDTO {

    // 제안서 작성DTO
    @Data
    public static class SaveDTO {
        private Long projectId;
        private String coverLetter;
        private String approach;
        private Long estimatedDays;
        private Long proposedPrice;

        public void validate() {
            if (coverLetter == null || coverLetter.isBlank()) {
                throw new BadRequestException("자기소개를 입력해 주세요");
            }
            if (coverLetter.length() <= 10) {
                throw new BadRequestException("자기소개는 10글자 이상 작성해주세요");
            }
            if (estimatedDays == null || estimatedDays <= 0) {
                throw new BadRequestException("예상작업 기간을 입력해주세요");
            }
            if (proposedPrice == null || proposedPrice <= 0) {
                throw new BadRequestException("희망 금액을 입력해 주세요");
            }
        }

    }

    @Data
    public static class UpdateDTO {
        private String coverLetter;
        private String approach;
        private Long estimatedDays;
        private Long proposedPrice;

        public void validate() {
            if (coverLetter == null || coverLetter.isBlank()) {
                throw new BadRequestException("자기소개를 입력해 주세요");
            }
            if (estimatedDays != null && estimatedDays <= 0) {
                throw new BadRequestException("예상작업 기간을 입력해주세요");
            }
            if (proposedPrice != null && proposedPrice <= 0) {
                throw new BadRequestException("희망 금액을 입력해 주세요");
            }
        }
    }
}
