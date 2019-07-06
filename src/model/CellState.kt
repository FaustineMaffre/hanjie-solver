package model

import common.allEquals

/**
 * State of a cell of a hanjie: undecided, black or white.
 */
enum class CellState constructor(private var symbol: String) {
    UNDECIDED("?"),
    BLACK("■"),
    WHITE(" ");

    override fun toString(): String = symbol

    companion object {

        /**
         * Generates the array of cells from a list of states such that, for each cell:
         * - if the corresponding cell in each array contains the same value,
         * the final cell contains this value;
         * - the final cell contains undecided value otherwise.
         */
        fun makeIntersectionOfStates(states: List<Array<CellState>>): Array<CellState> {
            require(states.isNotEmpty())

            val sizes = states.map { a -> a.size }
            require(sizes.allEquals())

            val size = sizes[0]

            return Array(size) { i ->
                when {
                    states.map { state -> state[i] }.allEquals() -> states[0][i] // if all equals, take the value from any array
                    else -> CellState.UNDECIDED // else undecided
                }
            }
        }
    }
}
