# Info
The following code is for use by Team #4234 in the 2026-2027 FTC season BioBuzz. It was forked from the Pedro-Pathing quickstart repo and includes GoBilda Pinpoint code and drivers.

## Software & Library Versions
* **FTC SDK Version:** `11.2.1` (`org.firstinspires.ftc:RobotCore:11.2.1`, `Hardware:11.2.1`, `Vision:11.2.1`, `FtcCommon:11.2.1`, `Inspection:11.2.1`, `Blocks:11.2.1`, `OnBotJava:11.2.1`, `RobotServer:11.2.1`)
* **Pedro-Pathing Autonomous Suite:**
  * `com.pedropathing.ivy:pedro:1.1.1` (Pedro Pathing Ivy command-based framework)
  * `com.pedropathing:revhub:3.0.0`
  * `com.pedropathing:tuning:1.0.0`
* **GoBilda Pinpoint Driver & SDK:** Uses native FTC SDK driver (`com.qualcomm.hardware.gobilda.GoBildaPinpointDriver`) integrated into `com.pedropathing.revhub.localizers.PinpointLocalizer`.
* **Android Target/Compile Configuration:** `compileSdk 34`, `targetSdkVersion 28`, `minSdkVersion 24`, NDK `21.3.6528147`, Java 8 compatibility (`JavaVersion.VERSION_1_8`).

---

# Hardware
The robot hardware is defined as follows:

Four drivetrain motors in a mecanum holonomic drivetrain (frontRight, frontLeft, backRight, backLeft)
* `frontRight` uses the `FORWARD` direction and is connected to its mecanum wheel through a bevel gear.
* `frontLeft` uses the `REVERSE` direction and is connected to its mecanum wheel through a bevel gear.
* `backRight` uses the `FORWARD` direction and is connected to its mecanum wheel directly.
* `backLeft` uses the `REVERSE` direction and is connected to its mecanum wheel directly.

One motor for the intake and transfer (not yet included in the code)

One GoBilda Huskylens camera using onboard models for Apriltag ID

One Gobilda Pinpoint module including two dead odometry wheels and one PinPoint gyro module.

## Detailed Hardware Map & Device Configuration Catalog

| Device Name (Exact String) | Hardware Class | Direction | Zero Power Behavior | Run Mode | Ports / Hardware Details |
|---|---|---|---|---|---|
| `"frontRight"` | `DcMotorEx` | `FORWARD` | `BRAKE` | `RUN_WITHOUT_ENCODER` | Mecanum drivetrain (bevel gear) |
| `"frontLeft"` | `DcMotorEx` | `REVERSE` | `BRAKE` | `RUN_WITHOUT_ENCODER` | Mecanum drivetrain (bevel gear) |
| `"backRight"` | `DcMotorEx` | `FORWARD` | `BRAKE` | `RUN_WITHOUT_ENCODER` | Mecanum drivetrain (direct mount) |
| `"backLeft"` | `DcMotorEx` | `REVERSE` | `BRAKE` | `RUN_WITHOUT_ENCODER` | Mecanum drivetrain (direct mount) |
| `"pinpoint"` | `GoBildaPinpointDriver` | N/A | N/A | N/A | REV Control Hub I2C Bus 0 (with runtime fallback) |
| `"turret"` | `DcMotorEx` | `FORWARD` | `BRAKE` | `RUN_TO_POSITION` | Turret rotation (Gear ratio: `2.75:1`, `14` ticks/rad) |
| `"flywheel"` | `DcMotorEx` | `FORWARD` | `FLOAT` | `RUN_USING_ENCODER` | Shooter Flywheel (Max `6000 RPM`, `70°` launch angle, $85\%$ efficiency) |
| `"huskylens"` | `HuskyLens` | N/A | N/A | N/A | Computer Vision ($320 \times 240$ resolution, center $X=160$) |
| `"intake"` *(planned)* | `DcMotorEx` / `CRServo` | `FORWARD` | `BRAKE` | N/A | `SubIntake` mechanism (planned addition) |

### GoBilda Pinpoint Odometry & Mounting Specs
* **Odometry Pod Type:** `GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD` (`goBILDA_4_BAR_POD`).
* **X Pod Offset (Forward/Backward):** `-7.472082423412894` inches (negative = behind robot rotation center).
* **Y Pod Offset (Left/Right Strafe):** `1.8371110450564407` inches (positive = forward of robot rotation center).
* **X Pod Direction:** `GoBildaPinpointDriver.EncoderDirection.REVERSED`
* **Y Pod Direction:** `GoBildaPinpointDriver.EncoderDirection.REVERSED`
* **Distance & Offset Units:** `DistanceUnit.INCH`

