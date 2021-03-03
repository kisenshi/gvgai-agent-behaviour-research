package heuristic_diversification.model;

import heuristic_diversification.mapelites.Features;

public class FeaturesInfo {
    int arraySize;
    int minValueMap;
    int maxValueMap;
    int bucketSize;
    boolean isPercentage;
    String[] bucketsRangeInfo;

    public FeaturesInfo(Features featureInfo) {
        this.arraySize = featureInfo.featureArraySize();
        this.minValueMap = featureInfo.getFeatureMinValue();
        this.maxValueMap = featureInfo.getFeatureMaxValue();
        this.bucketSize = featureInfo.getFeatureBucketSize();
        this.isPercentage = featureInfo.isPercentageFeature();
        this.bucketsRangeInfo = featureInfo.featureArrayInfo();
    }
}