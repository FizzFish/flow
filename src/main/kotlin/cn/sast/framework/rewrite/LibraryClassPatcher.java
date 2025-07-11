package cn.sast.framework.rewrite;

import java.util.Collections;
import soot.Body;
import soot.IntType;
import soot.Local;
import soot.LocalGenerator;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootFieldRef;
import soot.SootMethod;
import soot.Unit;
import soot.VoidType;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.NullConstant;
import soot.jimple.Stmt;
import soot.jimple.infoflow.cfg.FlowDroidEssentialMethodTag;
import soot.jimple.infoflow.util.SystemClassHandler;

public class LibraryClassPatcher {
   public void patchLibraries() {
      this.patchHandlerImplementation();
      this.patchThreadImplementation();
      this.patchActivityImplementation();
      this.patchTimerImplementation();
      this.patchActivityGetFragmentManager();
      this.patchMessageObtainImplementation();
   }

   private void patchMessageObtainImplementation() {
      SootClass sc = Scene.v().getSootClassUnsafe("android.os.Message");
      if (sc != null && sc.resolvingLevel() >= 2) {
         sc.setLibraryClass();
         SootMethod smMessageConstructor = Scene.v().grabMethod("<android.os.Message: void <init>()>");
         if (smMessageConstructor != null) {
            SootField tmp = sc.getFieldUnsafe("int what");
            if (tmp == null) {
               tmp = Scene.v().makeSootField("what", IntType.v());
               sc.addField(tmp);
            }

            tmp = sc.getFieldUnsafe("int arg1");
            if (tmp == null) {
               tmp = Scene.v().makeSootField("arg1", IntType.v());
               sc.addField(tmp);
            }

            tmp = sc.getFieldUnsafe("int arg2");
            if (tmp == null) {
               tmp = Scene.v().makeSootField("arg2", IntType.v());
               sc.addField(tmp);
            }

            tmp = sc.getFieldUnsafe("java.lang.Object obj");
            if (tmp == null) {
               tmp = Scene.v().makeSootField("obj", Scene.v().getObjectType());
               sc.addField(tmp);
            }

            SystemClassHandler systemClassHandler = SystemClassHandler.v();
            SootMethod smObtain1 = sc.getMethodUnsafe("android.os.Message obtain(android.os.Handler,int)");
            SootFieldRef tmpRef = tmp.makeRef();
            if (smObtain1 != null && (!smObtain1.hasActiveBody() || systemClassHandler.isStubImplementation(smObtain1.getActiveBody()))) {
               this.generateMessageObtainMethod(smObtain1, new LibraryClassPatcher.IMessageObtainCodeInjector() {
                  @Override
                  public void injectCode(Body body, Local messageLocal) {
                     body.getUnits().add(Jimple.v().newAssignStmt(Jimple.v().newInstanceFieldRef(messageLocal, tmpRef), body.getParameterLocal(1)));
                  }
               });
            }

            SootMethod smObtain2 = sc.getMethodUnsafe("android.os.Message obtain(android.os.Handler,int,int,int,java.lang.Object)");
            if (smObtain2 != null && (!smObtain2.hasActiveBody() || systemClassHandler.isStubImplementation(smObtain2.getActiveBody()))) {
               this.generateMessageObtainMethod(smObtain2, new LibraryClassPatcher.IMessageObtainCodeInjector() {
                  @Override
                  public void injectCode(Body body, Local messageLocal) {
                     body.getUnits().add(Jimple.v().newAssignStmt(Jimple.v().newInstanceFieldRef(messageLocal, tmpRef), body.getParameterLocal(1)));
                     body.getUnits().add(Jimple.v().newAssignStmt(Jimple.v().newInstanceFieldRef(messageLocal, tmpRef), body.getParameterLocal(2)));
                     body.getUnits().add(Jimple.v().newAssignStmt(Jimple.v().newInstanceFieldRef(messageLocal, tmpRef), body.getParameterLocal(3)));
                     body.getUnits().add(Jimple.v().newAssignStmt(Jimple.v().newInstanceFieldRef(messageLocal, tmpRef), body.getParameterLocal(4)));
                  }
               });
            }

            SootMethod smObtain3 = sc.getMethodUnsafe("android.os.Message obtain(android.os.Handler,int,int,int)");
            if (smObtain3 != null && (!smObtain3.hasActiveBody() || systemClassHandler.isStubImplementation(smObtain3.getActiveBody()))) {
               this.generateMessageObtainMethod(smObtain3, new LibraryClassPatcher.IMessageObtainCodeInjector() {
                  @Override
                  public void injectCode(Body body, Local messageLocal) {
                     body.getUnits().add(Jimple.v().newAssignStmt(Jimple.v().newInstanceFieldRef(messageLocal, tmpRef), body.getParameterLocal(1)));
                     body.getUnits().add(Jimple.v().newAssignStmt(Jimple.v().newInstanceFieldRef(messageLocal, tmpRef), body.getParameterLocal(2)));
                     body.getUnits().add(Jimple.v().newAssignStmt(Jimple.v().newInstanceFieldRef(messageLocal, tmpRef), body.getParameterLocal(3)));
                  }
               });
            }

            SootMethod smObtain4 = sc.getMethodUnsafe("android.os.Message obtain(android.os.Handler,int,java.lang.Object)");
            if (smObtain4 != null && (!smObtain4.hasActiveBody() || systemClassHandler.isStubImplementation(smObtain4.getActiveBody()))) {
               this.generateMessageObtainMethod(smObtain4, new LibraryClassPatcher.IMessageObtainCodeInjector() {
                  @Override
                  public void injectCode(Body body, Local messageLocal) {
                     body.getUnits().add(Jimple.v().newAssignStmt(Jimple.v().newInstanceFieldRef(messageLocal, tmpRef), body.getParameterLocal(1)));
                     body.getUnits().add(Jimple.v().newAssignStmt(Jimple.v().newInstanceFieldRef(messageLocal, tmpRef), body.getParameterLocal(2)));
                  }
               });
            }
         }
      }
   }

