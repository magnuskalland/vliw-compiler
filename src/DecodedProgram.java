package src;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import src.instructions.*;

class DecodedProgram {
    private int loopStart, loopEnd, optimalInitiationInterval;
    private ArrayList<DecodedInstruction> decodedProgram;

    public DecodedProgram(String[] encodedProgram) {
        decodedProgram = new ArrayList<>();
        for (int i = 0; i < encodedProgram.length; i++) {
            decodedProgram.add(parseInstruction(encodedProgram[i], i));
        }
        computeInitiationInterval();
    }

    ArrayList<DecodedInstruction> getProgram() {
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
        DecodedInstruction instr;
        for (int i = loopStart; i < loopEnd; i++) {
            instr = decodedProgram.get(i);
            if (instr instanceof Alu)
                values[0]++;
            else if (instr instanceof Mul)
                values[1]++;
            else if (instr instanceof Mem)
                values[2]++;
            else if (instr instanceof Branch)
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
        sb.append(decodedProgram.stream().map(Object::toString).collect(Collectors.joining("\t")));
        return sb.toString();
    }

    private DecodedInstruction parseInstruction(String instruction, int address) {
        String[] split = instruction.split(",");
        String mnemonic = split[0];

        /* fat pattern match */
        if (mnemonic.equals("add")) {
            return new Add(mnemonic,
                    Integer.parseInt(replaceX(split[1])),
                    Integer.parseInt(replaceX(split[2])),
                    Integer.parseInt(replaceX(split[3])));
        }

        else if (mnemonic.equals("addi")) {
            return new Addi(mnemonic,
                    Integer.parseInt(replaceX(split[1])),
                    Integer.parseInt(replaceX(split[2])),
                    Integer.parseInt(split[3]));

        } else if (mnemonic.equals("sub")) {
            return new Sub(mnemonic,
                    Integer.parseInt(replaceX(split[1])),
                    Integer.parseInt(replaceX(split[2])),
                    Integer.parseInt(replaceX(split[3])));

        } else if (mnemonic.equals("mulu")) {
            return new Mulu(mnemonic,
                    Integer.parseInt(replaceX(split[1])),
                    Integer.parseInt(replaceX(split[2])),
                    Integer.parseInt(replaceX(split[3])));

        } else if (mnemonic.equals("ld")) {
            String[] mem = split[2].split("\\(");
            return new Ld(mnemonic,
                    Integer.parseInt(replaceX(split[1])),
                    Integer.parseInt(mem[0]),
                    Integer.parseInt(replaceParenthesis(replaceX(mem[1]))));

        } else if (mnemonic.equals("st")) {
            String[] mem = split[2].split("\\(");
            return new St(mnemonic,
                    Integer.parseInt(replaceX(split[1])),
                    Integer.parseInt(mem[0]),
                    Integer.parseInt(replaceParenthesis(replaceX(mem[1]))));

        } else if (mnemonic.equals("loop")) {
            loopStart = Integer.parseInt(split[1]);
            loopEnd = address;
            return new Loop(mnemonic, loopStart);

        } else if (mnemonic.equals("loop.pip")) {
            loopStart = Integer.parseInt(split[1]);
            loopEnd = address;
            return new Looppip(mnemonic, loopStart);

        } else if (mnemonic.equals("nop")) {
            return new Nop(mnemonic);

        } else if (mnemonic.equals("mov")) {
            if (split[1].charAt(0) == 'p') {
                return new MovPredicate(mnemonic, Integer.parseInt(replaceP(split[1])), Boolean.parseBoolean(split[2]));
            }

            else if (split[1].equals("LC") || split[1].equals("EC")) {
                return new MovLoop(mnemonic, split[1], Integer.parseInt(split[2]));
            }

            else if (split[2].charAt(0) != 'x') {
                return new Movi(mnemonic, Integer.parseInt(replaceX(split[1])), Integer.parseInt(split[2]));
            }

            else {
                return new Mov(mnemonic, Integer.parseInt(replaceX(split[1])), Integer.parseInt(replaceX(split[2])));
            }
        }

        System.out.printf("Pattern matching failed on %s\n", instruction);
        System.exit(1);
        return null;
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
