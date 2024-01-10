package com.example.allowrepeatcallers;

public class class_Feature {
    private String featureName;
    private String featureDescription;
    private Boolean featureActivated;
    private boolean firstTime=true;
    public class_Feature(String featureName, String featureDescription, Boolean featureActivated) {
        this.featureName = featureName;
        this.featureDescription = featureDescription;
        this.featureActivated = featureActivated;
    }
    public class_Feature(){

    }
    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public String getFeatureDescription() {
        return featureDescription;
    }

    public void setFeatureDescription(String featureDescription) {
        this.featureDescription = featureDescription;
    }

    public Boolean getFeatureActivated() {
        return featureActivated;
    }

    public void setFeatureActivated(Boolean featureActivated_ip) {
        this.featureActivated = featureActivated_ip;
    }

}
