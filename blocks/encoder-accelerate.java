public void EncoderDrive(double speed, double leftInches, double rightInches, double AccelerationInches, int Direction) {
    // Declares variables that are used for this method
    int NewLeftTarget;
    int NewRightTarget;
    int RightPosition;
    int LeftPosition;
    double LeftPower;
    double RightPower;

    // Resets encoders to 0
    DriveLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    DriveRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    // Checks to make sure that encoders are reset.
    while(DriveLeft.getCurrentPosition() > 1 && DriveRight.getCurrentPosition()> 1){
        sleep(25);
    }


    if (opModeIsActive()) {
        // Determine new target position, and pass to motor controller
        // Calculates the needed encoder ticks by multiplying a pre-determined amount of CountsPerInches,
        // and the method input gets the actual distance travel in inches
        NewLeftTarget = DriveLeft.getCurrentPosition() + (int) (leftInches * CountsPerInch);
        NewRightTarget = DriveRight.getCurrentPosition() + (int) (rightInches * CountsPerInch);
        // Gets the current position of the encoders at the beginning of the EncoderDrive method
        RightPosition = DriveRight.getCurrentPosition();
        LeftPosition = DriveLeft.getCurrentPosition();
        // Gives the encoders the target.
        DriveLeft.setTargetPosition(NewLeftTarget);
        DriveRight.setTargetPosition(NewRightTarget);

        DriveRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        DriveLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        while(DriveLeft.getCurrentPosition() > 1){
            sleep(15);
        }



        // Turn On RUN_TO_POSITION
        DriveLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        DriveRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // reset the timeout time and start motion.
        runtime.reset();
        // This gets where the motor encoders will be at full position when it will be at full speed.
        double LeftEncoderPositionAtFullSpeed = ((AccelerationInches*(CountsPerInch)) + LeftPosition);
        double RightEncoderPositionAtFullSpeed = ((AccelerationInches*(CountsPerInch)) + RightPosition);
        boolean Running = true;


        // This gets the absolute value of the encoder positions at full speed - the current speed, and while it's greater than 0, it will continues increasing the speed.
        // This allows the robot to accelerate over a set number of inches, which reduces wheel slippage and increases overall reliability
        while (DriveLeft.isBusy() && DriveRight.isBusy() && Running && opModeIsActive()) {
            // While encoders are not at position
            if (((Math.abs(speed)) - (Math.abs(DriveLeft.getPower()))) > .05){
                // This allows the robot to accelerate over a set distance, rather than going full speed.  This reduces wheel slippage and increases reliability.
                LeftPower = (Range.clip(Math.abs((DriveLeft.getCurrentPosition()) / (LeftEncoderPositionAtFullSpeed)), .15, speed));
                RightPower =(Range.clip(Math.abs((DriveRight.getCurrentPosition()) / (RightEncoderPositionAtFullSpeed)), .15, speed));

                DriveLeft.setPower(LeftPower*Direction);
                DriveRight.setPower(RightPower*Direction);

                telemetry.addData("In Accel loop CM", +Distance.getDistance(DistanceUnit.CM));
                telemetry.addData("Accelerating", RightEncoderPositionAtFullSpeed);
                telemetry.addData("Path1", "Running to %7d :%7d", NewLeftTarget, NewRightTarget);
                telemetry.addData("Path2", "Running at %7d :%7d", DriveLeft.getCurrentPosition(), DriveRight.getCurrentPosition());
                telemetry.addData("Sections Complete:", +SectionsCompleted);
                telemetry.update();
            }else if(Math.abs(NewLeftTarget) - Math.abs(DriveLeft.getCurrentPosition()) < -1) {
                Running = false;
            }else{
                // Multiplies the speed desired by the direction, which has a value of either 1, or -1, and allows for backwards driving with the ramp up function
                DriveLeft.setPower((speed*Direction));
                DriveRight.setPower((speed*Direction));

                telemetry.addData("In Reg loop CM", +Distance.getDistance(DistanceUnit.CM));
                telemetry.addData("Path1", "Running to %7d :%7d", NewLeftTarget, NewRightTarget);
                telemetry.addData("Path2", "Running at %7d :%7d", DriveLeft.getCurrentPosition(), DriveRight.getCurrentPosition());
                telemetry.addData("Sections Complete:", +SectionsCompleted);
                telemetry.update();
            }

            // Display information for the driver.


        }

        // Stops all motion
        // Set to run without encoder, so it's not necessary to declare this every time after the method is used
        DriveLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        DriveRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        // Set power to 0
        DriveLeft.setPower(0);
        DriveRight.setPower(0);

    }
}