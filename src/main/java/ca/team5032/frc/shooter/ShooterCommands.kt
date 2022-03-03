package ca.team5032.frc.shooter

import ca.team5032.frc.Perseverance
import edu.wpi.first.wpilibj2.command.CommandBase

class ShooterCommand : CommandBase() {

    init {
        addRequirements(Perseverance.shooter)
    }

    override fun execute() = Perseverance.shooter.shoot()

}

