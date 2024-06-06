/*
 * Copyright 2024 Herluf Baggesen
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
package ca.uwaterloo.flix.language.phase
import ca.uwaterloo.flix.language.ast._

object ResilienceTester {

  def resilienceFactor(okTree: SyntaxTree.Tree, badTree: SyntaxTree.Tree): Double = {
    assert(okTree.loc.source.name == badTree.loc.source.name)
    // Do a preorder traversal of both syntax trees
    val ok = preorderTraverse(okTree)
    val bad = preorderTraverse(badTree)
//    println(Parser2.syntaxTreeToDebugString(okTree))

    // Count the lengths of identical prefix and suffix.
    val mismatchRange = {
      var i = 0
      val max = ok.length.min(bad.length)
      while( i < max && ok(i) == bad(i)) {
        i = i + 1
      }
      var j = 0
      val okRev = ok.reverse
      val badRev = bad.reverse
      val jMax = (max - i)
      while(j < jMax & okRev(j) == badRev(j)) {
        j = j + 1
      }
      (i, j)
    }

    // Drop matching prefix and suffix.
    val regionOfOk = ok.drop(mismatchRange._1).dropRight(mismatchRange._2)
    val regionOfBad = bad.drop(mismatchRange._1).dropRight(mismatchRange._2)
    // Compute length of longest common subsequence of mismatch region
    val lcsOfMismatch =  try {
      if (regionOfBad.length <= 8000 && regionOfOk.length <= 8000) {
        longestCommonSubsequence(regionOfOk, regionOfBad)
      } else {
        println(s"ERR\t${okTree.loc.source.name} mismatch too long! ${regionOfOk.length} ${regionOfBad.length}")
        0
      }
    } catch {
      case _: Throwable =>
        println(s"ERR\tCould not compute lcs of ${okTree.loc.source.name}")
        0
    }
    // Add lenghts of matching prefix and suffix for final lcs.
    val preAndSufficMatchLen = mismatchRange._1 + mismatchRange._2
    val lcs = preAndSufficMatchLen + lcsOfMismatch

//    println(regionOfOk.mkString("\n"))
//    println("")
//    println(regionOfBad.mkString("\n"))
//    println("")
//    println(s"ok  traverse length: ${ok.length} ${ok.length - preAndSufficMatchLen}")
//    println(s"bad traverse length: ${bad.length} ${bad.length - preAndSufficMatchLen}")
//    println(s"mismatchRange: ${mismatchRange}")
//    println(s"lcs of mismatch: ${lcsOfMismatch}")
//    println(s"lcs: ${lcs}")

    // Divide lcs with length of preorder traversal of ok tree for a resilience factor.
    lcs.toDouble / ok.length.toDouble
  }

  private def preorderTraverse(tree: SyntaxTree.Tree): List[String] = {
    tree.kind.toString +: tree.children.toList.flatMap {
      case t: SyntaxTree.Tree => preorderTraverse(t)
      case t: Token => List("'" + t.text + "'")
    }
  }

  private def longestCommonSubsequence(s1: List[String], s2: List[String]): Int = {
    def longest(a: Int, b: Int) = a.max(b)

    lazy val lcs: ((Int, Int)) => Int = Memo.mutableHashMapMemo[(Int, Int), Int] {
      case (0, _) => 0
      case (_, 0) => 0
      case (i, j) if s1(i - 1) == s2(j - 1) => lcs((i - 1, j - 1)) + 1
      case (i, j) => longest(lcs((i, j - 1)), lcs((i - 1, j)))
    }

    lcs((s1.length, s2.length))
  }

  /** A function memoization strategy.  See companion for various
    * instances employing various strategies.
    */
  sealed trait Memo[@specialized(Int) K, @specialized(Int, Long, Double) V] {
    def apply(z: K => V): K => V
  }

  object Memo extends MemoInstances with MemoFunctions

  sealed abstract class MemoInstances {
  }

  /** @define immuMapNote As this memo uses a single var, it's
    * thread-safe. */
  trait MemoFunctions {
    def memo[@specialized(Int) K, @specialized(Int, Long, Double) V](f: (K => V) => K => V): Memo[K, V] = new Memo[K, V] {
      def apply(z: K => V) = f(z)
    }

    def mutableMapMemo[K, V](a: collection.mutable.Map[K, V]): Memo[K, V] =
      memo[K, V](f => k => a.getOrElseUpdate(k, f(k)))

    /** Cache results in a [[scala.collection.mutable.HashMap]].
      * Nonsensical if `K` lacks a meaningful `hashCode` and
      * `java.lang.Object.equals`.
      */
    def mutableHashMapMemo[K, V]: Memo[K, V] =
      mutableMapMemo(new collection.mutable.HashMap[K, V])
  }

}
