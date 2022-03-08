package ca.team5032.frc.transfer

import ca.team5032.frc.utils.TRANSFER_ID
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX
import edu.wpi.first.wpilibj2.command.SubsystemBase

class Transfer : SubsystemBase() {

    private val transferVictor = WPI_VictorSPX(TRANSFER_ID)

}