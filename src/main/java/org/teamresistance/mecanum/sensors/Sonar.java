package org.teamresistance.mecanum.sensors;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Timer;

public class Sonar {
    
    private final int ADRESS = 0xE1;
    private I2C i2c;
    
    public Sonar(I2C.Port port) {
        i2c = new I2C(port, ADRESS);
    }
    
    //Returns Feet!
    public double getDistance() {
        byte[] info = new byte[2];
        i2c.write(0xE0, 0x51); // System.out.println(!i2c.write(0xE0, 0x51));
        Timer.delay(0.05); // 0.05 works
        i2c.read(0xE1, info.length, info); // System.out.println(!i2c.read(0xE1, info.length, info));
        return accelFromBytes(info[1], info[0]) / 30.48;
    }
    
    private int accelFromBytes(byte first, byte second) {
        short tempLow = (short) (first & 0xff);
        short tempHigh = (short) ((second << 8) & 0xff00);
        return tempLow | tempHigh;
    }
    
}
