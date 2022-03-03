package ca.team5032.frc.led

import ca.team5032.frc.Perseverance
import ca.team5032.frc.drive.DriveTrain
import ca.team5032.frc.utils.DoubleProperty
import ca.team5032.frc.utils.Tabbed
import edu.wpi.first.wpilibj.motorcontrol.Spark
import edu.wpi.first.wpilibj2.command.SubsystemBase

class LEDSystem : SubsystemBase(), Tabbed {

    companion object {

        const val BLINKIN_ID = 9

        val DEFAULT_COLOUR = DoubleProperty("Default Colour", -0.93)

    }

    val blinkin = Spark(9)

    init {
        buildConfig(DEFAULT_COLOUR)
    }

    override fun periodic() {
        if (Perseverance.drive.state == DriveTrain.State.DRIVING) {
            blinkin.set(.91)
        } else {
            blinkin.set(DEFAULT_COLOUR())
        }
    }

}