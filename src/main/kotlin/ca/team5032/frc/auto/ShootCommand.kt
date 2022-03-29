package ca.team5032.frc.auto

import ca.team5032.frc.Perseverance
import ca.team5032.frc.subsystems.Shooter
import ca.team5032.frc.subsystems.Superstructure
import edu.wpi.first.wpilibj2.command.CommandBase

class ShootCommand(private val ballCount: Int) : CommandBase() {

    private var hadBall = false
    private var totalShot = 0

    private var ticksWithoutBall = 0
    private var tickThreshold = 0.5 / Perseverance.period

    override fun initialize() {
        hadBall = false
        totalShot = 0
        ticksWithoutBall = 0

        Superstructure.shooter.shoot()

        // 1. Transfer has a ball
        if (Superstructure.transfer.hasBall() && ballCount >= 1) {
            Superstructure.transfer.up()
        }
        // TODO: shouldn't be needed once proper ball management is added
        if (!Superstructure.transfer.hasBall() && Superstructure.intake.hasBall() && ballCount >= 1) {
            Superstructure.transfer.up()
            Superstructure.intake.cycle()
        }
        // 2. Intake has a ball
        if (Superstructure.intake.hasBall() && ballCount == 2) {
            Superstructure.intake.cycle()
        }
    }

    override fun execute() {
        if (Superstructure.transfer.hasBall()) {
            hadBall = true
        // TODO: Make sure ball gets shot before shooter stops, perhaps a sensor is added after shooter?
        } else if (!Superstructure.transfer.hasBall() && hadBall) { // Speed drops due to ball passing it
            totalShot ++
            hadBall = false

            if (totalShot == ballCount) this.cancel()
        }

        if (ballCount == 2 && totalShot == 1 && Superstructure.transfer.hasBall()) Superstructure.intake.stop()
        if (!Superstructure.transfer.hasBall() && !Superstructure.intake.hasBall()) {
            ticksWithoutBall ++

            if (ticksWithoutBall >= tickThreshold) this.cancel()
        } else {
            ticksWithoutBall = 0
        }
    }

    override fun end(interrupted: Boolean) {
        Superstructure.shooter.stop()
        Superstructure.transfer.stop()

        if (ballCount == 2) Superstructure.intake.stop()
    }

}