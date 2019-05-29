package model.descriptions

import model.CellState
import java.util.*

/**
 * Description of a row of an hanjie.
 */
class RowDescription: LineDescription {
    constructor(numberOfCells: Int, description: List<Int>, possibleStates: List<Array<CellState>>):
            super(numberOfCells, description, possibleStates)

    constructor(numberOfCells: Int, description: List<Int>):
            super(numberOfCells, description)

    public override fun clone(): RowDescription {
        val descriptionClone = ArrayList(description)
        val possibleStatesClone = possibleStates.map { it.copyOf() }

        return RowDescription(numberOfCells, descriptionClone, possibleStatesClone)
    }
}