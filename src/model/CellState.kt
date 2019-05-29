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
         * Generates the array of cells from a list of arrays such that, for each cell:
         * - if the corresponding cell in each array contains the same value,
         * the final cell contains this value;
         * - the final cell contains undecided value otherwise.
         */
        fun intersectionStates(arrays: List<Array<CellState>>): Array<CellState> {
            require(arrays.isNotEmpty())

            val sizes = arrays.map { a -> a.size }
            require(sizes.allEquals())

            val size = sizes[0]
            val res = Array(size) { UNDECIDED }

            (0 until size).forEach { i ->
                if(arrays.map { it[i] }.allEquals()) {
                    res[i] = arrays[0][i] // if all equals, take the value from any array
                }
            }

            return res
        }
    }
}
