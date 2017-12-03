package org.usfirst.frc.team6303.robot;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

//Citadel class'i
public class Robot extends IterativeRobot {
	//Otonomda mode degistirmek icin dashboard'da arayuz hazirlanmasi
	Command autonomousCommand;
	SendableChooser autoChooser;
	public int mode = 1;
	
	//Motor suruculeri
	Spark motorLeft1 = new Spark(2);
	Spark motorLeft2 = new Spark(1);
	Spark motorRight1 = new Spark(0);
	Spark motorRight2 = new Spark(3);
	Spark ropeClimb = new Spark(7);
	Spark ballPickUp = new Spark(4);
	Spark ballFeeder = new Spark(9);
	Spark ballThrow = new Spark(6);

	//Gyro ve gyro ile carptigimiz value
	ADXRS450_Gyro gyro = new ADXRS450_Gyro();
	double kP = 0.05;
	
	//Robot drive ve joystick
	RobotDrive myRobot = new RobotDrive(motorLeft1, motorLeft2, motorRight1, motorRight2);
	Joystick stick = new Joystick(0);
	
	//Kamera'dan value almak icin
	NetworkTable table;
	double centerX;
	double area;
	
	//Kumandanin'in sag ve sol cubuklarinin valuelari
	double leftStickX;
	double leftStickY;
	double rightStickX;
	
	//Kumandanin butonlarini
	boolean buttonA;
	boolean buttonB;
	boolean buttonX;
	boolean buttonY;
	boolean triggerL1;
	double triggerL2;
	boolean triggerR1;
	double triggerR2;
	
	//Ip & Gear gibi seylerde kolay anlasilsin diye
	boolean forward = true;
	boolean backward = false;
	
	//Top toplama ve beslemede on/off
	boolean feedStartStop = false;
	boolean pickupStartStop = false;
	boolean throwStartStop = false;
	
	//Otonomda her seyi sirasiyla yapsin diye kullanilan booleanlar
	boolean auto1 = true;
	boolean auto2 = true;
	boolean auto3 = true;
	boolean auto4 = true;
	boolean auto5 = true;

	//Kullandigimiz timerlar
	Timer timerAutonomous = new Timer();
	Timer goTime = new Timer();
	Timer feedThrow = new Timer();
	
	//Rope Climb & Ball Throw
	public void motorControlOnPress(SpeedController controller, boolean isPressed, boolean forwardOrBackward, double speed) {
		if (isPressed && forwardOrBackward)
			controller.set(speed);
		
		else if (isPressed && !forwardOrBackward)
			controller.set(-speed);
		
		else
			controller.set(0.0);
	}
	
	public void throwBall(SpeedController controller, boolean isPressed, double speed) {
		if(buttonX)
			controller.set(-speed);
		
		else {
			if(isPressed) {
				throwStartStop = !throwStartStop;
				feedThrow.reset();
				feedThrow.start();
			}
			
			if(throwStartStop)
				controller.set(speed);
			
			else
				controller.set(0.0);
		}
	}
	
	
	//Ball Feed & Ball Pickup
	public void motorControlOnSwitch(SpeedController feed, SpeedController pickup, boolean feedIsPressed, boolean pickupIsPressed, double feedSpeed, double pickupSpeed, boolean feedAuto) {
		//On/off yapiyor
			if(buttonA)
				feed.set(-feedSpeed);
			
			else if(feedIsPressed)
				feedStartStop = !feedStartStop;
			
			if((feedThrow.get() >= 0.5 && feedStartStop) || (feedThrow.get() >= 0.5 && feedAuto))
				feed.set(feedSpeed);
			
			else
				feed.set(0.0);
			
			
			if(buttonB)
				pickup.set(-pickupSpeed);
			
			else if(pickupIsPressed)
				pickupStartStop = !pickupStartStop;
			
			if(pickupStartStop && !buttonB)
				pickup.set(pickupSpeed);
			
			else
				pickup.set(0.0);
		
	}
	
