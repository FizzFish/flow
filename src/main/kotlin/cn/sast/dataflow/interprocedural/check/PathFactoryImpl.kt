package cn.sast.dataflow.interprocedural.check

import cn.sast.dataflow.interprocedural.analysis.CompanionV
import cn.sast.dataflow.interprocedural.analysis.HeapValuesEnv
import cn.sast.dataflow.interprocedural.analysis.IData
import cn.sast.dataflow.interprocedural.analysis.IHeapValues
import cn.sast.dataflow.interprocedural.analysis.IReNew
import cn.sast.dataflow.interprocedural.analysis.IValue
import cn.sast.dataflow.interprocedural.check.heapimpl.ImmutableElementHashMap

public class PathFactoryImpl : PathFactory<IValue> {
   public open fun setModelData(env: HeapValuesEnv, obj: IValue, mt: Any, data: IData<IValue>): IData<IValue> {
      return data.cloneAndReNewObjects(new IReNew<IValue>(data, env, obj, mt) {
         {
            this.$data = `$data`;
            this.$env = `$env`;
            this.$obj = `$obj`;
            this.$mt = `$mt`;
         }

         @Override
         public CompanionV<IValue> checkNeedReplace(CompanionV<IValue> c) {
            if (this.$data is ImmutableElementHashMap) {
               val p: ModelBind = ModelBind.Companion.v(this.$env, this.$obj, this.$mt, this.$data, (c as PathCompanionV).getPath());
               if (c is CompanionValueOfConst) {
                  return new CompanionValueOfConst((c as CompanionValueOfConst).getValue(), p, (c as CompanionValueOfConst).getAttr());
               }

               if (c is CompanionValueImpl1) {
                  return new CompanionValueImpl1((c as CompanionValueImpl1).getValue(), p);
               }
            }

            return null;
         }

         public IValue checkNeedReplace(IValue old) {
            return IReNew.DefaultImpls.checkNeedReplace(this, old);
         }

         @Override
         public IReNew<IValue> context(Object value) {
            return IReNew.DefaultImpls.context(this, value);
         }
      });
   }

   public override fun updatePath(env: HeapValuesEnv, data: IHeapValues<IValue>, newPath: (IValue, IPath) -> IPath): IHeapValues<IValue> {
      return data.cloneAndReNewObjects(new IReNew<IValue>(newPath) {
         {
            this.$newPath = `$newPath`;
         }

         @Override
         public CompanionV<IValue> checkNeedReplace(CompanionV<IValue> c) {
            val p: IPath = this.$newPath.invoke(c.getValue(), (c as PathCompanionV).getPath()) as IPath;
            if (c is CompanionValueOfConst) {
               return new CompanionValueOfConst((c as CompanionValueOfConst).getValue(), p, (c as CompanionValueOfConst).getAttr());
            } else {
               return new CompanionValueImpl1((c as CompanionValueImpl1).getValue(), p) as? CompanionV;
            }
         }

         public IValue checkNeedReplace(IValue old) {
            return IReNew.DefaultImpls.checkNeedReplace(this, old);
         }

         @Override
         public IReNew<IValue> context(Object value) {
            return IReNew.DefaultImpls.context(this, value);
         }
      });
   }
}
