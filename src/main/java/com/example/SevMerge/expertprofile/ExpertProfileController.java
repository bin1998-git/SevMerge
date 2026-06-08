package com.example.SevMerge.expertprofile;

import com.example.SevMerge.core.util.ApiResponse;
import com.example.SevMerge.core.util.Define;
import com.example.SevMerge.member.Member;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/expert-profile")
@RequiredArgsConstructor
public class ExpertProfileController {

  private final ExpertProfileService expertProfileService;

  /**
   * 전문가 프로필 등록
   * POST /expert-profile
   */
  @PostMapping
  public ResponseEntity<ApiResponse<ExpertProfileResponse>> save(
          @RequestBody @Valid ExpertProfileRequest.SaveRequest req,
          HttpSession session) {

    Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
    ExpertProfileResponse response = expertProfileService.save(sessionUser, req);
    return ResponseEntity.ok(ApiResponse.ok(response));
  }

  /**
   * 전문가 프로필 조회
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

    Member sessionUser = (Member) session.getAttribute(Define.SESSION_USER);
    ExpertProfileResponse response = expertProfileService.update(sessionUser.getId(), req);
    return ResponseEntity.ok(ApiResponse.ok(response));
  }
}