package org.firstinspires.ftc.teamcode.SubSystems;

public class SubData {

    final double SHOOTING_ANGLE_DEGREES = 60;
    final double FLYWHEEL_DIAMETER_INCHES = 5;
    public static boolean isBlueTeam = false;

    public double calculateTargetRPM(double distance, double speed, double direction, double shootingangle) {

        double targetRPM = 0;
        // cos (ang) * Dia * RPM = vel = d/t
        






        return targetRPM;
    }

    public static void setTeamColor(boolean isBlueTeamInput) {

        isBlueTeam = isBlueTeamInput;

    }

    public static boolean recallTeam() {

        return isBlueTeam;

    }


}
