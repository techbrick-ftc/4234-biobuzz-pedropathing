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
import org.firstinspires.ftc.teamcode.pedro.Constants;

@Autonomous(name = "Red Flower Side Test Auto 1")
public class RdFlTestAuto1 extends OpMode {
    private Follower follower;
    private final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose start = poseFactory.of(56, 8, 0);
    private final Pose point1 = poseFactory.of(56.6583, 23.6479, 0);
    private final Pose path1 = poseFactory.of(97.0695, 126.9396, -11.0891);
    private final Pose path1Control1 = poseFactory.of(0, 48.0832, 0);
    private final Pose path1Control2 = poseFactory.of(22.78, 141.5, 0);

    public Path path1() {
        return Paths.line(start, point1).linear(start, point1);
    }

    public Path path1_2() {
        return Paths.curve(point1, path1Control1, path1Control2, path1).tangent();
    }

    public Command autoRoutine() {
        return follow(follower, path1())
                .then(follow(follower, path1_2()));
    }

    @Override
    public void init() {
        Scheduler.reset();

        follower = Constants.create(hardwareMap);
        follower.setPose(start);
    }

    @Override
    public void start() {
        schedule(autoRoutine());
    }

    @Override
    public void loop() {
        follower.update();
        Scheduler.execute();
        // add your other methods needed in the loop here

        telemetry.addData("X", follower.pose().x());
        telemetry.addData("Y", follower.pose().y());
        telemetry.addData("Heading", Math.toDegrees(follower.pose().heading()));
        telemetry.addData("Follower Mode", follower.mode());
        telemetry.update();
    }

}
