package com.ruslan.keyboard.entities;

public class IMESettings extends BaseEntity {

    private Integer sound;
    private Integer vibration;
    private Integer candidates;

    public IMESettings() {}

    public IMESettings(Integer id, Integer sound, Integer vibration, Integer candidates) {
        super(id);
        this.sound = sound;
        this.vibration = vibration;
        this.candidates = candidates;
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
}
