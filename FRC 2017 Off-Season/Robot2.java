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
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

//Citadel class'i
public class Robot2 extends IterativeRobot {
	//Motor suruculerin initilize edilmesi 
	boolean takeGear; //Start
	boolean giveGear; //Back
	//Gear pinomatik sistemi
	DoubleSolenoid solenoid = new DoubleSolenoid(1, 2);
	Compressor comp = new Compressor();
	Joystick stick = new Joystick(0);
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
	public void teleopPeriodic() {

		giveGear = stick.getRawButton(7); //button Back will make pneumatics drop the gear
		takeGear = stick.getRawButton(8); //button Start will make pneumatics take the gear
		

			gearControlPneumatics(solenoid, takeGear, true);
			gearControlPneumatics(solenoid, giveGear, false);
		
	}
}
