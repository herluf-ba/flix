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

class FuzzPrefixes extends AnyFunSuite with TestUtils {

  /**
    * The number of prefixes to compile for each program.
    */
  private val N: Int = 75

  private val coreAndStandardLibrary = List(
    "Prelude.flix" -> Files.readString(Paths.get("/src/library/Prelude.flix")),
    "Comparison.flix" -> Files.readString(Paths.get("/src/library/Comparison.flix")),
    "Coerce.flix" -> Files.readString(Paths.get("/src/library/Coerce.flix")),
    "Neg.flix" -> Files.readString(Paths.get("/src/library/Neg.flix")),
    "Add.flix" -> Files.readString(Paths.get("/src/library/Add.flix")),
    "Sub.flix" -> Files.readString(Paths.get("/src/library/Sub.flix")),
    "Mul.flix" -> Files.readString(Paths.get("/src/library/Mul.flix")),
    "Div.flix" -> Files.readString(Paths.get("/src/library/Div.flix")),
    "Bool.flix" -> Files.readString(Paths.get("/src/library/Bool.flix")),
    "Channel.flix" -> Files.readString(Paths.get("/src/library/Channel.flix")),
    "Thread.flix" -> Files.readString(Paths.get("/src/library/Thread.flix")),
    "Time.flix" -> Files.readString(Paths.get("/src/library/Time.flix")),
    "Eq.flix" -> Files.readString(Paths.get("/src/library/Eq.flix")),
    "Hash.flix" -> Files.readString(Paths.get("/src/library/Hash.flix")),
    "Sendable.flix" -> Files.readString(Paths.get("/src/library/Sendable.flix")),
    "Order.flix" -> Files.readString(Paths.get("/src/library/Order.flix")),
    "PartialOrder.flix" -> Files.readString(Paths.get("/src/library/PartialOrder.flix")),
    "LowerBound.flix" -> Files.readString(Paths.get("/src/library/LowerBound.flix")),
    "UpperBound.flix" -> Files.readString(Paths.get("/src/library/UpperBound.flix")),
    "JoinLattice.flix" -> Files.readString(Paths.get("/src/library/JoinLattice.flix")),
    "MeetLattice.flix" -> Files.readString(Paths.get("/src/library/MeetLattice.flix")),
    "ToString.flix" -> Files.readString(Paths.get("/src/library/ToString.flix")),
    "Reflect.flix" -> Files.readString(Paths.get("/src/library/Reflect.flix")),
    "Debug.flix" -> Files.readString(Paths.get("/src/library/Debug.flix")),
    "Ref.flix" -> Files.readString(Paths.get("/src/library/Ref.flix")),
    "Array.flix" -> Files.readString(Paths.get("/src/library/Array.flix")),
    "Assert.flix" -> Files.readString(Paths.get("/src/library/Assert.flix")),
    "Benchmark.flix" -> Files.readString(Paths.get("/src/library/Benchmark.flix")),
    "BigDecimal.flix" -> Files.readString(Paths.get("/src/library/BigDecimal.flix")),
    "BigInt.flix" -> Files.readString(Paths.get("/src/library/BigInt.flix")),
    "Boxable.flix" -> Files.readString(Paths.get("/src/library/Boxable.flix")),
    "Boxed.flix" -> Files.readString(Paths.get("/src/library/Boxed.flix")),
    "Chain.flix" -> Files.readString(Paths.get("/src/library/Chain.flix")),
    "Char.flix" -> Files.readString(Paths.get("/src/library/Char.flix")),
    "CodePoint.flix" -> Files.readString(Paths.get("/src/library/CodePoint.flix")),
    "Console.flix" -> Files.readString(Paths.get("/src/library/Console.flix")),
    "DelayList.flix" -> Files.readString(Paths.get("/src/library/DelayList.flix")),
    "DelayMap.flix" -> Files.readString(Paths.get("/src/library/DelayMap.flix")),
    "Down.flix" -> Files.readString(Paths.get("/src/library/Down.flix")),
    "Float32.flix" -> Files.readString(Paths.get("/src/library/Float32.flix")),
    "Float64.flix" -> Files.readString(Paths.get("/src/library/Float64.flix")),
    "Int8.flix" -> Files.readString(Paths.get("/src/library/Int8.flix")),
    "Int16.flix" -> Files.readString(Paths.get("/src/library/Int16.flix")),
    "Int32.flix" -> Files.readString(Paths.get("/src/library/Int32.flix")),
    "Int64.flix" -> Files.readString(Paths.get("/src/library/Int64.flix")),
    "Iterable.flix" -> Files.readString(Paths.get("/src/library/Iterable.flix")),
    "Iterator.flix" -> Files.readString(Paths.get("/src/library/Iterator.flix")),
    "List.flix" -> Files.readString(Paths.get("/src/library/List.flix")),
    "Map.flix" -> Files.readString(Paths.get("/src/library/Map.flix")),
    "Nec.flix" -> Files.readString(Paths.get("/src/library/Nec.flix")),
    "Nel.flix" -> Files.readString(Paths.get("/src/library/Nel.flix")),
    "Object.flix" -> Files.readString(Paths.get("/src/library/Object.flix")),
    "Option.flix" -> Files.readString(Paths.get("/src/library/Option.flix")),
    "Random.flix" -> Files.readString(Paths.get("/src/library/Random.flix")),
    "Result.flix" -> Files.readString(Paths.get("/src/library/Result.flix")),
    "Set.flix" -> Files.readString(Paths.get("/src/library/Set.flix")),
    "String.flix" -> Files.readString(Paths.get("/src/library/String.flix")),
    "System.flix" -> Files.readString(Paths.get("/src/library/System.flix")),
    "MultiMap.flix" -> Files.readString(Paths.get("/src/library/MultiMap.flix")),
    "MutQueue.flix" -> Files.readString(Paths.get("/src/library/MutQueue.flix")),
    "MutDeque.flix" -> Files.readString(Paths.get("/src/library/MutDeque.flix")),
    "MutDisjointSets.flix" -> Files.readString(Paths.get("/src/library/MutDisjointSets.flix")),
    "MutList.flix" -> Files.readString(Paths.get("/src/library/MutList.flix")),
    "MutSet.flix" -> Files.readString(Paths.get("/src/library/MutSet.flix")),
    "MutMap.flix" -> Files.readString(Paths.get("/src/library/MutMap.flix")),
    "Files.flix" -> Files.readString(Paths.get("/src/library/Files.flix")),
    "IOError.flix" -> Files.readString(Paths.get("/src/library/IOError.flix")),
    "Reader.flix" -> Files.readString(Paths.get("/src/library/Reader.flix")),
    "File.flix" -> Files.readString(Paths.get("/src/library/File.flix")),
    "Environment.flix" -> Files.readString(Paths.get("/src/library/Environment.flix")),
    "Applicative.flix" -> Files.readString(Paths.get("/src/library/Applicative.flix")),
    "CommutativeGroup.flix" -> Files.readString(Paths.get("/src/library/CommutativeGroup.flix")),
    "CommutativeMonoid.flix" -> Files.readString(Paths.get("/src/library/CommutativeMonoid.flix")),
    "CommutativeSemiGroup.flix" -> Files.readString(Paths.get("/src/library/CommutativeSemiGroup.flix")),
    "Foldable.flix" -> Files.readString(Paths.get("/src/library/Foldable.flix")),
    "FromString.flix" -> Files.readString(Paths.get("/src/library/FromString.flix")),
    "Functor.flix" -> Files.readString(Paths.get("/src/library/Functor.flix")),
    "Filterable.flix" -> Files.readString(Paths.get("/src/library/Filterable.flix")),
    "Group.flix" -> Files.readString(Paths.get("/src/library/Group.flix")),
    "Identity.flix" -> Files.readString(Paths.get("/src/library/Identity.flix")),
    "Monad.flix" -> Files.readString(Paths.get("/src/library/Monad.flix")),
    "MonadZero.flix" -> Files.readString(Paths.get("/src/library/MonadZero.flix")),
    "MonadZip.flix" -> Files.readString(Paths.get("/src/library/MonadZip.flix")),
    "Monoid.flix" -> Files.readString(Paths.get("/src/library/Monoid.flix")),
    "Reducible.flix" -> Files.readString(Paths.get("/src/library/Reducible.flix")),
    "SemiGroup.flix" -> Files.readString(Paths.get("/src/library/SemiGroup.flix")),
    "Traversable.flix" -> Files.readString(Paths.get("/src/library/Traversable.flix")),
    "Witherable.flix" -> Files.readString(Paths.get("/src/library/Witherable.flix")),
    "UnorderedFoldable.flix" -> Files.readString(Paths.get("/src/library/UnorderedFoldable.flix")),
    "Collectable.flix" -> Files.readString(Paths.get("/src/library/Collectable.flix")),
    "Validation.flix" -> Files.readString(Paths.get("/src/library/Validation.flix")),
    "StringBuilder.flix" -> Files.readString(Paths.get("/src/library/StringBuilder.flix")),
    "RedBlackTree.flix" -> Files.readString(Paths.get("/src/library/RedBlackTree.flix")),
    "GetOpt.flix" -> Files.readString(Paths.get("/src/library/GetOpt.flix")),
    "Concurrent/Channel.flix" -> Files.readString(Paths.get("/src/library/Concurrent/Channel.flix")),
    "Concurrent/Condition.flix" -> Files.readString(Paths.get("/src/library/Concurrent/Condition.flix")),
    "Concurrent/CyclicBarrier.flix" -> Files.readString(Paths.get("/src/library/Concurrent/CyclicBarrier.flix")),
    "Concurrent/ReentrantLock.flix" -> Files.readString(Paths.get("/src/library/Concurrent/ReentrantLock.flix")),
    "Time/Duration.flix" -> Files.readString(Paths.get("/src/library/Time/Duration.flix")),
    "Time/Epoch.flix" -> Files.readString(Paths.get("/src/library/Time/Epoch.flix")),
    "Time/Instant.flix" -> Files.readString(Paths.get("/src/library/Time/Instant.flix")),
    "Fixpoint/Phase/Stratifier.flix" -> Files.readString(Paths.get("/src/library/Fixpoint/Phase/Stratifier.flix")),
    "Fixpoint/Phase/Compiler.flix" -> Files.readString(Paths.get("/src/library/Fixpoint/Phase/Compiler.flix")),
    "Fixpoint/Phase/Simplifier.flix" -> Files.readString(Paths.get("/src/library/Fixpoint/Phase/Simplifier.flix")),
    "Fixpoint/Phase/IndexSelection.flix" -> Files.readString(Paths.get("/src/library/Fixpoint/Phase/IndexSelection.flix")),
    "Fixpoint/Phase/VarsToIndices.flix" -> Files.readString(Paths.get("/src/library/Fixpoint/Phase/VarsToIndices.flix")),
    "Fixpoint/Debugging.flix" -> Files.readString(Paths.get("/src/library/Fixpoint/Debugging.flix")),
    "Fixpoint/Interpreter.flix" -> Files.readString(Paths.get("/src/library/Fixpoint/Interpreter.flix")),
    "Fixpoint/Options.flix" -> Files.readString(Paths.get("/src/library/Fixpoint/Options.flix")),
    "Fixpoint/PredSymsOf.flix" -> Files.readString(Paths.get("/src/library/Fixpoint/PredSymsOf.flix")),
    "Fixpoint/Solver.flix" -> Files.readString(Paths.get("/src/library/Fixpoint/Solver.flix")),
    "Fixpoint/SubstitutePredSym.flix" -> Files.readString(Paths.get("/src/library/Fixpoint/SubstitutePredSym.flix")),
    "Fixpoint/Ast/Datalog.flix" -> Files.readString(Paths.get("/src/library/Fixpoint/Ast/Datalog.flix")),
    "Fixpoint/Ast/Shared.flix" -> Files.readString(Paths.get("/src/library/Fixpoint/Ast/Shared.flix")),
    "Fixpoint/Ast/PrecedenceGraph.flix" -> Files.readString(Paths.get("/src/library/Fixpoint/Ast/PrecedenceGraph.flix")),
    "Fixpoint/Ast/Ram.flix" -> Files.readString(Paths.get("/src/library/Fixpoint/Ast/Ram.flix")),
    "Eff/Random.flix" -> Files.readString(Paths.get("/src/library/Eff/Random.flix")),
    "Graph.flix" -> Files.readString(Paths.get("/src/library/Graph.flix")),
    "Vector.flix" -> Files.readString(Paths.get("/src/library/Vector.flix")),
    "Regex.flix" -> Files.readString(Paths.get("/src/library/Regex.flix")),
    "Adaptor.flix" -> Files.readString(Paths.get("/src/library/Adaptor.flix")),
    "ToJava.flix" -> Files.readString(Paths.get("/src/library/ToJava.flix")),
  )

  coreAndStandardLibrary.foreach {
    case (name, input) => test(name)(compilePrefixes(name, input))
  }

  /**
    * We break the given string `input` down into N prefixes and compile each of them.
    *
    * For example, if N is 100 and the input has length 300 then we create prefixes of length 3, 6, 9, ...
    *
    * The program may not be valid: We just care that it does not crash the compiler.
    */
  private def compilePrefixes(name: String, input: String): Unit = {
    val length = input.length
    val step = length / N

    val flix = new Flix()
    flix.compile()
    for (i <- 1 until N) {
      val e = Math.min(i * step, length)
      val prefix = input.substring(0, e)
      flix.addSourceCode(s"$name-prefix-$e", prefix)
      flix.compile() // We simply care that this does not crash.
    }
  }

}