### DFRobot HuskyLens Camera Specs
* **Resolution:** $320 \times 240$ pixels
* **Screen Center X Coordinate (`SCREEN_CENTER_X`):** `160`
* **Supported Modes:** `APRILTAG` (`HuskyLens.Algorithm.TAG_RECOGNITION`), `COLOR` (`HuskyLens.Algorithm.COLOR_RECOGNITION`)
* **API Usage:** Call `update()` once per main loop iteration; query `getLargestBlock()` or `getTargetXError()`.

---

# AprilTag Family, ID Mapping & Vision System Specifications

## AprilTag Standards & Tag Family
* **AprilTag Family:** `36h11` tag family (`AprilTagProcessor.TagFamily.TAG_36h11` / HuskyLens `TAG_RECOGNITION`).
* **Default Target Tag ID:** `SubVision.TARGET_TAG_ID = 1` (configured in `SubVision.java`).

## FTC Season AprilTag Library ID Reference Table
Below are the standard FTC AprilTag IDs used across season field elements:

| Tag ID Range / ID | Description / Season Field Element | Alliance / Designation |
|---|---|---|
| **`11`** | Red Alliance Submersible / Observation Zone Tag | **Red Alliance** |
| **`12`** | Red Alliance Submersible Main Target Tag | **Red Alliance** |
| **`13`** | Red Alliance Net Zone / Sample Goal Tag | **Red Alliance** |
| **`14`** | Blue Alliance Submersible / Observation Zone Tag | **Blue Alliance** |
| **`15`** | Blue Alliance Submersible Main Target Tag | **Blue Alliance** |
| **`16`** | Blue Alliance Net Zone / Sample Goal Tag | **Blue Alliance** |
| **`1` – `3`** | Left / Center / Right Backstage Target Tags | **Blue Alliance** *(Legacy/Standard)* |
| **`4` – `6`** | Left / Center / Right Backstage Target Tags | **Red Alliance** *(Legacy/Standard)* |
| **`7` – `10`** | Audience Wall Alignment Target Tags | **Neutral / Field Wall** |

## HuskyLens AprilTag Integration Details
* **Onboard Tag Processing:** HuskyLens detects 36h11 family AprilTags natively using internal vision hardware.
* **Bounding Box Output Fields (`HuskyLens.Block`):**
  * `block.id`: Detected Tag ID (integer, matching table above).
  * `block.x` & `block.y`: Bounding box center coordinates on screen.
  * `block.width` & `block.height`: Bounding box pixel size.
* **Targeting Math:** Horizontal pixel error is calculated as `getTargetXError() = block.x - 160` (positive = target is right of center).

---

# Pedro Pathing File Directory Reference

All Pedro Pathing configuration, tuning, and procedure files are located within the `pedro` package:

| Purpose / Functionality | Absolute Project File Path | Class Name / Key Exports |
|---|---|---|
| **Core Configuration & Tuned Constants** | `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/pedro/Constants.java` | `Constants` (`drivetrainConfig`, `localizerConfig`, `foresightConfig`, `Follower create(HardwareMap)`) |
| **Dashboard Autotuner Registry** | `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/pedro/Tuning.java` | `Tuning` (`mecanumTuner`, `pinpointTuner`, `foresightTuner`, `tests`) |
| **Pinpoint Localizer Tuner** | `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/pedro/procedures/PinpointTuner.java` | `PinpointTuner` (Pod offset, direction, and pod type calibration) |
| **Foresight Trajectory Algorithm Tuner** | `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/pedro/procedures/ForesightTuner.java` | `ForesightTuner` (Translational gain matrices, feedforward $k_V$, braking coefficients) |
| **Mecanum Kinematics Tuner** | `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/pedro/procedures/MecanumTuner.java` | `MecanumTuner` (Chassis direction & power mapping) |
| **OTOS Sensor Tuner** | `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/pedro/procedures/OTOSTuner.java` | `OTOSTuner` |
| **OctoQuad Sensor Tuner** | `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/pedro/procedures/OctoQuadTuner.java` | `OctoQuadTuner` |
| **Odometry Pod Tuners** | `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/pedro/procedures/TwoWheelTuner.java`<br>`TeamCode/src/main/java/org/firstinspires/ftc/teamcode/pedro/procedures/ThreeWheelTuner.java`<br>`TeamCode/src/main/java/org/firstinspires/ftc/teamcode/pedro/procedures/ThreeWheelIMUTuner.java` | `TwoWheelTuner`<br>`ThreeWheelTuner`<br>`ThreeWheelIMUTuner` |
| **Trajectory Validation Tests** | `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/pedro/procedures/Tests.java` | `Tests` |
| **Autonomous Routines** | `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/Autos/RdFlTestAuto1.java`<br>`TeamCode/src/main/java/org/firstinspires/ftc/teamcode/Autos/RdFlTestAuto2.java` | `RdFlTestAuto1`<br>`RdFlTestAuto2` (Pedro Ivy `Scheduler`, `PedroCommands.follow()`, `Paths.line()`, `Paths.curve()`) |

