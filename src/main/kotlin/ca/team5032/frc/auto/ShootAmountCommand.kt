package ca.team5032.frc.auto

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup
import edu.wpi.first.wpilibj2.command.WaitCommand

class ShootAmountCommand(amount: Int) : ParallelCommandGroup(
    AlignToTargetCommand(Limelight.Pipeline.ReflectiveTape, -1),
    SequentialCommandGroup(
        WaitCommand(0.3),
        ShootCommand(amount)
    )
)