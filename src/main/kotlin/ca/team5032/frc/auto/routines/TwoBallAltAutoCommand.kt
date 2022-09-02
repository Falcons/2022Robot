package ca.team5032.frc.auto.routines

import ca.team5032.frc.Perseverance
import ca.team5032.frc.auto.DriveDistanceCommand
import ca.team5032.frc.auto.RotateToAngleCommand
import ca.team5032.frc.auto.ShootAmountCommand
import edu.wpi.first.wpilibj2.command.InstantCommand
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup
import edu.wpi.first.wpilibj2.command.WaitCommand
import edu.wpi.first.wpilibj2.command.WaitUntilCommand

class TwoBallAltAutoCommand : SequentialCommandGroup(
    InstantCommand({ Perseverance.intake.deploy() }),
    WaitCommand(0.3),
    InstantCommand({ Perseverance.intake.cycle() }),
    DriveDistanceCommand(.65),
    WaitUntilCommand { Perseverance.intake.hasBall() },
    InstantCommand({ Perseverance.intake.stop() }),
    RotateToAngleCommand(180.0),
    ShootAmountCommand(2),
)