//FRC 2018 Code of Team 6303 Citadel
//Author: Kaan Bozkurt
package org.usfirst.frc.team6303.robot;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.drive.*;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Robot extends IterativeRobot {
	//Motor Controllers
	VictorSP frontLeft = new VictorSP(0);
	VictorSP rearLeft = new VictorSP(2);
	VictorSP frontRight = new VictorSP(1);
	VictorSP rearRight = new VictorSP(4);
	
	Spark catapult = new Spark(3);
	
	//Combining the Controllers
	MecanumDrive myRobot = new MecanumDrive(frontLeft, rearLeft, frontRight, rearRight);

	//Gyro
	ADXRS450_Gyro gyro = new ADXRS450_Gyro();
	//double kP = 0.05;
	
	//Joystick
	Joystick stick = new Joystick(0);

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
		
		//Starting the Camera
		CameraServer.getInstance().startAutomaticCapture();
		
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
	public void teleopPeriodic() {
		
		//Getting values from the Joystick
		leftStickX = stick.getRawAxis(4);
		leftStickY = stick.getRawAxis(1);
		rightStickX = stick.getRawAxis(0);
		rightStickY = stick.getRawAxis(5);
		
		buttonA = stick.getRawButton(1);
		buttonB = stick.getRawButton(2);
		buttonX = stick.getRawButton(3);
		buttonY = stick.getRawButton(4);
		
		//Move the robot
		myRobot.driveCartesian(-rightStickX * 0.8, leftStickY * 0.8, leftStickX * 0.8, gyro.getAngle());
		
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
