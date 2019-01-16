package org.usfirst.frc.team6303.robot;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

public class Robot extends IterativeRobot {
	
	VictorSP catapult = new VictorSP(2);
	VictorSP motorLeft1 = new VictorSP(1);
	VictorSP motorLeft2 = new VictorSP(3);
	VictorSP motorRight1 = new VictorSP(0);
	VictorSP motorRight2 = new VictorSP(4);
	
	SpeedControllerGroup left = new SpeedControllerGroup(motorLeft1, motorLeft2);
	SpeedControllerGroup right = new SpeedControllerGroup(motorRight1, motorRight2);
	
	DifferentialDrive myRobot = new DifferentialDrive(left, right);
	Joystick stick = new Joystick(0);
	
	double leftStickX;
	double leftStickY;
	double rightStickX;
	double rightStickY;
	double triggerLeft;
	double triggerRight;

	boolean buttonA;
	boolean buttonB;
	boolean buttonX;
	boolean buttonY;
	
	String gameData = "LLL";
	Timer timerAuto = new Timer();
	
	ADXRS450_Gyro gyro = new ADXRS450_Gyro();
	double gyro_angle;
	boolean can_turn_again = true;
	int gyro_must_angle;
	//double kP = 0.05;
	
	int loopTurnX = 0;
	int loopTurnB = 0;
	int loopCatapult = 1000000;
	SendableChooser<Integer> autoChooser;
	int mode = 10;
	
	@Override
	public void robotInit() {
		myRobot.setSafetyEnabled(false);
		
		CameraServer.getInstance().startAutomaticCapture();
		
		
		catapult.setInverted(true);
		autoChooser = new SendableChooser<Integer>();
		autoChooser.addObject("Left", 0);
		//autoChooser.addObject("Left Switch (robot ters basliyo?)", 1);
		
		autoChooser.addDefault("Middle", 10);
		
		autoChooser.addObject("Right", 20);
		//autoChooser.addObject("Right Switch (robot ters basliyo?)", 21);
	}
	
	@Override
	public void autonomousInit() {
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		mode = (int)autoChooser.getSelected();
		
		timerAuto.reset();
		timerAuto.start();
	}
	
	@Override
	public void teleopInit() {
		CameraServer.getInstance().startAutomaticCapture();
	}

	@Override
	public void autonomousPeriodic() {
		switch(3) {
		case 2: //orta
			if(gameData.charAt(0) == 'R') {
				if(timerAuto.get() < 0.5) {
					myRobot.tankDrive(0.6, 0.6);
				}
				else if(timerAuto.get() < 1.05)
					myRobot.tankDrive(0.8, -0.8);
				else if(timerAuto.get() < 1.88)
					myRobot.tankDrive(0.56, 0.56);
				else if(timerAuto.get() < 2.49)
					myRobot.tankDrive(-0.82, 0.82);
				else if(timerAuto.get() < 5.49)
					myRobot.tankDrive(0.6, 0.6);
				else if(timerAuto.get() < 9) {
					myRobot.tankDrive(0, 0);
					catapult.set(1);
				}
				else
					catapult.set(0);
			}
			else {
				if(timerAuto.get() < 0.6) {
					myRobot.tankDrive(0.5, 0.5);
				}
				else if(timerAuto.get() < 1.1)
					myRobot.tankDrive(-0.8, 0.8);
				else if(timerAuto.get() < 2.5)
					myRobot.tankDrive(0.6, 0.6);
				else if(timerAuto.get() < 3)
					myRobot.tankDrive(0.75, -0.75);
				else if(timerAuto.get() < 6)
					myRobot.tankDrive(0.6, 0.6);
				else if(timerAuto.get() < 10) {
					myRobot.tankDrive(0, 0);
					catapult.set(1);
				}
				else
					catapult.set(0);
			}
			break;
		case 3: //sag
			if(gameData.charAt(0) == 'L'){
				if(timerAuto.get() < 1)
					myRobot.tankDrive(0.84, 0.46);
				else if(timerAuto.get() < 3)
					myRobot.tankDrive(0.5, 0.6);
				else
					myRobot.tankDrive(0, 0);
			}
			else {
				if(timerAuto.get() < 3)
					myRobot.tankDrive(0.7, 0.7);
				else if(timerAuto.get() < 5) {
					myRobot.tankDrive(0, 0);
					catapult.set(1);
				}
				else if(timerAuto.get() < 7)
					catapult.set(-1);
				else
					catapult.set(0);
			}
			break;
		}
	}
	
	@Override
	public void teleopPeriodic() {
		if(loopTurnX < 100000)
			loopTurnX += 1;
		if(loopTurnB < 100000)
			loopTurnB += 1;
		if(loopCatapult < 100000)
			loopCatapult += 1;
		//leftStickX = stick.getRawAxis(0);
		leftStickY = stick.getRawAxis(1);
		//rightStickX = stick.getRawAxis(4);
		rightStickY = stick.getRawAxis(5);
		triggerLeft = stick.getRawAxis(2);
		triggerRight = stick.getRawAxis(3);
		
		buttonA = stick.getRawButton(1); //shoot (on button press)
		buttonB = stick.getRawButton(2); //ball feed (on / off)
		buttonX = stick.getRawButton(3); //ball collect (on / off)
		buttonY = stick.getRawButton(4); //gear collection (on / off)
		
		if(triggerRight > 0.3)
			catapult.set(1);
		else if(triggerLeft > 0.3)
			catapult.set(-1);
		else
			catapult.set(0);
			
		/*
		if(buttonA || buttonB || buttonX || buttonY) {
			timerTurn.reset();
			timerTurn.start();
		}
		if(timerTurn.get() < 1.3)
			myRobot.tankDrive(-1, 1);
		else
		*/
		if(buttonX)
			loopTurnX = 0;
		if(buttonB)
			loopTurnB = 0;
		/*
		if(loopTurnX < 500)
			myRobot.tankDrive(0.5, 0.5);
		else if(loopTurnX < 1000)
			myRobot.tankDrive(0.5, 0.5);
		else if(loopTurnB < 500)
			myRobot.tankDrive(0.5, 0.5);
		else if(loopTurnB < 1000)
			myRobot.tankDrive(0.5, 0.5);
		else*/
			myRobot.tankDrive(-leftStickY, -rightStickY);
		/*
		if(buttonY || triggerRight > 0.3) {
			loopCatapult = 0;
		}
		if(triggerLeft > 0.3)
			catapult.set(-1);
		else if(loopCatapult < 500)
			catapult.set(1);
		else if(loopCatapult < 1000)
			catapult.set(-1);
		else
			catapult.set(0);
			*/
	}
}
