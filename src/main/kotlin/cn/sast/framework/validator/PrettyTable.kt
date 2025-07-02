package cn.sast.framework.validator

import java.io.Closeable
import java.io.Flushable
import java.io.PrintWriter
import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nPrettyTable.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PrettyTable.kt\ncn/sast/framework/validator/PrettyTable\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,61:1\n1557#2:62\n1628#2,3:63\n1557#2:66\n1628#2,3:67\n1557#2:70\n1628#2,3:71\n1567#2:74\n1598#2,4:75\n*S KotlinDebug\n*F\n+ 1 PrettyTable.kt\ncn/sast/framework/validator/PrettyTable\n*L\n17#1:62\n17#1:63,3\n21#1:66\n21#1:67,3\n24#1:70\n24#1:71,3\n53#1:74\n53#1:75,4\n*E\n"])
public class PrettyTable(out: PrintWriter, head: List<String>) : Closeable, Flushable {
   private final val out: PrintWriter
   private final val table: MutableList<List<String>>
   private final val columns: Int
   private final var blockSize: List<Int>

   init {
      this.out = out;
      this.table = new ArrayList<>();
      this.table.add(head);
      this.columns = head.size();
      val `$this$map$iv`: java.lang.Iterable = head;
      val `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(head, 10));

      for (Object item$iv$iv : $this$map$iv) {
         `destination$iv$iv`.add((`item$iv$iv` as java.lang.String).length());
      }

      this.blockSize = `destination$iv$iv` as MutableList<Int>;
   }

   public fun addLine(line: List<Any?>) {
      var `$this$map$iv`: java.lang.Iterable = line;
      var `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(line, 10));

      for (Object item$iv$iv : $this$map$iv) {
         var var10000: java.lang.String;
         label36: {
            if (`item$iv$iv` != null) {
               var10000 = `item$iv$iv`.toString();
               if (var10000 != null) {
                  break label36;
               }
            }

            var10000 = "";
         }

         `destination$iv$iv`.add(var10000);
      }

      val lineStr: java.util.List = `destination$iv$iv` as java.util.List;
      this.table.add(`destination$iv$iv` as MutableList<java.lang.String>);
      if (_Assertions.ENABLED && this.columns != line.size()) {
         throw new AssertionError("Assertion failed");
      } else {
         `$this$map$iv` = RangesKt.until(0, this.columns) as java.lang.Iterable;
         `destination$iv$iv` = new ArrayList(CollectionsKt.collectionSizeOrDefault(`$this$map$iv`, 10));
         val var20: java.util.Iterator = `$this$map$iv`.iterator();

         while (var20.hasNext()) {
            val var21: Int = (var20 as IntIterator).nextInt();
            `destination$iv$iv`.add(Math.max(this.blockSize.get(var21).intValue(), (lineStr.get(var21) as java.lang.String).length()));
         }

         this.blockSize = `destination$iv$iv` as MutableList<Int>;
      }
   }

   public fun dump() {
      val lines: Int = this.table.size();
      val normalBar: java.lang.String = "+${CollectionsKt.joinToString$default(this.blockSize, "+", null, null, 0, null, PrettyTable::dump$lambda$3, 30, null)}+";
      val doubleBar: java.lang.String = "+${CollectionsKt.joinToString$default(this.blockSize, "+", null, null, 0, null, PrettyTable::dump$lambda$4, 30, null)}+";
      this.out.println(normalBar);
      this.printLine(this.out, this.table.get(0));
      this.out.println(doubleBar);

      for (int i = 1; i < lines; i++) {
         this.printLine(this.out, this.table.get(i));
         this.out.println(normalBar);
      }
   }

   public override fun flush() {
      this.out.flush();
   }

   public override fun close() {
      this.out.close();
   }

   private fun PrintWriter.printLine(line: List<String>) {
      `$this$printLine`.print("|");
      val `$this$mapIndexed$iv`: java.lang.Iterable = line;
      val `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(line, 10));
      var `index$iv$iv`: Int = 0;

      for (Object item$iv$iv : $this$mapIndexed$iv) {
         val var11: Int = `index$iv$iv`++;
         if (var11 < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         val totalSize: Int = this.blockSize.get(var11).intValue() - (`item$iv$iv` as java.lang.String).length() + 2;
         `destination$iv$iv`.add(
            "${StringsKt.repeat(" ", totalSize / 2)}${`item$iv$iv` as java.lang.String}${StringsKt.repeat(" ", totalSize - totalSize / 2)}"
         );
      }

      `$this$printLine`.print(CollectionsKt.joinToString$default(`destination$iv$iv` as java.util.List, "|", null, null, 0, null, null, 62, null));
      `$this$printLine`.println("|");
   }

   @JvmStatic
   fun `dump$lambda$3`(it: Int): java.lang.CharSequence {
      return StringsKt.repeat("-", it + 2);
   }

   @JvmStatic
   fun `dump$lambda$4`(it: Int): java.lang.CharSequence {
      return StringsKt.repeat("=", it + 2);
   }
}
