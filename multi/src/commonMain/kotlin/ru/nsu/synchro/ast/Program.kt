package ru.nsu.synchro.ast

data class Program(val context: Context?, val set: Set?, val data: Data) : AstNode
