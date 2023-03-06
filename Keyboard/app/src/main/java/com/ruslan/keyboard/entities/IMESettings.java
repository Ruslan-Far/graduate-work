package com.ruslan.keyboard.entities;

public class IMESettings extends BaseEntity {

    private Integer sound;
    private Integer vibration;
    private Integer candidates;
    private Integer canBackgroundColor;
    private Integer canAdditTextColor;
    private Integer canOrthoTextColor;
    private Integer canPredTextColor;
    private String canFont;
    private Integer learningRate;

    public IMESettings() {}

    public IMESettings(Integer id, Integer sound, Integer vibration, Integer candidates,
                       Integer canBackgroundColor, Integer canAdditTextColor, Integer canOrthoTextColor,
                       Integer canPredTextColor, String canFont, Integer learningRate) {
        super(id);
        this.sound = sound;
        this.vibration = vibration;
        this.candidates = candidates;
        this.canBackgroundColor = canBackgroundColor;
        this.canAdditTextColor = canAdditTextColor;
        this.canOrthoTextColor = canOrthoTextColor;
        this.canPredTextColor = canPredTextColor;
        this.canFont = canFont;
        this.learningRate = learningRate;
    }

    public Integer getSound() {
        return sound;
    }

    public void setSound(Integer sound) {
        this.sound = sound;
    }

    public Integer getVibration() {
        return vibration;
    }

    public void setVibration(Integer vibration) {
        this.vibration = vibration;
    }

    public Integer getCandidates() {
        return candidates;
    }

    public void setCandidates(Integer candidates) {
        this.candidates = candidates;
    }

    public Integer getCanBackgroundColor() {
        return canBackgroundColor;
    }

    public void setCanBackgroundColor(Integer canBackgroundColor) {
        this.canBackgroundColor = canBackgroundColor;
    }

    public Integer getCanAdditTextColor() {
        return canAdditTextColor;
    }

    public void setCanAdditTextColor(Integer canAdditTextColor) {
        this.canAdditTextColor = canAdditTextColor;
    }

    public Integer getCanOrthoTextColor() {
        return canOrthoTextColor;
    }

    public void setCanOrthoTextColor(Integer canOrthoTextColor) {
        this.canOrthoTextColor = canOrthoTextColor;
    }

    public Integer getCanPredTextColor() {
        return canPredTextColor;
    }

    public void setCanPredTextColor(Integer canPredTextColor) {
        this.canPredTextColor = canPredTextColor;
    }

    public String getCanFont() {
        return canFont;
    }

    public void setCanFont(String canFont) {
        this.canFont = canFont;
    }

    public Integer getLearningRate() {
        return learningRate;
    }

    public void setLearningRate(Integer learningRate) {
        this.learningRate = learningRate;
    }
}
