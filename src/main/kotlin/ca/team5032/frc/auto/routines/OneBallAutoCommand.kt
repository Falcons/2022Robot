package ca.team5032.frc.auto.routines

import ca.team5032.frc.auto.DriveDistanceCommand
import ca.team5032.frc.auto.ShootAmountCommand
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup

class OneBallAutoCommand : ParallelCommandGroup(
    DriveDistanceCommand(-1.00, -0.6),
    ShootAmountCommand(2)
)