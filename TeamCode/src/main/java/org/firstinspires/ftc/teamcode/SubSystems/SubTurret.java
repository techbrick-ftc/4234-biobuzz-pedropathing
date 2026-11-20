package org.firstinspires.ftc.teamcode.SubSystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class SubTurret {

    private final DcMotorEx turretRotation;
    private final DcMotorEx flyWheel;

    // Turret Info
    public static final double TURRET_GEAR_RATIO = 2.75;
    public static final double TURRET_TICKS_PER_RADIAN = 14;
    public static final double DEFAULT_TURRET_POWER = 0.8;
    public static final int TARGET_TOLERANCE_TICKS = 10;

    // Flywheel Info
    final double FLYWHEEL_RPM = 6000;

    public static boolean isBlueTeam = SubData.isBlueTeam;

    private int turretTargetAngle = 0;
    private double turretPower = DEFAULT_TURRET_POWER;
    private double distanceToTarget = 0;

    public SubTurret(HardwareMap hardwareMap) {
        isBlueTeam = SubData.recallTeam();

        // Motor Definitions
        turretRotation = hardwareMap.get(DcMotorEx.class, "turret");
        flyWheel = hardwareMap.get(DcMotorEx.class, "flywheel");

        // Directions
        turretRotation.setDirection(DcMotorSimple.Direction.FORWARD);
        flyWheel.setDirection(DcMotorSimple.Direction.FORWARD);

        // Zero Power Behavior
        turretRotation.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        flyWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        // Run Modes
        turretRotation.setTargetPosition(0);
        turretRotation.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        turretRotation.setPower(turretPower);

        flyWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void setTargetAngle(double targetAngleInRadians) {
        double ticksPerRad = TURRET_GEAR_RATIO * TURRET_TICKS_PER_RADIAN;
        double currentAngleInRadians = turretRotation.getCurrentPosition() / ticksPerRad;
        double deltaAngle = AngleUnit.normalizeRadians(targetAngleInRadians - currentAngleInRadians);
        double unwrappedTargetAngle = currentAngleInRadians + deltaAngle;

        turretTargetAngle = (int) Math.round(unwrappedTargetAngle * ticksPerRad);
        turretRotation.setTargetPosition(turretTargetAngle);
        turretRotation.setPower(turretPower);
    }

    public void setTargetRPM(double targetRPM) {
        flyWheel.setPower(targetRPM / FLYWHEEL_RPM);
    }

    public void setTargetAngle(double targetAngle, AngleUnit unit) {
        setTargetAngle(unit.toRadians(targetAngle));
    }

    public void setTurretPower(double power) {
        this.turretPower = power;
        turretRotation.setPower(power);
    }

    public void Set(double robotX, double robotY, double robotR, double targetX, double targetY) {

        // Turret Calculations
        distanceToTarget = Math.hypot(targetX - robotX, targetY - robotY);
        double targetAngleRelativeToField = Math.atan2(targetY - robotY, targetX - robotX);
        double targetAngleRelativeToRobot = AngleUnit.normalizeRadians(targetAngleRelativeToField - robotR);

        // Flywheel Calculations


        setTargetAngle(targetAngleRelativeToRobot);
        setTurretPower(0);

    }

    // Flywheel Control Methods

    public void setFlywheelPower(double power) {
        flyWheel.setPower(power);
    }
    public void setFlywheelVelocity(double velocity) {
        flyWheel.setVelocity(velocity);
    }
    public double getFlywheelVelocity() {
        return flyWheel.getVelocity();
    }

    // Status Methods

    public int getTurretCurrentPosition() {
        return turretRotation.getCurrentPosition();
    }
    public double getTurretTargetAngle(AngleUnit unit) {
        double radians = turretTargetAngle / (TURRET_GEAR_RATIO * TURRET_TICKS_PER_RADIAN);
        return unit.fromRadians(radians);
    }
    public double getTurretCurrentAngle(AngleUnit unit) {
        double radians = getTurretCurrentPosition() / (TURRET_GEAR_RATIO * TURRET_TICKS_PER_RADIAN);
        return unit.fromRadians(radians);
    }
    public boolean isTurretAtTarget() {
        return Math.abs(getTurretCurrentPosition() - turretTargetAngle) <= TARGET_TOLERANCE_TICKS;
    }

    public double getDistanceToTarget() {
        return distanceToTarget;
    }

    public DcMotorEx getTurretRotation() {
        return turretRotation;
    }

    public DcMotorEx getFlyWheel() {
        return flyWheel;
    }
}
