package com.example.SevMerge.expertprofile;

import com.example.SevMerge.core.exception.BadRequestException;
import com.example.SevMerge.core.exception.NotFoundException;
import com.example.SevMerge.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExpertProfileService {

  private final ExpertProfileRepository expertProfileRepository;

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
        .avgRating(java.math.BigDecimal.ZERO)
        .totalReviews(0)
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
        .map(ExpertProfileResponse::from)
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

    return ExpertProfileResponse.from(profile);
  }
}