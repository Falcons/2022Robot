package ca.team5032.frc.auto

import ca.team5032.frc.Perseverance
import ca.team5032.frc.drive.DriveTrain
import edu.wpi.first.wpilibj2.command.CommandBase
import java.util.*

class DriveBackwardsCommand : CommandBase() {

    private val MAX_SHOOT_TIME = 0.75 * 1000
    private val MAX_TRANSFER_TIME = 0.3 * 1000
    private val MAX_DRIVE_TIME = 1.5 * 1000

    private var startTimeShoot: Long = 0
    private var startTimeTransfer: Long = 0
    private var startTimeDrive: Long = 0

    override fun initialize() {
        startTimeShoot = Date().time
    }

    override fun execute() {
        if (startTimeTransfer != 0L && Date().time - startTimeTransfer > MAX_TRANSFER_TIME) {
            if (startTimeDrive == 0L) startTimeDrive = Date().time
            if (Date().time - startTimeDrive > MAX_DRIVE_TIME) {
                Perseverance.drive.state = DriveTrain.State.STATIONARY
                this.cancel()
                return
            }
            Perseverance.drive.state = DriveTrain.State.AUTONOMOUS
            Perseverance.drive.auto(DriveTrain.AutoSpeeds(-0.4, 0.0, 0.0))
        } else {
            if (Date().time - startTimeShoot > MAX_SHOOT_TIME) {
                if (startTimeTransfer == 0L) startTimeTransfer = Date().time
                Perseverance.shooter.transferUp()
            }
            Perseverance.shooter.shootAuto()
        }
    }

}