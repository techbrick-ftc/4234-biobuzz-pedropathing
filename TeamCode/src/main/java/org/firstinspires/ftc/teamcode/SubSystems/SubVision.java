package org.firstinspires.ftc.teamcode.SubSystems;

import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class SubVision {

    // Vision modes. Add a new entry here to support another HuskyLens algorithm.
    public enum Mode {
        APRILTAG(HuskyLens.Algorithm.TAG_RECOGNITION),
        COLOR(HuskyLens.Algorithm.COLOR_RECOGNITION);

        private final HuskyLens.Algorithm algorithm;

        Mode(HuskyLens.Algorithm algorithm) {
            this.algorithm = algorithm;
        }
    }

    // HuskyLens screen is 320x240
    public static final int SCREEN_CENTER_X = 160;
    public static int TARGET_TAG_ID = 1;

    private static final HuskyLens.Block[] NO_BLOCKS = new HuskyLens.Block[0];

    private final HuskyLens huskyLens;
    private final boolean connected;

    private Mode mode;
    private HuskyLens.Block[] blocks = NO_BLOCKS;

    public SubVision(HardwareMap hardwareMap) {
        this(hardwareMap, "huskylens", Mode.APRILTAG);
    }

    public SubVision(HardwareMap hardwareMap, String hardwareName, Mode startMode) {
        huskyLens = hardwareMap.get(HuskyLens.class, hardwareName);
        connected = huskyLens.knock();
        setMode(startMode);
    }

    // Call once per loop so each loop only does one I2C read
    public void update() {
        HuskyLens.Block[] result = huskyLens.blocks();
        blocks = (result != null) ? result : NO_BLOCKS;
    }

    public void setMode(Mode newMode) {
        mode = newMode;
        huskyLens.selectAlgorithm(newMode.algorithm);
        blocks = NO_BLOCKS;
    }

    public void nextMode() {
        Mode[] modes = Mode.values();
        setMode(modes[(mode.ordinal() + 1) % modes.length]);
    }

    public Mode getMode() {
        return mode;
    }

    public boolean isConnected() {
        return connected;
    }

    public HuskyLens.Block[] getBlocks() {
        return blocks;
    }

    public boolean hasTarget() {
        return blocks.length > 0;
    }

    // Tag ID in APRILTAG mode, learned color ID in COLOR mode. Returns null if not seen.
    public HuskyLens.Block getBlockById(int id) {
        for (HuskyLens.Block block : blocks) {
            if (block.id == id) return block;
        }
        return null;
    }

    // Largest block on screen (usually the closest target). Returns null if nothing is seen.
    public HuskyLens.Block getLargestBlock() {
        HuskyLens.Block largest = null;
        for (HuskyLens.Block block : blocks) {
            if (largest == null || block.width * block.height > largest.width * largest.height) {
                largest = block;
            }
        }
        return largest;
    }

    // Horizontal pixel offset of the largest block from screen center (positive = right). 0 if nothing is seen.
    public double getTargetXError() {
        HuskyLens.Block target = getLargestBlock();
        return (target != null) ? target.x - SCREEN_CENTER_X : 0;
    }
}

// Developed by Team 4234