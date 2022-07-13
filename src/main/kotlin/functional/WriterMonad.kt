package functional

import org.assertj.core.api.Assertions.*

/*
    Source: https://www.youtube.com/watch?v=C2w45qRc3aU
 */

class Simple {
    init {
        // simple
        assertThat(addOne(2)).isEqualTo(3)
        assertThat(square(2)).isEqualTo(4)
        assertThat(addOne(square(2))).isEqualTo(5)
    }

    fun square(x: Int): Int {
        return x * x
    }

    fun addOne(x: Int): Int {
        return x + 1
    }
}

class NumberWithLogsBad {
    init {
        // with logs (BAD)
        val result = addOne(square(3))
        assertThat(result.result).isEqualTo(10)
        assertThat(result.logs).isEqualTo(listOf("Squared 3 to get 9", "Added one to 9 to get 10"))

        /* not possible with this implementation:
            - square(square(2))
            - addOne(5)
        */
    }

    data class NumberWithLogs(val result: Int, val logs: List<String>)

    fun square(x: Int): NumberWithLogs {
        val result = x * x
        return NumberWithLogs(result, listOf("Squared $x to get $result"))
    }

    fun addOne(x: NumberWithLogs): NumberWithLogs {
        val result = x.result + 1
        return NumberWithLogs(result, x.logs + "Added one to ${x.result} to get $result")
    }
}

class NumberWithLogsMonad {
    init {
        val wrappedNumber = wrapWithLog(2);
        val first = runWithLogs(wrappedNumber, squareFun())
        val second = runWithLogs(first, addOneFun())

        assertThat(first.result).isEqualTo(4)
        assertThat(first.logs).isEqualTo(listOf("Squared 2 to get 4"))

        assertThat(second.result).isEqualTo(5)
        assertThat(second.logs).isEqualTo(listOf("Squared 2 to get 4", "Added one to 4 to get 5"))
    }

    data class NumberWithLogs(val result: Int, val logs: List<String>)

    fun wrapWithLog(x: Int): NumberWithLogs {
        return NumberWithLogs(
            result = x,
            logs = listOf()
        )
    }

    fun runWithLogs(input: NumberWithLogs, transform: (_: Int) -> NumberWithLogs): NumberWithLogs {
        val newNumberWithLogs = transform(input.result)
        return NumberWithLogs(newNumberWithLogs.result, input.logs + newNumberWithLogs.logs)
    }

    fun squareFun(): (Int) -> NumberWithLogs {
        return { NumberWithLogs(it * it, listOf("Squared $it to get ${it * it}")) }
    }

    fun addOneFun(): (Int) -> NumberWithLogs {
        return { NumberWithLogs(it + 1, listOf("Added one to $it to get ${it + 1}")) }
    }
}

fun runOptionExample() {
    Simple()
    NumberWithLogsBad()
    NumberWithLogsMonad() // or WriterMonad

    /*
    All monads have 3 components:
        1. Wrapper Type:    A wrapper of some sort that marks the type of the monad. ((NumberWithLogs))
        2. Wrap Function:   A function that takes normal values and wraps it up in the monad,
                            like a constructor of sorts. Allows entry to the monad ecosystem.
                            It is also called: return, pure, unit
                            (not great names, wrap would be better) in our example: ((wrapWithLogs))
        3. Run Function:    A function which takes the wrapper type and a transform function
                            that excepts the unwrapped type and returns the wrapped type.
                            This is also called: bind, flatMap, >>=
                            In our example: ((runWithLogs))

                We could also make this monad except a generic type, not just numbers
     */
}

