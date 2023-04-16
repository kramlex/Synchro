package ru.nsu.synchro.app.machine.ast

import kotlin.time.Duration

data class DelayNode(
    val delayDuration: Duration
) : StatementNode
