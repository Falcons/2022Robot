package ca.team5032.frc.auto

import ca.team5032.frc.Perseverance
import ca.team5032.frc.subsystems.DriveTrain
import ca.team5032.frc.utils.MecanumLinearOdometry
import ca.team5032.frc.utils.Metres
import edu.wpi.first.wpilibj2.command.CommandBase

class DriveForwardCommand(private val distance: Double) : CommandBase() {

    private val linearOdometry = MecanumLinearOdometry(
        Perseverance.drive.rearLeft,
        Perseverance.drive.frontLeft,
        Perseverance.drive.frontRight,
        Perseverance.drive.rearRight,
        unit = Metres
    )

    override fun initialize() {
        Perseverance.drive.state = DriveTrain.State.Autonomous

        linearOdometry.zero()
    }

    override fun execute() {
        if (linearOdometry.getElapsedDistance() < distance) {
            Perseverance.drive.autonomousInput.ySpeed = 0.6
        } else {
            this.cancel()
        }
    }

    override fun end(interrupted: Boolean) {
        Perseverance.drive.state = DriveTrain.State.Idle

        Perseverance.drive.autonomousInput.ySpeed = 0.0
    }

}