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
    }

    public boolean isActive()  { return "ACTIVE".equals(status); }
    public boolean isExpired() { return "EXPIRED".equals(status); }

    public String getProfileImageUrl() {
        if (profileImage == null || profileImage.isBlank()) return "/images/default.png";
        if (profileImage.startsWith("http")) return profileImage;
        return "/images/" + profileImage;
    }
}