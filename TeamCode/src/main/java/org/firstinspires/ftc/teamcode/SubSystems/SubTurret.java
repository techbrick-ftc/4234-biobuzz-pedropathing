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
    public static final double TURRET_MOTOR_RPM = 435;
    public static final double TURRET_GEAR_RATIO = 2.75;
    public static final double TURRET_TICKS_PER_RADIAN = 14;
    public static final double DEFAULT_TURRET_POWER = 0.8;
    public static final int TARGET_TOLERANCE_TICKS = 10;

    // Flywheel Info
    final double SHOOTING_ANGLE_DEGREES = 60;
    final double FLYWHEEL_DIAMETER_INCHES = 5;
    final double FLYWHEEL_EFFICIENCY = 0.85;
    final double SHOOTER_ANGLE = 70;


    private int turretTargetAngle = 0;
    private double turretPower = DEFAULT_TURRET_POWER;
    private double lastDistanceToTarget = 0;
    private final boolean isRedTeam;

    public SubTurret(HardwareMap hardwareMap) {
        this(hardwareMap, "pinpoint", false);
    }

    public SubTurret(HardwareMap hardwareMap, String pinpointHardwareName, final boolean IS_RED_TEAM) {
        this.isRedTeam = IS_RED_TEAM;

        // Motor Definitions
        turretRotation = hardwareMap.get(DcMotorEx.class, "turret");
        flyWheel = hardwareMap.get(DcMotorEx.class, "flywheel");

        // Directions [TODO: Change once motors installed]
        turretRotation.setDirection(DcMotorSimple.Direction.FORWARD);
        flyWheel.setDirection(DcMotorSimple.Direction.FORWARD);

        // Zero Power Behavior
        turretRotation.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        flyWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        // Run Modes
        turretRotation.setTargetPosition(0);
        turretRotation.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        turretRotation.setPower(turretPower);

        flyWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER); // TODO: See if with/without encoder is better.
    }

    /**
     * Sets the target turret angle in radians.
     * Performs continuous angle unwrapping relative to current turret position
     * to take the shortest path and prevent ~360 degree spin flips near [-PI, PI] boundary.
     *
     * @param targetAngleInRadians Target turret angle in radians
     */
    public void setTargetAngle(double targetAngleInRadians) {
        double ticksPerRad = TURRET_GEAR_RATIO * TURRET_TICKS_PER_RADIAN;
        double currentAngleInRadians = turretRotation.getCurrentPosition() / ticksPerRad;
        double deltaAngle = AngleUnit.normalizeRadians(targetAngleInRadians - currentAngleInRadians);
        double unwrappedTargetAngle = currentAngleInRadians + deltaAngle;

        turretTargetAngle = (int) Math.round(unwrappedTargetAngle * ticksPerRad);
        turretRotation.setTargetPosition(turretTargetAngle);
        turretRotation.setPower(turretPower);
    }

    /**
     * Sets the target turret angle in the specified angle unit.
     *
     * @param targetAngle Target angle value
     * @param unit AngleUnit (RADIANS or DEGREES)
     */
    public void setTargetAngle(double targetAngle, AngleUnit unit) {
        setTargetAngle(unit.toRadians(targetAngle));
    }

    /**
     * Sets the power multiplier for the turret motor.
     *
     * @param power Motor power (0.0 to 1.0)
     */
    public void setTurretPower(double power) {
        this.turretPower = power;
        turretRotation.setPower(power);
    }

    /**
     * Automatically calculates relative target angle and updates turret position.
     *
     * @param robotX Current robot X position
     * @param robotY Current robot Y position
     * @param robotR Current robot heading in radians
     * @param targetX Target field X position
     * @param targetY Target field Y position
     * @return Distance to target
     */
    public double autoSet(double robotX, double robotY, double robotR, double targetX, double targetY) {
        // Calculate Distance
        lastDistanceToTarget = Math.hypot(targetX - robotX, targetY - robotY);

        // Angle of target relative to field at the robot in radians
        double targetAngleRelativeToField = Math.atan2(targetY - robotY, targetX - robotX);

        // Angle of target relative to position and rotation of the robot in radians
        double targetAngleRelativeToRobot = AngleUnit.normalizeRadians(targetAngleRelativeToField - robotR);

        // Set Turret Position
        setTargetAngle(targetAngleRelativeToRobot);

        return lastDistanceToTarget;
    }

    // --- Flywheel Control Methods ---

    public void setFlywheelPower(double power) {
        flyWheel.setPower(power);
    }

    public void setFlywheelVelocity(double velocity) {
        flyWheel.setVelocity(velocity);
    }

    public void stopFlywheel() {
        flyWheel.setPower(0);
    }

    public double getFlywheelVelocity() {
        return flyWheel.getVelocity();
    }

    // --- Getters and Status Methods ---

    public int getTurretTargetPosition() {
        return turretTargetAngle;
    }

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

    public double getLastDistanceToTarget() {
        return lastDistanceToTarget;
    }

    public boolean isRedTeam() {
        return isRedTeam;
    }

    public DcMotorEx getTurretRotation() {
        return turretRotation;
    }

    public DcMotorEx getFlyWheel() {
        return flyWheel;
    }
}
