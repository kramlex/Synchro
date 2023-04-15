package ru.nsu.synchro.msecd.machine.shared

class SharedMemoryException: RuntimeException {
    constructor()
    constructor(message: String?) : super(message)
}
