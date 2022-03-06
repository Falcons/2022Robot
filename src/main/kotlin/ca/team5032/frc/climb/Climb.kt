package ca.team5032.frc.climb

import ca.team5032.frc.Perseverance
import ca.team5032.frc.utils.DoubleProperty
import ca.team5032.frc.utils.Tabbed
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX
import edu.wpi.first.wpilibj.DigitalInput
import edu.wpi.first.wpilibj2.command.SubsystemBase


class Climb : SubsystemBase(), Tabbed {

    companion object {
        // ID for climb falcon
        const val CLIMB_ID = 4

        // Default power for climb
        val DEFAULT_POWER = DoubleProperty("Power", 0.30)
    }

    enum class State {
        UP,
        DOWN,
        LOCKED_DOWN,
        LOCKED_UP,
        IDLE
    }

    private val climbFalcon = WPI_TalonFX(CLIMB_ID)

    //TODO get actual sensor value
    var bottomSensor = DigitalInput(0)
    var topSensor = DigitalInput(1)

    //setting start values
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

        if (state == State.IDLE || state == State.LOCKED_DOWN || state == State.LOCKED_UP) {
            climbFalcon.setNeutralMode(NeutralMode.Brake)
        } else {
            climbFalcon.setNeutralMode(NeutralMode.Coast)
        }

        if (!bottomSensor.get()){
            state = State.LOCKED_DOWN
            if (power < 0) power = 0.0
        }

        if (!topSensor.get()) {
            state = State.LOCKED_UP
            if (power > 0) power = 0.0
        }

        climbFalcon.set(power)
        power = 0.0
    }

    val sensorValue: Boolean
        get() = bottomSensor.get()

    fun climbUp() {
        power = DEFAULT_POWER()
        state = State.UP
    }

    fun climbDown() {
        power = -DEFAULT_POWER()
        state = State.DOWN
    }

}