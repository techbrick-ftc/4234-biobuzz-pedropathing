package org.firstinspires.ftc.teamcode.SubSystems;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.UnnormalizedAngleUnit;

import java.util.List;

public class SubDrivetrain {

    private final DcMotorEx frontRight;
    private final DcMotorEx frontLeft;
    private final DcMotorEx backRight;
    private final DcMotorEx backLeft;
    private final GoBildaPinpointDriver pinpoint;

    private double offset;

    // Heading lock tuning (radians). Tune KP first with KD = 0, then add KD to stop overshoot.
    public static double HEADING_KP = 0.8;
    public static double HEADING_KD = 0.05;
    public static double TURN_DEADZONE = 0.05;
    public static double SETTLE_VELOCITY = 0.3; // rad/s
    public static double MAX_CORRECTION = 0.5;

    private boolean headingLockEnabled = true;
    private boolean headingLocked = false;
    private double targetHeading = 0;

    public SubDrivetrain(HardwareMap hardwareMap, final double offsetIMU) {
        this(hardwareMap, "pinpoint", offsetIMU);
    }

    public SubDrivetrain(HardwareMap hardwareMap, String pinpointHardwareName, final double offsetIMU) {
        // Motor Definitions
        frontRight = hardwareMap.get(DcMotorEx.class, "frontRight");
        frontLeft = hardwareMap.get(DcMotorEx.class, "frontLeft");
        backRight = hardwareMap.get(DcMotorEx.class, "backRight");
        backLeft = hardwareMap.get(DcMotorEx.class, "backLeft");

        // Directions
        frontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        // Zero Power Behavior
        frontRight.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        frontLeft.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

        // Run Modes
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        offset = offsetIMU;

        // Initialize GoBilda Pinpoint Computer with fallback lookup
        GoBildaPinpointDriver tempPinpoint;
        try {
            tempPinpoint = hardwareMap.get(GoBildaPinpointDriver.class, pinpointHardwareName);
        } catch (Exception e) {
            List<GoBildaPinpointDriver> pinpoints = hardwareMap.getAll(GoBildaPinpointDriver.class);
            if (!pinpoints.isEmpty()) {
                tempPinpoint = pinpoints.get(0);
            } else {
                throw new IllegalArgumentException(
                    "Could not find a goBILDA Pinpoint driver in your active Robot Configuration.\n" +
                    "Please open Driver Station -> Configure Robot -> Control Hub -> I2C Bus 0,\n" +
                    "ensure 'goBILDA® Pinpoint Odometry Computer' is added, set a name, and press SAVE & ACTIVATE.", e
                );
            }
        }
        pinpoint = tempPinpoint;
        pinpoint.resetPosAndIMU();
    }

    public void drive(double xMovement, double yMovement, double rotation, double powerMultiplier, boolean fieldCentric) {
        if ((Math.abs(xMovement) >= TURN_DEADZONE) || (Math.abs(yMovement) >= TURN_DEADZONE) || (Math.abs(rotation) >= TURN_DEADZONE)) {

            double angle = getImu();
            rotation = computeRotation(rotation, angle);

            double x = fieldCentric ? (xMovement * Math.cos(-angle) - yMovement * Math.sin(-angle)) : xMovement;
            double y = fieldCentric ? (yMovement * Math.cos(-angle) + xMovement * Math.sin(-angle)) : yMovement;

            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rotation), 1.0);

            double frontLeftPower  = (y + x + rotation) / denominator;
            double backLeftPower   = (y - x + rotation) / denominator;
            double frontRightPower = (y - x - rotation) / denominator;
            double backRightPower  = (y + x - rotation) / denominator;

            frontLeft.setPower(frontLeftPower * powerMultiplier);
            backLeft.setPower(backLeftPower * powerMultiplier);
            frontRight.setPower(frontRightPower * powerMultiplier);
            backRight.setPower(backRightPower * powerMultiplier);

        } else {
            // Robot idle: drop the lock so it doesn't fight being repositioned
            headingLocked = false;
            stop();
        }
    }

    // Heading lock: hold the heading the robot settled at once the driver releases the turn stick
    private double computeRotation(double stickRotation, double currentHeading) {
        if (!headingLockEnabled || Math.abs(stickRotation) >= TURN_DEADZONE) {
            headingLocked = false;
            return stickRotation;
        }

        double headingVelocity = pinpoint.getHeadingVelocity(UnnormalizedAngleUnit.RADIANS);

        // Wait for the robot to stop coasting before capturing the target, so it doesn't snap back
        if (!headingLocked) {
            if (Math.abs(headingVelocity) > SETTLE_VELOCITY) return 0;
            targetHeading = currentHeading;
            headingLocked = true;
        }

        // Wrapped to [-PI, PI] so it always corrects the short way around
        double error = AngleUnit.normalizeRadians(targetHeading - currentHeading);
        double correction = HEADING_KP * error - HEADING_KD * headingVelocity;
        correction = Math.max(-MAX_CORRECTION, Math.min(MAX_CORRECTION, correction));

        // Positive rotation turns clockwise while heading increases counter-clockwise, so negate
        return -correction;
    }

    public void setHeadingLock(boolean enabled) {
        headingLockEnabled = enabled;
        if (!enabled) headingLocked = false;
    }

    public boolean isHeadingLockEnabled() {
        return headingLockEnabled;
    }

    public boolean isHeadingLocked() {
        return headingLocked;
    }

    public double getTargetHeading() {
        return targetHeading;
    }

    public double getHeadingError() {
        return headingLocked ? AngleUnit.normalizeRadians(targetHeading - getImu()) : 0;
    }

    public void To(double X_Movement, double Y_Movement, double Rotation, double powerMultiplier, boolean fieldCentric) {
        drive(X_Movement, Y_Movement, Rotation, powerMultiplier, fieldCentric);
    }

    public void stop() {
        frontRight.setPower(0);
        frontLeft.setPower(0);
        backRight.setPower(0);
        backLeft.setPower(0);
    }

    public double getImu() {
        return AngleUnit.normalizeRadians(getRawImu() - offset);
    }

    public double getRawImu() {
        pinpoint.update();
        return pinpoint.getHeading(AngleUnit.RADIANS);
    }

    public GoBildaPinpointDriver getPinpoint() {
        return pinpoint;
    }

    public void setOffset(double off) {
        offset = off;
        headingLocked = false;
    }

    public void resetHeading() {
        offset = getRawImu();
        headingLocked = false;
    }

    public void recalibrate() {
        resetHeading();
    }
}
