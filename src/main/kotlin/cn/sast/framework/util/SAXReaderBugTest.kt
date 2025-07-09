package cn.sast.framework.util

import org.dom4j.io.SAXReader
import java.io.StringReader

object SAXReaderBugTest {
    fun test() {
        val xml = """
            <?xml version="1.0" encoding="UTF‑8"?>
            <MessageCollection>
              <Plugin><![CDATA[${"1".repeat(1124)}]]></Plugin>
            </MessageCollection>
        """.trimIndent()
        SAXReader().read(StringReader(xml))
    }
}
