package ca.uwaterloo.flix.language.phase

import ca.uwaterloo.flix.TestUtils
import ca.uwaterloo.flix.language.errors.{LexerError, WeederError}
import ca.uwaterloo.flix.util.Options
import org.scalatest.funsuite.AnyFunSuite

class TestLexer extends AnyFunSuite with TestUtils {

  test("LexerError.BlockCommentTooDeep.01") {
    val input = "/* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* this is 32 levels deep */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */"
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError.BlockCommentTooDeep](result)
  }

  test("LexerError.BlockCommentTooDeep.02") {
    val input = "/* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* // this is 33 levels deep */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */"
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError.BlockCommentTooDeep](result)
  }

  test("LexerError.BlockCommentTooDeep.03") {
    // Note: The innermost block-comment is unterminated,
    // but the lexer should stop after bottoming out so this should still be a 'too deep' error.
    val input = "/* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* this is 32 levels deep and unclosed */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */"
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError.BlockCommentTooDeep](result)
  }

  test("LexerError.BlockCommentTooDeep.04") {
    // Note: The innermost block-comment is unterminated,
    // but the lexer should stop after bottoming out so this should still be a 'too deep' error.
    val input = "/* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* /* this is unclosed and deep */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */ */"
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError.BlockCommentTooDeep](result)
  }

  test("LexerError.DoubleDottedNumber.01") {
    val input = "1.2.3"
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError.DoubleDottedNumber](result)
  }

  test("LexerError.DoubleDottedNumber.02") {
    val input = "12..3"
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError.DoubleDottedNumber](result)
  }

  test("LexerError.DoubleDottedNumber.03") {
    val input = "123.."
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError.DoubleDottedNumber](result)
  }

  test("LexerError.DoubleDottedNumber.04") {
    val input = "123..32f32"
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError.DoubleDottedNumber](result)
  }

  test("LexerError.DoubleDottedNumber.05") {
    val input = "12332..f32"
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError.DoubleDottedNumber](result)
  }

  // DoubleEInNumber

  test("LexerError.StringInterpolationTooDeep.01") {
    val input = """ "${"${"${"${"${"${"${"${"${"${"${"${"${"${"${${"${"${"${"${"${"${"${"${"${"${"${"${"${"${"${"${"${"${}"}"}"}"}"}"}"}"}"}"}"}"}"}"}"}"}"}"}}"}"}"}"}"}"}"}"}"}"}"}"}"}"}" """
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError.StringInterpolationTooDeep](result)
  }

  test("LexerError.StringInterpolationTooDeep.02") {
    // Note: The innermost interpolation is unterminated,
    // but the lexer should stop after bottoming out so this should still be a 'too deep' error.
    val input = """ "${"${"${"${"${"${"${"${"${"${"${"${"${"${"${${"${"${"${"${"${"${"${"${"${"${"${"${"${"${"${"${"${"${unterminated"}"}"}"}"}"}"}"}"}"}"}"}"}"}"}"}"}"}}"}"}"}"}"}"}"}"}"}"}"}"}"}"}" """
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError.StringInterpolationTooDeep](result)
  }

  test("LexerError.StringInterpolationTooDeep.03") {
    // Note: The innermost interpolation is unterminated,
    // but the lexer should stop after bottoming out so this should still be a 'too deep' error.
    val input = """ "${"${"${"${"${"${"${"${"${"${"${"${"${"${"${${"${"${"${"${"${"${"${"${"${"${"${"${"${"${"${"${"${"${"${"${unclosed and deep"}"}"}"}"}"}"}"}"}"}"}"}"}"}"}"}"}"}"}"}"}}"}"}"}"}"}"}"}"}"}"}"}"}"}"}" """
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError.StringInterpolationTooDeep](result)
  }

  test("LexerError.UnexpectedChar.01") {
    val input = "€"
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError.UnexpectedChar](result)
  }

  test("LexerError.UnexpectedChar.02") {
    val input = "⟂"
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError.UnexpectedChar](result)
  }

  test("LexerError.UnexpectedChar.03") {
    // Unicode hex U+2189, just below valid math unicode char
    val input = "↉"
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError.UnexpectedChar](result)
  }

  test("LexerError.UnexpectedChar.04") {
    // Unicode hex U+2300, just above valid math unicode char
    val input = "⌀"
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError.UnexpectedChar](result)
  }

  test("LexerError.UnterminatedBlockComment.01") {
    val input =
      s"""
         |/* This is unterminated
         |def f(): Unit = ()
       """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError.UnterminatedBlockComment](result)
  }

  test("LexerError.UnterminatedBlockComment.02") {
    val input = "def f(): /* This is unterminated Unit = ()"
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError.UnterminatedBlockComment](result)
  }

  test("LexerError.UnterminatedBlockComment.03") {
    val input =
      s"""
         |def f(): Unit = ()
         |/* This is unterminated""".stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError.UnterminatedBlockComment](result)
  }

  test("LexerError.UnterminatedBlockComment.04") {
    val input =
      s"""
         | /* /* This is unterminated and nested */
         |def f(): Unit = ()
       """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError.UnterminatedBlockComment](result)
  }

  test("LexerError.UnterminatedBuiltIn.01") {
    val input = "$BUILT_IN; 42"
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError.UnterminatedBuiltIn](result)
  }

  test("LexerError.UnterminatedBuiltIn.02") {
    val input = "$BUILT_IN"
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError.UnterminatedBuiltIn](result)
  }

  test("LexerError.UnterminatedBuiltIn.03") {
    val input = "$BUILT_/*IN*/$"
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError.UnterminatedBuiltIn](result)
  }

  test("LexerError.UnterminatedBuiltIn.04") {
    val input = "$BUILT_IN"
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError.UnterminatedBuiltIn](result)
  }

  test("LexerError.UnterminatedChar.01") {
    val input = "'a"
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError.UnterminatedChar](result)
  }

  test("LexerError.UnterminatedChar.02") {
    val input = "'\uffff"
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError.UnterminatedChar](result)
  }

  test("LexerError.UnterminatedChar.03") {
    val input = "'a/* This is a block-comment */'"
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError.UnterminatedChar](result)
  }

  test("LexerError.UnterminatedChar.04") {
    val input = "'/* This is a block-comment */a"
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError.UnterminatedChar](result)
  }

  test("LexerError.UnterminatedInfixFunction.01") {
    val input = "1 `add 2"
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError.UnterminatedInfixFunction](result)
  }

  test("LexerError.UnterminatedInfixFunction.02") {
    val input = "1 `add/*this is a block comment*/` 2"
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError.UnterminatedInfixFunction](result)
  }

  test("LexerError.UnterminatedInfixFunction.03") {
    val input = "1 `add 2"
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError.UnterminatedInfixFunction](result)
  }

  test("LexerError.UnterminatedRegex.01") {
    val input = """ regex" """
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError.UnterminatedRegex](result)
  }

  test("LexerError.UnterminatedRegex.02") {
    val input = """ regex"\" """
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError.UnterminatedRegex](result)
  }

  test("LexerError.UnterminatedRegex.03") {
    val input = """ regex" regex\" """
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError.UnterminatedRegex](result)
  }

  test("LexerError.UnterminatedString.01") {
    val input = """ "This is unterminated """
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError.UnterminatedString](result)
  }

  test("LexerError.UnterminatedString.02") {
    val input = """ "\ """
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError.UnterminatedString](result)
  }

  test("LexerError.UnterminatedString.03") {
    val input = "\""
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError.UnterminatedString](result)
  }

  test("LexerError.UnterminatedString.04") {
    val input = """ regex" regex"" """
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError.UnterminatedString](result)
  }

  test("LexerError.UnterminatedString.05") {
    // Actually not related to the error but asserts that the error
    // is not a false positive
    val input = """ def f(): String = "This is terminated" """
    val result = compile(input, Options.TestWithLibNix)
    expectSuccess(result)
  }

  test("LexerError.UnterminatedStringInterpolation.01") {
    val input = """ "Hi ${name!" """
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError.UnterminatedStringInterpolation](result)
  }

  test("LexerError.UnterminatedStringInterpolation.02") {
    val input = """ "${"Hi ${name!"}" """
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError.UnterminatedStringInterpolation](result)
  }

  test("LexerError.UnterminatedStringInterpolation.03") {
    val input = """ "${"Hi ${name!}"" """
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError.UnterminatedStringInterpolation](result)
  }

  test("LexerError.TrailingUnderscoreInNumber.01") {
    val input =
      s"""
         |def f(): Int = 1_
         """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError](result)
  }

  test("LexerError.TrailingUnderscoreInNumber.02") {
    val input =
      s"""
         |def f(): Int = 1_000_
           """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError](result)
  }

  test("LexerError.HexLiteralStartsOnUnderscore.01") {
    val input =
      s"""
         |def f(): Int32 = 0x_1
           """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError](result)
  }

  test("TrailingUnderscoreInNumber.Int.03") {
    val input =
      s"""
         |def f(): Int32 = 0x1_
           """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError](result)
  }

  test("LexerError.Int8.01") {
    val input =
      s"""
         |def f(): Int8 = 1_i8
           """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError](result)
  }

  test("LexerError.Int8.02") {
    val input =
      s"""
         |def f(): Int8 = 1_000_i8
           """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError](result)
  }

  test("LexerError.Int8.03") {
    val input =
      s"""
         |def f(): Int8 = 0x_1i8
           """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError](result)
  }

  test("LexerError.Int8.04") {
    val input =
      s"""
         |def f(): Int8 = 0x1_i8
           """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError](result)
  }

  test("LexerError.Int16.01") {
    val input =
      s"""
         |def f(): Int16 = 1_i16
           """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError](result)
  }

  test("LexerError.Int16.02") {
    val input =
      s"""
         |def f(): Int16 = 1_000_i16
           """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError](result)
  }

  test("LexerError.Int16.03") {
    val input =
      s"""
         |def f(): Int16 = 0x_1i16
           """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError](result)
  }

  test("LexerError.Int16.04") {
    val input =
      s"""
         |def f(): Int16 = 0x1_i16
           """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError](result)
  }

  test("LexerError.Int32.01") {
    val input =
      s"""
         |def f(): Int32 = 1_i32
           """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError](result)
  }

  test("LexerError.Int32.02") {
    val input =
      s"""
         |def f(): Int32 = 1_000_i32
           """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError](result)
  }

  test("LexerError.Int32.03") {
    val input =
      s"""
         |def f(): Int32 = 0x_1i32
           """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError](result)
  }

  test("LexerError.Int32.04") {
    val input =
      s"""
         |def f(): Int32 = 0x1_i32
           """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError](result)
  }

  test("LexerError.Int64.01") {
    val input =
      s"""
         |def f(): Int64 = 1_i64
           """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError](result)
  }

  test("LexerError.Int64.02") {
    val input =
      s"""
         |def f(): Int64 = 1_000_i64
           """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError](result)
  }

  test("LexerError.Int64.03") {
    val input =
      s"""
         |def f(): Int64 = 0x_1i64
           """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError](result)
  }

  test("LexerError.Int64.04") {
    val input =
      s"""
         |def f(): Int64 = 0x1_i64
           """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError](result)
  }

  test("LexerError.BigInt.01") {
    val input =
      s"""
         |def f(): BigInt = 1_ii
           """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError](result)
  }

  test("LexerError.BigInt.02") {
    val input =
      s"""
         |def f(): BigInt = 1_000_ii
           """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError](result)
  }

  test("LexerError.BigInt.03") {
    val input =
      s"""
         |def f(): BigInt = 0x_1ii
           """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError](result)
  }

  test("LexerError.BigInt.04") {
    val input =
      s"""
         |def f(): BigInt = 0x1_ii
           """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError](result)
  }

  test("LexerError.Float.01") {
    val input =
      s"""
         |def f(): Float = 1_.0
         """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError](result)
  }

  test("LexerError.Float.02") {
    val input =
      s"""
         |def f(): Float = 1_000_.0
           """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError](result)
  }

  test("LexerError.Float.03") {
    val input =
      s"""
         |def f(): Float = 1.0_
           """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError](result)
  }

  test("LexerError.Float32.01") {
    val input =
      s"""
         |def f(): Float32 = 1_.0f32
           """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError](result)
  }

  test("LexerError.Float32.02") {
    val input =
      s"""
         |def f(): Float32 = 1_000_.0f32
           """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError](result)
  }

  test("LexerError.Float32.03") {
    val input =
      s"""
         |def f(): Float32 = 1.0_f32
           """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError](result)
  }

  test("LexerError.Float64.01") {
    val input =
      s"""
         |def f(): Float64 = 1_.0f64
           """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError](result)
  }

  test("LexerError.Float64.02") {
    val input =
      s"""
         |def f(): Float64 = 1_000_.0f64
           """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError](result)
  }

  test("LexerError.Float64.03") {
    val input =
      s"""
         |def f(): Float64 = 1.0_f64
           """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError](result)
  }

  test("LexerError.BigDecimal.01") {
    val input =
      s"""
         |def f(): BigDecimal = 1_.0ff
           """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError](result)
  }

  test("LexerError.BigDecimal.02") {
    val input =
      s"""
         |def f(): BigDecimal = 1_000_.0ff
           """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError](result)
  }

  test("LexerError.BigDecimal.03") {
    val input =
      s"""
         |def f(): Float64 = 1.0_ff
           """.stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError](result)
  }

  test("LexerError.EOI.01") {
    val input = """def foo(): String = """"
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError](result)
  }

  test("LexerError.EOI.02") {
    val input =
      """def foo(): Char = '"""
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError](result)
  }

  test("LexerError.EOI.03") {
    val input =
      """def foo (): String = "\"""
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError](result)
  }

  test("LexerError.EOI.04") {
    val input = """def foo (): Char = "\"""
    val result = compile(input, Options.TestWithLibNix)
    expectError[LexerError](result)
  }

  // TODO: Move. This is a lexer thing
  test("MalformedChar.Char.01") {
    val input =
      """
        |def f(): Char = 'ab'
        |""".stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[WeederError.MalformedChar](result)
  }

  // TODO: Move. This is a lexer thing
  test("MalformedChar.Char.02") {
    val input =
      """
        |def f(): Char = ''
        |""".stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[WeederError.MalformedChar](result)
  }

  // TODO: Move. This is a lexer thing
  test("MalformedChar.Pattern.Char.01") {
    val input =
      """
        |def f(x: Char): Bool = match x {
        |  case 'ab' => true
        |  case _ => false
        |}
        |""".stripMargin
    val result = compile(input, Options.TestWithLibNix)
    expectError[WeederError.MalformedChar](result)
  }

  // TODO: Move, This is Lexer stuff
  test("MalformedUnicodeEscapeSequence.Char.01") {
    val input =
      """
        |def f(): Char = 'BSuINVALID'
        |""".stripMargin.replace("BS", "\\")
    val result = compile(input, Options.TestWithLibNix)
    expectError[WeederError.MalformedUnicodeEscapeSequence](result)
  }

  // TODO: Move, This is Lexer stuff
  test("MalformedUnicodeEscapeSequence.Char.02") {
    val input =
      """
        |def f(): Char = 'BSu000'
        |""".stripMargin.replace("BS", "\\")
    val result = compile(input, Options.TestWithLibNix)
    expectError[WeederError.MalformedUnicodeEscapeSequence](result)
  }

  // TODO: Move, This is Lexer stuff
  test("MalformedUnicodeEscapeSequence.Interpolation.01") {
    val input =
      """
        |def f(): String = '${25}BSuINVALID'
        |""".stripMargin.replace("BS", "\\")
    val result = compile(input, Options.TestWithLibNix)
    expectError[WeederError.MalformedUnicodeEscapeSequence](result)
  }

  // TODO: Move, This is Lexer stuff
  test("MalformedUnicodeEscapeSequence.Interpolation.02") {
    val input =
      """
        |def f(): String = '${25}BSu000'
        |""".stripMargin.replace("BS", "\\")
    val result = compile(input, Options.TestWithLibNix)
    expectError[WeederError.MalformedUnicodeEscapeSequence](result)
  }

  // TODO: Move, This is Lexer stuff
  test("MalformedUnicodeEscapeSequence.Pattern.Char.01") {
    val input =
      """
        |def f(x: Char): Bool = match x {
        |  case 'BSuINVALID' => true
        |  case _ => false
        |}
        |""".stripMargin.replace("BS", "\\")
    val result = compile(input, Options.TestWithLibNix)
    expectError[WeederError.MalformedUnicodeEscapeSequence](result)
  }

  // TODO: Move, This is Lexer stuff
  test("MalformedUnicodeEscapeSequence.Pattern.Char.02") {
    val input =
      """
        |def f(x: Char): Bool = match x {
        |  case 'BSu000' => true
        |  case _ => false
        |}
        |""".stripMargin.replace("BS", "\\")
    val result = compile(input, Options.TestWithLibNix)
    expectError[WeederError.MalformedUnicodeEscapeSequence](result)
  }

  // TODO: Move, This is Lexer stuff
  test("MalformedUnicodeEscapeSequence.Pattern.String.01") {
    val input =
      """
        |def f(x: String): Bool = match x {
        |  case "BSuINVALID" => true
        |  case _ => false
        |}
        |""".stripMargin.replace("BS", "\\")
    val result = compile(input, Options.TestWithLibNix)
    expectError[WeederError.MalformedUnicodeEscapeSequence](result)
  }

  // TODO: Move, This is Lexer stuff
  test("MalformedUnicodeEscapeSequence.Pattern.String.02") {
    val input =
      """
        |def f(x: String): Bool = match x {
        |  case "BSu000" => true
        |  case _ => false
        |}
        |""".stripMargin.replace("BS", "\\")
    val result = compile(input, Options.TestWithLibNix)
    expectError[WeederError.MalformedUnicodeEscapeSequence](result)
  }

  // TODO: Move, This is Lexer stuff
  test("MalformedUnicodeEscapeSequence.String.01") {
    // In scala, unicode escapes are preprocessed,
    // and other escapes are not processed in triple-quoted strings.
    // So we use BS in the input and replace it with a real backslash afterward.
    val input =
      """
        |def f(): String = "BSuINVALID"
        |""".stripMargin.replace("BS", "\\")
    val result = compile(input, Options.TestWithLibNix)
    expectError[WeederError.MalformedUnicodeEscapeSequence](result)
  }

  // TODO: Move, This is Lexer stuff
  test("MalformedUnicodeEscapeSequence.String.02") {
    val input =
      """
        |def f(): String = "BSu000"
        |""".stripMargin.replace("BS", "\\")
    val result = compile(input, Options.TestWithLibNix)
    expectError[WeederError.MalformedUnicodeEscapeSequence](result)
  }
}
