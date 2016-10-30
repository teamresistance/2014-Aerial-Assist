/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package com.team86.mecanum;

import com.team86.mecanum.util.Util;
import com.team86.mecanum.math.Vector2f;
import com.team86.mecanum.math.Vector3f;
import edu.wpi.first.wpilibj.ADXL345_I2C;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.Random;

/**
 * This class forms the foundation of the robot program. The CompactRIO control
 * system that operates the robot continuously runs this class, calling the
 * <code>autonomousPeriodic()</code>, <code>teleopPeriodic()</code>, and 
 * <code>testPeriodic()</code> methods roughly 50 times per second; the
 * frequency of the method calls depends on those of the driver station packets.
 * By extending the IterativeRobot class, the 
 * 
 */
public class Main extends IterativeRobot {
   
    /*
     * This object, custom-made, represents a vector in an R2 vector space.
     * This particular object stands for a vector that is oriented perpendicularly
     * towards the goal directly opposite the robot.
     */
    private Vector2f fieldPosition = new Vector2f(0,0);
    
    /*
     * The "snorfler" is the ball retrieval, loading, and removal mechanism.
     * The name snorfler is intentionally nonsensical.
     * A dedicated class encapsulates the algorithms that control the snorfler.
     */
    private Snorfler snorfler = new Snorfler();
    
    /*
     * The "Fist of Death" is the piston/spring mechanism that imparts linear 
     * momentum to the ball. The name is a reference to the "Dilbert" comic strip.
     * As with the Snorfler class, a dedicated class encapsulates the algorithms
     * that control the Fist of Death.
     */
    private FistOfDeath fistOfDeath = new FistOfDeath();
    
    /*
     * The DriveTrain class encapsulates the algorithms that take signal from the
     * joysticks and converts it into movement.
     */
    private DriveTrain driveTrain = new DriveTrain();
    private boolean snorflerLatch = false;  //
    
    /*
     * The Autonomous class encapsulate the algorithms that allow the robot to
     * run autonomously.
     */
    private Autonomous autonomous = new Autonomous();
    
    /**
     * This method initialises all robot components. It is called once when the
     * robot starts up.
     */
    public void robotInit() {
        //IO.ROBOT_DRIVE.setExpiration(0.1f);
        
        //IO.GYRO.setSensitivity(0.00785);
        
        /*
         * These motors' drive directions are inverted depending on their position
         * on the robot. This is a side effect of using a mecanum drivetrain.
         */
        IO.ROBOT_DRIVE.setInvertedMotor(CustomDrive1.MotorType.kFrontRight, true); //true
        IO.ROBOT_DRIVE.setInvertedMotor(CustomDrive1.MotorType.kFrontLeft, false); //false
        IO.ROBOT_DRIVE.setInvertedMotor(CustomDrive1.MotorType.kRearRight, true); 
        IO.ROBOT_DRIVE.setInvertedMotor(CustomDrive1.MotorType.kRearLeft, false);
        //IO.ACCELEROMETER.reset();
        
        IO.COMPRESSOR.start();  // Activates the compressor.
        
        IO.TRIGGER.set(false);  // Turns off the solenoid controlling the trigger.
        IO.FIST_OF_DEATH.set(false);    // Turns off the solenoid controlling the "Fist of Death".
        
        SmartDashboard.putBoolean("Pressure Switch: ", IO.COMPRESSOR.getPressureSwitchValue());
        
        //IO.SNORFLER.set(true);
        
        //TODO: Determine direction of snorfler motors

    }
    /**
     * This method runs once to initialise all objects needed for autonomous operation.
     */
    public void autonomousInit(){
        IO.COMPRESSOR.start();
        autonomous.init();  // Initialises the Autonomous object.
    }
     
    
    /**
     * This method is called every time the CompactRIO loops through this class;
     * it only executes when the driver station commands the robot to run in 
     * autonomous mode.
     */
    
    public void autonomousPeriodic() {
        
        autonomous.update();
        
    }

    /**
     * This method runs once to initialise all objects needed for teleoperation.
     */
    public void teleopInit() {
        driveTrain.init();
        //IO.ACCELEROMETER.reset();
        //IO.ACCELEROMETER.initialize();
        
        /*
         * These Smart Dashboard fields are for debugging and will most likely be removed in the final product.
         */
        SmartDashboard.putNumber("Error Deadband", 0.5);
        SmartDashboard.putNumber("Error kP", 0.018);
        SmartDashboard.putNumber("Gyro Angle", 0);
    }
    
