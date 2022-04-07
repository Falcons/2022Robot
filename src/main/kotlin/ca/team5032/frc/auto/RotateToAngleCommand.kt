package ca.team5032.frc.auto

import ca.team5032.frc.Perseverance
import ca.team5032.frc.subsystems.DriveTrain
import ca.team5032.frc.utils.MAXIMUM_ROTATION_SPEED
import ca.team5032.frc.utils.MINIMUM_ROTATION_SPEED
import edu.wpi.first.wpilibj2.command.CommandBase
import kotlin.math.abs
import kotlin.math.sign

class RotateToAngleCommand(private val angle: Double) : CommandBase() {

    override fun initialize() {
        Perseverance.drive.changeState(DriveTrain.State.Autonomous)
        Perseverance.drive.gyro.reset()
    }

    override fun execute() {
        val currentAngle = Perseverance.drive.gyro.angle

        val desiredAngle = angle

        val output = Perseverance.drive.rotationController.calculate(currentAngle, desiredAngle)
        val absolute = abs(output)
        val sign = sign(output)

        val remapped = absolute * (MAXIMUM_ROTATION_SPEED - MINIMUM_ROTATION_SPEED) + MINIMUM_ROTATION_SPEED

        Perseverance.drive.autonomousInput.zRotation = sign * remapped

        if (Perseverance.drive.rotationController.atSetpoint()) this.cancel()
    }

    override fun isFinished(): Boolean {
        return abs(Perseverance.drive.gyro.angle - angle) < 10
    }

    override fun end(interrupted: Boolean) {
        Perseverance.drive.autonomousInput.zRotation = 0.0
        Perseverance.drive.changeState(DriveTrain.State.Idle)
    }

}