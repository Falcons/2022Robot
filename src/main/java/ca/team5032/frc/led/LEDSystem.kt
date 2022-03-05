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

    val blinkin = Spark(0)

    init {
        buildConfig(DEFAULT_COLOUR)
    }

    override fun periodic() {
        if (!Perseverance.climb.bottomSensor.get()) {
            if (!Perseverance.climb.topSensor.get()) {
                blinkin.set(.73)
            } else {
                blinkin.set(.91)
            }
        } else {
            if (!Perseverance.climb.topSensor.get()) {
                blinkin.set(.61)
            } else {
                blinkin.set(.93)
            }
        }
    }

}