/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.team86.mecanum;

import com.team86.mecanum.math.Vector2f;
import com.team86.mecanum.util.Util;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This class contains the algorithms that allow the robot to drive.
 * This class is run as often as its calling methods in the <code>Main</code>
 * class are.
 * @author Frank McCoy
 */
public class DriveTrain {
    
    /*
     * The angle measure, in degrees,  between the robot's position vector and 
     * the game field's position vector. The game field's position vector is 
     * defined as the vector orthogonal to the imaginary line delineated by the
     * truss directed towards the goal opposite the operators' current position.
     */
    private float setPoint = 0; 
    
    public void init() {
        IO.GYRO.reset();
        setPoint = 0;
        
    }
    
    /**
     * Drives the robot according to its current position.
     * 
     * @param gyroAngle 
     * The angle measured by the gyroscope, which represents the robot's current
     * position.
     */
    public void update(float gyroAngle) {
        /*
         * The driver can reset the robot's initial position. In other words, the
         * robot's position relative to the field at the moment the driver presses
         * button 6 on the left joystick becomes its new initial position.
         */
        if(IO.LEFT_JOYSTICK.getRawButton(6)){
            IO.GYRO.reset();
            setPoint = 0;
        }
        
        teleopDrive(gyroAngle); // Calls the method that contains the driving algorithm.
    }
    
    /*
     * This method contains the algorithm that allows the robot to drive.
     */
    private void teleopDrive(float gyroAngle) {
        double x = Util.scaledJoystickOutput(IO.LEFT_JOYSTICK.getX());  // Gets the x-value from the joystick.
        double y = Util.scaledJoystickOutput(IO.LEFT_JOYSTICK.getY());  // Gets the y-vlaue from the joystick.
        Vector2f joystick = new Vector2f((float)x, (float)y);   // Constructs a vector representing the desired velocity vector.
        joystick = joystick.rotate(-90);    // Projects the vector onto the field position vector.
        double rotation = Util.scaledJoystickOutput(IO.RIGHT_JOYSTICK.getX());  // Gets the x-value from the joystick; it represents the desired rotation.
        //rotation = IO.LEFT_JOYSTICK.getThrottle();
        //double rightJoystick = Util.scaledJoystickOutput(IO.RIGHT_JOYSTICK.getY());
        
        //SmartDashboard.putNumber("Rotation", rotation); // Display the rotation rate for debugging purposes.
        
        /*
         * This if-else block controls whether or not joystick input is valid.
         * If the joystick value is in the deadband, apply a P transformation to
         * the discrepency between the robot's current position relative to the 
         * field and its position upon startup.
         * Otherwise, let the joystick value through; the aforementioned discrepency
         * is left unmodified.
         */
        if (rotation > -0.1 && rotation < 0.1){
            System.out.println("Correcting");
            
            /*
             * Determines the discrepency between the robot's current position
             * and its position upon startup.
             */
            float error = setPoint - (float)gyroAngle;
            //SmartDashboard.putNumber("Error", error);   // Outputs the discrepency to the Smart Dashboard for debugging
            /*
             * If the discrepency is less than a predetermined number (the "Error
             * Deadband") the discrepency is essentially zero. This prevents the
             * robot from attempting to compensate for an infinitesimal discrepency.
             */
            if (Math.abs(error) < 0.5) error = 0;
            rotation = error * 0.017;    // Multiply the error by 
            rotation = Math.min(rotation, 1);
            rotation = Math.max(rotation, -1);
        }else{
            setPoint = (float)gyroAngle;
        }
        
        IO.ROBOT_DRIVE.mecanumDrive_Cartesian(joystick.getX(), joystick.getY(), rotation, gyroAngle);
        
        /*
        SmartDashboard.putNumber("x", joystick.getX());
        SmartDashboard.putNumber("y", joystick.getY());
        SmartDashboard.putNumber("rotation", rotation);
        SmartDashboard.putNumber("gyroAngle", gyroAngle);
        */
    }
    
    
    
}