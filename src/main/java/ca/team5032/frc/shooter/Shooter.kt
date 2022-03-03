package ca.team5032.frc.shooter

import ca.team5032.frc.Perseverance
import ca.team5032.frc.utils.DoubleProperty
import ca.team5032.frc.utils.Tabbed
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX
import edu.wpi.first.wpilibj2.command.SubsystemBase

class Shooter : SubsystemBase(), Tabbed {

    companion object {
        // ID of the victor for intake control.
        const val SHOOTER_ID = 0

        // The default power of the intake motor.
        val DEFAULT_POWER = DoubleProperty("Power", 0.25)
    }

    enum class State {
        SHOOTING,
        IDLE
    }

    private val shooterFalcon = WPI_TalonFX(SHOOTER_ID)

    var state: State = State.IDLE
    var power = 0.0

    init {
        if (Perseverance.debugMode) {
            tab.addString("State") { state.name }
        }

        buildConfig(DEFAULT_POWER)
    }

    override fun periodic() {
        if (power == 0.0) state = State.IDLE

        shooterFalcon.set(power)

        power = 0.0
    }

    fun shoot() {
        power = DEFAULT_POWER()
        state = State.SHOOTING
    }



}