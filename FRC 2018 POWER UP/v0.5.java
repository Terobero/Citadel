package org.usfirst.frc.team6303.robot;


import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.drive.*;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Timer;
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
	Spark frontLeft = new Spark(0);
	Spark rearLeft = new Spark(2);
	Spark frontRight = new Spark(1);
	Spark rearRight = new Spark(4);
	
	Spark ele_up = new Spark(5);
	Spark ele_down = new Spark(3);
	
	Spark wheel1 = new Spark(6);
	Spark wheel2 = new Spark(7);
	
	MecanumDrive myRobot = new MecanumDrive(frontLeft, rearLeft, frontRight, rearRight);

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
	
	//Ip & Gear gibi seylerde kolay anlasilsin diye
	boolean forward = true;
	boolean backward = false;
	
	//Game Data
	String gameData = "LLL";
	NetworkTable table;
	NetworkTableInstance inst;
	
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
	
	AnalogInput ultra_left;
	AnalogInput ultra_right;
	
	double sensor_left;
	double sensor_right;
	
	DoubleSolenoid pistonLeft = new DoubleSolenoid(2,3);
	DoubleSolenoid pistonRight = new DoubleSolenoid(4,5);

	
	
	public void pinomatik(DoubleSolenoid solenoid, boolean isPressed, boolean take) {

		if(isPressed && take)
			solenoid.set(DoubleSolenoid.Value.kForward);

		else if(isPressed && !take)
			solenoid.set(DoubleSolenoid.Value.kReverse);

		else
			solenoid.set(DoubleSolenoid.Value.kOff);
	}
	
	
	
	
	@Override
	public void robotInit() {
		inst = NetworkTableInstance.getDefault();
		table = inst.getTable("GRIP/myContoursReport");
		
		//rearRight.setInverted(true);
		gyro.calibrate();

		autoChooser = new SendableChooser<Integer>();
		autoChooser.addObject("Left Duz", 0);
		autoChooser.addObject("Left Switch (Robot Ters Basliyo)", 1);
		
		autoChooser.addDefault("Middle", 10);
		
		autoChooser.addObject("Right Duz", 20);
		autoChooser.addObject("Right Switch (Robot Ters Basliyo)", 21);
		
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
		mode = (int) autoChooser.getSelected();
		sensors = (boolean) sensorChooser.getSelected();
		
		timerAuto.reset();
		timerAuto.start();
		
		if(sensors) {
			ultra_left = new AnalogInput(0);
			ultra_right = new AnalogInput(1);
		}
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
		
		
		if(sensors) {
			sensor_left = ultra_left.getVoltage();
			sensor_right = ultra_right.getVoltage();
		}
		
		switch(autoChooser.getSelected()) {
		//0,1 - 10 - 20, 21
		
		case 0: //left duz
			if (timerAuto.get() < 3)
				myRobot.driveCartesian(-aps, ams, 0);
			
			else if (timerAuto.get() < 3 + 500/cm_per_s)
				myRobot.driveCartesian(0, ams, 0);
			
			else
				myRobot.driveCartesian(0, 0, 0);
			
			break;
			
			
		case 1: //left switch
			if(!osm && timerAuto.get() < 3)
				myRobot.driveCartesian(aps, -ams, 0);
			
			else if(osm) {
				if(timerAuto.get() < 300/cm_per_s && gameData.charAt(0) == 'L') {
					myRobot.driveCartesian(-ams, aps - 0.1, 0);
					putAuto.reset();
					putAuto.start();
				}
				else if(timerAuto.get() < 560/cm_per_s && gameData.charAt(0) == 'R') {
					myRobot.driveCartesian(-ams, aps - 0.1, 0);
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
				if (sensors) {
					myRobot.driveCartesian(0, -ams, 0);
					if(sensor_left > 3000) {
						osm = true;
						gyro.reset();
						timerAuto.reset();
					}
				}
				
				else if(timerAuto.get() < 3 + 450/cm_per_s)
					myRobot.driveCartesian(0, -ams, 0);
				
			}
			
			else {
				if(sensors) {
					myRobot.driveCartesian(0, -ams, 0);
					if(sensor_left < 2000)
						ism = true;
				}
				
				else
					ism = true;
			}
			
			break;
			
			
		case 10: //middle
			
			if (centerXs.length > 0)
				x = x / centerXs.length;
			
			if (timerAuto.get() < 120/cm_per_s)
				myRobot.driveCartesian(0, ams, 0);

			else if(gameData.charAt(0) == 'L') {
				if(sensors)
					if(sensor_left < 500)
						myRobot.driveCartesian(0, aps + 0.1, 0);
				
				else if(!reached){
					if (currentArea >= 1000) { //degisecek (max kamera boyutunun birazi falan)
						reached = true;
						putAuto.reset();
						putAuto.start();
					}
					
					else if(x < 150)
						myRobot.driveCartesian(-aps, 0, 0);
					
					else if(x > 170)
						myRobot.driveCartesian(aps, 0, 0);
					
					else
						myRobot.driveCartesian(-aps, 0, 0);
				}
				
				else if(last) {
					if (lastTimer.get() < 300/cm_per_s) 
						myRobot.driveCartesian(0, ams, 0);
					
					else
						myRobot.driveCartesian(0, 0, 0);
				}
				
				else if(reached) {
					if (putAuto.get() < 2) {
						//put box
					}
					
					else {
						//stop box
						if(sensors) {
							if(sensor_left < 500)
								myRobot.driveCartesian(-ams + 0.1, 0, 0);
						}
						
						else if(putAuto.get() < 2 + 300/cm_per_s)
							myRobot.driveCartesian(-ams + 0.1, 0, 0);
						
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
				myRobot.driveCartesian(aps, ams, 0);
			
			else if (timerAuto.get() < 3 + 500/cm_per_s)
				myRobot.driveCartesian(0, ams, 0);
			
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

	
	@Override
	public void teleopPeriodic() {
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
		rightStickY = stick.getRawAxis(5);
		buttonA = stick.getRawButton(1);
		buttonB = stick.getRawButton(2);
		buttonX = stick.getRawButton(3);
		buttonY = stick.getRawButton(4);
		triggerL1 = stick.getRawButton(5);
		triggerR1 = stick.getRawButton(6);
		/*
		if(buttonY)
			ele_down.set(-1.0);
		else if(buttonX)
			ele_down.set(1.0);
		else
			ele_down.set(0);
		
		if(buttonA)
			ele_up.set(1.0);
		else if(buttonB)
			ele_up.set(-1.0);
		else  
			ele_up.set(0);
		*/
		//if((rightStickY > 0 && rightStickY > leftStickY) || (rightStickY < 0 && rightStickY < leftStickY))
		//	leftStickY = rightStickY;
		System.out.println(stick.getPOV());
		
		if(leftStickX > 0.1 || leftStickX < -0.1) {
			frontRight.set(leftStickX*0.82);
			frontLeft.set(-leftStickX* 0.8);
			rearRight.set(-leftStickX* 0.82);
			rearLeft.set(leftStickX* 0.8);
		}
		else
			myRobot.driveCartesian(leftStickX * 0.8, leftStickY * 0.8, rightStickX * 0.8);
		
		
		if(buttonA) {
			pistonLeft.set(DoubleSolenoid.Value.kForward);
			pistonRight.set(DoubleSolenoid.Value.kForward);
		}
		
		else if(buttonB) {
			pistonLeft.set(DoubleSolenoid.Value.kReverse);
			pistonRight.set(DoubleSolenoid.Value.kReverse);
		}
		
		else {
			pistonLeft.set(DoubleSolenoid.Value.kOff);
			pistonRight.set(DoubleSolenoid.Value.kOff);
		}
		
		if(buttonX) {
			wheel1.set(1.0);
			wheel2.set(1.0);
		}
		else if(buttonY) {
			wheel1.set(-1.0);
			wheel2.set(-1.0);
		}
		else {
			wheel1.set(0);
			wheel2.set(0);
		}
		
		switch(stick.getPOV()) {
		case 0:
			ele_up.set(1.0);
			break;
		case 180:
			ele_up.set(-1.0);
			break;
		case 90:
			ele_down.set(1.0);
			break;
		case 270:
			ele_down.set(-1.0);
			break;
		default:
			ele_up.set(0);
			ele_down.set(0);
		}
		
		
		/*if(leftStickX < 0.2 && leftStickX > -0.2) 
			myRobot.driveCartesian(0, leftStickY, 0);
		else if(leftStickX < -0.6 && (leftStickY < 0.3 || leftStickY > -0.3))
			mecLeft();
		else if(leftStickX > 0.6 && (leftStickY < 0.3 || leftStickY > -0.3))
			mecRight();
		else {
			rearRight.set(-0.5);
			frontLeft.set(0.5);
		}*/
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		//liora
		
		/* if(leftStickX<= 0.5 && leftStickX>=-0.5) { //forward backward
			if(leftStickY<0) {//forward
				frontRight.set(-0.82);
				frontLeft.set(- 0.8);
				rearRight.set(- 0.82);
				rearLeft.set(-0.8);
			}
			else if(leftStickY>0) {//backward
				frontRight.set(0.82);
				frontLeft.set(0.8);
				rearRight.set(0.82);
				rearLeft.set(0.8);
			}
		}
		
		if(leftStickY<= 0.5 && leftStickY>=-0.5) {//left right
			if(leftStickX<0) {//left
				frontRight.set(-0.82);
				frontLeft.set(0.8);
				rearRight.set(0.82);
				rearLeft.set(-0.8);
			}
			else if(leftStickX>0) {//right
				frontRight.set(0.82);
				frontLeft.set(-0.8);
				rearRight.set(-0.82);
				rearLeft.set(0.8);
			}
		}
		
		if(leftStickX> 0.5 && leftStickX<1) {// right diagonals
			if(leftStickY>-1 && leftStickY<-0.5) {// up
				frontRight.set(0);
				frontLeft.set(-0.8);
				rearRight.set(-0.82);
				rearLeft.set(0);
			}
			else if(leftStickY>0.5 && leftStickY<1) {//down
				frontRight.set(0.82);
				frontLeft.set(0);
				rearRight.set(0);
				rearLeft.set(0.8);
			}
		}
		
		if(leftStickX> -1 && leftStickX<-0.5) {// left diagonals
			if(leftStickY>-1 && leftStickY<-0.5) {// up
				frontRight.set(-0.82);
				frontLeft.set(0);
				rearRight.set(0);
				rearLeft.set(-0.8);
			}
			else if(leftStickY>0.5 && leftStickY<1) {//down
				frontRight.set(0);
				frontLeft.set(0.8);
				rearRight.set(0.82);
				rearLeft.set(0);
			}
		}*/
		
		//liora
		
		//else
			//myRobot.driveCartesian(leftStickX * 0.8, leftStickY * 0.8, rightStickX * 0.8);
		//myRobot.driveCartesian(0, 0.2, 0);
		//System.out.println(ultra_left.getVoltage() + " " + ultra_right.getVoltage());
	}
	/*double s = 0.5;
	
	public void mecLeft() {
			frontRight.set(-0.82*s);
			frontLeft.set(0.8*s);
			rearRight.set(0.82*s);
			rearLeft.set(-0.8*s);
	}
	
	public void mecRight() {
		frontRight.set(0.82*s);
		frontLeft.set(-0.8*s);
		rearRight.set(-0.82*s);
		rearLeft.set(0.8*s);
}*/
}