	@Override
	public void robotInit() {
		//Ters motorlari duzeltiyor
		myRobot.setSafetyEnabled(false);
		motorRight1.setInverted(true);
		motorRight2.setInverted(true);

		//Network Table kameradan value aliyor (GRIP'ten)
		table = NetworkTable.getTable("GRIP/myContoursReport");

		//Otonomda mod secme arayuzu olusturma
		autoChooser = new SendableChooser();
		autoChooser.addObject("bluenun solu/ redin sagi ama redde top atmiyor", 4); //degisecek
		autoChooser.addDefault("direk duz gidiyor", 5); //degisecek
		SmartDashboard.putData("Autonomous Mode Chooser", autoChooser);
	}

	@Override
	public void autonomousInit() {
		gyro.reset();
		mode = (int) autoChooser.getSelected();

		timerAutonomous.reset();
		timerAutonomous.start();
	}

	@Override
	public void autonomousPeriodic() {

		double[] defaultValue = new double[0];
		double[] areas = table.getNumberArray("area", defaultValue);
		double[] centerXs = table.getNumberArray("centerX", defaultValue);
		
		for (double a : areas) {
			area = a;
			SmartDashboard.putNumber("Area:", area); //for realtime feedback
		}                    
		for (double x : centerXs) {
			centerX = x;
			SmartDashboard.putNumber("Center X: ", centerX); //for realtime feedback
		}
		
		//NOT: caseleri switch halinde yazarsak daha temiz gorunur orn: switch(int a) case 1: ilk sey (bitince a++) case 2: ikinci sey.......
		switch (mode) { //autoChooser.getSelected() 
		/*case 1:
			if(auto1) {
				if(timerAutonomous.get() < 1.0) { //degisecek
					myRobot.mecanumDrive_Cartesian(0, 0.2, 0, 0);
				}

				else {
					if(centerX >= 150 && centerX <= 170 && area <= 60000 && area >= 100) //Full area: 76800, camera is 320 x 240
						myRobot.mecanumDrive_Cartesian(0, 0.2, 0, 0);

					else if(centerX < 150 && area <= 60000 && area >= 100)
						myRobot.mecanumDrive_Cartesian(-0.2, 0, 0, 0);

					else if(centerX > 170 && area <= 60000 && area >= 100)
						myRobot.mecanumDrive_Cartesian(0.2, 0, 0, 0);

					else 
						auto1 = false;
				}
			}

			else {
				if(auto2) {
					if(auto3) {
						goTime.reset();
						goTime.start();
						auto3 = false;
					}

					else {
						if(goTime.get() <= 3.0 && goTime.get() >= 2.0) //degisecek 2 saniye orada durucak
							myRobot.mecanumDrive_Cartesian(0, -0.2, 0, 0);

						else
							auto2 = false;
					}
				}

				else {
					if(gyro.getAngle() <= 90)
						myRobot.mecanumDrive_Cartesian(0, 0, 0.2, 0);

					else {
						if(auto4) {
							goTime.reset();
							goTime.start();
							auto4 = false;
						}

						else {
							if(goTime.get() < 3) //degisecek
									myRobot.mecanumDrive_Cartesian(0.2, 0, 0, 0);

							else {
								if(gyro.getAngle() <= 135) 
									myRobot.mecanumDrive_Cartesian(0, 0, 0.2, 0);

								else {
									myRobot.mecanumDrive_Cartesian(0, 0, 0, 0);
									motorControlOnSwitch(ballFeeder, ballThrow, true, true, 0.4, 1.0, true);
								}
							}
						}
					}
				}
			}
			break;

		case 2:
			//ters tarafin frontu
			break;

		case 3:
			if(auto1) {
				if(timerAutonomous.get() <= 2.0) { //degisecek
					myRobot.mecanumDrive_Cartesian(0, 0.2, 0, 0);
				}

				else if(gyro.getAngle() >= -45){ //degisecek
					myRobot.mecanumDrive_Cartesian(0, 0, -0.2, 0);
				}

				else {
					if(centerX >= 150 && centerX <= 170 && area <= 60000 && area >= 100) //Full area: 76800, camera is 320 x 240
						myRobot.mecanumDrive_Cartesian(0, 0.2, 0, 0);

					else if(centerX < 150 && area <= 60000 && area >= 100)
						myRobot.mecanumDrive_Cartesian(-0.2, 0, 0, 0);

					else if(centerX > 170 && area <= 60000 && area >= 100)
						myRobot.mecanumDrive_Cartesian(0.2, 0, 0, 0);

					else 
						auto1 = false;
				}
			}

			else {
				if(auto2) {
					if(auto3) {
						goTime.reset();
						goTime.start();
						auto3 = false;
					}

					else {
						if(goTime.get() <= 3.0 && goTime.get() >= 2.0) //degisecek 2 saniye orada durucak
							myRobot.mecanumDrive_Cartesian(0, -0.2, 0, 0);

						else
							auto2 = false;
					}
				}

				else {
					if(gyro.getAngle() <= 180)
						myRobot.mecanumDrive_Cartesian(0, 0, 0.2, 0);

					else
						motorControlOnSwitch(ballFeeder, ballThrow, true, true, 0.4, 1.0, true);
				}
			}
			break;
*/
		case 4:
			if(timerAutonomous.get() < 2.6) {
				myRobot.mecanumDrive_Cartesian(0, -0.3, 0, 0);
			}
			
			else {
				if (gyro.getAngle() <= 26) { 
					//timerAutonomous.stop();
					myRobot.mecanumDrive_Cartesian(0, 0, 1.0, 0);
				}
				else if(timerAutonomous.get() < 2.6 + 2.4 + 0.22) {
					myRobot.mecanumDrive_Cartesian(0, -0.3, 0, 0);
				}
				else if(timerAutonomous.get() < 2.6 + 2.4 + 3 + 0.22)
					myRobot.mecanumDrive_Cartesian(0, 0, 0, 0);
				else if(timerAutonomous.get() < 2.6 + 2.3 + 3 + 2.3 + 0.22)
					myRobot.mecanumDrive_Cartesian(0, 0.3, 0, 0);
				else if(gyro.getAngle() <= 206)
					myRobot.mecanumDrive_Cartesian(0, 0, 1.0, 0);
				else {
					myRobot.mecanumDrive_Cartesian(0, 0, 0, 0);
					motorControlOnSwitch(ballFeeder, ballPickUp, true, false, 0.5, 0, true);
					throwBall(ballThrow, true, 0.8);
				}
			break;
			}
			
			case 5:
				if(timerAutonomous.get() < 2.8)
					myRobot.mecanumDrive_Cartesian(0, -0.3, 0, 0);
				
				else
					myRobot.mecanumDrive_Cartesian(0, 0, 0, 0);
			}
	}

