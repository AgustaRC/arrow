package arrow.effects

import arrow.effects.extensions.fx.concurrent.concurrent
import arrow.effects.extensions.fx.unsafeRun.runBlocking
import arrow.effects.suspended.fx.Fx
import arrow.effects.suspended.fx.FxOf
import arrow.effects.suspended.fx.invoke
import arrow.test.UnitSpec
import arrow.test.laws.ConcurrentLaws
import arrow.typeclasses.Eq
import arrow.unsafe
import io.kotlintest.runner.junit4.KotlinTestRunner
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class FxTest : UnitSpec() {

  fun <A> EQ(): Eq<FxOf<A>> = Eq { a, b ->
    unsafe {
      runBlocking {
        Fx {
          try {
            a() == b()
          } catch (e: Throwable) {
            val errA = try {
              a()
              throw IllegalArgumentException()
            } catch (err: Throwable) {
              err
            }
            val errB = try {
              b()
              throw IllegalStateException()
            } catch (err: Throwable) {
              err
            }
            println("Found errors: $errA and $errB")
            errA == errB
          }
        }
      }
    }
  }

  init {
    testLaws(ConcurrentLaws.laws(Fx.concurrent(), EQ(), EQ(), EQ()))
  }

}