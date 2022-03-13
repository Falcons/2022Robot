package ca.team5032.frc.subsystems

import ca.team5032.frc.Perseverance
import ca.team5032.frc.utils.*
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX
import edu.wpi.first.wpilibj.DigitalInput

class Intake : Subsystem<Intake.State>(State.Idle), Tabbed {

    companion object {
        // The default power of the intake motor.
        val DEFAULT_POWER = DoubleProperty("Power", 0.75)
    }

    sealed class State {
        object Intaking : State()
        object Ejecting : State()
        object Idle : State()
    }

    private val intakeVictor = WPI_VictorSPX(INTAKE_ID)
    private val sensor = DigitalInput(INTAKE_SENSOR_ID)

    init {
        tab.addString("intake state") { state.javaClass.simpleName }

        buildConfig(DEFAULT_POWER)
    }

    override fun periodic() {
        state.let {
            if (Perseverance.transfer.hasBall() && hasBall() && it is State.Intaking) return

            when (it) {
                is State.Intaking -> intakeVictor.set(-DEFAULT_POWER.value)
                is State.Ejecting -> intakeVictor.set(DEFAULT_POWER.value)
                is State.Idle -> intakeVictor.set(0.0)
            }
        }
    }

    fun intake() = setState(State.Intaking)
    fun eject() = setState(State.Ejecting)
    fun stop() = setState(State.Idle)

    fun hasBall() = sensor.get()

}