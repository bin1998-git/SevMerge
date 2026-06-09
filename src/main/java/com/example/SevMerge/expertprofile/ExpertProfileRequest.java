package com.example.SevMerge.expertprofile;

import lombok.Data;

public class ExpertProfileRequest {

  /**
   * 전문가 프로필 등록/수정 요청 DTO
   */
  @Data
  public static class SaveRequest {

    private String profileImage;  // 폼에서 미전송 시 기존값 유지

    private String intro;

    private String career;

    private String speciality;

    private String githubUrl;

    private String contactEmail;
  }
}