   private void generateMessageObtainMethod(SootMethod sm, LibraryClassPatcher.IMessageObtainCodeInjector injector) {
      RefType tpMessage = RefType.v("android.os.Message");
      sm.getDeclaringClass().setLibraryClass();
      sm.setPhantom(false);
      sm.addTag(new FlowDroidEssentialMethodTag());
      JimpleBody body = Jimple.v().newBody(sm);
      sm.setActiveBody(body);
      body.insertIdentityStmts();
      SootMethod smMessageConstructor = Scene.v().grabMethod("<android.os.Message: void <init>()>");
      LocalGenerator lg = Scene.v().createLocalGenerator(body);
      Local messageLocal = lg.generateLocal(tpMessage);
      body.getUnits().add(Jimple.v().newAssignStmt(messageLocal, Jimple.v().newNewExpr(tpMessage)));
      body.getUnits().add(Jimple.v().newInvokeStmt(Jimple.v().newSpecialInvokeExpr(messageLocal, smMessageConstructor.makeRef())));
      if (injector != null) {
         injector.injectCode(body, messageLocal);
      }

      body.getUnits().add(Jimple.v().newReturnStmt(messageLocal));
   }

   private void patchActivityImplementation() {
      SootClass scApplicationHolder = createOrGetApplicationHolder();
      SootClass sc = Scene.v().getSootClassUnsafe("android.app.Activity");
      if (sc != null && sc.resolvingLevel() >= 2 && scApplicationHolder != null) {
         sc.setLibraryClass();
         SootMethod smRun = sc.getMethodUnsafe("android.app.Application getApplication()");
         if (smRun != null && (!smRun.hasActiveBody() || SystemClassHandler.v().isStubImplementation(smRun.getActiveBody()))) {
            smRun.setPhantom(false);
            smRun.addTag(new FlowDroidEssentialMethodTag());
            Body b = Jimple.v().newBody(smRun);
            smRun.setActiveBody(b);
            Local thisLocal = Jimple.v().newLocal("this", sc.getType());
            b.getLocals().add(thisLocal);
            b.getUnits().add(Jimple.v().newIdentityStmt(thisLocal, Jimple.v().newThisRef(sc.getType())));
            SootFieldRef appStaticFieldRef = scApplicationHolder.getFieldByName("application").makeRef();
            Local targetLocal = Jimple.v().newLocal("retApplication", appStaticFieldRef.type());
            b.getLocals().add(targetLocal);
            b.getUnits().add(Jimple.v().newAssignStmt(targetLocal, Jimple.v().newStaticFieldRef(appStaticFieldRef)));
            Unit retStmt = Jimple.v().newReturnStmt(targetLocal);
            b.getUnits().add(retStmt);
         }
      }
   }

