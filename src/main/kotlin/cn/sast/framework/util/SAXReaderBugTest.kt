package cn.sast.framework.util

import java.io.StringReader
import org.dom4j.Document
import org.dom4j.io.SAXReader

public object SAXReaderBugTest {
    public fun test() {
        val doc: Document = SAXReader()
            .read(
                StringReader(
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<MessageCollection xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n  xsi:noNamespaceSchemaLocation=\"messagecollection.xsd\">\n  <Plugin>\n  <![CDATA[\n${StringsKt.repeat(
                        "1", 1124
                    )}\n]]>\n  </Plugin>\n</MessageCollection>"
                )
            )
    }
}