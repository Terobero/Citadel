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
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

//Citadel class'i
public class Robot extends IterativeRobot {
	//Otonomda mode degistirmek icin dashboard'da arayuz hazirlanmasi
	Command autonomousCommand;
	SendableChooser autoChooser;
	
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
	ADXRS450_Gyro gyro;
	double kP = 0.03;
	
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
	boolean buttonRopeUp; //R1
	boolean buttonRopeDown; //L1
	double triggerR2;
	
	//Ip & Gear gibi seylerde kolay anlasilsin diye
	boolean forward = true;
	boolean backward = false;
	
	//Top toplama ve beslemede on/off
	boolean startStop = false;
	
	//Otonomda her seyi sirasiyla yapsin diye kullanilan booleanlar
	boolean auto1 = true;
	boolean auto2 = true;
	boolean auto3 = true;
	boolean auto4 = true;

	//Kullandigimiz timerlar
	Timer timerAutonomous = new Timer();
	Timer goTime = new Timer();
	
	//Rope Climb & Ball Throw
	public void motorControlOnPress(SpeedController controller, boolean isPressed, boolean forwardOrBackward, double speed) {
		if (isPressed && forwardOrBackward)
			controller.set(speed);
		
		else if (isPressed && !forwardOrBackward)
			controller.set(-speed);
		
		else
			controller.set(0.0);
	}
	
	
	//Ball Feed & Ball Pickup
	public void motorControlOnSwitch(SpeedController feed, SpeedController pickup, boolean isPressed, double feedSpeed, double pickupSpeed) {
		if(triggerR2 > 0.2 && isPressed) {
				feed.set(-feedSpeed);
				pickup.set(-pickupSpeed);
		}
		//On/off yapiyor
		else if(isPressed) {
			startStop = !startStop;
		}
			

		if(startStop) {
			feed.set(feedSpeed);
			pickup.set(pickupSpeed);
		}
		else{
			feed.set(0.0);
			pickup.set(0.0);
		}
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
		autoChooser.addDefault("Blue mu red mi Front", 1); //degisecek
		autoChooser.addDefault("Blue mu red mi Front", 2); //degisecek
		autoChooser.addObject("Right (mert'in anlattigi kiminki emin degiliz), diger rengin lefti", 2); //degisecek
		autoChooser.addObject("Left rightin tersi, diger rengin righti", 3); //degisecek
		SmartDashboard.putData("Autonomous Mode Chooser", autoChooser);
	}

	@Override
	public void autonomousInit() {
		gyro = new ADXRS450_Gyro();
		gyro.reset();

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
		switch ((int) autoChooser.getSelected()) {
		case 1:
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
									motorControlOnSwitch(ballFeeder, ballThrow, true, 0.4, 1.0);
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
						motorControlOnSwitch(ballFeeder, ballThrow, true, 0.4, 1.0);
				}
			}
			break;

		case 4:
			//cok karisikmis
			if(timerAutonomous.get() < 5)
				myRobot.mecanumDrive_Cartesian(0, 0.2, 0, 0);
			break;
		}		
	}

	@Override
	public void teleopInit() {
		gyro = new ADXRS450_Gyro();
		gyro.reset();
	}

	@Override
	public void teleopPeriodic() {
		
		SmartDashboard.putNumber("Gyro Angle:", gyro.getAngle()); //realtime feedback
		
		leftStickX = stick.getRawAxis(0); 
		leftStickY = stick.getRawAxis(1); 
		rightStickX = stick.getRawAxis(4);
		triggerR2 = stick.getRawAxis(3); 
		
		myRobot.mecanumDrive_Cartesian(leftStickX, leftStickY, rightStickX, kP * gyro.getAngle());
		
		buttonA = stick.getRawButton(1); //shoot (on button press)
		buttonB = stick.getRawButton(2); //ball feed (on / off)
		buttonX = stick.getRawButton(3); //ball collect (on / off)
		buttonRopeDown = stick.getRawButton(5); //button L1 will cause the rope motor make robot go down
		buttonRopeUp = stick.getRawButton(6); //button R1 will cause the rope motor make robot go up
		
		motorControlOnPress(ropeClimb, buttonRopeUp, forward, 1.0);
		motorControlOnPress(ropeClimb, buttonRopeDown, backward, 1.0);

		motorControlOnPress(ballThrow, buttonA, forward, 1.0);
		motorControlOnSwitch(ballFeeder, ballPickUp, buttonX, 0.4, 1.0);
	}
}
