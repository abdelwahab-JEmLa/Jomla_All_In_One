package com.example.clientjetpack.ID1.Test

class OperationTrackerImp : FireBaseHandler.OperationTracker {
    private var algorithmeCounteAssertSuiveur = 0

    override fun incrementCounter() {
        algorithmeCounteAssertSuiveur += 1
    }

    override fun getCounterAlgorithmeCounteAssertSuiveur(): Int {
        return algorithmeCounteAssertSuiveur
    }

    override fun restartConter() {
        algorithmeCounteAssertSuiveur = 0
    }
}
