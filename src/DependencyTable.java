package src;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import src.instructions.AbstractInstruction;
import src.instructions.AbstractProducer;
import src.instructions.IConsumer;
import src.instructions.MovLoop;

class DependencyTable {
    private ArrayList<AbstractInstruction> program;
    private int loopStart, loopEnd;
    private ArrayList<Set<Integer>> localDependencies;
    private ArrayList<Set<Integer>> interloopDependencies;
    private ArrayList<Set<Integer>> loopInvariantDependencies;
    private ArrayList<Set<Integer>> postLoopDependencies;

    public DependencyTable(DecodedProgram program) {
        this.program = program.getProgram();
        this.loopStart = program.getLoopStart();
        this.loopEnd = program.getLoopEnd();

        localDependencies = freshArrayList();
        interloopDependencies = freshArrayList();
        loopInvariantDependencies = freshArrayList();
        postLoopDependencies = freshArrayList();

        detectLocalDependencies();
        if (!noLoop()) {
            detectInterloopDependencies();
            detectInvariantDependencies();
            detectPostLoopDependencies();
        }
    }

    private void detectLocalDependencies() {
        if (noLoop()) {
            detectLocalDependenciesBlock(0, program.size());
            return;
        }
        detectLocalDependenciesBlock(0, loopStart);
        detectLocalDependenciesBlock(loopStart, loopEnd + 1);
        detectLocalDependenciesBlock(loopEnd + 1, program.size());
    }

    private void detectLocalDependenciesBlock(int start, int end) {
        AbstractProducer producer;
        IConsumer consumer;
        int[] registerOperands;
        for (int i = start; i < end; i++) {
            if (!(program.get(i) instanceof IConsumer)) {
                continue;
            }
            consumer = (IConsumer) program.get(i);

            for (int j = start; j < i; j++) {
                if (!(program.get(j) instanceof AbstractProducer)) {
                    continue;
                }
                producer = (AbstractProducer) program.get(j);
                registerOperands = consumer.getReadRegisters();
                for (int k = 0; k < registerOperands.length; k++) {
                    if (producer.getDestination() == registerOperands[k]) {
                        localDependencies.get(i).add(registerOperands[k]);
                    }
                }
            }
        }
    }

    private void detectInterloopDependencies() {
        int[] consumedRegisters;

        for (int i = loopStart; i < loopEnd + 1; i++) {
            if (!(program.get(i) instanceof IConsumer)) {
                continue;
            }
            consumedRegisters = ((IConsumer) program.get(i)).getReadRegisters();
            addDependencies(interloopDependencies, i, consumedRegisters, getProduced(i, loopEnd + 1));
        }
    }

    private void detectInvariantDependencies() {
        int[] consumedRegisters;
        Set<Integer> uniquelyProducedRegisters = setDifference(getProduced(0, loopStart),
                getProduced(loopStart, loopEnd + 1));

        for (int i = loopStart; i < loopEnd + 1; i++) {
            if (!(program.get(i) instanceof IConsumer)) {
                continue;
            }
            consumedRegisters = ((IConsumer) program.get(i)).getReadRegisters();
            addDependencies(loopInvariantDependencies, i, consumedRegisters, uniquelyProducedRegisters);
        }
    }

    private void detectPostLoopDependencies() {
        int[] consumedRegisters;
        for (int i = loopEnd + 1; i < program.size(); i++) {
            if (!(program.get(i) instanceof IConsumer)) {
                continue;
            }
            consumedRegisters = ((IConsumer) program.get(i)).getReadRegisters();
            addDependencies(
                    postLoopDependencies, i, consumedRegisters, getProduced(loopStart,
                            loopEnd + 1));
        }
    }

    @Override
    public String toString() {
        int width = 65;
        StringBuilder sb = new StringBuilder();
        AbstractInstruction instr;

        /* preamble */
        sb.append(new String(new char[width]).replace("\0", "-"));
        sb.append("\n");
        sb.append("| id | instr | dest | local | interloop | invariant | post loop |");
        sb.append("\n");
        sb.append(new String(new char[width]).replace("\0", "-"));
        sb.append("\n");

        /* body */
        for (int i = 0; i < program.size(); i++) {
            instr = program.get(i);
            String line;

            if (instr instanceof MovLoop) {
                line = String.format("| %2s | %5s | %-4s |       |           |           |           |",
                        instr.getAddress(), instr.getMnemonic(),
                        ((MovLoop) instr).getDest());
            }

            else if (!(instr instanceof AbstractProducer)) {
                line = String.format("| %2s | %5s |      | %-5s | %-9s | %-9s | %-9s |",
                        instr.getAddress(), instr.getMnemonic(),
                        getDependencyAsString(localDependencies.get(i)),
                        getDependencyAsString(interloopDependencies.get(i)),
                        getDependencyAsString(loopInvariantDependencies.get(i)),
                        getDependencyAsString(postLoopDependencies.get(i)));
            }

            else {
                line = String.format("| %2d | %5s | %-4s | %-5s | %-9s | %-9s | %-9s |",
                        instr.getAddress(), instr.getMnemonic(),
                        ((AbstractProducer) instr).getDestination(),
                        getDependencyAsString(localDependencies.get(i)),
                        getDependencyAsString(interloopDependencies.get(i)),
                        getDependencyAsString(loopInvariantDependencies.get(i)),
                        getDependencyAsString(postLoopDependencies.get(i)));
            }

            sb.append(line);
            sb.append("\n");
            sb.append(new String(new char[width]).replace("\0", "-"));
            sb.append("\n");
        }

        return sb.toString();
    }

    private void addDependencies(ArrayList<Set<Integer>> dependencies, int index, int[] consumedRegisters,
            Set<Integer> producedRegisters) {
        for (int i = 0; i < consumedRegisters.length; i++) {
            if (producedRegisters.contains(consumedRegisters[i])) {
                dependencies.get(index).add(consumedRegisters[i]);
            }
        }
    }

    private Set<Integer> getProduced(int start, int end) {
        Set<Integer> produced = new HashSet<>();
        for (int i = start; i < end; i++) {
            if (program.get(i) instanceof AbstractProducer) {
                produced.add(((AbstractProducer) program.get(i)).getDestination());
            }
        }
        return produced;
    }

    private Set<Integer> setDifference(Set<Integer> set1, Set<Integer> set2) {
        Set<Integer> output = new HashSet<>();
        for (Integer i : set1) {
            if (!(set2.contains(i))) {
                output.add(i);
            }
        }
        return output;
    }

    private ArrayList<Set<Integer>> freshArrayList() {
        ArrayList<Set<Integer>> array = new ArrayList<>();
        for (int i = 0; i < program.size(); i++) {
            array.add(new HashSet<Integer>());
        }
        return array;
    }

    private String getDependencyAsString(Set<Integer> dependency) {
        StringBuilder sb = new StringBuilder();
        if (!dependency.isEmpty()) {
            sb.append("x");
        }
        sb.append(dependency.stream().map(Object::toString).collect(Collectors.joining(", x")));
        return sb.toString();
    }

    private boolean noLoop() {
        return loopStart == -1 && loopEnd == -1;
    }
}
