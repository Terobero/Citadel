//FRC 2018 Code of Team 6303 Citadel
//Author: Kaan Bozkurt - Liora Nasi - Mert Gerdan
package org.usfirst.frc.team6303.robot;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.drive.*;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.VictorSP;


public class Robot extends IterativeRobot {
	//Motor Controllers
	VictorSP frontLeft = new VictorSP(0);
	VictorSP rearLeft = new VictorSP(1);
	VictorSP frontRight = new VictorSP(2);
	VictorSP rearRight = new VictorSP(3);
	
	Spark catapult = new Spark(4);
	
	//Combining the Controllers
	MecanumDrive myRobot = new MecanumDrive(frontLeft, rearLeft, frontRight, rearRight);

	//Gyro
	ADXRS450_Gyro gyro = new ADXRS450_Gyro();
	double kP = 0.05;
	double gyro_angle = 0;
	int loop = 0;
	boolean ng = false;
	double rotation = 0;
	
	//Joystick
	Joystick stick = new Joystick(0);
	double mult = 0.8; //The value we multiple with the values we got from the joystick

	//Values from Joystick
	double leftStickX;
	double leftStickY;
	double rightStickX;
	double rightStickY;
	
	boolean buttonA;
	boolean buttonB;
	boolean buttonX;
	boolean buttonY;
	boolean triggerL1;
	boolean triggerR1;
	double triggerL2;
	double triggerR2;
	
	//Game Data
	String gameData = "LLL";
	
	//Timer for Autonomous
	Timer timerAuto = new Timer();
	
	@Override
	public void robotInit() {
		myRobot.setSafetyEnabled(false);
		rearLeft.setInverted(true);
		rearRight.setInverted(true);
		//Starting the Camera
		//CameraServer.getInstance().startAutomaticCapture();
		
		//Reseting the Gyro
		gyro.reset();
		gyro.calibrate();
	}

	@Override
	public void autonomousInit() {
		//Getting the Game Data from FMS
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		
		//Reseting and Starting the Autonomous Timer
		timerAuto.reset();
		timerAuto.start();

	}

	@Override
	public void autonomousPeriodic() {
		//If we need to put on the right side of the Switch
		if(gameData.charAt(0) == 'R') {
			//Go to right for 1.5 seconds
			if(timerAuto.get() < 1.5)
				myRobot.driveCartesian(-0.5, 0, 0);
			//Go forward for 3.5 seconds
			else if(timerAuto.get() < 5) {
				myRobot.driveCartesian(0, -0.5, 0);
				catapult.set(-0.7);
			}
			//Put the cube for 2 seconds
			else if(timerAuto.get() < 7) {
				myRobot.driveCartesian(0, 0, 0);
				catapult.set(1.0);
			}
			//Stop everything
			else
				catapult.set(0.0);
		}
		
		//If we need to put on the left side of the Switch
		else {
			//Go to left for 1.5 seconds
			if(timerAuto.get() < 1.5)
				myRobot.driveCartesian(0.5, 0, 0);
			//Go forward for 3.5 seconds
			else if(timerAuto.get() < 5) {
				myRobot.driveCartesian(0, -0.5, 0);
				catapult.set(-0.7);
			}
			//Put the cube for 2 seconds
			else if(timerAuto.get() < 7) {
				myRobot.driveCartesian(0, 0, 0);
				catapult.set(1.0);
			}
			//Stop everything
			else
				catapult.set(0.0);
		}	
	}

	@Override
	public void teleopInit() {
		gyro.reset();
	}
	
	@Override
	public void teleopPeriodic() {
		
		rotation = gyro.getAngle();
		
		//Getting values from the Joystick
		rightStickX = -stick.getRawAxis(4);
		leftStickY = stick.getRawAxis(1);
		leftStickX = stick.getRawAxis(0);
		rightStickY = stick.getRawAxis(5);
		
		buttonA = stick.getRawButton(1);
		buttonB = stick.getRawButton(2);
		buttonX = stick.getRawButton(3);
		buttonY = stick.getRawButton(4);
		
		if(buttonY)
			mult = 0.95;
		if(buttonA)
			mult = 0.5;
		
		
		if(rightStickX < 0.05 && rightStickX > -0.05) {		
			loop += 1;
			// manual gyro normal drive class
			if(loop > 50) {
				if(ng) {
					gyro.reset();
					ng = false;
				}
				double fR = (leftStickX * 0.8 + leftStickY + rotation * 0.05);
				double rR = -(leftStickX * 0.8 - leftStickY - rotation * 0.05);
				double fL = -(-leftStickX * 0.8 + leftStickY - rotation * 0.05);
				double rL = (-leftStickX * 0.8 - leftStickY + rotation * 0.05);
				
				// manual motor set cartesian drive class 
				
				frontRight.set(fR);
				rearRight.set(rR);
				frontLeft.set(fL);
				rearLeft.set(rL);
			}
			else {
				myRobot.driveCartesian(0, 0, 0);
				rotation = 0;
				
				double fR = (leftStickX * 0.8 + leftStickY + rotation * 0.05);
				double rR = -(leftStickX * 0.8 - leftStickY - rotation * 0.05);
				double fL = -(-leftStickX * 0.8 + leftStickY - rotation * 0.05);
				double rL = (-leftStickX * 0.8 - leftStickY + rotation * 0.05);
				
				// manual motor set cartesian drive class 
				
				frontRight.set(fR);
				rearRight.set(rR);
				frontLeft.set(fL);
				rearLeft.set(rL);
			}
			
		}
		
		else {
			myRobot.driveCartesian(-leftStickX * mult, leftStickY * mult, rightStickX * mult);
			loop = 0;
			ng = true;	
		}
		
		//Catapult Motor Backward
		if(buttonB)
			catapult.set(-1.0);
		//Catapult Motor Forward
		else if(buttonX) 
			catapult.set(1.0);
		//Stop Catapult Motor
		else
			catapult.set(0.0);
	}
}
