package ca.team5032.frc.led

import ca.team5032.frc.Perseverance
import ca.team5032.frc.subsystems.Shooter.State.*
import ca.team5032.frc.utils.BLINKIN_ID
import ca.team5032.frc.utils.DoubleProperty
import ca.team5032.frc.utils.Tabbed
import edu.wpi.first.wpilibj.motorcontrol.Spark
import edu.wpi.first.wpilibj2.command.SubsystemBase

class LEDSystem : SubsystemBase(), Tabbed {

    companion object {

        val DEFAULT_COLOUR = DoubleProperty("Default Colour", -0.93)

    }

    private val blinkin = Spark(BLINKIN_ID)

    init {
        buildConfig(DEFAULT_COLOUR)
    }

    override fun periodic() {
        when (Perseverance.shooter.state) {
            is RampingUp -> blinkin.set(.91)
            is AtSpeed -> blinkin.set(.73)
            is Idle -> blinkin.set(-.39)
        }
    }

}