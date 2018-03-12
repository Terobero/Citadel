package org.usfirst.frc.team6303.robot;



import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.AnalogInput;
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
	//Otonomda mode degistirmek icin dashboard'da arayuz hazirlanmasi
	SendableChooser<Integer> autoChooser;
	SendableChooser<Boolean> sensorChooser;
	public int mode = 1;
	public boolean sensors = true;
	
	
	//Solenoid solenoid = new Solenoid(1);
	//Compressor comp = new Compressor();
	

	//Motor suruculeri
	VictorSP frontLeft = new VictorSP(0);
	VictorSP rearLeft = new VictorSP(2);
	VictorSP frontRight = new VictorSP(1);
	VictorSP rearRight = new VictorSP(4);
	
	Spark catapult = new Spark(3);
	
	/*Spark ele_up = new Spark(5);
	Spark ele_down = new Spark(3);
	
	Spark wheel1 = new Spark(6);
	Spark wheel2 = new Spark(7);*/
	
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
	double ams = 0.6; //auto_max_speed
	double aps = 0.3; //auto_precise_speed
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
	
	//DoubleSolenoid pistonLeft = new DoubleSolenoid(5,4);
	//DoubleSolenoid pistonRight = new DoubleSolenoid(7,6);
	
	
	/*VisionThread visionThread;
	double centerX = 0.0;
	double area = 0.0;
	
	Object imgLock = new Object();*/

	
	
	
	@Override
	public void robotInit() {
		
		//inst = NetworkTableInstance.getDefault();
		//table = inst.getTable("GRIP/myContoursReport");
		
		rearLeft.setInverted(true);
		//gyro.calibrate();

		autoChooser = new SendableChooser<Integer>();
		autoChooser.addObject("Left Duz", 0);
		autoChooser.addObject("Left Switch (Robot Ters Basliyo)", 1);
		
		autoChooser.addDefault ("Middle", 10);
		
		autoChooser.addObject("Right Duz", 20);
		autoChooser.addObject("Right Switch (Robot Ters Basliyo)", 21);
		
		SmartDashboard.putData("Autonomous Mode Chooser", autoChooser);
		
		sensorChooser = new SendableChooser<Boolean>();
		
		sensorChooser.addObject("On", true);
		sensorChooser.addDefault("Off", false);
		
		
		SmartDashboard.putData("Sensor", sensorChooser);
		
		//ultra.setAutomaticMode(true);
		
		/*UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
	    camera.setResolution(320, 240);
	    
	    visionThread = new VisionThread(camera, new GripPipeline(), pipeline -> {
	        if (!pipeline.filterContoursOutput().isEmpty()) {
	            Rect r = Imgproc.boundingRect(pipeline.filterContoursOutput().get(0));
	            synchronized (imgLock) {
	                centerX = r.x + (r.width / 2);
	                area = r.x * r.y;
	            }
	        }
	    });
	    visionThread.start();*/


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
		
		double x = 0;
		double area = 0;
	    /*synchronized (imgLock) {
	        x = this.centerX;
	        area = this.area;
	    }*/
		
		
		
		// switch is on right side
		/*if (gameData.charAt(0) == 'R') {
			while (timerAuto.get() < 5) {
				myRobot.driveCartesian(0, -0.1, 0);
			}
			
		}
		
		// switch is on left side
		else if (gameData.charAt(0) == 'L') {
			
		}*/
		switch(autoChooser.getSelected()) {
		//0,1 - 10 - 20, 21
		
		case 0: //left duz
			if(gameData.charAt(0) == 'R') {
				if (timerAuto.get() < 5)
					myRobot.driveCartesian(0, -ams, 0);
				
				else if (timerAuto.get() < 8){
					//catapult.set(1.0);
					myRobot.driveCartesian(0, 0, 0);
				}
				else {
					catapult.set(0.0);
				}
				
				break;
			}
			else {
				if (timerAuto.get() < 3)
					myRobot.driveCartesian(-aps, -ams, 0);
				
				else if (timerAuto.get() < 3 + 500/cm_per_s)
					myRobot.driveCartesian(0, -ams, 0);
				
				else if (timerAuto.get() < 3 + 800/cm_per_s){
					myRobot.driveCartesian(0, 0, 0);
					//catapult.set(-1.0);
				}
				else if(timerAuto.get() < 3 + 1000/cm_per_s)
					catapult.set(0.0);
				
				break;
			}
			
			
			
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
			
		/*	
		case 10: //middle
			
			
			if (timerAuto.get() < 120/cm_per_s)
				myRobot.driveCartesian(0, ams, 0);

			else if(gameData.charAt(0) == 'L') {
				if(sensors)
					if(sensor_left < 500)
						myRobot.driveCartesian(0, aps + 0.1, 0);
				
				else if(!reached){
					if (area >= 320 * 24) { //degisecek (max kamera boyutunun birazi falan)
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
			
		*/
			
		
		case 10:
			
			if(gameData.charAt(0) == 'L') {
				if(timerAuto.get() < 2.0)
					myRobot.driveCartesian(-0.5, 0, 0);
				else if(timerAuto.get() < 6.0) {
					myRobot.driveCartesian(0, -0.5, 0);
					catapult.set(-0.7);
				}
				else if(timerAuto.get() < 8.0) {
					myRobot.driveCartesian(0, 0, 0);
					catapult.set(1.0);
				}
				else
					catapult.set(0.0);
			}
			
			if(gameData.charAt(0) == 'R') {
				if(timerAuto.get() < 2.0)
					myRobot.driveCartesian(0.5, 0, 0);
				else if(timerAuto.get() < 6.0) {
					myRobot.driveCartesian(0, -0.5, 0);
					catapult.set(-0.7);
				}
				else if(timerAuto.get() < 8.0) {
					myRobot.driveCartesian(0, 0, 0);
					catapult.set(1.0);
				}
				else
					catapult.set(0.0);
			}
			
			break;
			
			
		case 20: //right duz
			if(gameData.charAt(0) == 'R') {
				if (timerAuto.get() < 4)
					myRobot.driveCartesian(0, -ams, 0);
				
				else if (timerAuto.get() < 6){
					catapult.set(1.0);
					myRobot.driveCartesian(0, 0, 0);
				}
				else {
					catapult.set(0.0);
				}
				
				break;
			}
			else {
				if (timerAuto.get() < 3)
					myRobot.driveCartesian(aps, -ams, 0);
				
				else if (timerAuto.get() < 3 + 500/cm_per_s)
					myRobot.driveCartesian(0, -ams, 0);
				
				else
					myRobot.driveCartesian(0, 0, 0);
				
				break;
			}
			
		case 21: //right switch
			break;
		}
		
		
		
		
	}

	
	@Override
	public void teleopInit() {
		
	}

	
	@Override
	public void teleopPeriodic() {
		leftStickX = stick.getRawAxis(4); //emre otay olacak ise right stick x ile degistir
		leftStickY = stick.getRawAxis(1);
		triggerL2 = stick.getRawAxis(2);
		triggerR2 = stick.getRawAxis(3);
		
		/*triggerR2 = 0.5 + triggerR2/2;
		triggerR2 = 1 - triggerR2;
		if (triggerR2 < 0.4)
			triggerR2 = 0.4;
		else if(triggerR2 > 0.9)
			triggerR2 = 0.9;*/

		rightStickX = stick.getRawAxis(0);
		rightStickY = stick.getRawAxis(5);
		buttonA = stick.getRawButton(1);
		buttonB = stick.getRawButton(2);
		buttonX = stick.getRawButton(3);
		buttonY = stick.getRawButton(4);
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
		
		/*if(leftStickX > 0.1 || leftStickX < -0.1) {
			frontRight.set(leftStickX*0.82);
			frontLeft.set(-leftStickX* 0.8);
			rearRight.set(-leftStickX* 0.82);
			rearLeft.set(leftStickX* 0.8);
		}
		else*/
			myRobot.driveCartesian(rightStickX * 0.8, leftStickY * -0.8, -leftStickX * 0.8);
		
		if(buttonB)
			catapult.set(-1.0);
		
		else if(buttonX) 
			catapult.set(1.0);
		
		else
			catapult.set(0.0);
		
		
		/*if(triggerR2 > 0.3 && buttonA) {
			ele_up.set(-1.0);
		}
		
		else if(buttonA) {
			ele_down.set(-1.0);
		}
		
		if(triggerR2 > 0.3 && buttonY) {
			ele_up.set(1.0);
		}
		
		else if(buttonY) {
			ele_down.set(1.0);
		}
		
		if(buttonX){
			pistonLeft.set(DoubleSolenoid.Value.kForward);
			pistonRight.set(DoubleSolenoid.Value.kForward);
		}
		
		else if(triggerR2 > 0.3 && buttonX){
			pistonLeft.set(DoubleSolenoid.Value.kReverse);
			pistonRight.set(DoubleSolenoid.Value.kReverse);
		}
		
		else{
			pistonLeft.set(DoubleSolenoid.Value.kOff);
			pistonRight.set(DoubleSolenoid.Value.kOff);
		}
		
		if(buttonB) {
			wheel1.set(1.0);
			wheel2.set(1.0);
		}
		else if(triggerR2 > 0.3 && buttonB) {
			wheel1.set(-1.0);
			wheel2.set(-1.0);
		}
		else {
			wheel1.set(0);
			wheel2.set(0);
		}*/
		
		
		
		
		
		
		
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
		
		
		
	}
}
