package com.example.SevMerge.expertprofile;

import com.example.SevMerge.core.util.ApiResponse;
import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.member.Member;
import com.example.SevMerge.member.SessionUser;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.SevMerge.member.MemberRepository;


@RestController
@RequestMapping("/expert-profile")
@RequiredArgsConstructor
public class ExpertProfileController {

  private final ExpertProfileService expertProfileService;
  private final MemberRepository memberRepository;

  /**
   * 전문가 프로필 등록
   * POST /expert-profile
   */
  @PostMapping
  public ResponseEntity<ApiResponse<ExpertProfileResponse>> save(
          @RequestBody @Valid ExpertProfileRequest.SaveRequest req,
          HttpSession session) {

    SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
    Member member = memberRepository.findById(sessionUser.getId())
            .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));
    ExpertProfileResponse response = expertProfileService.save(member, req);
    return ResponseEntity.ok(ApiResponse.ok(response));
  }

  /**
   * 전문가 프로필 조회
   * GET /expert-profile/{memberId}
   * GET /expert-profile/{memberId}
   */
  @GetMapping("/{memberId}")
  public ResponseEntity<ApiResponse<ExpertProfileResponse>> get(
      @PathVariable Long memberId) {

    ExpertProfileResponse response = expertProfileService.getByMemberId(memberId);
    return ResponseEntity.ok(ApiResponse.ok(response));
  }

  /**
   * 전문가 프로필 수정
   * PUT /expert-profile
   */
  @PutMapping
  public ResponseEntity<ApiResponse<ExpertProfileResponse>> update(
      @RequestBody @Valid ExpertProfileRequest.SaveRequest req,
      HttpSession session) {

    SessionUser sessionUser = (SessionUser) session.getAttribute(Define.SESSION_USER);
    ExpertProfileResponse response = expertProfileService.update(sessionUser.getId(), req);
    return ResponseEntity.ok(ApiResponse.ok(response));
  }
}