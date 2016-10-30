/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.team86.mecanum.sensors;

import com.team86.mecanum.math.Vector2f;
import com.team86.mecanum.math.Vector3f;
import edu.wpi.first.wpilibj.ADXL345_I2C;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author Jim Hofmann - Mentor Team 86j
 */
public class CustomADXL345 extends ADXL345_I2C {
    
    private final float gravity = 32.17f;
    private Vector3f kp = new Vector3f(0,0,0);
       
    private final static Timer accTimer = new Timer();
    
    private Vector3f previousAcceleration = new Vector3f(0,0,0);
    private Vector3f velocity = new Vector3f(0.0f, 0.0f, 0.0f);
    private float deltaTime = 0;
    private float deadBand = .5f;
    private float correctionFactor = .75f;
    
    public CustomADXL345(int moduleNumber, ADXL345_I2C.DataFormat_Range range) {
        super(moduleNumber, range);
        accTimer.start();
        SmartDashboard.putBoolean("Reset Accel", false);
        SmartDashboard.putNumber("X", 0);
        SmartDashboard.putNumber("Y", 0);
        SmartDashboard.putNumber("Z", 0);
        SmartDashboard.putNumber("Correction Factor", 0);
    }

    public void reset() {
        accTimer.reset();
        velocity = new Vector3f(0,0,0);
        previousAcceleration = new Vector3f(0,0,0);
        deltaTime = 0;
        AllAxes axes = getAccelerations();
        kp.setX((float)axes.XAxis);
        kp.setY((float)axes.YAxis);
        kp.setZ((float)axes.ZAxis);
    }
    
    public void initialize() {
        /*
        SmartDashboard.putNumber("Dead Band", 0.25);
        SmartDashboard.putNumber("Correction Factor", 0.75);
        */
        SmartDashboard.putNumber("Weight", 0.75);
        Vector3f tempKP = new Vector3f(0,0,0);
        for(int i = 0; i < 10; i++) {
            AllAxes axes = getAccelerations();
            tempKP = tempKP.add(new Vector3f((float)axes.XAxis, (float)axes.YAxis, (float)axes.ZAxis));
            try {
                Thread.sleep(30);
            } catch(Exception e) {
                
            }
        }
        kp = tempKP.div(10);
    }
    
    public void update() {
        float weight = 0.75f;//(float)SmartDashboard.getNumber("Weight");
       // deadBand = (float)SmartDashboard.getNumber("Dead Band");
        //correctionFactor = (float)SmartDashboard.getNumber("Correction Factor");
        
        if(SmartDashboard.getBoolean("Reset Accel"))
            reset();
        
        deltaTime = (float)accTimer.get();
        accTimer.reset();
        
        AllAxes axes = getAccelerations();
        
        //acceleration = new Vector3f(((float)axes.XAxis - kp.getX()) * gravity,
        //                            ((float)axes.YAxis - kp.getY()) * gravity,
        //                            ((float)axes.ZAxis - kp.getZ()) * gravity);
        
        
        Vector3f currentAcceleration =
                    new Vector3f(((float)axes.XAxis - kp.getX()) * gravity,
                                ((float)axes.YAxis - kp.getY()) * gravity,
                                ((float)axes.ZAxis - kp.getZ()) * gravity);
        
        //System.out.println("DT: " + deltaTime);
        
        Vector3f weightedAcceleration = previousAcceleration.mul(weight).add(currentAcceleration.mul(1 - weight));
        velocity = velocity.add(deadBand(weightedAcceleration).mul(deltaTime));
        velocity = correctVelocity(velocity, weightedAcceleration);
        /*
        velocity = new Vector3f((float)SmartDashboard.getNumber("X") * deltaTime,
                                (float)SmartDashboard.getNumber("Y") * deltaTime,
                                (float)SmartDashboard.getNumber("Z") * deltaTime);
                */
        previousAcceleration = weightedAcceleration;
    }

    private Vector3f deadBand(Vector3f velocity) {
        Vector3f temp = velocity;
        
        if(Math.abs(temp.getX()) < deadBand)
            temp.setX(0);
        if(Math.abs(temp.getY()) < deadBand)
            temp.setY(0);
        if(Math.abs(temp.getZ()) < deadBand)
            temp.setZ(0);
        
        return temp;
    }
    
    int counterX = 0;
    int counterY = 0;
    int counterZ = 0;
    public Vector3f correctVelocity(Vector3f velocity, Vector3f acceleration) {
        Vector3f temp = velocity;
        int maxCount = 20;
        
        if(acceleration.getX() == 0) {
            counterX++;
            if(counterX >= maxCount)
                temp.setX(temp.getX() * correctionFactor);
        } else 
            counterX = 0;
        
        if(acceleration.getY() == 0) {
           counterY++;
            if(counterY >= maxCount)
                temp.setY(temp.getY() * correctionFactor);
        } else
            counterY = 0;
        
        if(acceleration.getZ() == 0) {
           counterZ++;
            if(counterZ >= maxCount)
                temp.setZ(temp.getZ() * correctionFactor);
        } else 
            counterZ = 0;
        
        return temp;
    }
    public Vector3f getAccelerationVec3f(){
        return previousAcceleration;
    }
    
    public Vector3f getVelocityVec3f() {
        return velocity;
    }  

    public Vector2f getVelocityVec2f() {
        return new Vector2f(velocity.getX(), velocity.getY());
    }
    
    public float getDeltaTime() {
        return deltaTime;
    }
    
    public float getDeadBand() {
        return deadBand;
    }
    
    public void setDeadBand(float deadBand) {
        this.deadBand = deadBand;
    }
    
}
