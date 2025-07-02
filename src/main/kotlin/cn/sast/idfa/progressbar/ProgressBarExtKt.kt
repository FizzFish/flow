package cn.sast.idfa.progressbar

import cn.sast.idfa.progressbar.ProgressBarExt.DefaultProcessInfoRenderer
import java.lang.reflect.Field
import me.tongfei.progressbar.ProgressBar
import me.tongfei.progressbar.ProgressBarRenderer

public fun <Render : ProgressBarRenderer> ProgressBar.wrapper(newRenderWrapper: (ProgressBar, ProgressBarRenderer) -> Render): Render {
   var field: Field = ProgressBar.class.getDeclaredField("action");
   field.setAccessible(true);
   val action: Any = field.get(`$this$wrapper`);
   field = action.getClass().getDeclaredField("renderer");
   field.setAccessible(true);
   val var10000: Any = field.get(action);
   val newRender: ProgressBarRenderer = newRenderWrapper.invoke(`$this$wrapper`, var10000 as ProgressBarRenderer) as ProgressBarRenderer;
   field.set(action, newRender);
   return (Render)newRender;
}

public fun ProgressBar.wrapper(): DefaultProcessInfoRenderer {
   return wrapper(`$this$wrapper`, ProgressBarExtKt::wrapper$lambda$2);
}

fun `wrapper$lambda$2`(processBar: ProgressBar, render: ProgressBarRenderer): ProgressBarExt.DefaultProcessInfoRenderer {
   return new ProgressBarExt.DefaultProcessInfoRenderer(processBar, render);
}
