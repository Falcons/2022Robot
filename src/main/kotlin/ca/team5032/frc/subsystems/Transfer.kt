package ca.team5032.frc.subsystems

import ca.team5032.frc.utils.DoubleProperty
import ca.team5032.frc.utils.Subsystem
import ca.team5032.frc.utils.TRANSFER_ID
import ca.team5032.frc.utils.Tabbed
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX

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

    override fun periodic() {
        when (state) {
            State.TransferringUp -> transferVictor.set(-DEFAULT_POWER.value)
            State.TransferringOut -> transferVictor.set(DEFAULT_POWER.value)
            State.Idle -> transferVictor.set(0.0)
        }
    }

    fun up() = setState(State.TransferringUp)
    fun down() = setState(State.TransferringOut)
    fun stop() = setState(State.Idle)

}