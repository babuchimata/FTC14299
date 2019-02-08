public void turn(double angle, double speed)
    {
        leftBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

        double initalAngle = angles.firstAngle;
        double motorPower;
        double minMotorPower = 0.2;
        double powerScaleFactor;
        double targetAngle;
        double currentAngle;
        double deltaAngle;
        double robotAngle = angles.firstAngle;
        double previousAngle = angles.firstAngle;

        targetAngle = initalAngle + angle;

        while (Math.abs(targetAngle - robotAngle)> .1)
        {
            angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
            currentAngle = angles.firstAngle;

            //update speed dynamically to slow when approaching the target
            powerScaleFactor = Math.abs(targetAngle-robotAngle)/angle;
            motorPower = powerScaleFactor*speed;
            if (motorPower < minMotorPower)
            {
                motorPower = minMotorPower;
            }

            //determine which direction the robot should turn



            if ((targetAngle - robotAngle) > 0) {
                leftBack.setPower(-motorPower);
                leftFront.setPower(-motorPower);
                rightBack.setPower(motorPower);
                rightFront.setPower(motorPower);
            } else {
                leftBack.setPower(motorPower);
                rightBack.setPower(-motorPower);
                leftFront.setPower(motorPower);
                rightFront.setPower(-motorPower);
            }


            //define how the angle is changing and deal with the stupid 180 -> -180 thing
            deltaAngle = currentAngle - previousAngle;
            if (deltaAngle > 180)
            {
                deltaAngle -= 360;
            }
            else if(deltaAngle < -180)
            {
                deltaAngle += 360;
            }

            robotAngle += deltaAngle;
            previousAngle = currentAngle;

            telemetry.addData("robotangle", robotAngle);
            telemetry.addData("deltaAngle", deltaAngle);
            telemetry.addData("currentAngle", currentAngle);

            telemetry.update();

        }
        stopRobot();