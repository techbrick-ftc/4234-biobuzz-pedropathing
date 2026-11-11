package org.firstinspires.ftc.teamcode.TeleOPs;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "Main TeleOP")
public class TeleOPMain extends LinearOpMode {

    public void runOpMode() throws InterruptedException {

        waitForStart();
        if (isStopRequested()) return;

        while (opModeIsActive()) {

            sleep(20);

        }
    }
}
