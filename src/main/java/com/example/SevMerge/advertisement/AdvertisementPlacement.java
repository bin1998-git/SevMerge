package com.example.SevMerge.advertisement;

public enum AdvertisementPlacement {
    MAIN_BANNER("메인 배너", 7, 10000),
    EXPERT_CAROUSEL("전문가 캐러셀", 7, 5000);

    private final String label;
    private final int durationDays;
    private final int price;

    AdvertisementPlacement(String label, int durationDays, int price) {
        this.label = label;
        this.durationDays = durationDays;
        this.price = price;
    }

    public String getLabel() { return label; }
    public int getDurationDays() { return durationDays; }
    public int getPrice() { return price; }
}