   public static SootClass createOrGetApplicationHolder() {
      SootClass scApplication = Scene.v().getSootClassUnsafe("android.app.Application");
      if (scApplication != null && scApplication.resolvingLevel() >= 2) {
         String applicationHolderClassName = "il.ac.tau.MyApplicationHolder";
         SootClass scApplicationHolder;
         if (!Scene.v().containsClass(applicationHolderClassName)) {
            scApplicationHolder = Scene.v().makeSootClass(applicationHolderClassName, 1);
            scApplicationHolder.setSuperclass(Scene.v().getSootClass("java.lang.Object"));
            Scene.v().addClass(scApplicationHolder);
            scApplicationHolder.addField(Scene.v().makeSootField("application", scApplication.getType(), 9));
            scApplicationHolder.validate();
         } else {
            scApplicationHolder = Scene.v().getSootClassUnsafe(applicationHolderClassName);
         }

         return scApplicationHolder;
      } else {
         return null;
      }
   }

   private void patchThreadImplementation() {
      SootClass sc = Scene.v().getSootClassUnsafe("java.lang.Thread");
      if (sc != null && sc.resolvingLevel() >= 2) {
         sc.setLibraryClass();
         SystemClassHandler systemClassHandler = SystemClassHandler.v();
         SootMethod smRun = sc.getMethodUnsafe("void run()");
         if (smRun != null && (!smRun.hasActiveBody() || systemClassHandler.isStubImplementation(smRun.getActiveBody()))) {
            smRun.addTag(new FlowDroidEssentialMethodTag());
            SootMethod smStart = sc.getMethodUnsafe("void start()");
            if (smStart != null && (!smStart.hasActiveBody() || systemClassHandler.isStubImplementation(smStart.getActiveBody()))) {
               smStart.addTag(new FlowDroidEssentialMethodTag());
               SootMethod smCons = sc.getMethodUnsafe("void <init>(java.lang.Runnable)");
               if (smCons != null && (!smCons.hasActiveBody() || systemClassHandler.isStubImplementation(smCons.getActiveBody()))) {
                  smCons.addTag(new FlowDroidEssentialMethodTag());
                  SootClass runnable = Scene.v().getSootClassUnsafe("java.lang.Runnable");
                  if (runnable != null && runnable.resolvingLevel() >= 2) {
                     int fieldIdx = 0;
                     SootField fldTarget = null;

                     while (sc.getFieldByNameUnsafe("target" + fieldIdx) != null) {
                        fieldIdx++;
                     }

                     fldTarget = Scene.v().makeSootField("target" + fieldIdx, runnable.getType());
                     sc.addField(fldTarget);
                     this.patchThreadConstructor(smCons, runnable, fldTarget);
                     this.patchThreadRunMethod(smRun, runnable, fldTarget);
                     this.patchThreadRunMethod(smStart, runnable, fldTarget);
                  }
               }
            }
         }
      }
   }

   private void patchThreadRunMethod(SootMethod smRun, SootClass runnable, SootField fldTarget) {
      SootClass sc = smRun.getDeclaringClass();
      sc.setLibraryClass();
      smRun.setPhantom(false);
      Body b = Jimple.v().newBody(smRun);
      smRun.setActiveBody(b);
      Local thisLocal = Jimple.v().newLocal("this", sc.getType());
      b.getLocals().add(thisLocal);
      b.getUnits().add(Jimple.v().newIdentityStmt(thisLocal, Jimple.v().newThisRef(sc.getType())));
      Local targetLocal = Jimple.v().newLocal("target", runnable.getType());
      b.getLocals().add(targetLocal);
      b.getUnits().add(Jimple.v().newAssignStmt(targetLocal, Jimple.v().newInstanceFieldRef(thisLocal, fldTarget.makeRef())));
      Unit retStmt = Jimple.v().newReturnVoidStmt();
      b.getUnits().add(Jimple.v().newIfStmt(Jimple.v().newEqExpr(targetLocal, NullConstant.v()), retStmt));
      b.getUnits().add(Jimple.v().newInvokeStmt(Jimple.v().newInterfaceInvokeExpr(targetLocal, runnable.getMethod("void run()").makeRef())));
      b.getUnits().add(Jimple.v().newInvokeStmt(Jimple.v().newInterfaceInvokeExpr(thisLocal, runnable.getMethod("void run()").makeRef())));
      b.getUnits().add(retStmt);
   }

