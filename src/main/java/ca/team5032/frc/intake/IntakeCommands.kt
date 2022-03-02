package ca.team5032.frc.intake

import ca.team5032.frc.Perseverance
import edu.wpi.first.wpilibj2.command.CommandBase

class IntakeCommand : CommandBase() {

    init {
        addRequirements(Perseverance.intake)
    }

    override fun execute() = Perseverance.intake.intake()

}

class EjectCommand : CommandBase() {

    init {
        addRequirements(Perseverance.intake)
    }

    override fun execute() = Perseverance.intake.eject()

}