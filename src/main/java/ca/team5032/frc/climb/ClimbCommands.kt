package ca.team5032.frc.climb

import ca.team5032.frc.Perseverance
import edu.wpi.first.wpilibj2.command.CommandBase

class ClimbUp (): CommandBase() {
    /**
     * Creates a new ClimbCommands.
     */
    init {
        // Use addRequirements() here to declare subsystem dependencies.
        addRequirements(Perseverance.climb)
    }


    // Called every time the scheduler runs while the command is scheduled.
    override fun execute() = Perseverance.climb.climbUp()
}

class ClimbDown (): CommandBase() {

    init {
        // Use addRequirements() here to declare subsystem dependencies.
        addRequirements(Perseverance.climb)
    }


    // Called every time the scheduler runs while the command is scheduled.
    override fun execute() = Perseverance.climb.climbDown()
}
