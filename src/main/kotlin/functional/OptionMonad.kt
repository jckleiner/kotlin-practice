package functional

import org.assertj.core.api.Assertions.*

/*
    Source: https://www.youtube.com/watch?v=C2w45qRc3aU
 */

sealed class Option<out T>
object None : Option<Nothing>()
data class Some<out T>(val value: T) : Option<T>()

fun <T> some(x: T): Option<T> {
    return Some(x)
}

// called map / flatMap in arrow:
// https://github.com/arrow-kt/arrow/blob/main/arrow-libs/core/arrow-core/src/commonMain/kotlin/arrow/core/Option.kt#L614
fun <A, B> run(input: Option<A>, transform: (A) -> Option<B>): Option<B> {
    return when (input) {
        is None -> None
        is Some -> transform(input.value)
    }
}


fun runWriterExample() {
    /*
        1. Wrapper Type:    Option<T>
        2. Wrap Function:   turns T's into Option<T>, it's called Some / None
        3. Run Function:    runs transformations on Option<T>'s
     */
    assertThat(classicApproach()).isEqualTo("rex")
    assertThat(monadicApproach()).isEqualTo(some("rex"))
}

fun monadicApproach(): Option<String> {
    val user: Option<User> = getCurrentUserO()
    val user2: Option<User> = run(user, changeUserF())
    val pet: Option<Pet> = run(user2, getPetF())
    val nickname: Option<String> = run(pet, getNicknameF())
    return nickname
}

fun classicApproach(): String? {
    val user = getCurrentUser()
    if (user == null) {
        return null
    }

    val pet = getPet(user)
    if (pet == null) {
        return null
    }

    val nickname = getNickname(pet)
    return nickname
}


data class User(var pet: Pet?)
data class Pet(var nickname: String?)

fun getCurrentUser(): User? {
    return User(null)
}

fun getPet(user: User): Pet? {
    return Pet(null)
}

fun getNickname(pet: Pet): String? {
    return "rex"
}

fun getCurrentUserO(): Option<User> {
    return some(User(Pet("betty")))
}

fun getPetF(): (User) -> Option<Pet> {
    return { if (it.pet == null) None else some(it.pet!!) }
}

fun getNicknameF(): (Pet) -> Option<String> {
    return { if (it.nickname == null) None else some(it.nickname!!) }
}

fun changeUserF(): (User) -> Option<User> {
    return { some(User(Pet("rex"))) }
}