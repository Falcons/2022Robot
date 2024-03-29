package ca.team5032.frc.auto

import ca.team5032.frc.Perseverance
import edu.wpi.first.wpilibj2.command.CommandBase

class ShootCommand(private val ballCount: Int) : CommandBase() {

    var hadBall = false
    var totalShot = 0

    var ticksWithoutBall = 0
    var tickThreshold = 0.2 / Perseverance.period

    override fun initialize() {
        hadBall = false
        totalShot = 0
        ticksWithoutBall = 0

        Perseverance.shooter.shoot()
        Perseverance.transfer.up()

        if (Perseverance.intake.hasBall()) {
            Perseverance.intake.cycle()
        }
    }

    override fun execute() {
        if (Perseverance.transfer.hasBall()) {
            hadBall = true
        } else if (!Perseverance.transfer.hasBall() && hadBall) { // Speed drops due to ball passing it
            totalShot ++
            hadBall = false

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

    override fun isFinished(): Boolean {
        return ticksWithoutBall >= tickThreshold || totalShot == ballCount
    }

}