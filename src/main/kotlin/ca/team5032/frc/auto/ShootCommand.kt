package ca.team5032.frc.auto

import ca.team5032.frc.Perseverance
import ca.team5032.frc.subsystems.Shooter
import edu.wpi.first.wpilibj2.command.CommandBase

class ShootCommand(private val ballCount: Int) : CommandBase() {

    var reachedSpeed = false
    var totalShot = 0

    var ticksWithoutBall = 0
    var tickThreshold = 0.5 / Perseverance.period

    override fun initialize() {
        reachedSpeed = false
        totalShot = 0
        ticksWithoutBall = 0

        Perseverance.shooter.shoot()

        // 1. Transfer has a ball
        if (Perseverance.transfer.hasBall() && ballCount >= 1) {
            Perseverance.transfer.up()
        }
        if (!Perseverance.transfer.hasBall() && Perseverance.intake.hasBall() && ballCount >= 1) {
            Perseverance.transfer.up()
            Perseverance.intake.intake()
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
        if (!Perseverance.transfer.hasBall() && !Perseverance.intake.hasBall()) {
            ticksWithoutBall ++

            if (ticksWithoutBall >= tickThreshold) this.cancel()
        } else {
            ticksWithoutBall = 0
        }
    }

    override fun end(interrupted: Boolean) {
        Perseverance.shooter.stop()
        Perseverance.transfer.stop()

        if (ballCount == 2) Perseverance.intake.stop()
    }

}