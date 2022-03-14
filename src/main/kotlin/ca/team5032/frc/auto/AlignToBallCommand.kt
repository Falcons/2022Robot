package ca.team5032.frc.auto

import ca.team5032.frc.Perseverance
import edu.wpi.first.wpilibj2.command.CommandBase

class AlignToHUBCommand : CommandBase() {

    private val limelight = Perseverance.limelight

    init {

    }

    override fun initialize() {
        limelight.pipeline = Limelight.Pipeline.ReflectiveTape
    }

    override fun execute() {

    }


}