package com.example.SevMerge.expertprofile;

import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.exception.NotFoundException;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.review.ReviewRepository;
import com.example.SevMerge.review.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExpertProfileService {

  private final ExpertProfileRepository expertProfileRepository;
  private final ReviewRepository reviewRepository;

  /**
   * 전문가 프로필 등록
   */
  @Transactional
  public ExpertProfileResponse save(Member member, ExpertProfileRequest.SaveRequest req) {
    if (expertProfileRepository.existsByMemberId(member.getId())) {
      throw new BadRequestException("이미 전문가 프로필이 존재합니다.");
    }

    ExpertProfile profile = ExpertProfile.builder()
        .member(member)
        .profileImage(req.getProfileImage())
        .intro(req.getIntro())
        .career(req.getCareer())
        .speciality(req.getSpeciality())
        .githubUrl(req.getGithubUrl())
        .contactEmail(req.getContactEmail())
        .isCertified(false)
        .build();

    expertProfileRepository.save(profile);
    return ExpertProfileResponse.from(profile);
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
              ExpertProfileResponse res = ExpertProfileResponse.from(profile);
              res.setAvgRating(avg != null ? BigDecimal.valueOf(avg) : BigDecimal.ZERO);
              res.setTotalReviews(count);
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



    return ExpertProfileResponse.from(profile);
  }

  /**
   * 전문가 프로필 수정
   */
  @Transactional
  public ExpertProfileResponse update(Long memberId, ExpertProfileRequest.SaveRequest req) {
    ExpertProfile profile = expertProfileRepository.findByMemberId(memberId)
        .orElseThrow(() -> new NotFoundException("전문가 프로필을 찾을 수 없습니다."));

    profile.setProfileImage(req.getProfileImage());
    profile.setIntro(req.getIntro());
    profile.setCareer(req.getCareer());
    profile.setSpeciality(req.getSpeciality());
    profile.setGithubUrl(req.getGithubUrl());
    profile.setContactEmail(req.getContactEmail());

    return ExpertProfileResponse.from(profile);
  }
}