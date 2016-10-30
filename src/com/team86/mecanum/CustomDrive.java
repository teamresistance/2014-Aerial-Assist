/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.team86.mecanum;

import com.team86.mecanum.math.Vector2d;
import edu.wpi.first.wpilibj.*;

public class CustomDrive {

    private Victor leftFront;
    private Victor rightFront;
    private Victor leftBack;
    private Victor rightBack;
    
    public CustomDrive(int LF, int RF, int LB, int RB) {
        leftFront = new Victor(LF);
        rightFront = new Victor(RF);
        leftBack = new Victor(LB);
        rightBack = new Victor(RB);
    }
    
    public void mecDrive(double x, double y, double rotation, double gyroAngle) {
        
        double speed = new Vector2d(x, y).length();
        
       // double v1 = speed * Math.sin()
        
    }
}
