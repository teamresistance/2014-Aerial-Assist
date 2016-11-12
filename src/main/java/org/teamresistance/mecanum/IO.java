package org.teamresistance.mecanum;

import org.teamresistance.mecanum.sensors.CustomADXL345;

import edu.wpi.first.wpilibj.ADXL345_I2C;
import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.interfaces.Gyro;

/**
 * This class encapsulates all inputs and outputs needed by the program.
 * @author Frank McCoy
 */
public class IO {
    
    /*
     * Don't construct this class.
     */
    private IO() {}
    
    /*
     * May need to change these so that it can work better with the defaults.
     * The left and right joysticks are used by the actual driver of the robot.
     * The far joystick is used by the co-driver.
     */
    public static final Joystick LEFT_JOYSTICK = new Joystick(1);
    public static final Joystick RIGHT_JOYSTICK = new Joystick(2);
    public static final Joystick FAR_JOYSTICK = new Joystick(3);
    
    /*
     * These likely need to be changed for the robot; this will suck to change.
     * These constructors assume the default digital IO slot is used.
     * These Victors control the motors that drive the wheels.
     */
    public static final Victor LEFT_FRONT = new Victor(1);
    public static final Victor RIGHT_FRONT = new Victor(2);
    public static final Victor LEFT_BACK = new Victor(3);
    public static final Victor RIGHT_BACK = new Victor(4);
    
    /*
     * This object controls the robot's motion. It replaces the provided MecanumDrive
     * class, which provides similar functionality.
     */
    public static RobotDrive ROBOT_DRIVE =
            new RobotDrive(RIGHT_BACK, RIGHT_FRONT, LEFT_BACK, LEFT_FRONT);
    
    /*
     * The Gyro object allows for the robot's angle relative to the field to be
     * measured. The constant GYRO_CHANNEL is the channel associated with the 
     * gyroscope.
     */
    public static final int GYRO_CHANNEL = 1;
    public static final Gyro GYRO = new AnalogGyro(GYRO_CHANNEL);
    
    /*
     * Since there is no dedicated object that represents the ultrasonic sensor,
     * an AnalogChannel object provides the means of accessing the ultrasonic
     * sensor's readings. The constant SONAR_CHANNEL is the channel associated 
     * with the ultrasonic sensor.
     */
    public static final int SONAR_CHANNEL = 4;
    public static final AnalogInput SONAR = new AnalogInput(SONAR_CHANNEL);
    
    //public static final Sonar SONAR = new Sonar(1);
    
    /*
     * This object provides readings from the accelerometer. It replaces the 
     * provided ADXL345_I2C class, which provides similar functionality.
     */
    public static final CustomADXL345 ACCELEROMETER =
            new CustomADXL345(I2C.Port.kOnboard, ADXL345_I2C.Range.k2G);
    
    /*
     * These Victors control the motors on the snorfler.
     */
    public static final Victor LOW_ROLLER = new Victor(5);
    public static final Victor HIGH_ROLLER = new Victor(6);
    public static final Victor LEFT_SPINNER = new Victor(7);
    public static final Victor RIGHT_SPINNER = new Victor(8);
    
    /*
     * The compressor provides additional compressed air into the system,
     * negating the use of a large reserve tank.
     */
    public static final Compressor COMPRESSOR = new Compressor();
    
    /*
     * The banner sensor is a photoelectric sensor that detects the presence of
     * the ball. It outputs a digital signal represented by the DigitalInput
     * object.
     */
    public static final DigitalInput BANNER_SENSOR = new DigitalInput(2);
    
    /*
     * This DigitalInput object provides a reading from a limit switch mounted 
     * below the ball holder (the 'tusks'). The 
     */
    public static final DigitalInput PERMIT_FIRE = new DigitalInput(3);
    
    /*
     * These solenoids control the firing mechanism, known as the "Fist of Death".
     * The trigger solenoid controls the part that prevents the Fist of Death from firing.
     * The Fist of Death solenoid controls the piston that fires the mechanism.
     */
    public static final Solenoid FIST_OF_DEATH = new Solenoid(1);
    public static final Solenoid TRIGGER = new Solenoid(2);
    
    /*
     * This solenoid controls the snorfler's moveable section; it makes it either
     * move up or down.
     */
    public static final Solenoid SNORFLER = new Solenoid(3);
    
    /*
     * These buttons control the automatic loading and firing process.
     */
    public static final Button BUTTON_TRIGGER = new JoystickButton(FAR_JOYSTICK, 1);
    public static final Button BUTTON_SNORFLE = new JoystickButton(FAR_JOYSTICK, 4);
    public static final Button BUTTON_LOAD = new JoystickButton(FAR_JOYSTICK, 5);
     
    /*
     * These buttons provide manual control of the snorfler with the exception
     * of the first and last buttons. The first button controls the position of
     * the snorfler's moveable section; the last button causes the snorfler to
     * spin the ball without either loading or ejecting it. (Use it as a taunt.)
     */
    public static final Button BUTTON_SNORFLE_POSITION = new JoystickButton(FAR_JOYSTICK, 3);
    public static final Button BUTTON_SNORFLE_SUCK = new JoystickButton(FAR_JOYSTICK, 10);
    public static final Button BUTTON_SNORFLE_UNLOAD = new JoystickButton(FAR_JOYSTICK, 11);
    public static final Button BUTTON_SNORFLE_AWESOME = new JoystickButton(FAR_JOYSTICK, 2);
    
    public static final Button BUTTON_CANCEL = new JoystickButton(FAR_JOYSTICK, 6);
    
    public static final Button BUTTON_LIGHT = new JoystickButton(FAR_JOYSTICK, 7);
    
    public static final DigitalOutput RED_CHANNEL = new DigitalOutput(6);
    public static final DigitalOutput GREEN_CHANNEL = new DigitalOutput(7);
    public static final DigitalOutput BLUE_CHANNEL = new DigitalOutput(8);
    
    /**
     * Used to make all stuff that uses the limit switch positive logic(Active high)
     * rather than negative logic(active low)
     * @return 
     * a boolean value representing the inverse of the <code>PERMIT_FIRE</code>
     * limit switch.
     */
    public static boolean permitFire() {
        return !PERMIT_FIRE.get();
    }
    
}