   private void patchThreadConstructor(SootMethod smCons, SootClass runnable, SootField fldTarget) {
      SootClass sc = smCons.getDeclaringClass();
      sc.setLibraryClass();
      smCons.setPhantom(false);
      Body b = Jimple.v().newBody(smCons);
      smCons.setActiveBody(b);
      Local thisLocal = Jimple.v().newLocal("this", sc.getType());
      b.getLocals().add(thisLocal);
      b.getUnits().add(Jimple.v().newIdentityStmt(thisLocal, Jimple.v().newThisRef(sc.getType())));
      Local param0Local = Jimple.v().newLocal("p0", runnable.getType());
      b.getLocals().add(param0Local);
      b.getUnits().add(Jimple.v().newIdentityStmt(param0Local, Jimple.v().newParameterRef(runnable.getType(), 0)));
      b.getUnits().add(Jimple.v().newAssignStmt(Jimple.v().newInstanceFieldRef(thisLocal, fldTarget.makeRef()), param0Local));
      b.getUnits().add(Jimple.v().newReturnVoidStmt());
   }

   private void patchHandlerImplementation() {
      SootClass sc = Scene.v().getSootClassUnsafe("android.os.Handler");
      if (sc != null && sc.resolvingLevel() >= 2) {
         sc.setLibraryClass();
         SootClass runnable = Scene.v().getSootClassUnsafe("java.lang.Runnable");
         if (runnable != null && runnable.resolvingLevel() >= 2) {
            SootMethod smPost = sc.getMethodUnsafe("boolean post(java.lang.Runnable)");
            SootMethod smPostAtFrontOfQueue = sc.getMethodUnsafe("boolean postAtFrontOfQueue(java.lang.Runnable)");
            SootMethod smPostAtTimeWithToken = sc.getMethodUnsafe("boolean postAtTime(java.lang.Runnable,java.lang.Object,long)");
            SootMethod smPostAtTime = sc.getMethodUnsafe("boolean postAtTime(java.lang.Runnable,long)");
            SootMethod smPostDelayed = sc.getMethodUnsafe("boolean postDelayed(java.lang.Runnable,long)");
            SootMethod smDispatchMessage = sc.getMethodUnsafe("void dispatchMessage(android.os.Message)");
            SystemClassHandler systemClassHandler = SystemClassHandler.v();
            if (smPost != null && (!smPost.hasActiveBody() || systemClassHandler.isStubImplementation(smPost.getActiveBody()))) {
               this.patchHandlerPostBody(smPost, runnable);
               smPost.addTag(new FlowDroidEssentialMethodTag());
            }

            if (smPostAtFrontOfQueue != null
               && (!smPostAtFrontOfQueue.hasActiveBody() || systemClassHandler.isStubImplementation(smPostAtFrontOfQueue.getActiveBody()))) {
               this.patchHandlerPostBody(smPostAtFrontOfQueue, runnable);
               smPostAtFrontOfQueue.addTag(new FlowDroidEssentialMethodTag());
            }

            if (smPostAtTime != null && (!smPostAtTime.hasActiveBody() || systemClassHandler.isStubImplementation(smPostAtTime.getActiveBody()))) {
               this.patchHandlerPostBody(smPostAtTime, runnable);
               smPostAtTime.addTag(new FlowDroidEssentialMethodTag());
            }

            if (smPostAtTimeWithToken != null
               && (!smPostAtTimeWithToken.hasActiveBody() || systemClassHandler.isStubImplementation(smPostAtTimeWithToken.getActiveBody()))) {
               this.patchHandlerPostBody(smPostAtTimeWithToken, runnable);
               smPostAtTimeWithToken.addTag(new FlowDroidEssentialMethodTag());
            }

            if (smPostDelayed != null && (!smPostDelayed.hasActiveBody() || systemClassHandler.isStubImplementation(smPostDelayed.getActiveBody()))) {
               this.patchHandlerPostBody(smPostDelayed, runnable);
               smPostDelayed.addTag(new FlowDroidEssentialMethodTag());
            }

            if (smDispatchMessage != null && (!smDispatchMessage.hasActiveBody() || systemClassHandler.isStubImplementation(smDispatchMessage.getActiveBody()))
               )
             {
               this.patchHandlerDispatchBody(smDispatchMessage);
               smDispatchMessage.addTag(new FlowDroidEssentialMethodTag());
            }
         }
      }
   }

