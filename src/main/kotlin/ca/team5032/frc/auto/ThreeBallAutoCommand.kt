package ca.team5032.frc.auto

import ca.team5032.frc.Perseverance
import edu.wpi.first.wpilibj2.command.InstantCommand
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup
import edu.wpi.first.wpilibj2.command.WaitCommand
import edu.wpi.first.wpilibj2.command.WaitUntilCommand

class ThreeBallAutoCommand : SequentialCommandGroup(
    InstantCommand({ Perseverance.intake.deployIntake() }),
    WaitCommand(0.3),
    InstantCommand({ Perseverance.intake.intake() }),
    DriveForwardCommand(.65),
    WaitUntilCommand { Perseverance.intake.hasBall() },
    InstantCommand({ Perseverance.intake.stop() }),
    RotateToAngleCommand(180.0),
    ShootAmountCommand(2),
    InstantCommand({
        Perseverance.limelight.changeState(Limelight.State.Targeting(
            Limelight.Pipeline.getBall()
        ))
    }),
    RotateToAngleCommand(-77.0),
    AlignToTargetCommand(
        Limelight.Pipeline.getBall(),
        1,
        false
    ),
    InstantCommand({
        Perseverance.limelight.changeState(Limelight.State.Targeting(
            Limelight.Pipeline.ReflectiveTape
        ))
    }),
    InstantCommand({
        Perseverance.intake.intake()
        Perseverance.transfer.up()
    }),
    DriveForwardCommand(3.0),
    WaitUntilCommand { Perseverance.transfer.hasBall() },
    InstantCommand({
        Perseverance.intake.stop()
        Perseverance.transfer.stop()
    }),
    RotateToAngleCommand(120.0),
    ShootAmountCommand(1),
    InstantCommand({
        Perseverance.limelight.changeState(Limelight.State.Targeting(
            Limelight.Pipeline.getBall()
        ))
    }),
    RotateToAngleCommand(180.0),
    AlignToTargetCommand(
        Limelight.Pipeline.getBall(),
        1,
        false
    ),
    InstantCommand({
        Perseverance.limelight.changeState(Limelight.State.Targeting(Limelight.Pipeline.ReflectiveTape))
    }),
    InstantCommand({
        Perseverance.intake.intake()
        Perseverance.transfer.up()
    }),
    DriveForwardCommand(2.07),
    WaitUntilCommand {
        Perseverance.transfer.hasBall() && Perseverance.intake.hasBall()
    },
    InstantCommand({
        Perseverance.intake.stop()
        Perseverance.transfer.stop()
    }),
    RotateToAngleCommand(180.0),
    DriveForwardCommand(1.70, 1.0),
    ShootAmountCommand(2)
)