/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.team86.mecanum;

import com.team86.mecanum.math.Vector2f;
import com.team86.mecanum.util.Util;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * In accordance with standard practice, this class, which contains the algorithms
 * needed to run the robot in autonomous mode, is kept separate from the main class.
 * This class is run as often as the <code>autonomousPeriodic()</code> method in
 * the <code>Main</code> class does.
 * @author paulb1997 & Frank & Bill- Why is Paul not here today? --
 */
public class Autonomous {
    public int autonomousState = 0; // Tracks the state of the robot.
    public Timer autonomousTimer = new Timer(); // Times the robot
    private float setPoint = 0; // 
    private int mode;    // The robot's mode of operation.
    
    /**
     * Initialize the <code>Autonomous</code> object.
     */
    public void init(){
        mode = (int)SmartDashboard.getNumber("Autonomous Mode: ");   // Gets the desired mode of operation.
        //mode = 4;
        autonomousTimer.start();    // Starts the timer.
        IO.GYRO.reset();
        setPoint = 0;
    }
    
    /**
     * Called every time the <code>autonomousPeriodic()</code> method runs.
     */
    public void update(){
        SmartDashboard.putNumber
        ("autonomousState: ", autonomousState); // Feeds the state to the Smart Dashboard.

        /*
         * Depending on the value of mode, either mode1(), mode2(), or mode3() is called.
         */
        switch(mode) {
            case 1:
                mode1();
                break;
            case 2:
              //  mode2();
                break;
            case 3:
                mode3();
                break;
            case 4:
                mode4();
                break;
            default:
                System.out.println("Invalid Autonomous Mode!");
        }
    }

    /**
     * This method contains the algorithm that allows the robot to launch a single ball preloaded in the snorfler.
     * The robot is programmed to load the ball in the shooter, move at top speed until it is
     * a certain distance from the goal, and fire the ball at the top goal , hopefully making it.
     */
    public void mode1(){
        float gyroAngle = (float)IO.GYRO.getAngle();    // The angle between the robot's position vector and the field's position vector.
        
        /*
         * Constants used to compare current times and robot distance.
         */
        final double rollerTime = 1f;
        final double setDistance = 5; //feet?
        final double driveTime = 4; //seconds
        
        /*
         * When autonomousState is 0, the snorfler's lower roller runs, loading the ball in the shooter.
         * The snorfler's lower roller will stop when the timer goes above rollerTime.
         * When autonomousState is 1, the robot will drive forward at half-speed.
         * It will stop when the robot either is setDistance away from the goal or the timer goes above driveTime.
         * When autonomousState is 2, the robot will fire the ball.
         */
        //SmartDashboard.putNumber("Timer: ", autonomousTimer.get());
        switch(autonomousState){
                case 0:
                    IO.LOW_ROLLER.set(-1);
                    if(autonomousTimer.get() >= rollerTime){
                        IO.LOW_ROLLER.set(0);
                        autonomousState = 1;
                    }
                    break;
                case 1:
                    //drive forward till we get to a certain distance from goal
                    //double sonarDistance = sonarDistance();
                    double sonarDistance = 0;
                    //SmartDashboard.putNumber("Sonar Distance: ", sonarDistance);
                    if (sonarDistance < setDistance && (autonomousTimer.get() - rollerTime <= driveTime)){
                        //drive straight
                        autonomousDrive(gyroAngle, 0, -0.15f, 1f);
                    }else{
                        autonomousDrive(gyroAngle, 0, 0, 0);
                        autonomousState = 2;
                    }
                    break;
                case 2:
                    if (fire()) autonomousState = 3;
                    break;
                default:
        }
    }

