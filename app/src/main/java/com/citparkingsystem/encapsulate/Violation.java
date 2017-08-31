package com.citparkingsystem.encapsulate;

/**
 * Created by Dave Tolentin on 7/27/2017.
 */

public class Violation {

    public String plateNumber;
    public String violationType;

    public Violation() {

    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getViolationType() {
        return violationType;
    }

    public void setViolationType(String violationType) {
        this.violationType = violationType;
    }
}
