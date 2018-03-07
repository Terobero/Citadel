/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
/*
public class Robot extends IterativeRobot {
	private static final String kDefaultAuto = "Default";
	private static final String kCustomAuto = "My Auto";
	private String m_autoSelected;
	private SendableChooser<String> m_chooser = new SendableChooser<>();


	@Override
	public void robotInit() {
		m_chooser.addDefault("Default Auto", kDefaultAuto);
		m_chooser.addObject("My Auto", kCustomAuto);
		SmartDashboard.putData("Auto choices", m_chooser);
	}


	@Override
	public void autonomousInit() {
		m_autoSelected = m_chooser.getSelected();
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		System.out.println("Auto selected: " + m_autoSelected);
	}


	@Override
	public void autonomousPeriodic() {
		switch (m_autoSelected) {
			case kCustomAuto:
				// Put custom auto code here
				break;
			case kDefaultAuto:
			default:
				// Put default auto code here
				break;
		}
	}
}
*/
package org.usfirst.frc.team6303.robot;


import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.drive.*;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Robot extends IterativeRobot {
	//Otonomda mode degistirmek icin dashboard'da arayuz hazirlanmasi
	SendableChooser<Integer> autoChooser;
	SendableChooser<Boolean> sensorChooser;
	public int mode = 1;
	public boolean sensors = true;
	
	
	//Solenoid solenoid = new Solenoid(1);
	//Compressor comp = new Compressor();
	

	//Motor suruculeri
	Spark frontLeft = new Spark(3);
	Spark rearLeft = new Spark(1);
	Spark frontRight = new Spark(2);
	Spark rearRight = new Spark(0);
	
	Spark ele_up = new Spark(4);
	Spark ele_down = new Spark(5);

	//Gyro ve gyro ile carptigimiz value
	ADXRS450_Gyro gyro = new ADXRS450_Gyro();
	double gyro_angle;
	boolean can_turn_again = true;
	int gyro_must_angle;
	//double kP = 0.05;
	
	//Robot drive ve joystick
	Joystick stick = new Joystick(0);

	//Kumandanin'in sag ve sol cubuklarinin valuelari
	double leftStickX;
	double leftStickY;
	double rightStickX;
	double rightStickY;
	
	//Kumandanin butonlarini
	boolean buttonA;
	boolean buttonB;
	boolean buttonX;
	boolean buttonY;
	boolean triggerL1;
	boolean triggerR1;
	double triggerL2;
	double triggerR2;
	
	boolean button1;
	boolean button2;
	boolean button3;
	boolean button4;
	boolean button5;
	boolean button6;
	
	//Ip & Gear gibi seylerde kolay anlasilsin diye
	boolean forward = true;
	boolean backward = false;
	
	//Game Data
	String gameData = "LLL";
	NetworkTable table;
	NetworkTableInstance inst;
	MecanumDrive myRobot = new MecanumDrive(frontLeft, rearLeft, frontRight, rearRight);
	
	//Timerlar ve Otonom seyleri
	Timer timerAuto = new Timer();
	int stupid = 5;
	double cm_per_s = 1; //degisecek
	double ams = 1; //auto_max_speed
	double aps = 0.5; //auto_precise_speed
	boolean reached = false;
	Timer putAuto = new Timer();
	boolean last = false;
	Timer lastTimer = new Timer();
	boolean ism = false; //in_switch_mode
	boolean osm = false; //out_switch_mode
	
	Ultrasonic ultra_left = new Ultrasonic(0, 1);
	Ultrasonic ultra_right = new Ultrasonic(0, 1);

	@Override
	public void robotInit() {
		inst = NetworkTableInstance.getDefault();
		table = inst.getTable("GRIP/myContoursReport");
		
		frontRight.setInverted(true);
		gyro.calibrate();
		//Otonomda mod secme arayuzu olusturma
		autoChooser = new SendableChooser<Integer>();
		autoChooser.addObject("Left Duz", 0);
		autoChooser.addObject("Left Switch (Robot Ters Basliyo)", 1);
		
		autoChooser.addDefault("Middle", 10);
		
		autoChooser.addObject("Right Duz", 20);
		autoChooser.addObject("Right Switch (Robot Ters Basliyo", 21);
		
		SmartDashboard.putData("Autonomous Mode Chooser", autoChooser);
		
		sensorChooser = new SendableChooser<Boolean>();
		
		sensorChooser.addDefault("On", true);
		sensorChooser.addObject("Off", false);
		
		SmartDashboard.putData("Sensor", sensorChooser);
		
		//ultra.setAutomaticMode(true);


	}

	@Override
	public void autonomousInit() {
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		System.out.println(gameData);
		mode = (int) autoChooser.getSelected();
		sensors = (boolean) sensorChooser.getSelected();
		
		timerAuto.reset();
		timerAuto.start();
	}

	@Override
	public void autonomousPeriodic() {
		
		double[] defaultValue = new double[0];
		
		NetworkTableEntry areaEntry = table.getEntry("area");
		double[] areas = areaEntry.getDoubleArray(defaultValue);
		double currentArea = 0;
		
		NetworkTableEntry centerXEntry = table.getEntry("centerX");
		double[] centerXs = centerXEntry.getDoubleArray(defaultValue);
		double x = 0;
		
		
		for (double area : areas) {
			System.out.println(area);
			currentArea = area;
		}
		for (double centerX : centerXs) {
			x += centerX;
		}
		
		switch(autoChooser.getSelected()) {
		//0,1 - 10 - 20, 21
		
		case 0: //left duz
			if (timerAuto.get() < 3)
				myRobot.driveCartesian(ams, -aps, 0);
			
			else if (timerAuto.get() < 3 + 500/cm_per_s)
				myRobot.driveCartesian(ams, 0, 0);
			
			else
				myRobot.driveCartesian(0, 0, 0);
			
			break;
			
			
		case 1: //left switch
			if(!osm && timerAuto.get() < 3)
				myRobot.driveCartesian(ams, -aps, 0);
			
			else if(osm) {
				if(timerAuto.get() < 300/cm_per_s) {
					myRobot.driveCartesian(0, -ams, 0);
					putAuto.reset();
					putAuto.start();
				}
				else if(putAuto.get() < 2) {
					//put box
				}
				
				else {
					//stop box
					myRobot.driveCartesian(0, 0, 0);
				}
			}
			
			else if(ism) {
				myRobot.driveCartesian(ams, 0, 0);
				if(ultra_right.getRangeMM() > 3000) {
					osm = true;
					gyro.reset();
					timerAuto.reset();
				}
				
			}
			
			else {
				myRobot.driveCartesian(ams, 0, 0);
				if(ultra_right.getRangeMM() < 2000)
					ism = true;
			}
			
			break;
			
			
		case 10: //middle
			
			if (centerXs.length > 0)
				x = x / centerXs.length;
			
			if (timerAuto.get() < 120/cm_per_s)
				myRobot.driveCartesian(ams, 0, 0);

			else if(gameData.charAt(0) == 'L') {
				if(ultra_left.getRangeMM() < 500)
					myRobot.driveCartesian(0, 0, 0);
				
				else if(!reached){
					if (currentArea >= 1000) { //degisecek (max kamera boyutunun birazi falan)
						reached = true;
						putAuto.reset();
						putAuto.start();
					}
					
					else if(x < 150)
						myRobot.driveCartesian(0, -aps, 0);
					
					else if(x > 170)
						myRobot.driveCartesian(0, aps, 0);
					
					else
						myRobot.driveCartesian(0, -aps, 0);
				}
				
				else if(last) {
					if (lastTimer.get() < 300/cm_per_s) {
						myRobot.driveCartesian(ams, 0, 0);
					}
					
					else
						myRobot.driveCartesian(0, 0, 0);
				}
				
				else if(reached) {
					if (putAuto.get() < 2) {
						//put box
					}
					
					else {
						//stop box
						if(ultra_left.getRangeMM() < 500)
							myRobot.driveCartesian(0, -ams + 0.1, 0);
						
						else {
							last = true;
							lastTimer.reset();
							lastTimer.start();
						}
					}
				}
			}
			
			else if(gameData.charAt(0) == 'R') {
				
			}
			
			break;
			
			
		case 20: //right duz
			if (timerAuto.get() < 3)
				myRobot.driveCartesian(ams, aps, 0);
			
			else if (timerAuto.get() < 3 + 500/cm_per_s)
				myRobot.driveCartesian(ams, 0, 0);
			
			else
				myRobot.driveCartesian(0, 0, 0);
			
			break;
		
			
		case 21: //right switch
			break;
		}
		
		
		
		
	}

	@Override
	public void teleopInit() {
		
	}
	
	public void gyroTurn() {
		if (can_turn_again) {
			gyro.reset();
			can_turn_again = false;
		}
		if (gyro.getAngle() < gyro_must_angle) {
			//myRobot.arcadeDrive(0, 0.45); //4:30 sn'de 180 derece
		} else {
			can_turn_again = true;
		}
	}
	
	@Override
	public void teleopPeriodic() {
		
		/*double x = xEntry.getDouble(0.0);
		double y = yEntry.getDouble(0.0);
		System.out.println(x + " " + y);*/
		
		/*double centerX;
		synchronized (imgLock) {
			centerX = this.centerX;
		}
		double turn = centerX - (IMG_WIDTH / 2);
		System.out.println(turn);*/
		
		//SmartDashboard.putNumber("Gyro Angle:", gyro.getAngle()); //realtime feedback
		leftStickX = stick.getRawAxis(0); 
		leftStickY = stick.getRawAxis(1);
		triggerL2 = stick.getRawAxis(2);
		triggerR2 = stick.getRawAxis(3);
		triggerR2 = 0.5 + triggerR2/2;
		triggerR2 = 1 - triggerR2;
		if (triggerR2 < 0.4)
			triggerR2 = 0.4;
		else if(triggerR2 > 0.9)
			triggerR2 = 0.9;
		rightStickX = stick.getRawAxis(4);
		//rightStickY = stick.getRawAxis(5);
		//buttonA = stick.getRawButton(1);
		//buttonB = stick.getRawButton(2);
		//buttonX = stick.getRawButton(3);
		//buttonY = stick.getRawButton(4);
		//triggerL1 = stick.getRawButton(5);
		//triggerR1 = stick.getRawButton(6);
		//myRobot.arcadeDrive(leftStickY, leftStickX);
		button1 = stick.getRawButton(1);
		button2 = stick.getRawButton(2);
		button3 = stick.getRawButton(3);
		button4 = stick.getRawButton(4);
		button5 = stick.getRawButton(5);
		button6 = stick.getRawButton(6);

		/*if(button1) {
			gyroTurn();
			gyro_must_angle = 90;
		}if(button2) {
			gyroTurn();
			gyro_must_angle = 180;
		}if(button3) {
			gyroTurn();
			gyro_must_angle = 270;
		}if(button4) {
			gyroTurn();
			gyro_must_angle = 360;
		}if(button5) {
			gyroTurn();
			gyro_must_angle = 45;
		}if(button6) {
			gyroTurn();
			gyro_must_angle = 720;
		}
		if(!can_turn_again)
			gyroTurn();
		*/
			
		/*Encoder e = new Encoder(4, 5, false, Encoder.EncodingType.k4X);
		e.reset();
		int maxDeg = 120;
		int minDeg = -10;
		VictorSP m = new VictorSP(6);
		
		if(button1) {
			for(int i = e.getRaw(); i < maxDeg; i ++) {
				m.set(0.5);
			}
			m.set(0);
		}
		else if(button2) {
			for(int i = e.getRaw(); i > minDeg; i --) {
				m.set(-0.5)s;
			}
			m.set(0);
		}*/
		
		//else if (button4)
		//	rearRight.setSpeed(triggerR2);
		//else
		if (button1)
			ele_up.set(1);
		
		else if(button2)
			ele_up.set(-1);
		else
			ele_up.set(0);
		
		if(button3)
			ele_down.set(1);
		else if(button4)
			ele_down.set(-1);
		else
			ele_down.set(0);
		
		switch(stick.getPOV()) {
		case 0:
			frontLeft.set(0.8);
			break;
		case 90:
			rearLeft.set(0.8);
			break;
		case 180:
			frontRight.set(0.8);
			break;
		case 270:
			rearRight.set(0.8);
			break;

			
		}
		//myRobot.driveCartesian(-leftStickY * 0.8, leftStickX * 0.8, rightStickX * 0.8);
		
		//System.out.println(ultra.getRangeMM());
		
		//myRobot.arcadeDrive(-leftStickY*triggerR2, leftStickX*triggerR2);
	}
}