    /**
     * This method contains the algorithm that allows the robot to launch two balls.
     * The robot is programmed to pick up a ball, move at top speed until it is
     * a certain distance from the goal, and fire the ball at the top goal , hopefully making it.
     * It will repeat the above process for the second ball.
     *
    public void mode2(){
        float gyroAngle = (float)IO.GYRO.getAngle();
        
        /*
         * Constants used to compare current times and robot distance.
         *
        final double rollerTime = 1;
        final double setDistance = 5; //feet?
        final double driveTime = 4; //seconds
        final double driveTime2 = 4; //seconds
        
        final double SNORFLE_SPEED = -1;    // The speed at which the snorfler's motors run.

        /*
         * When autonomousState is 0, the snorfler's lower roller runs, loading the ball into the shooter.
         * autonomousState will change when the timer goes above rollerTime.
         * When autonomousState is 1, the snorfler's moveable section moves up and the remaining rollers are activated.
         * When autonomousState is 2, the robot will drive backwards until it loads the second ball, at which point it will stop and shut down the snorfler's rollers.
         * When autonomousState is 3, the robot will drive forward at half-speed.
         * It will stop when the robot either is setDistance away from the goal or the timer goes above driveTime2.
         * When autonomousState is 4, the robot fires the first ball.
         * When autonomousState is 5, the snorfler's lower roller runs, loading the second ball into the shooter.
         * When autonomousState is 6, the robot fires the second ball.
         * When autonomousState is 7, the snorfler's rollers are stopped.
         *
        switch(autonomousState){
                case 0:
                    IO.LOW_ROLLER.set(SNORFLE_SPEED);
                    if(autonomousTimer.get() >= rollerTime){
                        autonomousState = 1;
                    }
                    break;
                case 1:
                    IO.SNORFLER.set(true); //lift the snorfler
                    IO.HIGH_ROLLER.set(SNORFLE_SPEED);
                    IO.LEFT_SPINNER.set(SNORFLE_SPEED);
                    IO.RIGHT_SPINNER.set(-SNORFLE_SPEED);
                    autonomousState = 2;
                case 2:
                    //drive forward till we get to a certain distance from goal
                    if (!IO.BANNER_SENSOR.get() && (autonomousTimer.get() - rollerTime <= driveTime)){
                        //drive straight
                        autonomousDrive(gyroAngle, 0, 0.0f, -0.5f);
                    }else{
                        autonomousDrive(gyroAngle, 0, 0, 0);
                        autonomousState = 3;
                        IO.HIGH_ROLLER.set(0);
                        IO.LEFT_SPINNER.set(0);
                        IO.RIGHT_SPINNER.set(0);
                        IO.LOW_ROLLER.set(0);
                        autonomousState = 3;
                    }
                    break;
                case 3: 
                    //drive forward till we get to a certain distance from goal
                    //double sonarDistance = sonarDistance();
                    if (sonarDistance > setDistance && (autonomousTimer.get() - rollerTime - driveTime <= driveTime2)){
                        //drive straight
                        autonomousDrive(gyroAngle, 0, 0.0f, 0.5f);
                    }else{
                        autonomousDrive(gyroAngle, 0, 0, 0);
                        autonomousState = 4;
                    }
                case 4:
                    if (fire()) autonomousState = 5;
                    break;
                case 5:
                    IO.LOW_ROLLER.set(SNORFLE_SPEED);
                    if(IO.permitFire()){ // we have a ball in the shooter
                        autonomousState = 6;
                    }
                break;
                case 6:
                   if (fire()) autonomousState = 7;
                    break; 
                case 7: 
                    //stop rollers
                    IO.HIGH_ROLLER.set(0);
                    IO.LEFT_SPINNER.set(0);
                    IO.RIGHT_SPINNER.set(0);
                    IO.LOW_ROLLER.set(0);
                    autonomousState = 8;
                default:
        }
    }*/
    
    public void mode3(){ 
        /*
         * Constants used to compare current times and robot distance.
         */
        final double rollerTime = 1f;
        final double driveTime = 2.2f; //seconds
        final double rotateTime = 2;
        final float rotateSpeed = 0.5f;

        float gyroAngle = (float)IO.GYRO.getAngle();    // The angle between the robot's position vector and the field's position vector.
        System.out.println("autonomousState: " + autonomousState + " Mode: " + mode);
        /*
         * When autonomousState is 0, the snorfler's lower roller runs, loading the ball in the shooter.
         * The snorfler's lower roller will stop when the timer goes above rollerTime.
         * When autonomousState is 1, the robot will drive forward at half-speed.
         * It will stop when the robot either is setDistance away from the goal or the timer goes above driveTime.
         * When autonomousState is 2, the robot will fire the ball.
         */
        //SmartDashboard.putNumber("Timer: ", autonomousTimer.get());
        switch(autonomousState){
                case 0:
                    IO.LOW_ROLLER.set(-1);
                    if(autonomousTimer.get() >= rollerTime){
                        IO.LOW_ROLLER.set(0);
                        autonomousState = 1;
                    }
                    break;
                case 1:
                    //drive forward till we get to a certain distance from goal
                    if ((autonomousTimer.get() - rollerTime <= driveTime)){
                        //drive straight
                        autonomousDrive(gyroAngle, 0, -0.5f, 0f);
                    }else{
                        autonomousDrive(gyroAngle, 0, 0, 0);
                        autonomousState = 2;
                    }
                    break;
                case 2:
                    System.out.println("GyroAngle: " + gyroAngle);
                    if(Math.abs(gyroAngle) < 70) {
                       autonomousDrive(gyroAngle, rotateSpeed,0,0);
                    } else {
                        autonomousDrive(gyroAngle, 0,0,0);
                        autonomousState = 3;
                    }
                    break;
                case 3:
                    if (fire()) autonomousState = 4;
                    break;
                default:
        }
    }
    
