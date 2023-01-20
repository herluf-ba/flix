/*
 *  Copyright 2023 Matthew Lutze
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package ca.uwaterloo.flix.language.phase.unification

import ca.uwaterloo.flix.api.Flix
import ca.uwaterloo.flix.language.ast._
import ca.uwaterloo.flix.util.Result.{Err, Ok}
import ca.uwaterloo.flix.util.collection.Bimap
import ca.uwaterloo.flix.util.{InternalCompilerException, Result}
import ca.uwaterloo.flix.language.phase.unification.SetFormula._

object CaseSetUnification {

  /**
    * Returns the most general unifier of the two given set formulas `tpe1` and `tpe2`.
    */
  def unify(tpe1: Type, tpe2: Type, renv: RigidityEnv, cases: List[Symbol.RestrictableCaseSym], enumSym: Symbol.RestrictableEnumSym)(implicit flix: Flix): Result[Substitution, UnificationError] = {
    ///
    /// Perform aggressive matching to optimize for common cases.
    ///
    if (tpe1 eq tpe2) {
      return Ok(Substitution.empty)
    }

    ///
    /// Get rid of of trivial variable cases.
    ///
    (tpe1, tpe2) match {
      case (t1@Type.Var(x, _), t2) if renv.isFlexible(x) && !t2.typeVars.contains(t1) =>
        return Ok(Substitution.singleton(x, t2))

      case (t1, t2@Type.Var(x, _)) if renv.isFlexible(x) && !t1.typeVars.contains(t2) =>
        return Ok(Substitution.singleton(x, t1))

      case _ => // nop
    }

    ///
    /// Run the expensive boolean unification algorithm.
    ///

    val (env, univ) = SetFormula.mkEnv(List(tpe1, tpe2), cases)
    val input1 = SetFormula.fromCaseType(tpe1, env, univ)
    val input2 = SetFormula.fromCaseType(tpe2, env, univ)

    booleanUnification(input1, input2, Set.empty, univ, enumSym, env).map {
      case subst => subst.toTypeSubstitution(enumSym, env)
    }
  }

  /**
    * Returns the most general unifier of the two given set formulas `tpe1` and `tpe2`.
    */
  private def booleanUnification(tpe1: SetFormula, tpe2: SetFormula, renv: Set[Int], univ: Set[Int], sym: Symbol.RestrictableEnumSym, env: Bimap[SetFormula.VarOrCase, Int])(implicit flix: Flix): Result[CaseSetSubstitution, UnificationError] = {
    // The boolean expression we want to show is 0.
    val query = mkEq(tpe1, tpe2)(univ)

    // Compute the variables in the query.
    val typeVars = query.freeVars.toList

    // Compute the flexible variables.
    val flexibleTypeVars = typeVars.filterNot(renv.contains)

    // Determine the order in which to eliminate the variables.
    //    val freeVars = computeVariableOrder(flexibleTypeVars)
    val freeVars = flexibleTypeVars

    // Eliminate all variables.
    try {
      val subst = successiveVariableElimination(query, freeVars)(univ, flix)

      //    if (!subst.isEmpty) {
      //      val s = subst.toString
      //      val len = s.length
      //      if (len > 50) {
      //        println(s.substring(0, Math.min(len, 300)))
      //        println()
      //      }
      //    }

      Ok(subst)
    } catch {
      case SetUnificationException =>
        val t1 = SetFormula.toCaseType(tpe1, sym, env, SourceLocation.Unknown)
        val t2 = SetFormula.toCaseType(tpe2, sym, env, SourceLocation.Unknown)
        Err(UnificationError.MismatchedBools(t1, t2)) // TODO make setty
    }
  }

  /**
    * A heuristic used to determine the order in which to eliminate variable.
    *
    * Semantically the order of variables is immaterial. Changing the order may
    * yield different unifiers, but they are all equivalent. However, changing
    * the can lead to significant speed-ups / slow-downs.
    *
    * We make the following observation:
    *
    * We want to have synthetic variables (i.e. fresh variables introduced during
    * type inference) expressed in terms of real variables (i.e. variables that
    * actually occur in the source code). We can ensure this by eliminating the
    * synthetic variables first.
    */
  private def computeVariableOrder(l: List[Type.Var]): List[Type.Var] = {
    val realVars = l.filter(_.sym.isReal)
    val synthVars = l.filterNot(_.sym.isReal)
    synthVars ::: realVars
  }

  /**
    * Performs successive variable elimination on the given set expression `f`.
    */
  private def successiveVariableElimination(f: SetFormula, fvs: List[Int])(implicit univ: Set[Int], flix: Flix): CaseSetSubstitution = fvs match {
    case Nil =>
      // Determine if f is necessarily empty when all (rigid) variables and constants are made flexible.
      if (eval(f).isEmpty)
        CaseSetSubstitution.empty
      else
        throw SetUnificationException

    case x :: xs =>
      val t0 = CaseSetSubstitution.singleton(x, Empty)(f)
      val t1 = CaseSetSubstitution.singleton(x, Cst(univ))(f)
      val se = successiveVariableElimination(mkIntersection(t0, t1), xs)

      val f1 = mkUnion(se(t0), mkIntersection(Var(x), mkComplement(se(t1))))
      val f2 = minViaTable(f1)
      val st = CaseSetSubstitution.singleton(x, f2)
      st ++ se
  }

  /**
    * An exception thrown to indicate that boolean unification failed.
    */
  private case object SetUnificationException extends RuntimeException

  /**
    * To unify two set formulas p and q it suffices to unify t = (p ∧ ¬q) ∨ (¬p ∧ q) and check t = 0.
    */
  private def mkEq(p: SetFormula, q: SetFormula)(implicit univ: Set[Int]): SetFormula =
    mkUnion(mkIntersection(p, mkComplement(q)), mkIntersection(mkComplement(p), q))

  /**
    * Evaluates the set formula. Assumes there are no variables in the formula.
    */
  private def eval(f: SetFormula)(implicit univ: Set[Int]): Set[Int] = f match {
    case SetFormula.Cst(s) => s
    case SetFormula.Not(f) => univ -- eval(f)
    case SetFormula.And(f1, f2) => eval(f1) & eval(f2)
    case SetFormula.Or(f1, f2) => eval(f1) ++ eval(f2)
    case SetFormula.Var(x) => throw InternalCompilerException("unexpected var", SourceLocation.Unknown)
  }

  private def minViaTable(f: SetFormula)(implicit univ: Set[Int]): SetFormula = {
    val bot = SetFormula.Cst(Set.empty): SetFormula
    val top = SetFormula.Cst(univ): SetFormula

    def visit(fvs: List[Int]): List[(SetFormula, Map[Int, SetFormula])] = fvs match {
      case Nil => List((top, Map.empty))
      case v :: vs =>
        visit(vs) flatMap {
          case (p, partialSubst) =>
            val p1 = mkIntersection(SetFormula.Not(SetFormula.Var(v)), p)
            val p2 = mkIntersection(SetFormula.Var(v), p)
            List((p1, partialSubst + (v -> bot)), (p2, partialSubst + (v -> top)))
        }
    }

    visit(f.freeVars.toList).foldLeft(bot) {
      case (acc, (p, subst)) => mkUnion(acc, mkIntersection(p, applySubst(f, subst)))
    }
  }

  private def applySubst(f: SetFormula, m: Map[Int, SetFormula])(implicit univ: Set[Int]): SetFormula = SetFormula.map(f)(m)(univ)
}
