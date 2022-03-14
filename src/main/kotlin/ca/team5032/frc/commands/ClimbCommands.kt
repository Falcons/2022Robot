package ca.team5032.frc.commands

import ca.team5032.frc.Perseverance
import edu.wpi.first.wpilibj2.command.CommandBase

class ClimbUpCommand : CommandBase() {
    init {
        addRequirements(Perseverance.climb)
    }

    override fun execute() {
        Perseverance.climb.up()
    }
}

class ClimbDownCommand : CommandBase() {
    init {
        addRequirements(Perseverance.climb)
    }

    override fun execute() {
        Perseverance.climb.down()
    }
}