package com.example.SevMerge.review;

import com.example.SevMerge.expertprofile.ExpertProfile;
import com.example.SevMerge.project.Project;
import lombok.Builder;
import lombok.Data;


public class ReviewResponse {

    @Data
    @Builder
    public static class ReviewSaveDTO {

        private String expertName;
        private String projectTitle;
        private String career;
        private Long expertId;

        public ReviewSaveDTO (ExpertProfile expertProfile , Project project){

            this.expertName = expertProfile.getMember().getName();
            this.projectTitle = project.getTitle();
            this.career = expertProfile.getCareer();
            this.expertId = expertProfile.getId();

        }


    }









}
