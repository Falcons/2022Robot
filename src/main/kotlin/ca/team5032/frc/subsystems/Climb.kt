package ca.team5032.frc.subsystems

import ca.team5032.frc.utils.*
import ca.team5032.frc.utils.motor.Falcon500
import com.ctre.phoenix.motorcontrol.NeutralMode
import edu.wpi.first.wpilibj.DigitalInput


class Climb : Subsystem<Climb.State>("Climb", State.Idle), Tabbed {

    companion object {
        // Default power for climb
        val MAIN_POWER = DoubleProperty("Power",  1.0)

        val PIVOT_POWER = DoubleProperty("Pivot Power", 0.3)
    }

    sealed class State {
        object Up : State()
        object Down : State()
        object Idle : State()
    }

    private val climbFalcon = Falcon500(CLIMB_MAIN_ID, null)

    // Limit switches.
    private val bottomSensor = DigitalInput(0)
    private val topSensor = DigitalInput(1)

    private val pivotSensor = DigitalInput(4)

    init {
        climbFalcon.setNeutralMode(NeutralMode.Brake)
        climbFalcon.ticks = TalonTicks

        tab.addNumber("Climb RPM") { climbFalcon.velocity(Rotations / Minutes) }

        buildConfig(MAIN_POWER)
    }

    override fun periodic() {
        state.let { // Mutex lock so doesn't destroy itself.
            if ((!bottomSensor.get() && it is State.Down) || (!topSensor.get() && it is State.Up)) {
                climbFalcon.set(0.0)
                return
            }

            when (it) {
                is State.Up -> climbFalcon.set(MAIN_POWER.value)
                is State.Down -> climbFalcon.set(-MAIN_POWER.value)
                is State.Idle -> climbFalcon.set(0.0)
            }
        }
    }

    fun up() { changeState(State.Up) }
    fun down() { changeState(State.Down) }
    fun stop() {  changeState(State.Idle) }

}