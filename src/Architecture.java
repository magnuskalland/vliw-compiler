package src;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class Architecture {
    public static final int N_UNITS = 5;
    static final Map<Instruction, FunctionalUnit> INST_TO_FU = Collections
            .unmodifiableMap(new HashMap<Instruction, FunctionalUnit>() {
                {
                    put(Instruction.Add, FunctionalUnit.ALU);
                    put(Instruction.Addi, FunctionalUnit.ALU);
                    put(Instruction.Sub, FunctionalUnit.ALU);
                    put(Instruction.Mul, FunctionalUnit.Mult);
                    put(Instruction.Mulu, FunctionalUnit.Mult);
                    put(Instruction.Ld, FunctionalUnit.Mem);
                    put(Instruction.St, FunctionalUnit.Mem);
                    put(Instruction.Loop, FunctionalUnit.Branch);
                    put(Instruction.Looppip, FunctionalUnit.Branch);
                    put(Instruction.Nop, FunctionalUnit.Any);
                    put(Instruction.MovBool, FunctionalUnit.ALU);
                    put(Instruction.MovLoop, FunctionalUnit.ALU);
                    put(Instruction.Movi, FunctionalUnit.ALU);
                    put(Instruction.Mov, FunctionalUnit.ALU);
                }
            });

    enum Instruction {
        Add, Addi, Sub, Mul, Mulu, Ld, St, Loop, Looppip, Nop, MovBool, MovLoop, Movi, Mov;
    }

    enum FunctionalUnit {
        ALU, Mult, Mem, Branch, Any;
    }
}
