package com.example.SevMerge.faq;

import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FaqService {

    private final FaqRepository faqRepository;

    // FAQ 목록
    public List<Faq> findAll() {
        return faqRepository.findAllIsActive();
    }
    public Faq findById(Long faqId) {
        return faqRepository.findById(faqId).orElseThrow(() ->
                    new BadRequestException("FAQ를 찾을수 없습니다.")
                );
    }


    @Transactional
    public void save(Member sessionUser, FaqRequest request) {

        if (!sessionUser.isAdmin()) {
            throw new BadRequestException("관리자만 FAQ를 등록 할수 있습니다.");
        }
        request.validate();
        Faq faqEntity = request.toEntity();
        faqRepository.save(faqEntity);
    }

    @Transactional
    public void delete(Member sessionUser, Long faqId) {
        if (!sessionUser.isAdmin()) {
            throw new BadRequestException("관리자만 삭제 가능");
        }
        Faq faqEntity = faqRepository.findById(faqId).orElseThrow(() ->
                new BadRequestException("해당 FAQ는 존재하지 않습니다.")
        );
        faqEntity.delete();
    }

    // 업데이트
    @Transactional
    public void update(Long faqId, Member sessionUser, FaqRequest request) {
        if (!sessionUser.isAdmin()) {
            throw new BadRequestException("관리자만 FAQ를 수정할수 있습니다");
        }
        Faq faqEntity = faqRepository.findById(faqId).orElseThrow(() ->
                new BadRequestException("해당 FAQ는 존재하지 않습니다.")
        );
        request.validate();
        faqEntity.setAnswer(request.getAnswer());
        faqEntity.setQuestion(request.getQuestion());
    }
}
