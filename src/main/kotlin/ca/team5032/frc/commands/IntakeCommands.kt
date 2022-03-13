package ca.team5032.frc.commands

import ca.team5032.frc.Perseverance
import edu.wpi.first.wpilibj2.command.CommandBase

class IntakeInCommand : CommandBase() {
    init {
        addRequirements(Perseverance.intake)
    }

    override fun initialize() {
        Perseverance.intake.intake()
    }

    override fun cancel() {
        Perseverance.intake.stop()
    }
}

class IntakeOutCommand : CommandBase() {
    init {
        addRequirements(Perseverance.intake)
    }

    override fun initialize() {
        Perseverance.intake.intake()
    }

    override fun cancel() {
        Perseverance.intake.stop()
    }
}