    public void mode4(){ 
        /*
         * Constants used to compare current times and robot distance.
         */
        final double rollerTime = 1f;
        final double driveTime = 2.2f; //seconds
        final double liftTime = 1;
        
        final float rotateSpeed = 0.5f;

        float gyroAngle = (float)IO.GYRO.getAngle();    // The angle between the robot's position vector and the field's position vector.
        System.out.println("autonomousState: " + autonomousState + " Mode: " + mode);
        /*
         * When autonomousState is 0, the snorfler's lower roller runs, loading the ball in the shooter.
         * The snorfler's lower roller will stop when the timer goes above rollerTime.
         * When autonomousState is 1, the robot will drive forward at half-speed.
         * It will stop when the robot either is setDistance away from the goal or the timer goes above driveTime.
         * When autonomousState is 2, the robot will fire the ball.
         */
        //SmartDashboard.putNumber("Timer: ", autonomousTimer.get());
        switch(autonomousState){
                case 0:
                    IO.LOW_ROLLER.set(-1);
                    if(autonomousTimer.get() >= rollerTime){
                        autonomousState = 1;
                    }
                    break;
                case 1:
                    IO.SNORFLER.set(true);
                    IO.LOW_ROLLER.set(-1f);
                    IO.HIGH_ROLLER.set(-1f);
                    if(autonomousTimer.get() - rollerTime >= liftTime)
                        autonomousState = 2;
                    break;
                case 2:
                    //drive forward till we get to a certain distance from goal
                    if ((autonomousTimer.get() - rollerTime - liftTime <= driveTime)){
                        //drive straight
                        autonomousDrive(gyroAngle, 0, -0.5f, 0f);
                        if(IO.BANNER_SENSOR.get()) {
                            IO.LOW_ROLLER.set(0);
                            IO.HIGH_ROLLER.set(0);
                        }
                    }else{
                        autonomousDrive(gyroAngle, 0, 0, 0);
                        autonomousState = 3;
                    }
                    break;
                case 3:
                    System.out.println("GyroAngle: " + gyroAngle);
                    if(Math.abs(gyroAngle) < 70) {
                       autonomousDrive(gyroAngle, rotateSpeed,0,0);
                    } else {
                        autonomousDrive(gyroAngle, 0,0,0);
                        autonomousState = 4;
                    }
                    break;
                case 4:
                    if (fire()) autonomousState = 5;
                    break;
                case 5:
                    IO.LOW_ROLLER.set(-1);
                    IO.HIGH_ROLLER.set(-1);
                    System.out.println("Timer: " + (autonomousTimer.get() - rollerTime - liftTime - driveTime - 1.2));
                    if(autonomousTimer.get() - rollerTime - liftTime - driveTime - 1.2 > 2.3) {
                        autonomousState = 6;
                    }
                    break;
                case 6:
                    if(IO.permitFire())
                        if (fire()) autonomousState = 7;
                    break;
                default:
        }
    }

    /**
     * Gets the distance measured by the ultrasonic sensor.
     * @return 
     * The distance measured by the ultrasonic sensor.
     *
    public double sonarDistance(){
        final double CONVERSION_FACTOR = 0.149352; // This value allows for converting from volts to feet.
        double sonarDistance = IO.SONAR.getVoltage() / CONVERSION_FACTOR;   // The sonar's voltage output is directly proportional to the measured distance.
        return sonarDistance;
     }*/

    /**
     * 
     * @param gyroAngle
     * @param x
     * @param y 
     */
    public void autonomousDrive(float gyroAngle, float rotation, float x, float y){
        float rot = rotation;
        Vector2f joystick = new Vector2f((float)x, (float)y);
        joystick = joystick.rotate(90);
        //rotation = IO.LEFT_JOYSTICK.getThrottle();
        //double rightJoystick = Util.scaledJoystickOutput(IO.RIGHT_JOYSTICK.getY());
        //SmartDashboard.putNumber("Rotation", rotation);

       if (rot > -0.05 && rot < 0.05){ //if I'm in the deadband
            System.out.println("Correcting");
            float error = setPoint - (float)gyroAngle;
            //SmartDashboard.putNumber("Error", error);
            //if (Math.abs(error) < SmartDashboard.getNumber("Error Deadband")) error = 0;
            rot = error * 0.017f;
            rot = Math.min(rot, 1);
            rot = Math.max(rot, -1);
            }else{  //let joystick do what it wants
                setPoint = (float)gyroAngle;
        }

        IO.ROBOT_DRIVE.mecanumDrive_Cartesian(joystick.getX(), joystick.getY(), rot, gyroAngle);
    }

    /*
     * 
     */
    private Timer timer = new Timer();
    private final double TRIGGER_RESET_TIME = 1f;
    private final double FIRE_RESET_TIME = 0.1f;
    private int state = 0;
    
    /*
     * 
     */
    private boolean fire(){
        switch(state){
            case 0:
                if(IO.permitFire()) {
                    IO.TRIGGER.set(true);
                    timer.reset(); //TODO: check redundancy
                    timer.start();
                    state++;
                }
                break;
            case 1:
                if(timer.get() > FIRE_RESET_TIME){
                    state++;
                    IO.FIST_OF_DEATH.set(true);
                }
                break;
            case 2:
                if(timer.get() > TRIGGER_RESET_TIME + FIRE_RESET_TIME){
                    state = 0;
                    IO.TRIGGER.set(false);
                    IO.FIST_OF_DEATH.set(false);
                    timer.stop();
                    return true;
                }
                break;
            default:
                System.out.println("Error: Invalid State");
        }
        return false;
    }
}