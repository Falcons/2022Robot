package ca.team5032.frc.auto

import ca.team5032.frc.Perseverance
import ca.team5032.frc.subsystems.Shooter
import edu.wpi.first.wpilibj2.command.CommandBase

class ShootCommand(private val ballCount: Int) : CommandBase() {

    var reachedSpeed = false
    var totalShot = 0

    override fun initialize() {
        Perseverance.shooter.shoot()

        // 1. Transfer has a ball
        if (Perseverance.transfer.hasBall() && ballCount >= 1) {
            Perseverance.transfer.up()
        }
        // 2. Intake has a ball
        if (Perseverance.intake.hasBall() && ballCount == 2) {
            Perseverance.intake.intake()
        }
    }

    override fun execute() {
        if (Perseverance.shooter.state is Shooter.State.AtSpeed) {
            reachedSpeed = true
        } else if (Perseverance.shooter.state is Shooter.State.RampingUp && reachedSpeed) { // Speed drops due to ball passing it
            totalShot ++
            reachedSpeed = false

            if (totalShot == ballCount) this.cancel()
        }

        if (ballCount == 2 && totalShot == 1 && Perseverance.transfer.hasBall()) Perseverance.intake.stop()
    }

    override fun end(interrupted: Boolean) {
        Perseverance.shooter.stop()
        Perseverance.transfer.stop()

        if (ballCount == 2) Perseverance.intake.stop()
    }

}