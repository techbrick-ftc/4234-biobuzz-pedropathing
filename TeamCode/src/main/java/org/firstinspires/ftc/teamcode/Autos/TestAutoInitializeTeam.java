package org.firstinspires.ftc.teamcode.Autos;

import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;
import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.SubSystems.SubData;
import org.firstinspires.ftc.teamcode.pedro.Constants;

@Autonomous(name = "Testing Init Team as Red")
public class TestAutoInitializeTeam extends OpMode {

    SubData data = null;

    @Override
    public void init() {

        data = new SubData();
        SubData.setTeamColor(false); // Set team color to Red (isBlueTeam = false)

        telemetry.addData("Status", "Initialized Team as Red");
        telemetry.addData("Team Color (isBlueTeam)", SubData.recallTeam());
        telemetry.update();

    }

    @Override
    public void start() {
        SubData.setTeamColor(false);
    }

    @Override
    public void loop() {
        telemetry.addData("Status", "Running Auto - Team Data Saved");
        telemetry.addData("Team Color (isBlueTeam)", SubData.recallTeam());
        telemetry.update();
    }

}
