package ca.team5032.frc.auto

import ca.team5032.frc.Perseverance
import ca.team5032.frc.subsystems.DriveTrain
import ca.team5032.frc.utils.MecanumLinearOdometry
import ca.team5032.frc.utils.Metres
import edu.wpi.first.wpilibj2.command.CommandBase

class TaxiCommand : CommandBase() {

    private val odometry = MecanumLinearOdometry(
        Perseverance.drive.frontLeft,
        Perseverance.drive.frontRight,
        Perseverance.drive.rearRight,
        Perseverance.drive.frontRight,
        unit = Metres
    )

    override fun initialize() {
        Perseverance.drive.state = DriveTrain.State.Autonomous

        odometry.zero()
    }

    override fun execute() {
        if (odometry.getElapsedDistance() < 1) {
            Perseverance.drive.autonomousInput.ySpeed = -0.6
        } else {
            this.cancel()
        }
    }

    override fun end(interrupted: Boolean) {
        Perseverance.drive.autonomousInput.ySpeed = 0.0
        Perseverance.drive.state = DriveTrain.State.Idle
    }

}