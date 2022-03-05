package ca.team5032.frc.shooter

import ca.team5032.frc.Perseverance
import edu.wpi.first.wpilibj2.command.CommandBase

class ShootHigh : CommandBase() {

    init {
        addRequirements(Perseverance.shooter)
    }

    override fun execute() = Perseverance.shooter.shootHigh()

}

class ShootLow : CommandBase() {

    init {
        addRequirements(Perseverance.shooter)
    }

    override fun execute() = Perseverance.shooter.shootLow()

}

//class TransferUp : CommandBase() {
//
//    init {
//        addRequirements(Perseverance.shooter)
//    }
//
//    override fun execute() = Perseverance.shooter.transferUp()
//}
//
//class TransferDown : CommandBase() {
//
//    init {
//        addRequirements(Perseverance.shooter)
//    }
//
//    override fun execute() = Perseverance.shooter.transferDown()
//}