---

# Subsystem Architecture & Implementation Notes

> [!IMPORTANT]
> **Subsystem Anomalies & Implementation Notes for AI Agents:**
> 1. **`SubTurret.java` Flywheel Power Bug:** In `SubTurret.Set()`, line 74 currently calls `setTurretPower(flywheelTargetRPM)` which erroneously sets the `turretRotation` motor power to the flywheel's calculated RPM (clamped to 1.0) instead of invoking `setFlywheelVelocity()` or `setFlywheelPower()`. When modifying or refactoring `SubTurret`, call `setFlywheelVelocity(flywheelTargetRPM)` or `setFlywheelPower(...)`.
> 2. **Trajectory Calculation Radial Distance:** `SubTurret.Set()` line 67 uses 2D $(\text{targetX} - \text{robotX})$ directly in the projectile launch velocity formula instead of radial ground distance $d = \sqrt{(\Delta X)^2 + (\Delta Y)^2}$ and uses $\Delta Y$ instead of vertical target height differential $\Delta Z = (\text{TARGET\_HEIGHT\_GLOBAL} - \text{SHOOTER\_HEIGHT})$.
> 3. **Heading Lock Coasting/Settling Protection:** `SubDrivetrain.computeRotation()` checks angular velocity (`pinpoint.getHeadingVelocity()`). When the turn joystick returns to zero, the controller waits until heading velocity drops below $0.3$ rad/s (`SETTLE_VELOCITY`) before capturing `targetHeading = currentHeading`, preventing the robot from snapping back while coasting.

---

# TeleOP & OpMode Architecture
The primary TeleOP is `@TeleOPMain`, which uses `LinearOpMode`.

## TeleOp Architecture & State Persistence
* **Loop Structure:** Single-threaded active while-loop (`while (opModeIsActive())`).
* **Primary TeleOp vs Diagnostics:** `@TeleOPMain` is the primary competition TeleOp. `Testing.java` is a diagnostic testing OpMode.
* **Cross-OpMode State Persistence:** Persistent alliance color and seesaw status are preserved between Autonomous and TeleOp using static methods in `SubData` (`SubData.isBlueTeam`, `SubData.setTeamColor()`, `SubData.recallTeam()`, `SubData.setSeesawHumanPlayerSideUp()`).
* **Subsystem Modules:** `SubDrivetrain`, `SubTurret`, `SubVision`, `SubData`, and Pedro Pathing `Follower`.

## Telemetry
All TeleOP telemetry while the robot is running the main code loop is located at the end of the file and separated into blocks based on which driver the information is relevant to, or whether it is just there for debugging.

```java
// Example TeleOp Telemetry Structure
telemetry.addLine("Gamepad 1 Controls -----------------------------");
telemetry.addData("Slow Mode", slowMode ? "ON ([DPAD_UP] to disable)" : "OFF");
telemetry.addLine();
telemetry.addLine("Gamepad 2 Controls -----------------------------");
telemetry.addData("Field Centric", fieldCentricActive ? "ON" : "OFF");
telemetry.addLine();
telemetry.addLine("Info -----------------------------");
telemetry.addData("Robot Pose X", "%.2f", robotPose.x());
telemetry.update();
```

## Driver Gamepad Mappings

