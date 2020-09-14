package zio.intellij.inspections.simplifications

import org.jetbrains.plugins.scala.codeInspection.collections._
import org.jetbrains.plugins.scala.lang.psi.api.expr.{ScExpression, ScInfixExpr}
import zio.intellij.inspections._
import zio.intellij.inspections.zioMethods._
import zio.intellij.utils.StringUtils._

class SimplifyFlatmapInspection extends ZInspection(ZipRightSimplificationType, ZipRightOperatorSimplificationType)

object ZipRightSimplificationType extends SimplificationType {

  override def hint: String = "Replace with .zipRight"

  override def getSimplification(expr: ScExpression): Option[Simplification] =
    expr match {
      case qual `.flatMap` `_ => x`(x) =>
        Some(replace(expr).withText(invocationText(qual, s"zipRight${x.getWrappedText}")))
      case _ => None
    }

}

object ZipRightOperatorSimplificationType extends SimplificationType {

  override def hint: String = "Replace with *>"

  override def getSimplification(expr: ScExpression): Option[Simplification] = {

    def replacement(qual: ScExpression, x: ScExpression) = x match {
        case _: ScInfixExpr => replace(expr).withText(s"${qual.getBracedText} *> (${x.getBracedText})")
        case _              => replace(expr).withText(s"${qual.getBracedText} *> ${x.getBracedText}")
    }

    expr match {
      case qual `.flatMap` `_ => x`(x) => Some(replacement(qual, x))
      case _                           => None
    }
  }

}
