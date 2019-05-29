package model

/**
 * Provides methods to solve a hanjie.
 */
object HanjieSolver {

    /**
     * Tries to solve this hanjie.
     */
    fun solve(hanjie: Hanjie) {
        // first, try to solve "by intersection"
        val solvable = solveByIntersection(hanjie)

        if(!solvable) {
            // no solution
            println("Found a contradiction: this hanjie is unsolvable.")

        } else if(hanjie.isComplete) {
            // already complete!
            println("Hanjie complete: was solved without hypotheses.")

        } else {
            // no contradiction but not solved yet
            println("Progression before hypotheses:\n$hanjie")

            var wasUpdated = true

            // until we cannot progress anymore
            while(wasUpdated) {
                wasUpdated = false

                (0 until hanjie.height).forEach { i ->
                    (0 until hanjie.width).forEach { j ->

                        // find the undecided cells
                        if(hanjie.getCell(i, j) == CellState.UNDECIDED) {
                            // make a "black cell" hypothesis in a cloned hanjie
                            val cloneBlackHypothesis = hanjie.clone()
                            cloneBlackHypothesis.setCell(i, j, CellState.BLACK)
                            println("Hypothesis: cell ($i, $j) is black.")

                            // try to solve by intersection with the new hypothesis
                            val solvableAfterBlackHypothesis = solveByIntersection(cloneBlackHypothesis)

                            if(!solvableAfterBlackHypothesis) {
                                // no solution: black hypothesis was false, hence the cell is white
                                hanjie.setCell(i, j, CellState.WHITE)

                                // try to solve by intersection with this new information
                                solveByIntersection(hanjie)

                                println("Contradiction: hence cell ($i, $j) is white:\n$hanjie")

                                wasUpdated = true

                            } else {
                                // make a "white cell" hypothesis in a cloned hanjie
                                val cloneWhiteHypothesis = hanjie.clone()
                                cloneWhiteHypothesis.setCell(i, j, CellState.WHITE)
                                println("Hypothesis: cell ($i, $j) is white.")

                                // try to solve by intersection with the new hypothesis
                                val solvableAfterWhiteHypothesis = solveByIntersection(cloneWhiteHypothesis)

                                if(!solvableAfterWhiteHypothesis) {
                                    // no solution: positive hypothesis was false, hence the cell is black
                                    hanjie.setCell(i, j, CellState.BLACK)

                                    // try to solve by intersection with this new information
                                    solveByIntersection(hanjie)

                                    println("Contradiction: hence cell ($i, $j) is black:\n$hanjie")

                                    wasUpdated = true

                                } else {
                                    // none of the hypotheses reached a contradiction:
                                    // keep cells that are decided in both hypotheses
                                    val intersectionOfHypotheses =
                                            makeIntersectionOfHanjies(cloneBlackHypothesis, cloneWhiteHypothesis)
                                    println("Both hypotheses work: we keep the cells decided in both.")

                                    // update current hanjie with only new, decided cells
                                    var intersectionUpdatedHanjie = false

                                    (0 until hanjie.height).forEach { iI ->
                                        (0 until hanjie.width).forEach { jI ->
                                            if(intersectionOfHypotheses.getCell(iI, jI) != CellState.UNDECIDED &&
                                                    intersectionOfHypotheses.getCell(iI, jI) != hanjie.getCell(iI, jI)) {
                                                hanjie.setCell(iI, jI, intersectionOfHypotheses.getCell(iI, jI))
                                                intersectionUpdatedHanjie = true
                                            }
                                        }
                                    }

                                    if(intersectionUpdatedHanjie) {
                                        println("After applying intersection of hypotheses:\n$hanjie")
                                        wasUpdated = true
                                    } else {
                                        println("No new information with the intersection of hypotheses.")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // final state
        println("Final:\n$hanjie")
    }

    /**
     * Tries to solve the given hanjie by making the intersection of possible states of cells
     * using their description.
     * Does not make any hypothesis.
     *
     * To be more precise: for each row and each column, computes the cells that are
     * always black or always white in every possible state of this row or column (in other words,
     * makes the "intersection" of all possible states), and sets these cells' values.
     * This is repeated until no more value is modified.
     *
     * Returns false if this hanjie could not be solved because a contradiction was found, that is,
     * there is a row or column with an empty set of possible states; true otherwise.
     *
     * If this method returns true, this does *not* mean that the hanjie was solved (i.e.,
     * all cells have a decided value); this can be checked by calling [Hanjie.isComplete].
     */
    private fun solveByIntersection(hanjie: Hanjie): Boolean {
        var isSolvable = true
        var wasUpdated = true

        // until we cannot progress anymore
        while(wasUpdated) {
            wasUpdated = false

            // rows
            (0 until hanjie.height).forEach { i ->
                if(hanjie.rowDescriptions[i].possibleStates.isEmpty()) {
                    // no possible state: the hanjie is unsolvable
                    isSolvable = false

                } else {
                    // find the intersection of possible states of the row
                    val intersectionPossibleStates =
                            CellState.intersectionStates(hanjie.rowDescriptions[i].possibleStates)

                    // set it to the row (removing now impossible states), tracking updates
                    if(setRow(hanjie, i, intersectionPossibleStates)) {
                        wasUpdated = true
                    }
                }
            }

            // columns
            (0 until hanjie.width).forEach { j ->
                if(hanjie.columnDescriptions[j].possibleStates.isEmpty()) {
                    // no possible state: the hanjie is unsolvable
                    isSolvable = false

                } else {
                    // find the intersection of possible states of the column
                    val intersectionPossibleStates =
                            CellState.intersectionStates(hanjie.columnDescriptions[j].possibleStates)

                    // set it to the column (removing now impossible states), tracking updates
                    if(setColumn(hanjie, j, intersectionPossibleStates)) {
                        wasUpdated = true
                    }
                }
            }
        }

        return isSolvable
    }

    /**
     * Sets the value of the [i]th row of the given hanjie to the given [values],
     * only taking into account the black or white values (not the undecided values).
     * Also updates the possible states of the columns.
     *
     * Returns true if at least one change was made.
     *
     * Does not check the length of the array of values.
     */
    private fun setRow(hanjie: Hanjie, i: Int, values: Array<CellState>): Boolean {
        var wasUpdated = false

        (0 until hanjie.width).forEach { j ->
            if(values[j] != CellState.UNDECIDED && hanjie.getCell(i, j) != values[j]) {
                hanjie.setCell(i, j, values[j])
                wasUpdated = true
            }
        }

        return wasUpdated
    }

    /**
     * Sets the value of the [j]th column of the given hanjie to the given [values],
     * only taking into account the black or white values (not the undecided values).
     * Also updates the possible states of the rows.
     *
     * Returns true if at least one change was made.
     *
     * Does not check the length of the array of values.
     */
    private fun setColumn(hanjie: Hanjie, j: Int, values: Array<CellState>): Boolean {
        var wasUpdated = false

        (0 until hanjie.height).forEach { i ->
            if(values[i] != CellState.UNDECIDED && hanjie.getCell(i, j) != values[i]) {
                hanjie.setCell(i, j, values[i])
                wasUpdated = true
            }
        }

        return wasUpdated
    }

    /**
     * Returns a hanjie (without descriptions) with, for each cell:
     * - if both hanjies contains the same value for this cell, this value;
     * - undecided otherwise.
     */
    private fun makeIntersectionOfHanjies(hanjie1: Hanjie, hanjie2: Hanjie): Hanjie {
        require(hanjie1.width == hanjie2.width)
        require(hanjie1.height == hanjie2.height)

        return Hanjie(hanjie1.width, hanjie1.height,
                (0 until hanjie1.height).map { i ->
                    (0 until hanjie1.width).map { j ->
                        if(hanjie1.getCell(i, j) == hanjie2.getCell(i, j)) hanjie1.getCell(i, j) else CellState.UNDECIDED
                    }.toTypedArray()
                }.toTypedArray()
        )
    }
}