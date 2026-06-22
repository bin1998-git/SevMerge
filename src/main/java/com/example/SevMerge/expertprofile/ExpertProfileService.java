package com.example.SevMerge.expertprofile;

import com.example.SevMerge.bid.BidRepository;
import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.exception.NotFoundException;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.MemberRepository;
import com.example.SevMerge.member.Role;
import com.example.SevMerge.project.Project;
import com.example.SevMerge.project.ProjectRepository;
import com.example.SevMerge.review.ReviewRepository;
import com.example.SevMerge.review.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExpertProfileService {

    private static final Pattern URL_PATTERN =
        Pattern.compile("^https?://[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=%]+$", Pattern.CASE_INSENSITIVE);

    private static String sanitizeUrl(String url) {
        if (url == null || url.isBlank()) return null;
        return URL_PATTERN.matcher(url.trim()).matches() ? url.trim() : null;
    }

    private final ExpertProfileRepository expertProfileRepository;
    private final ReviewRepository reviewRepository;
    private final BidRepository bidRepository;
    private final MemberRepository memberRepository;

    /**
     * 전문가 프로필 등록
     */
    @Transactional
    public ExpertProfileResponse save(Member member, ExpertProfileRequest.SaveRequest req) {
        Long memberId = member.getId();
        if (expertProfileRepository.existsByMemberId(memberId)) {
            throw new BadRequestException("이미 전문가 프로필이 존재합니다.");
        }

        Member managedMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        ExpertProfile profile = ExpertProfile.builder()
                .member(managedMember)
                .profileImage(req.getProfileImage())
                .intro(req.getIntro())
                .career(req.getCareer())
                .speciality(req.getSpeciality())
                .githubUrl(sanitizeUrl(req.getGithubUrl()))
                .contactEmail(req.getContactEmail())
                .isCertified(false)
                .build();

        expertProfileRepository.save(profile);
        return ExpertProfileResponse.from(profile);
    }

    /**
     * 전문가 프로필 존재 여부 확인 (member_id 기준)
     */
    public boolean existsByMemberId(Long memberId) {
        return expertProfileRepository.existsByMemberId(memberId);
    }

    /**
     * 전문가 전체 목록 조회 (목록 페이지용)
     */
    public List<ExpertProfileResponse> getAll() {
        return expertProfileRepository.findByExpert()
                .stream()
                .map(profile -> {
                    Double avg = reviewRepository.avgRating(profile.getMember().getId());
                    int count = reviewRepository.countByTargeterId(profile.getMember().getId());
                    Integer doneCount = bidRepository.doneProjectsByExpert(profile.getMember().getId());
                    Double globalAvg = reviewRepository.globalRating().orElse(3.5);

                    ExpertProfileResponse res = ExpertProfileResponse.from(profile);
                    res.setAvgRating(avg != null ? BigDecimal.valueOf(avg).setScale(1, RoundingMode.HALF_UP) : BigDecimal.ZERO);
                    res.setTotalReviews(count);
                    Grade grade = profile.checkGrade(avg, count, doneCount, globalAvg);
                    res.setGrade(grade.toString());
                    return res;
                })
                .toList();
    }

    /**
     * 전문가 프로필 단건 조회 (member_id 기준)
     */
    public ExpertProfileResponse getByMemberId(Long memberId) {
        ExpertProfile profile = expertProfileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new NotFoundException("전문가 프로필을 찾을 수 없습니다."));

        Double avg = reviewRepository.avgRating(memberId);
        int count = reviewRepository.countByTargeterId(memberId);
        Integer doneCount = bidRepository.doneProjectsByExpert(memberId);
        Double globalAvg = reviewRepository.globalRating().orElse(3.5);

        ExpertProfileResponse res = ExpertProfileResponse.from(profile);
        res.setAvgRating(avg != null ? BigDecimal.valueOf(avg).setScale(1, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        res.setTotalReviews(count);
        res.setGrade(profile.checkGrade(avg, count, doneCount, globalAvg).toString());
        return res;
    }

    /**
     * 전문가 프로필 수정
     */
    @Transactional
    public ExpertProfileResponse update(Long memberId, ExpertProfileRequest.SaveRequest req) {
        ExpertProfile profile = expertProfileRepository.findByMemberId(memberId)
                .orElseThrow(() -> new NotFoundException("전문가 프로필을 찾을 수 없습니다."));

        if (req.getProfileImage() != null) profile.setProfileImage(req.getProfileImage());
        profile.setIntro(req.getIntro());
        profile.setCareer(req.getCareer());
        profile.setSpeciality(req.getSpeciality());
        profile.setGithubUrl(sanitizeUrl(req.getGithubUrl()));
        profile.setContactEmail(req.getContactEmail());

        return ExpertProfileResponse.from(profile);
    }

    @Transactional
    public void manageExpertGrade(Long expertId) {

        ExpertProfile expert = expertProfileRepository.findByMemberId(expertId)
                .orElseThrow(() -> new NotFoundException("전문가를 찾을 수 없습니다."));

        if (!expert.getMember().getRole().equals(Role.EXPERT)) {
            throw new BadRequestException("전문가가 아닙니다.");
        }

        // 리뷰 평균 별점 조회, 프로젝트 완료 건수를 찾아
        Long memberId = expert.getMember().getId();
        Double avgRate = reviewRepository.avgRating(memberId) != null ? reviewRepository.avgRating(memberId) : 0.0;
        Integer reviewCount = reviewRepository.findMyReviews(memberId).size();
        Integer donCount = bidRepository.doneProjectsByExpert(memberId);
        Double globalAvg = reviewRepository.globalRating().orElse(3.5);

        expertProfileRepository.findByMemberId(memberId)
                .ifPresent(ep -> {
                    Grade newGrade = ep.checkGrade(avgRate, reviewCount, donCount, globalAvg);
                    ep.setExpertGrade(newGrade);
                    expertProfileRepository.save(ep);
                });
    }
}