package cn.sast.framework.report.coverage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jacoco.core.internal.analysis.Instruction;
import org.jacoco.core.internal.flow.LabelInfo;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.AbstractInsnNode;

public class InstructionsBuilder {
   private final boolean[] probes;
   private int currentLine;
   public Instruction currentInsn;
   private final Map<AbstractInsnNode, Instruction> instructions;
   private final List<Label> currentLabel;
   private final List<InstructionsBuilder.Jump> jumps;

   InstructionsBuilder(boolean[] probes) {
      this.probes = probes;
      this.currentLine = -1;
      this.currentInsn = null;
      this.instructions = new HashMap<>();
      this.currentLabel = new ArrayList<>(2);
      this.jumps = new ArrayList<>();
   }

   void setCurrentLine(int line) {
      this.currentLine = line;
   }

   void addLabel(Label label) {
      this.currentLabel.add(label);
      if (!LabelInfo.isSuccessor(label)) {
         this.noSuccessor();
      }
   }

   void addInstruction(AbstractInsnNode node) {
      Instruction insn = new Instruction(this.currentLine);
      int labelCount = this.currentLabel.size();
      if (labelCount > 0) {
         int i = labelCount;

         while (--i >= 0) {
            LabelInfo.setInstruction(this.currentLabel.get(i), insn);
         }

         this.currentLabel.clear();
      }

      if (this.currentInsn != null) {
         this.currentInsn.addBranch(insn, 0);
      }

      this.currentInsn = insn;
      this.instructions.put(node, insn);
   }

   void noSuccessor() {
      this.currentInsn = null;
   }

   void addJump(Label target, int branch) {
      this.jumps.add(new InstructionsBuilder.Jump(this.currentInsn, target, branch));
   }

   void addProbe(int probeId, int branch) {
      boolean executed = this.probes != null && this.probes[probeId];
      this.currentInsn.addBranch(executed, branch);
   }

   Map<AbstractInsnNode, Instruction> getInstructions() {
      for (InstructionsBuilder.Jump j : this.jumps) {
         j.wire();
      }

      return this.instructions;
   }

   private static class Jump {
      private final Instruction source;
      private final Label target;
      private final int branch;

      Jump(Instruction source, Label target, int branch) {
         this.source = source;
         this.target = target;
         this.branch = branch;
      }

      void wire() {
         this.source.addBranch(LabelInfo.getInstruction(this.target), this.branch);
      }
   }
}
