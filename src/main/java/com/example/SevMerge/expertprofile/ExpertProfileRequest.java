package com.example.SevMerge.expertprofile;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class ExpertProfileRequest {

  /**
   * 전문가 프로필 등록/수정 요청 DTO
   */
  @Data
  public static class SaveRequest {

    @NotBlank
    private String profileImage;

    private String intro;

    private String career;

    private String speciality;
  }
}