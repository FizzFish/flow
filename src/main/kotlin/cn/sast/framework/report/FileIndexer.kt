package cn.sast.framework.report

import cn.sast.common.IResFile
import cn.sast.framework.report.AbstractFileIndexer.CompareMode
import java.nio.file.Path
import java.util.Map.Entry

public class FileIndexer(fileNameToPathMap: Map<String, Set<IResFile>>, extensionToPathMap: Map<String, Set<IResFile>>) : AbstractFileIndexer<IResFile> {
   internal final val fileNameToPathMap: Map<String, Set<IResFile>>
   internal final val extensionToPathMap: Map<String, Set<IResFile>>

   public final val count: Long
      public final get() {
         return (this.count$delegate.getValue() as java.lang.Number).longValue();
      }


   init {
      this.fileNameToPathMap = fileNameToPathMap;
      this.extensionToPathMap = extensionToPathMap;
      this.count$delegate = LazyKt.lazy(FileIndexer::count_delegate$lambda$0);
   }

   public open fun getNames(path: IResFile, mode: CompareMode): List<String> {
      val p: Path = path.getPath();
      var var4: Int = 0;
      val var5: Int = p.getNameCount();

      val var6: Array<java.lang.String>;
      for (var6 = new java.lang.String[var5]; var4 < var5; var4++) {
         val var10002: Path = p.getName(var4);
         var6[var4] = var10002.toString();
      }

      return ArraysKt.toList(var6);
   }

   public override fun getPathsByName(name: String): Collection<IResFile> {
      val var10000: java.util.Set = this.fileNameToPathMap.get(name);
      return (java.util.Collection<IResFile>)(if (var10000 != null) var10000 else CollectionsKt.emptyList());
   }

   public fun getPathsByExtension(extension: String): Collection<IResFile> {
      val var10000: java.util.Set = this.extensionToPathMap.get(extension);
      return (java.util.Collection<IResFile>)(if (var10000 != null) var10000 else CollectionsKt.emptyList());
   }

   @JvmStatic
   fun `count_delegate$lambda$0`(`this$0`: FileIndexer): Long {
      var c: Long = 0L;

      for (Entry x : this$0.fileNameToPathMap.entrySet()) {
         c += (x.getValue() as java.util.Set).size();
      }

      return c;
   }
}
