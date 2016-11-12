package org.teamresistance.mecanum.util;

/*
*Purpose: Holds all utility methods
*Date created: 10/6/2013
*Date last modified: 10/6/2013
*Authors/Editors: Frank
*/

public class Util {
    
    //Allows the joystck's output to be scaled
    public static double scaledJoystickOutput(double input){
        /*
        if (Math.abs(input) < 0.15) {
            return 0;
        } else if(input > 0) {
            return input <= 0.8 && input >= 0.05 ? ((input * 0.55) + 0.0725) :  ((input * 2.4375) - 1.4375);
        } else {
            return input >= -0.8 && input <= -0.05 ? ((input * 0.55) - 0.0725) : ((input * 2.4375) + 1.4375);
        }       
        */
        return input;
        /*
        if(Math.abs(input) <= 0.1) 
            return 0;
        if(input < 0 )
            return input + 0.1;
        if(input > 0)
            return input - 0.1;
        
        return input;
                */
    }
}
