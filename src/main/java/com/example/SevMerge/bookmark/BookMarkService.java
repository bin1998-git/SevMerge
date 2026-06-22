package com.example.SevMerge.bookmark;


import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.expertprofile.ExpertProfile;
import com.example.SevMerge.expertprofile.ExpertProfileRepository;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookMarkService {

    private final BookMarkRepository bookMarkRepository;
    private final MemberRepository memberRepository;
    private final ExpertProfileRepository expertProfileRepository;

    // 북마크 확인여부
    public boolean isBookmarked(Long memberId, Long expertId) {
        // isPresent() 북마크가 DB 에 있으면 true 반환
        return bookMarkRepository.findByMemberIdAndExpertProfileId(memberId, expertId).isPresent();
    }

    // 내 북마크 리스트
    public List<BookMark> findAllMyBookMarks(Long memberId) {
        return bookMarkRepository.findAllByMemberId(memberId);
    }


    // 북마킹
    @Transactional
    public boolean toggle(Long memberId, Long expertId) {
        // 해당 사용자가 이 전문가를 북마크 했는지 확인
        Optional<BookMark> bookMark = bookMarkRepository.findByMemberIdAndExpertProfileId(memberId, expertId);
        if (bookMark.isPresent()) { // 북마크 기록이 DB에 있으면 true 반환 즉 마크를 한번더 누르면 삭제 처리
            bookMarkRepository.delete(bookMark.get());
            return false; // 북마크 취소
        } else { // 마크가 없으면 save 처리
            Member memberEntity = memberRepository.findById(memberId).orElseThrow(() ->
                    new BadRequestException("사용자를 찾을수 없습니다.")

            );
            ExpertProfile expertProfileEntitiy = expertProfileRepository.findById(expertId).orElseThrow(() ->
                    new BadRequestException("전문가가 존재하지 않습니다.")
            );
            bookMarkRepository.save(BookMark
                    .builder()
                    .member(memberEntity)
                    .expertProfile(expertProfileEntitiy)
                    .build()
            );
            return true; // 북마크된상태
        }
    }

    @Transactional
    public void delete(Long expertId,Long memberId){

        BookMark bookMark = bookMarkRepository.findByMemberIdAndExpertProfileId(memberId, expertId).orElseThrow(() ->
                    new BadRequestException("북마크가 없습니다.")
                );
        System.out.println(memberId);
        bookMarkRepository.delete(bookMark);
        System.out.println("삭제완료");
    }


    // 필터 검색 기능
    public List<BookMark> filterBookMarks(Long memberId, String keyword) {
        // 내가 등록한 북마크중에서 전문가 이름으로 검색
        return bookMarkRepository.findFilterByName(keyword, memberId);

    }


}
