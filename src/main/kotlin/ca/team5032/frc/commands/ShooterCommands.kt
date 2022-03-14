package ca.team5032.frc.commands

import ca.team5032.frc.Perseverance
import ca.team5032.frc.subsystems.Shooter
import edu.wpi.first.wpilibj2.command.CommandBase

class ShootAtRPMCommand : CommandBase() {
    init {
        addRequirements(Perseverance.shooter)
    }

    override fun execute() {
        Perseverance.shooter.shoot(Shooter.TARGET_RPM.value)
    }

}