package ca.team5032.frc.subsystems

import ca.team5032.frc.Perseverance
import ca.team5032.frc.utils.*
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX
import edu.wpi.first.wpilibj.DigitalInput
import edu.wpi.first.wpilibj.DoubleSolenoid
import edu.wpi.first.wpilibj.PneumaticsModuleType

class Intake : Subsystem<Intake.State>("Intake", State.Idle), Tabbed {

    companion object {
        // The default power of the intake motor.
        val DEFAULT_POWER = DoubleProperty("Power", 1.00)
    }

    sealed class State {
        object Intaking : State()
        object Ejecting : State()
        object Idle : State()
    }

    private val intakeVictor = WPI_VictorSPX(INTAKE_ID)
    private val sensor = DigitalInput(INTAKE_SENSOR_ID)

    private val intakeSolenoid = DoubleSolenoid(PneumaticsModuleType.CTREPCM, INTAKE_SOLENOID_1_ID, INTAKE_SOLENOID_2_ID)

    init {
        tab.addString("intake state") { state.javaClass.simpleName }
        raiseIntake()

        buildConfig(DEFAULT_POWER)
    }

    override fun periodic() {
        state.let {
            if (Perseverance.transfer.hasBall() && hasBall() && it is State.Intaking) {
                intakeVictor.set(0.0)
                return
            }

            when (it) {
                is State.Intaking -> intakeVictor.set(-DEFAULT_POWER.value)
                is State.Ejecting -> intakeVictor.set(DEFAULT_POWER.value)
                is State.Idle -> intakeVictor.set(0.0)
            }
        }
    }

    fun intake() { state = State.Intaking }
    fun eject() { state = State.Ejecting }
    fun stop() { state = State.Idle }

    fun deployIntake() = intakeSolenoid.set(DoubleSolenoid.Value.kReverse)
    fun raiseIntake() = intakeSolenoid.set(DoubleSolenoid.Value.kForward)

    fun hasBall() = sensor.get()

}