    Random random = new Random();
    static final Timer randomTimer = new Timer();
    static {
        randomTimer.start();
    }
    /**
     * This method is called every time the CompactRIO loops through this class;
     * it only executes when the driver station commands the robot to run in
     * teleoperated mode.
     */
    public void teleopPeriodic() {
        System.out.println("Gyro Angle: " + IO.GYRO.getAngle());
        updateLights();
        //System.out.println(IO.BUTTON_LIGHT.get());
        /*
        double sonarDistance = IO.SONAR.getDistance();
        SmartDashboard.putNumber("sonar Distance: ", sonarDistance);
        if(sonarDistance >= 6 && sonarDistance <= 10) {
            randomTimer.reset();
            randomTimer.stop();
            randomTimer.start();
            IO.RED_CHANNEL.set(false);
            IO.GREEN_CHANNEL.set(true);
            IO.BLUE_CHANNEL.set(false);
        } else {
            if(randomTimer.get() > 1) {
                IO.RED_CHANNEL.set(random.nextDouble() > 0.5f);
                IO.GREEN_CHANNEL.set(false);
                IO.BLUE_CHANNEL.set(random.nextDouble() > 0.5f);
                randomTimer.reset();
                randomTimer.stop();
                randomTimer.start();
            }   
        }
        */
        float gyroAngle = (float)IO.GYRO.getAngle();    // Gets the gyroscope's current angle.
        driveTrain.update(gyroAngle);   // Sends this angle to the drivetrain.
        System.out.println("Gyro Angle: " + gyroAngle); // Prints out the gyroscope's current angle to the console for debugging purposes.
        SmartDashboard.putNumber("Gyro Angle: ", gyroAngle);
        IO.ACCELEROMETER.update();  // Have the accelerometer sample another acceleration value.
        calculatePosition(gyroAngle);   //  Using the gyroscope's current angle, calculate the robot's position vector relative to the field.
        
        
        //Vector3f acceleration = IO.ACCELEROMETER.getAccelerationVec3f();
        //SmartDashboard.putNumber("Acceleration X: ", acceleration.getX());
        //SmartDashboard.putNumber("Acceleration Y: ", acceleration.getY());
        //SmartDashboard.putNumber("Acceleration Z: ", acceleration.getZ());
        
       // Vector3f velocity = IO.ACCELEROMETER.getVelocityVec3f();
        //SmartDashboard.putNumber("Velocity X: ", velocity.getX());
       // SmartDashboard.putNumber("Velocity Y: ", velocity.getY());
        //SmartDashboard.putNumber("Velocity Z: ", velocity.getZ());
        
        //double sonarDistance = sonarDistance(); // Gets the distance measured by the ultrasonic sensor.
        //SmartDashboard.putNumber("Sonar Distance: ", sonarDistance);    // Sends this distance value to the Smart Dashboard.
        
        snorfler.update();  // Updates the snorfler's state.
        fistOfDeath.update();   // Updates the Fist of Death's state.
        
    }
    
    /*
     * Given the angle between the robot's position vector and the field's position vector,
     * this method will calculate the robot's velocity relative to the field.
     */
    private void calculatePosition(float gyroAngle) {
        /*
         * If the user requests a reset of the field position vector, it is
         * reset to the value initially set when the robot starts up.
         */
        if(IO.LEFT_JOYSTICK.getRawButton(7))
            fieldPosition = new Vector2f(0,0);
        
        Vector2f velocity = IO.ACCELEROMETER.getVelocityVec2f();    // Gets the robot's instantaneous velocity.
        Vector2f fieldOrientedVelocity = velocity.rotate((float)IO.GYRO.getAngle());    // Projects the robot's instaneous velocity on the field position vector.
        
        //SmartDashboard.putNumber("Oriented Velocity X: ", fieldOrientedVelocity.getX());
        //SmartDashboard.putNumber("Oriented Velocity Y: ", fieldOrientedVelocity.getY());
        
        /*
         * Adds the robot's displacement vector to the field position vector.
         * The robot's displacement is determined by multiplying the 
         * (almost) instantaneous velocity by the time interval used to calculate
         * the velocity.
         */
        fieldPosition = fieldPosition.add(fieldOrientedVelocity.mul(IO.ACCELEROMETER.getDeltaTime()));
        
        /*
         * The displacement of the robot in both the x and y axes is fed to the
         * Smart Dashboard.
         */
        SmartDashboard.putNumber("Position X: ", fieldPosition.getX());
        SmartDashboard.putNumber("Position Y: ", fieldPosition.getY());
        
    }
    /**
     * Periodic code for test mode should go here. Users should override this 
     * method for code which will be called periodically at a regular rate while
     * the robot is in test mode.
     * This method is not used.
     */
    public void testPeriodic() {
    
    }
    
    /**
     * Gets the distance measured by the ultrasonic sensor.
     * @return 
     * The distance measured by the ultrasonic sensor.
     */
    public double sonarDistance() {
        final double CONVERSION_FACTOR = 0.149352; // This value allows for converting from volts to feet.
        double sonarDistance = IO.SONAR.getVoltage() / CONVERSION_FACTOR;   // The sonar's voltage output is directly proportional to the measured distance.
        return sonarDistance;
    }
    
    private void updateLights() {
        double sonarDistance = sonarDistance();
        SmartDashboard.putNumber("Sonar Distance", sonarDistance);
        if(sonarDistance > 6 && sonarDistance < 10) {
            IO.RED_CHANNEL.set(false);
            IO.GREEN_CHANNEL.set(true);
            IO.BLUE_CHANNEL.set(false);
        } else {
            IO.RED_CHANNEL.set(false);
            IO.GREEN_CHANNEL.set(false);
            IO.BLUE_CHANNEL.set(false);
        }
        IO.RED_CHANNEL.set(true);
        IO.GREEN_CHANNEL.set(false);
        IO.BLUE_CHANNEL.set(true);
    }
}