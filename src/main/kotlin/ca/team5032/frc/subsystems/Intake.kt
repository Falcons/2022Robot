package ca.team5032.frc.subsystems

import ca.team5032.frc.utils.DoubleProperty
import ca.team5032.frc.utils.INTAKE_ID
import ca.team5032.frc.utils.Subsystem
import ca.team5032.frc.utils.Tabbed
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX

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

    init {
        tab.addString("intake state") { state.javaClass.simpleName }

        buildConfig(DEFAULT_POWER)
    }

    override fun periodic() {
        state.let {
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

}