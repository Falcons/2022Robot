package ca.team5032.frc.auto

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup

class ShootAmountCommand(amount: Int) : SequentialCommandGroup(
    AlignToTargetCommand({ Limelight.Pipeline.ReflectiveTape }, -1),
    ShootCommand(amount)
)