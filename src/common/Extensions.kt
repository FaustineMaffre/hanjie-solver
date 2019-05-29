package common

/**
 * Returns true if the sequence contains elements that are all equal.
 */
fun <T> Iterable<T>.allEquals(): Boolean = this.distinct().size <= 1

/**
 * If the length of the string is less than [nbChars], returns the same string with appended spaces
 * at the end such that its length is [nbChars]; otherwise returns the same string (without
 * truncating it).
 */
fun String.completeWithSpaces(nbChars: Int): String = this + " ".repeat(Math.max(nbChars - this.length, 0))