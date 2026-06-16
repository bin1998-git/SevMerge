package com.example.SevMerge.faq;

import com.example.SevMerge.core.exception.BadRequestException;
import lombok.Data;

@Data
public class FaqRequest {

    private String question;
    private String answer;


    public void validate() {
        if (question == null || question.trim().isEmpty()) {
            throw new BadRequestException("질문을 작성해 주세요");
        }
        if (answer == null || answer.trim().isEmpty()) {
            throw new BadRequestException("답변을 작성해 주세요");
        }
    }

    public Faq toEntity() {

        return Faq.builder()
                .question(this.question)
                .answer(this.answer)
                .isActive(true)
                .build();

    }
}
