package cn.sast.framework.validator

import java.io.Closeable
import java.io.Flushable
import java.io.PrintWriter
import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nPrettyTable.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PrettyTable.kt\ncn/sast/framework/validator/PrettyTable\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,61:1\n1557#2:62\n1628#2,3:63\n1557#2:66\n1628#2,3:67\n1557#2:70\n1628#2,3:71\n1567#2:74\n1598#2,4:75\n*S KotlinDebug\n*F\n+ 1 PrettyTable.kt\ncn/sast/framework/validator/PrettyTable\n*L\n17#1:62\n17#1:63,3\n21#1:66\n21#1:67,3\n24#1:70\n24#1:71,3\n53#1:74\n53#1:75,4\n*E\n"])
class PrettyTable(private val out: PrintWriter, head: List<String>) : Closeable, Flushable {
    private val table: MutableList<List<String>> = ArrayList()
    private val columns: Int = head.size
    private var blockSize: List<Int>

    init {
        table.add(head)
        blockSize = head.map { it.length }
    }

    fun addLine(line: List<Any?>) {
        val lineStr = line.map { it?.toString() ?: "" }
        table.add(lineStr)
        require(columns == line.size) { "Assertion failed" }
        
        blockSize = (0 until columns).map { i ->
            maxOf(blockSize[i], lineStr[i].length)
        }
    }

    fun dump() {
        val lines = table.size
        val normalBar = "+${blockSize.joinToString("+") { "-".repeat(it + 2) }}+"
        val doubleBar = "+${blockSize.joinToString("+") { "=".repeat(it + 2) }}+"
        
        out.println(normalBar)
        printLine(out, table[0])
        out.println(doubleBar)

        for (i in 1 until lines) {
            printLine(out, table[i])
            out.println(normalBar)
        }
    }

    override fun flush() {
        out.flush()
    }

    override fun close() {
        out.close()
    }

    private fun PrintWriter.printLine(line: List<String>) {
        print("|")
        print(line.mapIndexed { index, item ->
            val totalSize = blockSize[index] - item.length + 2
            "${" ".repeat(totalSize / 2)}$item${" ".repeat(totalSize - totalSize / 2)}"
        }.joinToString("|"))
        println("|")
    }

    companion object {
        @JvmStatic
        private fun `dump$lambda$3`(it: Int): CharSequence {
            return "-".repeat(it + 2)
        }

        @JvmStatic
        private fun `dump$lambda$4`(it: Int): CharSequence {
            return "=".repeat(it + 2)
        }
    }
}