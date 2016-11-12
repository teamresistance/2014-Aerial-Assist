First of Death
---------------

> Robot code for the 2014 FRC robotics competition.

This code is **archived**. This branch contains the project retrofitted to run on the roboRIO.

### RoboRIO Port
For the code to run on the roboRIO, the old sunspotfrc SDK was swapped for the new 2016 WPILib and NetworkTables. The code itself escaped mostly unscathed, as the majority of the classes affected by the update turned out to be unused and only needed to be tweaked to where we could compile them.

The build system has been updated to Gradle to supply the latest WPI libraries. **The code should deploy out of the box.** After cloning the project, run the `deploy` Gradle task. If it fails, check the IP address listed in the `buid.gradle` file.