   private Body patchHandlerDispatchBody(SootMethod method) {
      SootClass sc = method.getDeclaringClass();
      sc.setLibraryClass();
      method.setPhantom(false);
      Body b = Jimple.v().newBody(method);
      method.setActiveBody(b);
      Local thisLocal = Jimple.v().newLocal("this", sc.getType());
      b.getLocals().add(thisLocal);
      b.getUnits().add(Jimple.v().newIdentityStmt(thisLocal, Jimple.v().newThisRef(sc.getType())));
      Local firstParam = null;

      for (int i = 0; i < method.getParameterCount(); i++) {
         Local paramLocal = Jimple.v().newLocal("param" + i, method.getParameterType(i));
         b.getLocals().add(paramLocal);
         b.getUnits().add(Jimple.v().newIdentityStmt(paramLocal, Jimple.v().newParameterRef(method.getParameterType(i), i)));
         if (i == 0) {
            firstParam = paramLocal;
         }
      }

      b.getUnits()
         .add(
            Jimple.v()
               .newInvokeStmt(
                  Jimple.v()
                     .newVirtualInvokeExpr(
                        thisLocal,
                        Scene.v().makeMethodRef(sc, "handleMessage", Collections.singletonList(method.getParameterType(0)), VoidType.v(), false),
                        firstParam
                     )
               )
         );
      Unit retStmt = Jimple.v().newReturnVoidStmt();
      b.getUnits().add(retStmt);
      return b;
   }

   private Body patchHandlerPostBody(SootMethod method, SootClass runnable) {
      SootClass sc = method.getDeclaringClass();
      sc.setLibraryClass();
      method.setPhantom(false);
      Body b = Jimple.v().newBody(method);
      method.setActiveBody(b);
      Local thisLocal = Jimple.v().newLocal("this", sc.getType());
      b.getLocals().add(thisLocal);
      b.getUnits().add(Jimple.v().newIdentityStmt(thisLocal, Jimple.v().newThisRef(sc.getType())));
      Local firstParam = null;

      for (int i = 0; i < method.getParameterCount(); i++) {
         Local paramLocal = Jimple.v().newLocal("param" + i, method.getParameterType(i));
         b.getLocals().add(paramLocal);
         b.getUnits().add(Jimple.v().newIdentityStmt(paramLocal, Jimple.v().newParameterRef(method.getParameterType(i), i)));
         if (i == 0) {
            firstParam = paramLocal;
         }
      }

      b.getUnits()
         .add(
            Jimple.v()
               .newInvokeStmt(
                  Jimple.v().newInterfaceInvokeExpr(firstParam, Scene.v().makeMethodRef(runnable, "run", Collections.emptyList(), VoidType.v(), false))
               )
         );
      Unit retStmt = Jimple.v().newReturnStmt(IntConstant.v(1));
      b.getUnits().add(retStmt);
      return b;
   }

