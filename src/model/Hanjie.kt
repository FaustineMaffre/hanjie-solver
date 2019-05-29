package model

import common.completeWithSpaces
import model.descriptions.ColumnDescription
import model.descriptions.LineDescription
import model.descriptions.RowDescription

/**
 * Represents a full hanjie, with its [height], [width], descriptions of each row and column and
 * state of each cell.
 */
class Hanjie(val width: Int, val height: Int,
             val rowDescriptions: Array<RowDescription>, val columnDescriptions: Array<ColumnDescription>):
        Cloneable {

    /**
     * State of each cell of this hanjie (undecided, black or white).
     */
    private val cells: Array<Array<CellState>> = Array(height) { Array(width) { CellState.UNDECIDED } }

    /**
     * Is true if all cells of this hanjie are either black or white (that is, are not undecided).
     */
    val isComplete: Boolean
        get() = cells.all{ a -> a.all { c -> c != CellState.UNDECIDED } }

    companion object {
        /**
         * Creates an array of row descriptions for an hanjie of the given [width]
         * from strings of the form of sequences "`n1,n2,...,nm`", where each `ni` is an integer,
         * separated by a '`;`'.
         */
        fun parseRowsDescription(width: Int, rowDescriptionsString: String): Array<RowDescription> {
            val rowDescriptionsStringsArray = rowDescriptionsString.split(';')
            return Array(rowDescriptionsStringsArray.size) {
                LineDescription.parseRowDescription(width, rowDescriptionsStringsArray[it])
            }
        }

        /**
         * Creates an array of column descriptions for an hanjie of the given [height]
         * from strings of the form of sequences "`n1,n2,...,nm`", where each `ni` is an integer,
         * separated by '`;`'.
         */
        fun parseColumnsDescription(height: Int, columnDescriptionsString: String): Array<ColumnDescription> {
            val columnDescriptionsStringsArray = columnDescriptionsString.split(';')
            return Array(columnDescriptionsStringsArray.size) {
                LineDescription.parseColumnDescription(height, columnDescriptionsStringsArray[it])
            }
        }
    }

    /**
     * Initializes a hanjie of the given [width] and [height] and by parsing the given descriptions,
     * that should be of the form of sequences "`n1,n2,...,nm`", where each `ni` is an integer,
     * separated by '`;`'.
     */
    constructor(width: Int, height: Int, rowDescriptionsString: String, columnDescriptionsString: String):
            this(width, height,
                    parseRowsDescription(width, rowDescriptionsString),
                    parseColumnsDescription(width, columnDescriptionsString))

    /**
     * Initializes a hanjie of the given [width] and [height] and the given cell states,
     * without description.
     *
     * (Only used when making the intersection of hanjies.)
     */
    constructor(width: Int, height: Int, cells: Array<Array<CellState>>):
            this(width, height,
                    Array(height) { RowDescription(0, listOf()) },
                    Array(width) { ColumnDescription(0, listOf()) }) {

        (0 until height).forEach { i -> (0 until width).forEach { j -> this.cells[i][j] = cells[i][j] } }
    }

    init {
        require(width > 0 && height > 0)
        require(rowDescriptions.size == height)
        require(columnDescriptions.size == width)
    }

    /**
     * Tries to solve this hanjie using the method [HanjieSolver.solve].
     */
    fun solve() {
        HanjieSolver.solve(this)
    }

    override fun toString(): String {
        val resBuilder = StringBuilder()

        val tabSize = 2
        val tab = " ".repeat(tabSize)
        val maxSpaceRows = maxSpaceRows()
        val maxSpaceColumns = maxSpaceColumns()

        // first: column descriptions
        (0 until maxSpaceColumns).forEach { i ->
            resBuilder.append(" ".repeat(tabSize * maxSpaceRows))

            (0 until width).forEach { j ->
                resBuilder.append(when {
                    i < columnDescriptions[j].description.size -> columnDescriptions[j][i].toString().completeWithSpaces(tabSize)
                    else -> tab
                })
            }

            resBuilder.append("\n")
        }

        // then: row descriptions and content
        (0 until height).forEach { i ->
            (0 until maxSpaceRows).forEach { j ->
                resBuilder.append(when {
                    j >= rowDescriptions[i].description.size -> tab
                    else -> rowDescriptions[i][j].toString().completeWithSpaces(tabSize)
                })
            }

            (0 until width).forEach { j ->
                resBuilder.append(cells[i][j].toString().completeWithSpaces(tabSize))
            }

            resBuilder.append("\n")
        }


        return resBuilder.toString()
    }

    private fun maxSpaceRows(): Int = rowDescriptions.map { it.description.size }.max()!!
    private fun maxSpaceColumns(): Int = columnDescriptions.map { it.description.size }.max()!!

    /**
     * Gets the current state of the cell at the [i]th row and [j]th column.
     */
    fun getCell(i: Int, j: Int): CellState = cells[i][j]

    /**
     * Sets the given [state] to the cell at the [i]th row and [j]th column,
     * and removes possible states of the row and column that subsequently become impossible.
     */
    fun setCell(i: Int, j: Int, state: CellState) {
        cells[i][j] = state
        rowDescriptions[i].removeImpossibleStates(j, state)
        columnDescriptions[j].removeImpossibleStates(i, state)
    }

    public override fun clone(): Hanjie {
        // clone row and column descriptions (and their possible states)
        val res = Hanjie(this.width, this.height,
                this.rowDescriptions.map { it.clone() }.toTypedArray(),
                this.columnDescriptions.map { it.clone() }.toTypedArray())

        // clone cell values
        (0 until height).forEach { i -> (0 until width).forEach { j -> res.cells[i][j] = this.cells[i][j] } }

        return res
    }
}


















