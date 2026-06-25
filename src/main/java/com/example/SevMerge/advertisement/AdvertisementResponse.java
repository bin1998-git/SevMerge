package com.example.SevMerge.advertisement;

import com.example.SevMerge.core.util.MyDateUtil;
import lombok.Getter;


@Getter
public class AdvertisementResponse {

    private Long id;
    private Long expertId;
    private String expertName;
    private String speciality;
    private String profileImage;
    private String placement;
    private String placementLabel;
    private Integer price;
    private String startDate;
    private String endDate;
    private String status;
    private String customMessage;
    private String bannerImage;
    private String avgRating;
    private String rejectReason;

    public AdvertisementResponse(Advertisement ad, String expertName, String speciality, String profileImage) {
        this.id = ad.getId();
        this.expertId = ad.getExpertId();
        this.expertName = expertName;
        this.speciality = speciality;
        this.profileImage = profileImage;
        this.placement = ad.getPlacement().name();
        this.placementLabel = ad.getPlacement().getLabel();
        this.price = ad.getPrice();
        this.startDate = MyDateUtil.timestampFormat(ad.getStartDate());
        this.endDate = MyDateUtil.timestampFormat(ad.getEndDate());
        this.status = ad.getStatus().name();
        this.customMessage = ad.getCustomMessage();
        this.bannerImage = ad.getBannerImage();
        this.rejectReason = ad.getRejectReason();
    }

    public void setAvgRating(String avgRating) {
        this.avgRating = avgRating;
    }

    public boolean isActive()  { return "ACTIVE".equals(status); }
    public boolean isExpired() { return "EXPIRED".equals(status); }
    public boolean isPending()  { return "PENDING".equals(status); }
    public boolean isRejected() { return "REJECTED".equals(status); }

    public String getDisplayImageUrl() {
        String img = (bannerImage != null && !bannerImage.isBlank()) ? bannerImage : profileImage;
        if (img == null || img.isBlank()) return "/images/default.png";
        if (img.startsWith("http")) return img;
        return "/images/" + img;
    }

    public boolean isHasProfileImage() {
        return profileImage != null && !profileImage.isBlank();
    }

    public boolean isHasRejectReason() {
        return rejectReason != null && !rejectReason.isBlank();
    }
}