   private void patchTimerImplementation() {
      SootClass sc = Scene.v().getSootClassUnsafe("java.util.Timer");
      if (sc != null && sc.resolvingLevel() >= 2) {
         sc.setLibraryClass();
         SootMethod smSchedule1 = sc.getMethodUnsafe("void schedule(java.util.TimerTask,long)");
         if (smSchedule1 != null && !smSchedule1.hasActiveBody()) {
            this.patchTimerScheduleMethod(smSchedule1);
            smSchedule1.addTag(new FlowDroidEssentialMethodTag());
         }

         SootMethod smSchedule2 = sc.getMethodUnsafe("void schedule(java.util.TimerTask,java.util.Date)");
         if (smSchedule2 != null && !smSchedule2.hasActiveBody()) {
            this.patchTimerScheduleMethod(smSchedule2);
            smSchedule2.addTag(new FlowDroidEssentialMethodTag());
         }

         SootMethod smSchedule3 = sc.getMethodUnsafe("void schedule(java.util.TimerTask,java.util.Date,long)");
         if (smSchedule3 != null && !smSchedule3.hasActiveBody()) {
            this.patchTimerScheduleMethod(smSchedule3);
            smSchedule3.addTag(new FlowDroidEssentialMethodTag());
         }

         SootMethod smSchedule4 = sc.getMethodUnsafe("void schedule(java.util.TimerTask,long,long)");
         if (smSchedule4 != null && !smSchedule4.hasActiveBody()) {
            this.patchTimerScheduleMethod(smSchedule4);
            smSchedule4.addTag(new FlowDroidEssentialMethodTag());
         }

         SootMethod smSchedule5 = sc.getMethodUnsafe("void scheduleAtFixedRate(java.util.TimerTask,java.util.Date,long)");
         if (smSchedule5 != null && !smSchedule5.hasActiveBody()) {
            this.patchTimerScheduleMethod(smSchedule5);
            smSchedule5.addTag(new FlowDroidEssentialMethodTag());
         }

         SootMethod smSchedule6 = sc.getMethodUnsafe("void scheduleAtFixedRate(java.util.TimerTask,long,long)");
         if (smSchedule6 != null && !smSchedule6.hasActiveBody()) {
            this.patchTimerScheduleMethod(smSchedule6);
            smSchedule6.addTag(new FlowDroidEssentialMethodTag());
         }
      }
   }

   private void patchTimerScheduleMethod(SootMethod method) {
      SootClass sc = method.getDeclaringClass();
      sc.setLibraryClass();
      method.setPhantom(false);
      Body b = Jimple.v().newBody(method);
      method.setActiveBody(b);
      Local thisLocal = Jimple.v().newLocal("this", sc.getType());
      b.getLocals().add(thisLocal);
      b.getUnits().add(Jimple.v().newIdentityStmt(thisLocal, Jimple.v().newThisRef(sc.getType())));
      Local firstParam = null;

      for (int i = 0; i < method.getParameterCount(); i++) {
         Local paramLocal = Jimple.v().newLocal("param" + i, method.getParameterType(i));
         b.getLocals().add(paramLocal);
         b.getUnits().add(Jimple.v().newIdentityStmt(paramLocal, Jimple.v().newParameterRef(method.getParameterType(i), i)));
         if (i == 0) {
            firstParam = paramLocal;
         }
      }

      SootMethod runMethod = Scene.v().grabMethod("<java.util.TimerTask: void run()>");
      if (runMethod != null) {
         Stmt invokeStmt = Jimple.v().newInvokeStmt(Jimple.v().newVirtualInvokeExpr(firstParam, runMethod.makeRef()));
         b.getUnits().add(invokeStmt);
      }

      b.getUnits().add(Jimple.v().newReturnVoidStmt());
   }

   private void patchActivityGetFragmentManager() {
      SootClass sc = Scene.v().getSootClassUnsafe("android.app.Activity");
      if (sc != null && sc.resolvingLevel() >= 2) {
         sc.setLibraryClass();
         SootMethod smGetFM = sc.getMethodUnsafe("android.app.FragmentManager getFragmentManager()");
         if (smGetFM != null && !smGetFM.hasActiveBody()) {
            Body b = Jimple.v().newBody(smGetFM);
            smGetFM.setActiveBody(b);
            Local thisLocal = Jimple.v().newLocal("this", sc.getType());
            b.getLocals().add(thisLocal);
            b.getUnits().add(Jimple.v().newIdentityStmt(thisLocal, Jimple.v().newThisRef(sc.getType())));
            SootClass scFragmentTransaction = Scene.v().forceResolve("android.app.FragmentManager", 2);
            Local retLocal = Jimple.v().newLocal("retFragMan", Scene.v().getSootClassUnsafe("android.app.FragmentManager").getType());
            b.getLocals().add(retLocal);
            b.getUnits().add(Jimple.v().newAssignStmt(retLocal, Jimple.v().newNewExpr(scFragmentTransaction.getType())));
            b.getUnits().add(Jimple.v().newReturnStmt(retLocal));
         }
      }
   }

   private interface IMessageObtainCodeInjector {
      void injectCode(Body var1, Local var2);
   }
}
