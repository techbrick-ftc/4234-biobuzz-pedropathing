package org.firstinspires.ftc.teamcode.TeleOPs;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.SubSystems.SubDrivetrain;

@TeleOp(name = "Testing TeleOp")
public class Testing extends LinearOpMode {

    // Define constants
    final double DRIVE_DEFAULT_POWER = 1.0;
    final double DRIVE_SLOW_POWER    = 0.4;

    // Drive variables
    boolean slowMode = false;
    double drivePow = DRIVE_DEFAULT_POWER;
    boolean fieldCentricActive = true;

    boolean previousB = false;
    boolean previousStickReset = false;

    double xP;
    double yP;
    double rP;

    // Declare drivetrain subsystem
    SubDrivetrain drive = null;

    @Override
    public void runOpMode() throws InterruptedException {

        // Initialize drivetrain subsystem
        drive = new SubDrivetrain(hardwareMap, 0);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        if (isStopRequested()) return;

        while (opModeIsActive()) {

            // Joystick inputs
            xP = gamepad1.left_stick_x;
            yP = -gamepad1.left_stick_y;
            rP = -gamepad1.right_stick_x;

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

            // Drive execution
            drive.drive(xP, yP, rP, drivePow, fieldCentricActive);

            // Telemetry
            telemetry.addData("Slow Mode", slowMode ? "ON ([B] to toggle)" : "OFF ([B] to toggle)");
            telemetry.addData("Heading (radians)", drive.getImu());
            telemetry.addData("Raw Heading (radians)", drive.getRawImu());
            telemetry.addData("Drive Power Multiplier", drivePow);
            telemetry.update();
        }
    }
}

// Developed by Team 4234
