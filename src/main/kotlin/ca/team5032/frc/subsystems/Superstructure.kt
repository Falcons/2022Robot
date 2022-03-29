package ca.team5032.frc.subsystems

import ca.team5032.frc.utils.Subsystem
import edu.wpi.first.wpilibj2.command.InstantCommand
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup
import edu.wpi.first.wpilibj2.command.WaitUntilCommand

object Superstructure : Subsystem<Superstructure.State>("Superstructure", State.Idle) {

    sealed class State {
        object Shooting : State()
        object Intaking : State()
        object Climbing : State()
        object Idle : State()
    }

    val shooter = Shooter()
    val intake = Intake()
    val transfer = Transfer()

    val climb = Climb()

    var ballCount = 0

    override fun periodic() {}

    override fun onStateChange(oldState: State, newState: State) {
        if (newState is State.Climbing) {
            intake.raise()
        }
    }

    fun shoot() {}

    fun intake() {
        intake.cycle()
    }

    /**
     * Moves balls into position in the intake->transfer area.
     */
    private fun cycle() {
        // If the transfer has a ball, there is no open slot, so return.
        if (transfer.hasBall()) return

        // If intake then has a ball, cycle intake until transfer has a ball.
        if (intake.hasBall()) {
            intake.cycle()

            SequentialCommandGroup(
                WaitUntilCommand(transfer::hasBall),
                InstantCommand(intake::stop)
            ).schedule()
        }
    }

}