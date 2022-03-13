package ca.team5032.frc.subsystems

import ca.team5032.frc.Perseverance
import ca.team5032.frc.utils.*
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX
import edu.wpi.first.wpilibj.DigitalInput

class Transfer : Subsystem<Transfer.State>(State.Idle), Tabbed {

    companion object {
        private val DEFAULT_POWER = DoubleProperty("Power", 0.75)
    }

    sealed class State {
        object TransferringUp : State()
        object TransferringOut : State()
        object Idle : State()
    }

    private val transferVictor = WPI_VictorSPX(TRANSFER_ID)
    private val sensor = DigitalInput(TRANSFER_SENSOR_ID)

    override fun periodic() {
        state.let {
            if (hasBall() &&
                it is State.TransferringUp &&
                Perseverance.shooter.state !is Shooter.State.AtSpeed
            ) return

            when (it) {
                State.TransferringUp -> transferVictor.set(-DEFAULT_POWER.value)
                State.TransferringOut -> transferVictor.set(DEFAULT_POWER.value)
                State.Idle -> transferVictor.set(0.0)
            }
        }
    }

    fun up() = setState(State.TransferringUp)
    fun down() = setState(State.TransferringOut)
    fun stop() = setState(State.Idle)

    fun hasBall() = sensor.get()

}