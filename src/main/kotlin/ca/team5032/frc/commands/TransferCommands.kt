package ca.team5032.frc.commands

import ca.team5032.frc.Perseverance
import edu.wpi.first.wpilibj2.command.CommandBase

class TransferUpCommand : CommandBase() {
    init {
        addRequirements(Perseverance.transfer)
    }

    override fun initialize() {
        Perseverance.transfer.up()
    }

    override fun cancel() {
        Perseverance.transfer.stop()
    }
}

class TransferDownCommand : CommandBase() {
    init {
        addRequirements(Perseverance.transfer)
    }

    override fun initialize() {
        Perseverance.transfer.down()
    }

    override fun cancel() {
        Perseverance.transfer.stop()
    }
}