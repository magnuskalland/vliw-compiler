package src;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import src.instructions.*;

class DecodedProgram {
    private int loopStart = -1, loopEnd = -1;
    private int optimalInitiationInterval;
    private ArrayList<AbstractInstruction> decodedProgram;

    public DecodedProgram(String[] encodedProgram) {
        decodedProgram = new ArrayList<>();
        for (int i = 0; i < encodedProgram.length; i++) {
            decodedProgram.add(parseInstruction(encodedProgram[i], i));
        }
        computeInitiationInterval();
    }

    public static void transformToPipelined(DecodedProgram program) {
        AbstractInstruction instr;
        for (int i = 0; i < program.getProgram().size(); i++) {
            instr = program.getProgram().get(i);
            if (instr instanceof Loop) {
                program.getProgram().set(i,
                        new Looppip(instr.getId(),
                                instr.getMnemonic(),
                                ((AbstractInstructionBranch) instr).getLabel()));
            }
        }
    }

    ArrayList<AbstractInstruction> getProgram() {
        return decodedProgram;
    }

    int getLoopStart() {
        return loopStart;
    }

    int getLoopEnd() {
        return loopEnd;
    }

    int optimalInitiationInterval() {
        return optimalInitiationInterval;
    }

    private void computeInitiationInterval() {
        int[] values = new int[4];
        AbstractInstruction instr;
        for (int i = loopStart; i < loopEnd; i++) {
            instr = decodedProgram.get(i);
            if (instr instanceof IInstructionAlu)
                values[0]++;
            else if (instr instanceof AbstractInstructionMul)
                values[1]++;
            else if (instr instanceof IInstructionMem)
                values[2]++;
            else if (instr instanceof AbstractInstructionBranch)
                values[3]++;
        }
        values[0] = (int) Math.ceil((double) values[0] / 2);
        optimalInitiationInterval = Arrays.stream(values).max().getAsInt();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-28s: %d\n", "Loop start address", loopStart));
        sb.append(String.format("%-28s: %d\n", "Loop end address", loopEnd));
        sb.append(String.format("%-28s: %d\n", "Optimal initiation interval", optimalInitiationInterval));
        sb.append("\t");
        sb.append(decodedProgram.stream().map(Object::toString).collect(Collectors.joining("\n\t")));
        sb.append("\n");
        return sb.toString();
    }

    private AbstractInstruction parseInstruction(String instruction, int address) {
        String[] split = instruction.split(",");
        String mnemonic = split[0];

        /* fat pattern match */

        /* add */
        if (mnemonic.equals("add")) {
            return new Add(address, mnemonic,
                    Integer.parseInt(replaceX(split[1])),
                    Integer.parseInt(replaceX(split[2])),
                    Integer.parseInt(replaceX(split[3])));
        }

        /* addi */
        else if (mnemonic.equals("addi")) {
            return new Addi(address,
                    mnemonic,
                    Integer.parseInt(replaceX(split[1])),
                    Integer.parseInt(replaceX(split[2])),
                    parseLiteral(split[3]));
        }

        /* sub */
        else if (mnemonic.equals("sub")) {
            return new Sub(address, mnemonic,
                    Integer.parseInt(replaceX(split[1])),
                    Integer.parseInt(replaceX(split[2])),
                    Integer.parseInt(replaceX(split[3])));

        }

        /* mulu */
        else if (mnemonic.equals("mulu")) {
            return new Mulu(address, mnemonic,
                    Integer.parseInt(replaceX(split[1])),
                    Integer.parseInt(replaceX(split[2])),
                    Integer.parseInt(replaceX(split[3])));

        }

        /* ld */
        else if (mnemonic.equals("ld")) {
            String[] mem = split[2].split("\\(");
            return new Ld(address, mnemonic,
                    Integer.parseInt(replaceX(split[1])),
                    parseLiteral(mem[0]),
                    Integer.parseInt(replaceParenthesis(replaceX(mem[1]))));

        }

        /* st */
        else if (mnemonic.equals("st")) {
            String[] mem = split[2].split("\\(");
            return new St(address, mnemonic,
                    Integer.parseInt(replaceX(split[1])),
                    parseLiteral(mem[0]),
                    Integer.parseInt(replaceParenthesis(replaceX(mem[1]))));

        }

        /* loop */
        else if (mnemonic.equals("loop")) {
            loopStart = Integer.parseInt(split[1]);
            loopEnd = address;
            return new Loop(address, mnemonic, loopStart);

        }

        /* looppip, never occur */
        else if (mnemonic.equals("loop.pip")) {
            loopStart = Integer.parseInt(split[1]);
            loopEnd = address;
            return new Looppip(address, mnemonic, loopStart);

        }

        /* nop, never occur */
        else if (mnemonic.equals("nop")) {
            return new Nop(address, mnemonic);

        }

        /* mov variants */
        else if (mnemonic.equals("mov")) {

            /* mov pX, true/false */
            if (split[1].charAt(0) == 'p') {
                return new MovPredicate(address, mnemonic,
                        Integer.parseInt(replaceP(split[1])),
                        Boolean.parseBoolean(split[2]));
            }

            /* mov LC/EC, imm */
            else if (split[1].equals("LC") || split[1].equals("EC")) {
                return new MovLoop(address, mnemonic, split[1], parseLiteral(split[2]));
            }

            /* mov dest, imm */
            else if (split[2].charAt(0) != 'x') {
                return new Movi(address, mnemonic,
                        Integer.parseInt(replaceX(split[1])),
                        parseLiteral(split[2]));
            }

            /* mov dest, source */
            else {
                return new Mov(address, mnemonic,
                        Integer.parseInt(replaceX(split[1])),
                        Integer.parseInt(replaceX(split[2])));
            }
        }

        System.out.printf("Pattern matching failed on %s\n", instruction);
        System.exit(1);
        return null;
    }

    private int parseLiteral(String literal) {
        String hexPrefix = "0x";
        if (literal.length() > hexPrefix.length() && literal.substring(0, 2).equals(hexPrefix)) {
            return Integer.parseInt(literal.substring(2, literal.length()), 16);
        }
        return Integer.parseInt(literal);
    }

    private String replaceX(String reg) {
        return reg.replace("x", "");
    }

    private String replaceP(String reg) {
        return reg.replace("p", "");
    }

    private String replaceParenthesis(String reg) {
        return reg.replace(")", "");
    }
}
