package ca.team5032.frc.climb

import ca.team5032.frc.Perseverance
import edu.wpi.first.wpilibj2.command.CommandBase

class ClimbUpCommand: CommandBase() {

    init {
        addRequirements(Perseverance.climb)
    }

    override fun execute() = Perseverance.climb.climbUp()
}

class ClimbDownCommand: CommandBase() {

    init {
        addRequirements(Perseverance.climb)
    }

    override fun execute() = Perseverance.climb.climbDown()
}