	@Override
	public void teleopInit() {
		
	}

	@Override
	public void teleopPeriodic() {
		
		//SmartDashboard.putNumber("Gyro Angle:", gyro.getAngle()); //realtime feedback
		
		leftStickX = stick.getRawAxis(0); 
		leftStickY = stick.getRawAxis(1); 
		rightStickX = stick.getRawAxis(4);
		//triggerL2 = stick.getRawAxis(2);
		triggerR2 = stick.getRawAxis(3); 
		
		myRobot.mecanumDrive_Cartesian(leftStickX, leftStickY, rightStickX, 0);
		
		buttonA = stick.getRawButton(1); //shoot (on button press)
		buttonB = stick.getRawButton(2); //ball feed (on / off)
		buttonX = stick.getRawButton(3); //ball collect (on / off)
		triggerL1 = stick.getRawButton(5);
		triggerR1 = stick.getRawButton(6);
		
			
		//motorControlOnPress(ropeClimb, triggerL2 > 0.2, forward, 1.0);
		motorControlOnPress(ropeClimb, triggerL1, backward, 1.0);

		throwBall(ballThrow, triggerR1, 0.8);
		motorControlOnSwitch(ballFeeder, ballPickUp, triggerR1, triggerR2 > 0.2, 0.4, 1.0, false);
	}
}