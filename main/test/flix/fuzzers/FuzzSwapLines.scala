/*
 * Copyright 2024 Magnus Madsen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package flix.fuzzers

import ca.uwaterloo.flix.TestUtils
import ca.uwaterloo.flix.api.Flix
import org.scalatest.funsuite.AnyFunSuite

import java.nio.file.{Files, Paths}
import scala.jdk.CollectionConverters._

class FuzzSwapLines extends AnyFunSuite with TestUtils {

  private val coreAndStandardLibrary = List(
    "Prelude.flix" -> Files.lines(Paths.get("./main/src/library/Prelude.flix")),
    "Comparison.flix" -> Files.lines(Paths.get("./main/src/library/Comparison.flix")),
    "Coerce.flix" -> Files.lines(Paths.get("./main/src/library/Coerce.flix")),
    "Neg.flix" -> Files.lines(Paths.get("./main/src/library/Neg.flix")),
    "Add.flix" -> Files.lines(Paths.get("./main/src/library/Add.flix")),
    "Sub.flix" -> Files.lines(Paths.get("./main/src/library/Sub.flix")),
    "Mul.flix" -> Files.lines(Paths.get("./main/src/library/Mul.flix")),
    "Div.flix" -> Files.lines(Paths.get("./main/src/library/Div.flix")),
    "Bool.flix" -> Files.lines(Paths.get("./main/src/library/Bool.flix")),
    "Channel.flix" -> Files.lines(Paths.get("./main/src/library/Channel.flix")),
    "Thread.flix" -> Files.lines(Paths.get("./main/src/library/Thread.flix")),
    "Time.flix" -> Files.lines(Paths.get("./main/src/library/Time.flix")),
    "Eq.flix" -> Files.lines(Paths.get("./main/src/library/Eq.flix")),
    "Hash.flix" -> Files.lines(Paths.get("./main/src/library/Hash.flix")),
    "Sendable.flix" -> Files.lines(Paths.get("./main/src/library/Sendable.flix")),
    "Order.flix" -> Files.lines(Paths.get("./main/src/library/Order.flix")),
    "PartialOrder.flix" -> Files.lines(Paths.get("./main/src/library/PartialOrder.flix")),
    "LowerBound.flix" -> Files.lines(Paths.get("./main/src/library/LowerBound.flix")),
    "UpperBound.flix" -> Files.lines(Paths.get("./main/src/library/UpperBound.flix")),
    "JoinLattice.flix" -> Files.lines(Paths.get("./main/src/library/JoinLattice.flix")),
    "MeetLattice.flix" -> Files.lines(Paths.get("./main/src/library/MeetLattice.flix")),
    "ToString.flix" -> Files.lines(Paths.get("./main/src/library/ToString.flix")),
    "Reflect.flix" -> Files.lines(Paths.get("./main/src/library/Reflect.flix")),
    "Debug.flix" -> Files.lines(Paths.get("./main/src/library/Debug.flix")),
    "Ref.flix" -> Files.lines(Paths.get("./main/src/library/Ref.flix")),
    "Array.flix" -> Files.lines(Paths.get("./main/src/library/Array.flix")),
    "Assert.flix" -> Files.lines(Paths.get("./main/src/library/Assert.flix")),
    "Benchmark.flix" -> Files.lines(Paths.get("./main/src/library/Benchmark.flix")),
    "BigDecimal.flix" -> Files.lines(Paths.get("./main/src/library/BigDecimal.flix")),
    "BigInt.flix" -> Files.lines(Paths.get("./main/src/library/BigInt.flix")),
    "Boxable.flix" -> Files.lines(Paths.get("./main/src/library/Boxable.flix")),
    "Boxed.flix" -> Files.lines(Paths.get("./main/src/library/Boxed.flix")),
    "Chain.flix" -> Files.lines(Paths.get("./main/src/library/Chain.flix")),
    "Char.flix" -> Files.lines(Paths.get("./main/src/library/Char.flix")),
    "CodePoint.flix" -> Files.lines(Paths.get("./main/src/library/CodePoint.flix")),
    "Console.flix" -> Files.lines(Paths.get("./main/src/library/Console.flix")),
    "DelayList.flix" -> Files.lines(Paths.get("./main/src/library/DelayList.flix")),
    "DelayMap.flix" -> Files.lines(Paths.get("./main/src/library/DelayMap.flix")),
    "Down.flix" -> Files.lines(Paths.get("./main/src/library/Down.flix")),
    "Float32.flix" -> Files.lines(Paths.get("./main/src/library/Float32.flix")),
    "Float64.flix" -> Files.lines(Paths.get("./main/src/library/Float64.flix")),
    "Int8.flix" -> Files.lines(Paths.get("./main/src/library/Int8.flix")),
    "Int16.flix" -> Files.lines(Paths.get("./main/src/library/Int16.flix")),
    "Int32.flix" -> Files.lines(Paths.get("./main/src/library/Int32.flix")),
    "Int64.flix" -> Files.lines(Paths.get("./main/src/library/Int64.flix")),
    "Iterable.flix" -> Files.lines(Paths.get("./main/src/library/Iterable.flix")),
    "Iterator.flix" -> Files.lines(Paths.get("./main/src/library/Iterator.flix")),
    "List.flix" -> Files.lines(Paths.get("./main/src/library/List.flix")),
    "Map.flix" -> Files.lines(Paths.get("./main/src/library/Map.flix")),
    "Nec.flix" -> Files.lines(Paths.get("./main/src/library/Nec.flix")),
    "Nel.flix" -> Files.lines(Paths.get("./main/src/library/Nel.flix")),
    "Object.flix" -> Files.lines(Paths.get("./main/src/library/Object.flix")),
    "Option.flix" -> Files.lines(Paths.get("./main/src/library/Option.flix")),
    "Random.flix" -> Files.lines(Paths.get("./main/src/library/Random.flix")),
    "Result.flix" -> Files.lines(Paths.get("./main/src/library/Result.flix")),
    "Set.flix" -> Files.lines(Paths.get("./main/src/library/Set.flix")),
    "String.flix" -> Files.lines(Paths.get("./main/src/library/String.flix")),
    "System.flix" -> Files.lines(Paths.get("./main/src/library/System.flix")),
    "MultiMap.flix" -> Files.lines(Paths.get("./main/src/library/MultiMap.flix")),
    "MutQueue.flix" -> Files.lines(Paths.get("./main/src/library/MutQueue.flix")),
    "MutDeque.flix" -> Files.lines(Paths.get("./main/src/library/MutDeque.flix")),
    "MutDisjointSets.flix" -> Files.lines(Paths.get("./main/src/library/MutDisjointSets.flix")),
    "MutList.flix" -> Files.lines(Paths.get("./main/src/library/MutList.flix")),
    "MutSet.flix" -> Files.lines(Paths.get("./main/src/library/MutSet.flix")),
    "MutMap.flix" -> Files.lines(Paths.get("./main/src/library/MutMap.flix")),
    "Files.flix" -> Files.lines(Paths.get("./main/src/library/Files.flix")),
    "IOError.flix" -> Files.lines(Paths.get("./main/src/library/IOError.flix")),
    "Reader.flix" -> Files.lines(Paths.get("./main/src/library/Reader.flix")),
    "File.flix" -> Files.lines(Paths.get("./main/src/library/File.flix")),
    "Environment.flix" -> Files.lines(Paths.get("./main/src/library/Environment.flix")),
    "Applicative.flix" -> Files.lines(Paths.get("./main/src/library/Applicative.flix")),
    "CommutativeGroup.flix" -> Files.lines(Paths.get("./main/src/library/CommutativeGroup.flix")),
    "CommutativeMonoid.flix" -> Files.lines(Paths.get("./main/src/library/CommutativeMonoid.flix")),
    "CommutativeSemiGroup.flix" -> Files.lines(Paths.get("./main/src/library/CommutativeSemiGroup.flix")),
    "Foldable.flix" -> Files.lines(Paths.get("./main/src/library/Foldable.flix")),
    "FromString.flix" -> Files.lines(Paths.get("./main/src/library/FromString.flix")),
    "Functor.flix" -> Files.lines(Paths.get("./main/src/library/Functor.flix")),
    "Filterable.flix" -> Files.lines(Paths.get("./main/src/library/Filterable.flix")),
    "Group.flix" -> Files.lines(Paths.get("./main/src/library/Group.flix")),
    "Identity.flix" -> Files.lines(Paths.get("./main/src/library/Identity.flix")),
    "Monad.flix" -> Files.lines(Paths.get("./main/src/library/Monad.flix")),
    "MonadZero.flix" -> Files.lines(Paths.get("./main/src/library/MonadZero.flix")),
    "MonadZip.flix" -> Files.lines(Paths.get("./main/src/library/MonadZip.flix")),
    "Monoid.flix" -> Files.lines(Paths.get("./main/src/library/Monoid.flix")),
    "Reducible.flix" -> Files.lines(Paths.get("./main/src/library/Reducible.flix")),
    "SemiGroup.flix" -> Files.lines(Paths.get("./main/src/library/SemiGroup.flix")),
    "Traversable.flix" -> Files.lines(Paths.get("./main/src/library/Traversable.flix")),
    "Witherable.flix" -> Files.lines(Paths.get("./main/src/library/Witherable.flix")),
    "UnorderedFoldable.flix" -> Files.lines(Paths.get("./main/src/library/UnorderedFoldable.flix")),
    "Collectable.flix" -> Files.lines(Paths.get("./main/src/library/Collectable.flix")),
    "Validation.flix" -> Files.lines(Paths.get("./main/src/library/Validation.flix")),
    "StringBuilder.flix" -> Files.lines(Paths.get("./main/src/library/StringBuilder.flix")),
    "RedBlackTree.flix" -> Files.lines(Paths.get("./main/src/library/RedBlackTree.flix")),
    "GetOpt.flix" -> Files.lines(Paths.get("./main/src/library/GetOpt.flix")),
    "Concurrent/Channel.flix" -> Files.lines(Paths.get("./main/src/library/Concurrent/Channel.flix")),
    "Concurrent/Condition.flix" -> Files.lines(Paths.get("./main/src/library/Concurrent/Condition.flix")),
    "Concurrent/CyclicBarrier.flix" -> Files.lines(Paths.get("./main/src/library/Concurrent/CyclicBarrier.flix")),
    "Concurrent/ReentrantLock.flix" -> Files.lines(Paths.get("./main/src/library/Concurrent/ReentrantLock.flix")),
    "Time/Duration.flix" -> Files.lines(Paths.get("./main/src/library/Time/Duration.flix")),
    "Time/Epoch.flix" -> Files.lines(Paths.get("./main/src/library/Time/Epoch.flix")),
    "Time/Instant.flix" -> Files.lines(Paths.get("./main/src/library/Time/Instant.flix")),
    "Fixpoint/Phase/Stratifier.flix" -> Files.lines(Paths.get("./main/src/library/Fixpoint/Phase/Stratifier.flix")),
    "Fixpoint/Phase/Compiler.flix" -> Files.lines(Paths.get("./main/src/library/Fixpoint/Phase/Compiler.flix")),
    "Fixpoint/Phase/Simplifier.flix" -> Files.lines(Paths.get("./main/src/library/Fixpoint/Phase/Simplifier.flix")),
    "Fixpoint/Phase/IndexSelection.flix" -> Files.lines(Paths.get("./main/src/library/Fixpoint/Phase/IndexSelection.flix")),
    "Fixpoint/Phase/VarsToIndices.flix" -> Files.lines(Paths.get("./main/src/library/Fixpoint/Phase/VarsToIndices.flix")),
    "Fixpoint/Debugging.flix" -> Files.lines(Paths.get("./main/src/library/Fixpoint/Debugging.flix")),
    "Fixpoint/Interpreter.flix" -> Files.lines(Paths.get("./main/src/library/Fixpoint/Interpreter.flix")),
    "Fixpoint/Options.flix" -> Files.lines(Paths.get("./main/src/library/Fixpoint/Options.flix")),
    "Fixpoint/PredSymsOf.flix" -> Files.lines(Paths.get("./main/src/library/Fixpoint/PredSymsOf.flix")),
    "Fixpoint/Solver.flix" -> Files.lines(Paths.get("./main/src/library/Fixpoint/Solver.flix")),
    "Fixpoint/SubstitutePredSym.flix" -> Files.lines(Paths.get("./main/src/library/Fixpoint/SubstitutePredSym.flix")),
    "Fixpoint/Ast/Datalog.flix" -> Files.lines(Paths.get("./main/src/library/Fixpoint/Ast/Datalog.flix")),
    "Fixpoint/Ast/Shared.flix" -> Files.lines(Paths.get("./main/src/library/Fixpoint/Ast/Shared.flix")),
    "Fixpoint/Ast/PrecedenceGraph.flix" -> Files.lines(Paths.get("./main/src/library/Fixpoint/Ast/PrecedenceGraph.flix")),
    "Fixpoint/Ast/Ram.flix" -> Files.lines(Paths.get("./main/src/library/Fixpoint/Ast/Ram.flix")),
    "Eff/Random.flix" -> Files.lines(Paths.get("./main/src/library/Eff/Random.flix")),
    "Graph.flix" -> Files.lines(Paths.get("./main/src/library/Graph.flix")),
    "Vector.flix" -> Files.lines(Paths.get("./main/src/library/Vector.flix")),
    "Regex.flix" -> Files.lines(Paths.get("./main/src/library/Regex.flix")),
    "Adaptor.flix" -> Files.lines(Paths.get("./main/src/library/Adaptor.flix")),
    "ToJava.flix" -> Files.lines(Paths.get("./main/src/library/ToJava.flix")),
  )

  coreAndStandardLibrary.foreach {
    case (name, input) => test(s"$name-swap-lines")(compileAllLinesLessOne(name, input))
  }

  /**
    * We compile all variants of the given program where we omit a single line.
    *
    * For example, we omit line 1 and compile the program. Then we omit line 2 and compile the program. And so forth.
    *
    * The program may not be valid: We just care that it does not crash the compiler.
    */
  private def compileAllLinesLessOne(name: String, stream: java.util.stream.Stream[String]): Unit = {
    val lines = stream.iterator().asScala.toList
    val numberOfLines = lines.length

    val flix = new Flix()
    flix.compile()
    for (i <- 0 until numberOfLines) {
      for (j <- 0 until numberOfLines) {
        if (i != j) {
          val src = lines.updated(i, lines(j)).updated(j, lines(i)).mkString("\n")
          flix.addSourceCode(s"$name-swap-lines-$i-and-$j", src)
          flix.compile() // We simply care that this does not crash.
        }
      }
    }
  }

}
