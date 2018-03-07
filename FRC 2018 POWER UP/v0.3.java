package org.usfirst.frc.team6303.robot;


import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.AnalogInput;
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
	Spark frontLeft = new Spark(3);
	Spark rearLeft = new Spark(1);
	Spark frontRight = new Spark(2);
	Spark rearRight = new Spark(0);
	
	Spark ele_up = new Spark(4);
	Spark ele_down = new Spark(5);
	
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
	
	AnalogInput ultra_left = new AnalogInput(0);
	AnalogInput ultra_right = new AnalogInput(1);
	
	double sensor_left;
	double sensor_right;

	@Override
	public void robotInit() {
		inst = NetworkTableInstance.getDefault();
		table = inst.getTable("GRIP/myContoursReport");
		
		//frontRight.setInverted(true);
		gyro.calibrate();

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
		
		sensor_left = ultra_left.getVoltage();
		sensor_right = ultra_right.getVoltage();
		
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
				myRobot.driveCartesian(-ams, aps, 0);
			
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
				myRobot.driveCartesian(-ams, 0, 0);
				if(sensor_left > 3000) {
					osm = true;
					gyro.reset();
					timerAuto.reset();
				}
				
			}
			
			else {
				myRobot.driveCartesian(-ams, 0, 0);
				if(sensor_left < 2000)
					ism = true;
			}
			
			break;
			
			
		case 10: //middle
			
			if (centerXs.length > 0)
				x = x / centerXs.length;
			
			if (timerAuto.get() < 120/cm_per_s)
				myRobot.driveCartesian(ams, 0, 0);

			else if(gameData.charAt(0) == 'L') {
				if(sensor_left < 500)
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
					if (lastTimer.get() < 300/cm_per_s) 
						myRobot.driveCartesian(ams, 0, 0);
					
					else
						myRobot.driveCartesian(0, 0, 0);
				}
				
				else if(reached) {
					if (putAuto.get() < 2) {
						//put box
					}
					
					else {
						//stop box
						if(sensor_left < 500)
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
		
		
		myRobot.driveCartesian(-leftStickY * 0.8, leftStickX * 0.8, rightStickX * 0.8);
		
		System.out.println(ultra_left.getVoltage() + " " + ultra_right.getVoltage());
	}
}
