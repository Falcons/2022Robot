package ca.team5032.frc.auto.routines

import ca.team5032.frc.Perseverance
import ca.team5032.frc.auto.*
import edu.wpi.first.wpilibj2.command.InstantCommand
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup
import edu.wpi.first.wpilibj2.command.WaitCommand
import edu.wpi.first.wpilibj2.command.WaitUntilCommand

class FiveBallAutoCommand : SequentialCommandGroup(
    InstantCommand({ Perseverance.intake.deploy() }),
    WaitCommand(0.3),
    InstantCommand({ Perseverance.intake.cycle() }),
    DriveDistanceCommand(.65),
    WaitUntilCommand { Perseverance.intake.hasBall() },
    InstantCommand({ Perseverance.intake.stop() }),
    RotateToAngleCommand(180.0),
    ShootAmountCommand(2),
    InstantCommand({
        Perseverance.limelight.changeState(
            Limelight.State.Targeting(
                Limelight.Pipeline.getBall()
            )
        )
    }),
    RotateToAngleCommand(-77.0),
    AlignToTargetCommand({ Limelight.Pipeline.getBall() }, 1, false),
    InstantCommand({
        Perseverance.limelight.changeState(
            Limelight.State.Targeting(
                Limelight.Pipeline.ReflectiveTape
            )
        )
    }),
    InstantCommand({
        Perseverance.intake.cycle()
        Perseverance.transfer.up()
    }),
    DriveDistanceCommand(3.0),
    WaitUntilCommand { Perseverance.transfer.hasBall() },
    InstantCommand({
        Perseverance.intake.stop()
        Perseverance.transfer.stop()
    }),
    RotateToAngleCommand(120.0),
    ShootAmountCommand(1),
    InstantCommand({
        Perseverance.limelight.changeState(
            Limelight.State.Targeting(
                Limelight.Pipeline.getBall()
            )
        )
    }),
    RotateToAngleCommand(180.0),
    AlignToTargetCommand({ Limelight.Pipeline.getBall() }, 1, false),
    InstantCommand({
        Perseverance.limelight.changeState(Limelight.State.Targeting(Limelight.Pipeline.ReflectiveTape))
    }),
    InstantCommand({
        Perseverance.intake.cycle()
        Perseverance.transfer.up()
    }),
    DriveDistanceCommand(2.17),
    WaitUntilCommand {
        Perseverance.transfer.hasBall() && Perseverance.intake.hasBall()
    },
    InstantCommand({
        Perseverance.intake.stop()
        Perseverance.transfer.stop()
    }),
    RotateToAngleCommand(180.0),
    DriveDistanceCommand(1.80, 1.0),
    ShootAmountCommand(2)
)