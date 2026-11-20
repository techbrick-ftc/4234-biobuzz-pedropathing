package org.firstinspires.ftc.teamcode.TeleOPs;

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.SubSystems.SubDrivetrain;
import org.firstinspires.ftc.teamcode.SubSystems.SubTurret;
import org.firstinspires.ftc.teamcode.pedro.Constants;

@TeleOp(name = "TeleOP Main")
public class TeleOPMain extends LinearOpMode {

    // Define constants
    final double DRIVE_DEFAULT_POWER = 1.0;
    final double DRIVE_SLOW_POWER    = 0.6;
    final double DRIVE_CRAWL_POWER   = 0.3;

    // Drive variables
    boolean slowMode = false;
    double drivePow = DRIVE_DEFAULT_POWER;
    boolean fieldCentricActive = true;

    boolean previousB = false;
    boolean previousStickReset = false;
    boolean previousBumpers = false;
    boolean previousX = false;

    double xP;
    double yP;
    double rP;

    boolean isBlueTeam = false;

    // Field Information
    boolean seesawHumanPlayerSideIsUp = false;
    double targetX;
    double targetY;

    final double RED_HUMAN_PLAYER_SIDE_TARGET_X = 58.8;
    final double RED_OTHER_SIDE_TARGET_X = 58.8;
    final double RED_HUMAN_PLAYER_SIDE_TARGET_Y = 57.6;
    final double RED_OTHER_SIDE_TARGET_Y = 86.4;

    final double BLUE_HUMAN_PLAYER_SIDE_TARGET_X = 85.2;
    final double BLUE_OTHER_SIDE_TARGET_X = 85.2;
    final double BLUE_HUMAN_PLAYER_SIDE_TARGET_Y = 86.4;
    final double BLUE_OTHER_SIDE_TARGET_Y = 57.6;

    final double TARGET_HEIGHT_GLOBAL = 34; // TODO: Measure


    // Declare drivetrain subsystem, turret, and follower
    SubDrivetrain drive = null;
    SubTurret turret = null;
    Follower follower = null;

    @Override
    public void runOpMode() throws InterruptedException {

        // Initialize subsystems and localizer follower
        drive = new SubDrivetrain(hardwareMap, 0);
        turret = new SubTurret(hardwareMap, "pinpoint", !isBlueTeam);
        follower = Constants.create(hardwareMap);
        follower.setPose(new Pose(0, 0, 0));

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        if (isStopRequested()) return;

        while (opModeIsActive()) {

            // Update Pedro Pathing Pinpoint localizer frame
            follower.update();
            Pose robotPose = follower.pose();

            // Input Handling and Drivetrain

                // Joystick inputs
                xP = gamepad1.left_stick_x;
                yP = -gamepad1.left_stick_y;
                rP = gamepad1.right_stick_x;

                // Slow mode toggle (B button edge detection)
                boolean currentB = gamepad1.b;
                if (currentB && !previousB) {
                    slowMode = !slowMode;
                    drivePow = slowMode ? DRIVE_SLOW_POWER : DRIVE_DEFAULT_POWER;
                }
                previousB = currentB;

                // Reset heading / recalibrate IMU (both stick buttons pressed edge detection)
                boolean currentStickReset = gamepad1.left_stick_button && gamepad1.right_stick_button;
                if (currentStickReset && !previousStickReset) {
                    drive.resetHeading();
                }
                previousStickReset = currentStickReset;


                // Field / robot centric toggle (both bumpers pressed edge detection)
                boolean currentBumpers = gamepad2.left_bumper && gamepad2.right_bumper;
                if (currentBumpers && !previousBumpers) {
                    fieldCentricActive = !fieldCentricActive;
                }
                previousBumpers = currentBumpers;


                // Heading lock toggle (X button edge detection)
                boolean currentX = gamepad1.x;
                if (currentX && !previousX) {
                    drive.setHeadingLock(!drive.isHeadingLockEnabled());
                }
                previousX = currentX;


                // Seesaw Toggle (B / Y)
                if (gamepad1.b) {
                    seesawHumanPlayerSideIsUp = false;
                } else if (gamepad1.y) {
                    seesawHumanPlayerSideIsUp = true;
                }


                // Drive execution
                drive.drive(xP, yP, rP, drivePow, fieldCentricActive);

                // Turret and Flywheel target coordinates
                if (isBlueTeam) {
                    if (seesawHumanPlayerSideIsUp) {
                        targetX = BLUE_HUMAN_PLAYER_SIDE_TARGET_X;
                        targetY = BLUE_HUMAN_PLAYER_SIDE_TARGET_Y;
                    } else {
                        targetX = BLUE_OTHER_SIDE_TARGET_X;
                        targetY = BLUE_OTHER_SIDE_TARGET_Y;
                    }
                } else {
                    if (seesawHumanPlayerSideIsUp) {
                        targetX = RED_HUMAN_PLAYER_SIDE_TARGET_X;
                        targetY = RED_HUMAN_PLAYER_SIDE_TARGET_Y;
                    } else {
                        targetX = RED_OTHER_SIDE_TARGET_X;
                        targetY = RED_OTHER_SIDE_TARGET_Y;
                    }
                }

                // Update turret position using current robot pose (x, y, heading) and target coordinates (targetX, targetY)
                double distanceToTarget = turret.autoSet(
                    robotPose.x(),
                    robotPose.y(),
                    robotPose.heading(),
                    targetX,
                    targetY
                );


            // Telemetry

                // Gamepad 1 Controls
                telemetry.addLine("Gamepad 1 Controls -----------------------------");
                telemetry.addData("Slow Mode", slowMode ? "ON ([DPAD_UP] to disable)" : "OFF ([DPAD_R/L/D] to enable)");
                telemetry.addLine();

                // Gamepad 2 Controls
                telemetry.addLine("Gamepad 2 Controls -----------------------------");
                telemetry.addData("Field Centric", fieldCentricActive ? "ON ([LEFT_STICK] + [RIGHT_STICK] to disable)]" : "OFF ([LEFT_STICK] + [RIGHT_STICK] to enable)]");
                telemetry.addLine();

                // Information
                telemetry.addLine("Info -----------------------------");
                telemetry.addData("Seesaw", seesawHumanPlayerSideIsUp ? "Human player side up. Press [B] to change" : "Human player side down. Press [Y] to change");
                telemetry.addData("Robot Pose X", "%.2f", robotPose.x());
                telemetry.addData("Robot Pose Y", "%.2f", robotPose.y());
                telemetry.addData("Robot Heading (deg)", "%.2f", Math.toDegrees(robotPose.heading()));
                telemetry.addData("Target Pose", "X: %.1f, Y: %.1f", targetX, targetY);
                telemetry.addData("Distance to Target", "%.2f", distanceToTarget);
                telemetry.addData("Turret Target Angle (deg)", "%.2f", turret.getTurretTargetAngle(AngleUnit.DEGREES));
                telemetry.addData("Turret Current Angle (deg)", "%.2f", turret.getTurretCurrentAngle(AngleUnit.DEGREES));
                telemetry.addData("Raw Heading (radians)", drive.getRawImu());
                telemetry.addData("Drive Power Multiplier", drivePow);

                telemetry.update();
        }
    }
}

// Developed by Team 4234
