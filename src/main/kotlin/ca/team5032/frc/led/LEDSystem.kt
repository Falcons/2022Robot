package ca.team5032.frc.led

import ca.team5032.frc.Perseverance
import ca.team5032.frc.subsystems.Shooter
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
        // TODO: Nice colours!
        when (Perseverance.shooter.state) {
            is Shooter.State.AtSpeed -> blinkin.set(.73) // green
            is Shooter.State.RampingUp -> blinkin.set(.83) // blue
            is Shooter.State.Idle -> blinkin.set(.61) // red
        }
    }

}