### Gamepad 1 (Chassis & Driver Operations)
* **Left Stick (X / Y):** Translation control ($v_x, v_y$).
* **Right Stick (X):** Rotation control ($\omega$).
* **DPAD UP:** Set Full Drive Power (`1.0`).
* **DPAD LEFT / RIGHT:** Set Slow Drive Power (`0.6`).
* **DPAD DOWN:** Set Crawl Drive Power (`0.3`).
* **Left Stick Button + Right Stick Button:** Reset IMU heading offset (`drive.resetHeading()`).
* **X Button:** Toggle Active Heading Lock ON/OFF (`drive.setHeadingLock()`).
* **Y Button:** Set Seesaw Human Player Side State to UP (`seesawHumanPlayerSideIsUp = true`).
* **B Button:** Set Seesaw Human Player Side State to DOWN (`seesawHumanPlayerSideIsUp = false`).

### Gamepad 2 (Scoring & Alliance Settings)
* **Left Bumper + Right Bumper:** Toggle Field-Centric vs Robot-Centric drive mode.
* **B Button:** Set Alliance Team Color to RED (`SubData.setTeamColor(false)`).
* **X Button:** Set Alliance Team Color to BLUE (`SubData.setTeamColor(true)`).

---

# Control Mathematics & Kinematics

## 1. Holonomic Field-Relative Mecanum Drive Kinematics
Translational velocities $(v_x, v_y)$ and rotational velocity $\omega$ are transformed from field-relative to robot-frame using the current IMU angle $\theta$:
$$x_{\text{robot}} = v_x \cos(-\theta) - v_y \sin(-\theta)$$
$$y_{\text{robot}} = v_y \cos(-\theta) + v_x \sin(-\theta)$$

Wheel power distribution and normalization:
$$\text{denominator} = \max(|y_{\text{robot}}| + |x_{\text{robot}}| + |\omega|, 1.0)$$
$$P_{\text{FL}} = \frac{y_{\text{robot}} + x_{\text{robot}} + \omega}{\text{denominator}} \times P_{\text{multiplier}}$$
$$P_{\text{BL}} = \frac{y_{\text{robot}} - x_{\text{robot}} + \omega}{\text{denominator}} \times P_{\text{multiplier}}$$
$$P_{\text{FR}} = \frac{y_{\text{robot}} - x_{\text{robot}} - \omega}{\text{denominator}} \times P_{\text{multiplier}}$$
$$P_{\text{BR}} = \frac{y_{\text{robot}} + x_{\text{robot}} - \omega}{\text{denominator}} \times P_{\text{multiplier}}$$

## 2. Active Heading Lock PD Controller
* **Tuning Parameters:** $K_p = 0.8$ (`HEADING_KP`), $K_d = 0.05$ (`HEADING_KD`), deadzone $= 0.05$, settle threshold $= 0.3$ rad/s (`SETTLE_VELOCITY`), max correction $= 0.5$.
* **Error Normalization:** $\text{error} = \text{normalizeRadians}(\text{targetHeading} - \text{currentHeading})$
* **PD Formula:** $\text{correction} = \text{clamp}(-0.5, 0.5, K_p \cdot \text{error} - K_d \cdot \omega_{\text{heading}})$

## 3. Turret Targeting & Ballistic Flywheel Trajectory
Given robot pose $(X_{\text{robot}}, Y_{\text{robot}}, \theta_{\text{heading}})$ and target position $(X_{\text{target}}, Y_{\text{target}})$:
* **Radial Ground Distance to Target:** $d = \sqrt{(X_{\text{target}} - X_{\text{robot}})^2 + (Y_{\text{target}} - Y_{\text{robot}})^2}$
* **Turret Field Angle:** $\theta_{\text{field}} = \text{atan2}(Y_{\text{target}} - Y_{\text{robot}}, X_{\text{target}} - X_{\text{robot}})$
* **Turret Robot Angle:** $\theta_{\text{turret, robot}} = \text{normalizeRadians}(\theta_{\text{field}} - \theta_{\text{heading}})$
* **Turret Ticks Conversion:** $\text{ticksPerRad} = 2.75 \times 14.0 = 38.5 \text{ ticks/rad}$
* **Ballistic Launch Velocity ($70^\circ$ Elevation Angle, $g = 32.2 \text{ ft/s}^2$):**
  $$v_0 = \sqrt{\frac{32.2 \cdot d^2}{2 \cos^2(70^\circ) \left( d \tan(70^\circ) - \Delta Z \right)}}$$
  $$\text{RPM} = \left( \frac{v_0}{2\pi \cdot (2 / 12)} \right) \times 60.0$$

