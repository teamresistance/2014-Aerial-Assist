/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.team86.mecanum;

import edu.wpi.first.wpilibj.Timer;

/**
 *
 * @author Frank
 */
public class FistOfDeath {
    private Timer timer = new Timer();
    private boolean fired = false;
    private final double TRIGGER_RESET_TIME = 2f;
    private final double FIRE_RESET_TIME = 0.3f;
    private int state = 0;
    
    private boolean previouslyFired = false;
    
    public void update() {
        fire();
    }
    
    private void fire(){
        switch(state){
            case 0:
                if(!previouslyFired && IO.BUTTON_TRIGGER.get() && IO.permitFire()) {
                    IO.TRIGGER.set(true);
                    timer.reset(); //TODO: check redundancy
                    timer.start();
                    state++;
                }
                break;
            case 1:
                if(timer.get() > FIRE_RESET_TIME){
                    state++;
                    //timer.reset();
                    IO.FIST_OF_DEATH.set(true);
                }
                break;
            case 2:
                if(timer.get() > TRIGGER_RESET_TIME + FIRE_RESET_TIME){
                    state = 0;
                    IO.TRIGGER.set(false);
                    
                    IO.FIST_OF_DEATH.set(false);
                    timer.stop();
                }
                break;
            default:
                System.out.println("Error: Invalid State");
        }
    }
}