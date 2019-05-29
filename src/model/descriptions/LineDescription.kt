package model.descriptions

import model.CellState
import java.util.*

/**
 * Description of a row or a column of an hanjie, that is, numbers indicating the sequences
 * of black cells for this row or column.
 */
abstract class LineDescription: Cloneable {

    /**
     * Number of cells in this line.
     */
    val numberOfCells: Int

    /**
     * Description of this line.
     */
    val description: List<Int>

    /**
     * Possible states for this line according to the description.
     *
     * Generated once when this description is created; then possible states can be removed by
     * using [LineDescription.removeImpossibleStates].
     */
    var possibleStates: List<Array<CellState>>
        private set

    companion object {
        /**
         * Parses a description for a row containing the given number of cells
         * of the form "`n1,n2,...,nm`", where each `ni` is an integer.
         */
        fun parseRowDescription(numberOfCells: Int, descriptionString: String): RowDescription =
                RowDescription(numberOfCells, parseDescriptionString(descriptionString))

        /**
         * Parses a description for a column containing the given number of cells
         * of the form "`n1,n2,...,nm`", where each `ni` is an integer.
         */
        fun parseColumnDescription(numberOfCells: Int, descriptionString: String): ColumnDescription =
                ColumnDescription(numberOfCells, parseDescriptionString(descriptionString))

        /**
         * Parses a description of the form "`n1,n2,...,nm`", where each `ni` is an integer, to a
         * list of integers.
         */
        private fun parseDescriptionString(descriptionString: String): List<Int> =
                descriptionString.split(',').map { Integer.parseInt(it) }

        /**
         * Generates every possible states according to the description of this line, that is,
         * every possible configuration of cells observing the description.
         */
        private fun generateAllPossibleStates(numberOfCells: Int, description: List<Int>): List<Array<CellState>> {
            val res = ArrayList<Array<CellState>>()

            val possibleStates =
                    generateAllPossibleStatesRec(numberOfCells, description, 0, 0, ArrayList())

            // convert array of indexes to array of cell states
            possibleStates.forEach { possibleState ->
                val cellStateArray = Array(numberOfCells) { CellState.WHITE }

                description.indices
                        .asSequence()
                        .map { possibleState[it] until possibleState[it] + description[it] }
                        .forEach { range -> range.forEach { cellStateArray[it] = CellState.BLACK } }

                res.add(cellStateArray)
            }

            return res
        }

        /**
         * Recursively computes all possible states for the sequences of the [description] after the
         * given [sequenceIndex] and for cells between the [startCellIndex] (included) and the end.
         * The [currentPossible] is the current possible state for the description and cells before
         * that.
         *
         * This returns a list of possible state in the form of list of integers; each element of a
         * list is the start index of a sequence of black cells (hence all resulting lists have
         * the same length than [description]).
         */
        private fun generateAllPossibleStatesRec(numberOfCells: Int, description: List<Int>,
                                                 sequenceIndex: Int, startCellIndex: Int,
                                                 currentPossible: ArrayList<Int>): List<ArrayList<Int>> {
            val res = ArrayList<ArrayList<Int>>()

            if(sequenceIndex >= description.size) {
                // base case: we exceeded the description size
                res.add(currentPossible)

            } else {
                // recursive case: call recursively for each possible start index of the current sequence
                val minNumberOfCellsTaken = minNumberOfCellsTaken(description, firstSequenceIndex = sequenceIndex)

                (startCellIndex until numberOfCells - minNumberOfCellsTaken + 1).forEach { i ->
                    val tempPossible = ArrayList(currentPossible) // clone current possible
                    tempPossible.add(i) // add start index for this sequence

                    res.addAll(generateAllPossibleStatesRec(numberOfCells, description,
                            sequenceIndex + 1, i + description[sequenceIndex] + 1, tempPossible))
                }

            }

            return res
        }

        /**
         * Computes the minimal number of cells that would be necessary for the description of this
         * line, from the first index to the last one (both included).
         *
         * For example, the description `5-3-2` would require at least 5 + 1 + 3 + 1 + 2 = 12 cells.
         */
        fun minNumberOfCellsTaken(description: List<Int>, firstSequenceIndex: Int = 0, lastSequenceIndex: Int = description.size - 1): Int {
            require(firstSequenceIndex <= lastSequenceIndex)
            require(firstSequenceIndex >= 0 && lastSequenceIndex >= 0)
            require(firstSequenceIndex < description.size && lastSequenceIndex < description.size)

            return (firstSequenceIndex..lastSequenceIndex).sumBy { description[it] + 1 } - 1
        }
    }

    /**
     * Creates a line description with the given number of cells in the line, the given
     * description and the given possible states.
     *
     * Used for clone only, as possible states should be generated in standard cases.
     */
    protected constructor(numberOfCells: Int, description: List<Int>, possibleStates: List<Array<CellState>>) {
        this.numberOfCells = numberOfCells
        this.description = description
        this.possibleStates = possibleStates
    }

    /**
     * Creates a line description with the given number of cells in the line, and the given
     * description.
     *
     * Generates the possible states.
     */
    constructor(numberOfCells: Int, description: List<Int>):
            this(numberOfCells, description, generateAllPossibleStates(numberOfCells, description))

    /**
     * Removes from the possible states every [state] that differ from the given cell at the given
     * [index].
     *
     * If the given [state] is [CellState.UNDECIDED], does nothing.
     */
    fun removeImpossibleStates(index: Int, state: CellState) {
        if(state != CellState.UNDECIDED) {
            possibleStates = possibleStates.filter { it[index] == state }
        }
    }

    operator fun get(i: Int): Int? {
        return description[i]
    }
}
