/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.teamresistance.mecanum;

import edu.wpi.first.wpilibj.Timer;

/**
 *
 * @author Frank
 */
public class Snorfler {
    private boolean snorflerLatch = false;
    private int snorflerState = 0;
    private final Timer snorflerTimer = new Timer();
    private boolean snorflerPosition = false;
    private boolean snorflePositionChange = false;
    
    private static final float SNORFLE_SPEED = -1;
    private static final float REVERSE_SNORFLE_SPEED = 1;
    private static final float LOAD_SPEED = -1.0f;
    
    public void update() {
        
        snorflePosition();
        IO.SNORFLER.set(snorflerPosition);
        
        manualOverrides();
        
        if(IO.BUTTON_SNORFLE.get() && !snorflerLatch){
            //Run the timer only when needed
            if (snorflerState == 2) 
                snorflerTimer.start();
            
            //Start updating the snorfler
            snorflerLatch = true;
        //Load ball into shooter when there isn't already a ball in the shooter, 
        //and the there is a ball currently in the snorfler
        }else if(IO.BUTTON_LOAD.get() && !IO.permitFire() && !snorflerLatch && snorflerState == 2){
            //Go to state 3 when snorlfer  updates
            snorflerState = 3;
            //Start updating snorfler
            snorflerLatch = true;
        }
        
        if(snorflerLatch){
            snorfle();
        }
    }
    
    private void snorfle(){
        //States

        //System.out.println("snorflerState: " + snorflerState);
        switch(snorflerState) {
            case 0: //0 snorfling/sucking HR 1 LR 1
                if(IO.BUTTON_CANCEL.get()) {
                    snorflerLatch = false;
                    stopMotors();
                    break;
                }
                IO.HIGH_ROLLER.set(SNORFLE_SPEED);
                IO.LOW_ROLLER.set(SNORFLE_SPEED);
                IO.LEFT_SPINNER.set(SNORFLE_SPEED);
                IO.RIGHT_SPINNER.set(-SNORFLE_SPEED);
                snorflerPosition = true;
                if(IO.BANNER_SENSOR.get()) snorflerState = 1;
                
                break;
            case 1: //1 holding/waiting HR 0 HR 0
                IO.HIGH_ROLLER.set(0);
                IO.LOW_ROLLER.set(0);
                IO.LEFT_SPINNER.set(0);
                IO.RIGHT_SPINNER.set(0);
                if (!IO.BUTTON_SNORFLE.get() && !IO.BUTTON_TRIGGER.get()) {
                    snorflerLatch = false;
                    snorflerState = 2;
                }
                break;
            case 2: //2 reversing HR -1 LR -1
               if (snorflerTimer.get() >= 2){
                    snorflerState = 0;
                    snorflerTimer.stop();
                    IO.HIGH_ROLLER.set(0);
                    IO.LOW_ROLLER.set(0);
                    IO.LEFT_SPINNER.set(0);
                    IO.RIGHT_SPINNER.set(0);
                    snorflerLatch = false;
                } else {
                    IO.HIGH_ROLLER.set(REVERSE_SNORFLE_SPEED);
                    IO.LOW_ROLLER.set(REVERSE_SNORFLE_SPEED);
                    IO.LEFT_SPINNER.set(0); //reverse later?
                    IO.RIGHT_SPINNER.set(0); //reverse later?
                }
                break;
            case 3: //3 loading HR 0 LR .5
                IO.LEFT_SPINNER.set(0);
                IO.RIGHT_SPINNER.set(0);
                if (IO.permitFire()) {
                    snorflerState = 0;
                    IO.HIGH_ROLLER.set(0);
                    IO.LOW_ROLLER.set(0);
                    snorflerLatch = false;
                } else {
                    IO.HIGH_ROLLER.set(LOAD_SPEED);
                    IO.LOW_ROLLER.set(LOAD_SPEED);
                }
                break;
            default:
                IO.HIGH_ROLLER.set(0);
                IO.LOW_ROLLER.set(0);
                IO.LEFT_SPINNER.set(0);
                IO.RIGHT_SPINNER.set(0);
        }
    }

    private void snorflePosition() {
        //if the button was just pressed and the banner sensor is not active(No ball in snorfler)
        //lower the snorlfer
        if(IO.BUTTON_SNORFLE_POSITION.get() && !snorflePositionChange && !IO.BANNER_SENSOR.get()) {
            snorflerPosition = !snorflerPosition;
            snorflePositionChange = true;
        } else if(!IO.BUTTON_SNORFLE_POSITION.get() && snorflePositionChange){
            snorflePositionChange = false;
        }
    }
    
    private void manualOverrides() {
        //Could run into a problem with the motors not returning to previous 
        //state properly after one of these buttons was pressed
        if(IO.BUTTON_SNORFLE_SUCK.get() && snorflerPosition) {
            IO.HIGH_ROLLER.set(1);
            IO.LOW_ROLLER.set(1);
        } else if(IO.BUTTON_SNORFLE_UNLOAD.get() && snorflerPosition) {
            IO.HIGH_ROLLER.set(-1);
            IO.LOW_ROLLER.set(-1); 
        } else if(IO.BUTTON_SNORFLE_AWESOME.get() && snorflerPosition) {
             IO.HIGH_ROLLER.set(-1);
            IO.LOW_ROLLER.set(1); 
        } else {
            IO.HIGH_ROLLER.set(0);
            IO.LOW_ROLLER.set(0); 
        }
    }
    
    private void stopMotors() {
        IO.HIGH_ROLLER.set(0);
        IO.LOW_ROLLER.set(0);
        IO.LEFT_SPINNER.set(0);
        IO.RIGHT_SPINNER.set(0);
        
    }
}
