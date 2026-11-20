package org.firstinspires.ftc.teamcode.SubSystems;

public class SubData {

    public static boolean isBlueTeam = false;

    public static void setTeamColor(boolean isBlueTeamInput) {

        isBlueTeam = isBlueTeamInput;

    }

    public static boolean recallTeam() {

        return isBlueTeam;

    }

    public static boolean isSeesawHumanPlayerSideUp = false;

    public static void setSeesawHumanPlayerSideUp(boolean isUp) {
        isSeesawHumanPlayerSideUp = isUp;
    }

    public static boolean recallSeesawHumanPlayerSideUp() {
        return isSeesawHumanPlayerSideUp;
    }


}