---

# Field Coordinate System & Goal Locations

All coordinates are defined in Pedro Pathing field relative inches:

| Target Feature | Alliance Color | Seesaw State | Target X (in) | Target Y (in) | Target Height (in) |
|---|---|---|---|---|---|
| Goal Target 1 | **Red** | Human Player Side UP | `58.8` | `57.6` | `34.0` |
| Goal Target 2 | **Red** | Human Player Side DOWN | `58.8` | `86.4` | `34.0` |
| Goal Target 3 | **Blue** | Human Player Side UP | `85.2` | `86.4` | `34.0` |
| Goal Target 4 | **Blue** | Human Player Side DOWN | `85.2` | `57.6` | `34.0` |

### Autonomous Starting Poses:
* **`RdFlTestAuto1`:** Start Pose $= (X: 56.0, Y: 8.0, \text{Heading}: 0.0^\circ)$
* **`RdFlTestAuto2`:** Start Pose $= (X: 56.0, Y: 8.0, \text{Heading}: 90.0^\circ)$

---

# Pedro Pathing Configuration ([Constants.java](file:///Users/user/StudioProjects/4234-biobuzz-pedropathing/TeamCode/src/main/java/org/firstinspires/ftc/teamcode/pedro/Constants.java))

```java
public static PinpointConfig localizerConfig = new PinpointConfig(c -> {
    c.name.set("pinpoint");
    c.podType.set(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
    c.xPodOffset.set(-7.472082423412894);
    c.yPodOffset.set(1.8371110450564407);
    c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED);
    c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED);
    c.globalDistanceUnit.set(DistanceUnit.INCH);
    c.offsetUnits.set(DistanceUnit.INCH);
});

public static ForesightConfig foresightConfig = new ForesightConfig(c -> {
    Controller primaryTranslationalForward = Controller.proportional(18.006759687465294);
    Controller secondaryTranslationalForward = Controller.proportional(6.65301655503812);
    Controller primaryTranslationalLateral = Controller.proportional(0.5684978920379179);
    Controller secondaryTranslationalLateral = Controller.proportional(0.21004478056456719);

    c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
    c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));

    c.coast.set(Controller.proportionalFeedforward(0.012959217645713771));
    c.brake.set(Controller.proportionalFeedforward(0.011015334998856706));

    c.headingFeedback.set(Controller.proportional(233.57996942580033));
    c.headingBrakeCoefficients.set(Vector2D.cartesian(2.237963189587226, -0.16131697764580916));

    c.linearBrakeCoefficients.set(Matrix.diag(0.007597656366109585, 0.917657235835212));
    c.quadraticBrakeCoefficients.set(Matrix.diag(0.0035907030340410614, -0.012269390806340414));

    c.maxAchievableForwardVelocity.set(78.19177022232485);
    c.maxAchievableStrafeVelocity.set(22.517118509807016);
    c.naturalForwardDeceleration.set(32.21711602906132);
    c.naturalStrafeDeceleration.set(37.637190748517945);
});
```

---

# Style Guidelines

## For Variables
Constants are to be formatted in all caps, with underscores "_" between words. (SCREAMING_SNAKE_CASE)

* Do: `THIS_IS_A_CONSTANT`
* Don't: `this_Is_A_Constant`, `ThisIsAConstant`, `thisISACONSTANT`, `THISISACONSTANT`, `thisIsAConstant`

Variables are to be formatted with the first letter of all words after the first word capitalized. (pascalCase)
* Do: `thisIsAVariable`
* Don't: `THISISAVARIABLE`, `ThisIsAVariable`, `thisisavariable`, `this_Is_A_Variable`

## Comments
A single comment line may proceed a logical block of code, where needed.

## Spacing
Code is to be separated into logical blocks. One enter after each logical block, two enters may be used in some cases to differenciate code of entirely different functions or a transition between modes (e.g. when switching between init code and the TeleOP loop)
* Do: <br>
boolean example1 = false; <br>
boolean example2 = false; <br>
boolean example3 = false; <br><br>
while (elapsedTime =< 5) { <br>
     boolean example1 = (Math.random()) < 0.5 ? true : false);
}

## Horizontal Alignment
Horizontal Alignment may be used to align "=" characters in areas where at least three objects are being declared or assigned.

* Do (Optional)

boolean example1   = false;
boolean isActiveR  = true;
boolean opticOn    = false;
