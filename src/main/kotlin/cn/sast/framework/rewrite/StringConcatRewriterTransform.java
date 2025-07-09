package cn.sast.framework.rewrite;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.Body;
import soot.BodyTransformer;
import soot.options.Options;

public class StringConcatRewriterTransform extends BodyTransformer {
   public static final String phase = "jb.rewriter";
   private static final Logger logger = LoggerFactory.getLogger(StringConcatRewriterTransform.class);

   protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
      if (Options.v().verbose()) {
         logger.debug("[" + b.getMethod().getName() + "] Rewrite string concat...");
      }

      new StringConcatRewriterPlugin().transformStringConcats(b);
   }
}
