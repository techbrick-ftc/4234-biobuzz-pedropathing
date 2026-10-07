package org.firstinspires.ftc.teamcode.SubSystems;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import java.util.List;

public class SubDrivetrain {

    private final DcMotorEx frontRight;
    private final DcMotorEx frontLeft;
    private final DcMotorEx backRight;
    private final DcMotorEx backLeft;
    private final GoBildaPinpointDriver pinpoint;

    private double offset;

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
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        frontLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.FORWARD);

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
        if ((Math.abs(xMovement) >= 0.05) || (Math.abs(yMovement) >= 0.05) || (Math.abs(rotation) >= 0.05)) {

            double angle = getImu();

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
            stop();
        }
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
    }

    public void resetHeading() {
        offset = getRawImu();
    }

    public void recalibrate() {
        resetHeading();
    }
}
