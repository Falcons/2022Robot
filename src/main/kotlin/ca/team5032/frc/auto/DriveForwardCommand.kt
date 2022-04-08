package ca.team5032.frc.auto

import ca.team5032.frc.Perseverance
import ca.team5032.frc.subsystems.DriveTrain
import ca.team5032.frc.utils.MecanumLinearOdometry
import ca.team5032.frc.utils.Metres
import edu.wpi.first.wpilibj2.command.CommandBase

class DriveForwardCommand(private val distance: Double) : CommandBase() {

    //var ticksSinceStart = 0
    //val maxTicks = seconds / Perseverance.period

    private val linearOdometry = MecanumLinearOdometry(
        Perseverance.drive.rearLeft,
        Perseverance.drive.frontLeft,
        Perseverance.drive.frontRight,
        Perseverance.drive.rearRight,
        unit = Metres
    )

    override fun initialize() {
        Perseverance.drive.changeState(DriveTrain.State.Autonomous)
        //ticksSinceStart = 0

        linearOdometry.zero()
    }

    override fun execute() {
        //ticksSinceStart ++

        if (linearOdometry.getElapsedDistance() <= distance) {
            Perseverance.drive.autonomousInput.ySpeed = 0.6
        } else {
            this.cancel()
        }
    }

    override fun end(interrupted: Boolean) {
        Perseverance.drive.changeState(DriveTrain.State.Idle)

        Perseverance.drive.autonomousInput.ySpeed = 0.0
    }

    override fun isFinished(): Boolean {
        return linearOdometry.getElapsedDistance() > distance
    }

}