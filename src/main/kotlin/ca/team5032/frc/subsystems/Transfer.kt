package ca.team5032.frc.subsystems

import ca.team5032.frc.Perseverance
import ca.team5032.frc.utils.*
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX
import edu.wpi.first.wpilibj.DigitalInput

class Transfer : Subsystem<Transfer.State>("Transfer", State.Idle), Tabbed {

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

    init {
        buildConfig(DEFAULT_POWER)
    }

    override fun periodic() {
        if (Perseverance.peripheralController.pov == 270) setState(State.TransferringUp)
        else if (Perseverance.peripheralController.pov == 90) setState(State.TransferringOut)
        else setState(State.Idle)

        state.let {
//            if (hasBall() &&
//                it is State.TransferringUp &&
//                Perseverance.shooter.state !is Shooter.State.AtSpeed
//            ) {
//                transferVictor.set(0.0)
//                return
//            }

            when (it) {
                State.TransferringUp -> transferVictor.set(-DEFAULT_POWER.value)
                State.TransferringOut -> transferVictor.set(DEFAULT_POWER.value)
                State.Idle -> transferVictor.set(0.0)
            }
        }

        stop()
    }

    fun up() = setState(State.TransferringUp)
    fun down() = setState(State.TransferringOut)
    fun stop() = setState(State.Idle)

    fun hasBall() = !sensor.get()

}