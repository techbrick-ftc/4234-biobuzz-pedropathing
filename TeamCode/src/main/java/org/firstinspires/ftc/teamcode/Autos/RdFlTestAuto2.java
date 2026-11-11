package org.firstinspires.ftc.teamcode.Autos;

// Used for command organization and scheduling instead of state machines.
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

// Pedropathing Imports
import static com.pedropathing.api.Paths.*;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.pedropathing.api.Paths;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.pedro.Constants;

@Autonomous
public class RdFlTestAuto2 extends LinearOpMode {

    // Define and build paths
    private final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose start = poseFactory.of(56, 8, Math.toRadians(90));
    private final Pose center = poseFactory.of(69.8049, 68.4415, Math.toRadians(90));
    private Follower follower;

    public Path startToCenter() {
        return Paths.line(start, center).linear(start, center);
    }

    private Command autoRoutine() {
        return sequential(
                follow(follower, startToCenter())
        );
    }

    public void initialize() {
        Scheduler.reset();

        follower = Constants.create(hardwareMap);
        follower.setPose(start);
        follower.update();
    }

    public void runOpMode() throws InterruptedException {
        initialize();
        waitForStart();

        schedule(autoRoutine());

        while (opModeIsActive()) {
            follower.update();
            Scheduler.execute();
            // add your other methods needed in the loop here
        }

    }
}
