package ca.team5032.frc.auto

import ca.team5032.frc.Perseverance
import ca.team5032.frc.subsystems.DriveTrain
import ca.team5032.frc.utils.MecanumLinearOdometry
import ca.team5032.frc.utils.Metres
import edu.wpi.first.wpilibj2.command.CommandBase
import kotlin.math.sign

class DriveForwardCommand(private val distance: Double, private val speed: Double = 0.6) : CommandBase() {

    private val linearOdometry = MecanumLinearOdometry(
        Perseverance.drive.rearLeft,
        Perseverance.drive.frontLeft,
        Perseverance.drive.frontRight,
        Perseverance.drive.rearRight,
        unit = Metres
    )

    //private val drivePid = PIDController(0.3, 0.0, 0.0)

    override fun initialize() {
        Perseverance.drive.changeState(DriveTrain.State.Autonomous)

        linearOdometry.zero()
    }

    override fun execute() {
        val error = distance - linearOdometry.getElapsedDistance()
        if (error > 0) {
            var desired = error
            desired = desired * (0.8 - 0.3) + (0.3 * sign(desired))

            Perseverance.drive.autonomousInput.ySpeed = speed
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