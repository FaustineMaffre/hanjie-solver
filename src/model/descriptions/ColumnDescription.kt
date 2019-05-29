package model.descriptions

import model.CellState
import java.util.*

/**
 * Description of a column of an hanjie.
 */
class ColumnDescription: LineDescription {
    constructor(numberOfCells: Int, description: List<Int>, possibleStates: List<Array<CellState>>):
            super(numberOfCells, description, possibleStates)

    constructor(numberOfCells: Int, description: List<Int>):
            super(numberOfCells, description)

    public override fun clone(): ColumnDescription {
        val descriptionClone = ArrayList(description)
        val possibleStatesClone = possibleStates.map { it.copyOf() }

        return ColumnDescription(numberOfCells, descriptionClone, possibleStatesClone)
    }
}