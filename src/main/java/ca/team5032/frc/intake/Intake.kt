package ca.team5032.frc.intake

import ca.team5032.frc.Perseverance
import ca.team5032.frc.utils.Tabbed
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX
import edu.wpi.first.wpilibj2.command.SubsystemBase

class Intake : SubsystemBase(), Tabbed {

    companion object {
        const val INTAKE_ID = 4
        const val DEFAULT_POWER = 0.75
    }

    enum class State {
        INTAKING,
        EJECTING,
        IDLE
    }

    private val victor = WPI_VictorSPX(INTAKE_ID)

    var state: State = State.IDLE
    var power = 0.0

    init {
        if (Perseverance.debugMode) {
            tab.addString("State") { state.name }
        }
    }

    override fun periodic() {
        if (power == 0.0) state = State.IDLE

        victor.set(power)

        power = 0.0
    }

    fun intake() {
        power = -DEFAULT_POWER
        state = State.INTAKING
    }

    fun eject() {
        power = DEFAULT_POWER
        state = State.EJECTING
    }

}