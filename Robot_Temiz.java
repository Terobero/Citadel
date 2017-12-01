package org.usfirst.frc.team6303.robot;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Robot_Temiz extends IterativeRobot {
	//Motor suruculeri
	Spark motorLeft1 = new Spark(2);
	Spark motorLeft2 = new Spark(1);
	Spark motorRight1 = new Spark(0);
	Spark motorRight2 = new Spark(3);
	Spark ropeClimb = new Spark(7); //degisicek
	Spark ballPickUp = new Spark(4); //degisicek
	Spark ballFeeder = new Spark(9); //degisicek
	Spark ballThrow = new Spark(6); //degisicek
	Jaguar gearTake = new Jaguar(8); //degisicek
	
	ADXRS450_Gyro gyro;
	static double kP = 0.03;
	
	RobotDrive myRobot = new RobotDrive(motorLeft1, motorLeft2, motorRight1, motorRight2);
	Joystick stick = new Joystick(0);
	Timer gearTimer = new Timer();
	
	double leftStickX;
	double leftStickY;
	double rightStickX;
	
	//Joystickin butonlari
	boolean buttonA;
	boolean buttonB;
	boolean buttonX;
	boolean buttonY;
	boolean buttonRopeUp; //R1
	boolean buttonRopeDown; //L1
	boolean takeGear; //Start
	boolean giveGear; //Back
	double triggerR2;
	
	//Ip & Gear gibi seylerde kolay anlasilsin diye
	boolean forward = true;
	boolean backward = false;
	
	boolean startStopCollect = false; //true for start, false for stop
	boolean startStopFeed = false; //true for start, false for stop
	
	//Gear pinomatik sistemi
	DoubleSolenoid solenoid = new DoubleSolenoid(1, 2);
	Compressor comp = new Compressor();

	
	public void gearControlOnPress(SpeedController controller, boolean isPressed, boolean forwardOrBackward) {
		if (isPressed && forwardOrBackward && gearTimer.get() < 0.5) //it takes 0.5 seconds to drop gear collection
			controller.set(1.0);
		
		else if(isPressed && !forwardOrBackward && gearTimer.get() < 1.0) //it takes 1.0 second to pickup gear collection
			controller.set(-1.0);
		
		else {
			controller.set(0.0);
			gearTimer.reset();
			gearTimer.start();
		}
	}
	
	
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
	public void motorControlOnSwitch(SpeedController controller, boolean isPressed, boolean feedOrCollect, double speed) {
		if(triggerR2 > 0.2) {
			if(isPressed)
				controller.set(-speed);
			
			else 
				controller.set(speed);
		}
		
		else {
			if(isPressed) {
				if(feedOrCollect)
					startStopFeed = !startStopFeed;
				
				else
					startStopCollect = !startStopCollect;
			}
			
			if((feedOrCollect && startStopFeed) || (!feedOrCollect && startStopCollect))
				controller.set(speed);
			
			else
				controller.set(0.0);
		}
	}
	
	//Gear Pinomatik
	public void gearControlPneumatics(DoubleSolenoid solenoid, boolean isPressed, boolean take) {
		if(isPressed && take)
			solenoid.set(DoubleSolenoid.Value.kForward);
			
		else if(isPressed && !take)
			solenoid.set(DoubleSolenoid.Value.kReverse);
			
		else
			solenoid.set(DoubleSolenoid.Value.kOff);
	}
	
	@Override
	public void robotInit() {
		motorRight1.setInverted(true);
	}

	@Override
	public void teleopInit() {
		gyro = new ADXRS450_Gyro();
		gyro.reset();
		gearTimer.reset();
	}

	@Override
	public void teleopPeriodic() {
		SmartDashboard.putNumber("Gyro Angle:", gyro.getAngle());
		
		leftStickX = stick.getRawAxis(0); 
		leftStickY = stick.getRawAxis(1); 
		rightStickX = stick.getRawAxis(4);
		triggerR2 = stick.getRawAxis(3); 
		
		myRobot.mecanumDrive_Cartesian(leftStickX, leftStickY, rightStickX, kP * gyro.getAngle()); // 0 should be changed with gyro.getAngle() if we wish to do a field oriented drive.
		
		buttonA = stick.getRawButton(1); //shoot (on button press)
		buttonB = stick.getRawButton(2); //ball feed (on / off)
		buttonX = stick.getRawButton(3); //ball collect (on / off)
		buttonY = stick.getRawButton(4); //gear collection (on / off)
		buttonRopeDown = stick.getRawButton(5); //button L1 will cause the rope motor make robot go down
		buttonRopeUp = stick.getRawButton(6); //button R1 will cause the rope motor make robot go up
		giveGear = stick.getRawButton(7); //button Back will make pneumatics drop the gear
		takeGear = stick.getRawButton(8); //button Start will make pneumatics take the gear
		
		motorControlOnPress(ropeClimb, buttonRopeUp, forward, 1.0);
		motorControlOnPress(ropeClimb, buttonRopeDown, backward, 1.0);

		motorControlOnPress(ballThrow, buttonA, forward, 1.0);
			
		gearControlOnPress(gearTake, buttonY, forward);
		gearControlOnPress(gearTake, buttonY, backward);
			
		motorControlOnSwitch(ballPickUp, buttonX, false, 0.4);	
		motorControlOnSwitch(ballFeeder, buttonB, true, 0.4);
		
		gearControlPneumatics(solenoid, takeGear, forward);
		gearControlPneumatics(solenoid, giveGear, backward);
	}
}
