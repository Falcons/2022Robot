package ca.team5032.frc.utils

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab

interface Tabbed {

    val tab: ShuffleboardTab
        get() = Shuffleboard.getTab(this.javaClass.simpleName)

}