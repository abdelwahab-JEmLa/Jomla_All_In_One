package com.example.clientjetpack.ID1.Test

import android.util.Log

class OperationTrackerImp : FireBaseHandler.OperationTracker {
    private var algorithmeCounteAssertSuiveur = 0

    override fun incrementCounter() {
        algorithmeCounteAssertSuiveur += 1
    }

    override fun getCounterAlgorithmeCounteAssertSuiveur(): Int {
        logActueleCounte()
        return algorithmeCounteAssertSuiveur
    }

    override fun restartConter() {
        algorithmeCounteAssertSuiveur = 0
    }

    private fun logActueleCounte() {
        Log.d("OperationTrackerImp", "Current counter value: $algorithmeCounteAssertSuiveur")
    }
}
