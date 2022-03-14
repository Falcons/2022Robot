package ca.team5032.frc.subsystems

import ca.team5032.frc.utils.CLIMB_ID
import ca.team5032.frc.utils.DoubleProperty
import ca.team5032.frc.utils.Subsystem
import ca.team5032.frc.utils.Tabbed
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX
import edu.wpi.first.wpilibj.DigitalInput


class Climb : Subsystem<Climb.State>("Climb", State.Idle), Tabbed {

    companion object {
        // Default power for climb
        val DEFAULT_POWER = DoubleProperty("Power", 0.50)
    }

    sealed class State {
        object Up : State()
        object Down : State()
        object Idle : State()
    }

    private val climbFalcon = WPI_TalonFX(CLIMB_ID)

    // Limit switches.
    private val bottomSensor = DigitalInput(0)
    private val topSensor = DigitalInput(1)

    init {
        climbFalcon.setNeutralMode(NeutralMode.Brake)

        buildConfig(DEFAULT_POWER)
    }

    override fun periodic() {
        state.let { // Mutex lock so doesnt destroy itself.
            if ((!bottomSensor.get() && it is State.Down) || (!topSensor.get() && it is State.Up)) {
                climbFalcon.set(0.0)
                return
            }

            when (it) {
                is State.Up -> climbFalcon.set(DEFAULT_POWER.value)
                is State.Down -> climbFalcon.set(-DEFAULT_POWER.value)
                is State.Idle -> climbFalcon.set(0.0)
            }
        }

        stop()
    }

    fun up() = setState(State.Up)
    fun down() = setState(State.Down)
    fun stop() = setState(State.Idle)

}