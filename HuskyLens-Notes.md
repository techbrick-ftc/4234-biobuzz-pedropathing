# HuskyLens Notes (`SubVision`)

File: `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/SubSystems/SubVision.java`

The HuskyLens code lives in its own subsystem file, the same way `SubDrivetrain`, `SubFlywheel`, etc. do, so any TeleOp or Auto can use it.

## Modes
- `APRILTAG`: HuskyLens tag recognition
- `COLOR`: HuskyLens color recognition

To add another mode later (for example object tracking), add one line to the `Mode` enum.

## Methods

| Method | What it does |
|---|---|
| `new SubVision(hardwareMap)` | Connects to the camera named `huskylens` in the robot config and starts in `APRILTAG` mode |
| `update()` | Reads from the camera; call it once per loop |
| `setMode(mode)` / `nextMode()` | Sets the mode, or switches to the next one (this is what a button would call) |
| `getMode()`, `isConnected()` | Current mode, and whether the camera answered at startup |
| `getBlocks()`, `hasTarget()` | Everything the camera currently sees, and whether it sees anything |
| `getBlockById(id)` | A specific tag ID, or a learned color ID in color mode (`null` if not seen) |
| `getLargestBlock()` | The biggest thing on screen, which is usually the closest (`null` if nothing seen) |
| `getTargetXError()` | How far the target is left or right of screen center, in pixels (positive = right, 0 if nothing seen); useful for aiming later |

The screen is 320 x 240, so the center X is 160 (`SCREEN_CENTER_X`).

## Setup
- **Robot config:** add the HuskyLens on an I2C port and name it `huskylens`.
- **Colors:** teach them on the camera itself in color recognition mode (use the learn button). Each learned color gets an ID (1, 2, ...) that shows up as `block.id`.
- **Switching modes:** the camera may see nothing for a frame or two right after a switch. That's normal.

## Using it in an OpMode
Use the same button pattern as the existing B / Y toggles:

```java
SubVision vision = new SubVision(hardwareMap);
boolean previousX = false;

// in the loop:
boolean currentX = gamepad1.x;
if (currentX && !previousX) {
    vision.nextMode();
}
previousX = currentX;

vision.update();
HuskyLens.Block target = vision.getLargestBlock();
```

Remember to import `com.qualcomm.hardware.dfrobot.HuskyLens` and `org.firstinspires.ftc.teamcode.SubSystems.SubVision`.