package ca.team5032.frc.commands

import ca.team5032.frc.Perseverance
import edu.wpi.first.wpilibj2.command.CommandBase

class ClimbUpCommand : CommandBase() {
    init {
        addRequirements(Perseverance.climb)
    }

    override fun initialize() {
        Perseverance.climb.up()
    }

    override fun cancel() {
        Perseverance.climb.stop()
    }
}

class ClimbDownCommand : CommandBase() {
    init {
        addRequirements(Perseverance.climb)
    }

    override fun initialize() {
        Perseverance.climb.down()
    }

    override fun cancel() {
        Perseverance.climb.stop()
    }
}