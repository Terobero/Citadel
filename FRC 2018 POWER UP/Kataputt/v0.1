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
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.VictorSP;
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
	VictorSP frontLeft = new VictorSP(0);
	VictorSP rearLeft = new VictorSP(1);
	VictorSP frontRight = new VictorSP(2);
	VictorSP rearRight = new VictorSP(3);
	
	Spark catapult = new Spark(4);
	
	SpeedControllerGroup left = new SpeedControllerGroup(frontLeft, rearLeft);
	SpeedControllerGroup right = new SpeedControllerGroup(frontRight, rearRight);

	DifferentialDrive myRobot = new DifferentialDrive(left, right);

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
	
	boolean can_throw = true;
	Timer throw_timer = new Timer();

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
		//0 - 10 - 20
		
		case 0: //left duz
			if (timerAuto.get() < 3)
				myRobot.arcadeDrive(ams, -aps);
			
			else if (timerAuto.get() < 3 + 500/cm_per_s)
				myRobot.arcadeDrive(ams, 0);
			
			else
				myRobot.arcadeDrive(0, 0);
			
			break;
			
			
			
		case 10: //middle
			
			if (centerXs.length > 0)
				x = x / centerXs.length;
			
			if (timerAuto.get() < 120/cm_per_s)
				myRobot.arcadeDrive(ams, 0);

			else if(gameData.charAt(0) == 'L') {
				if(sensors)
					if(sensor_left < 500)
						myRobot.arcadeDrive(aps + 0.1, 0);
				
				else if(!reached){
					if (currentArea >= 1000) { //degisecek (max kamera boyutunun birazi falan)
						reached = true;
						putAuto.reset();
						putAuto.start();
					}
					
					else if(x < 150)
						myRobot.arcadeDrive(0, -aps);
					
					else if(x > 170)
						myRobot.arcadeDrive(0, aps);
					
					else
						myRobot.arcadeDrive(0, -aps);
				}
				
				else if(last) {
					if (lastTimer.get() < 300/cm_per_s) 
						myRobot.arcadeDrive(ams, 0);
					
					else
						myRobot.arcadeDrive(0, 0);
				}
				
				else if(reached) {
					if (putAuto.get() < 2) {
						//put box
					}
					
					else {
						//stop box
						if(sensors) {
							if(sensor_left < 500)
								myRobot.arcadeDrive(0, -ams + 0.1);
						}
						
						else if(putAuto.get() < 2 + 300/cm_per_s)
							myRobot.arcadeDrive(0, -ams + 0.1);
						
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
				myRobot.arcadeDrive(ams, aps);
			
			else if (timerAuto.get() < 3 + 500/cm_per_s)
				myRobot.arcadeDrive(ams, 0);
			
			else
				myRobot.arcadeDrive(0, 0);
			
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

		buttonA = stick.getRawButton(1);
		buttonB = stick.getRawButton(2);
		buttonX = stick.getRawButton(3);
		buttonY = stick.getRawButton(4);
		
	
		if (buttonA && can_throw) {
			can_throw = false;
			throw_timer.reset();
			throw_timer.start();
		}
		
		if(!can_throw && throw_timer.get() < 0.8)
			catapult.set(1.0);
		else if(!can_throw && throw_timer.get() < 1.6)
			catapult.set(-1.0);
		else {
			can_throw = true;
			catapult.set(0);
		}
		
		if(buttonB)
			catapult.set(-1.0);
		
		if(buttonX)
			catapult.set(1.0);
		
		myRobot.arcadeDrive(leftStickY, leftStickX);


	}
	
}
