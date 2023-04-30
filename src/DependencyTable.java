package src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import src.instructions.AbstractInstruction;
import src.instructions.AbstractProducer;
import src.instructions.IConsumer;
import src.instructions.MovLoop;

class DependencyTable {
    private ArrayList<AbstractInstruction> program;
    private int loopStart, loopEnd;
    private ArrayList<Set<AbstractProducer>> localDependencies;
    private ArrayList<Set<AbstractProducer>> interloopDependencies;
    private ArrayList<Set<AbstractProducer>> loopInvariantDependencies;
    private ArrayList<Set<AbstractProducer>> postLoopDependencies;

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

    public ArrayList<Set<AbstractProducer>> getLocalDependencies() {
        return localDependencies;
    }

    public ArrayList<Set<AbstractProducer>> getInterloopDependencies() {
        return interloopDependencies;
    }

    public ArrayList<Set<AbstractProducer>> getLoopInvariantDependencies() {
        return loopInvariantDependencies;
    }

    public ArrayList<Set<AbstractProducer>> getPostLoopDependencies() {
        return postLoopDependencies;
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
                        localDependencies.get(i).add(producer);
                    }
                }
            }
        }
    }

    private void detectInterloopDependencies() {
        Set<AbstractProducer> dependencies, preLoopDependencies = getProduced(0, loopStart);
        for (int i = loopStart; i < loopEnd + 1; i++) {
            if (!(program.get(i) instanceof IConsumer)) {
                continue;
            }
            dependencies = getProduced(i, loopEnd + 1);
            addDependencies(interloopDependencies, i, (IConsumer) program.get(i),
                    addAllIfExists(dependencies, preLoopDependencies));
        }
    }

    private void detectInvariantDependencies() {
        Set<AbstractProducer> unique = setDifference(
                getProduced(0, loopStart),
                getProduced(loopStart, loopEnd + 1));

        for (int i = loopStart; i < loopEnd + 1; i++) {
            if (!(program.get(i) instanceof IConsumer)) {
                continue;
            }
            addDependencies(loopInvariantDependencies, i, (IConsumer) program.get(i), unique);
        }
    }

    private void detectPostLoopDependencies() {
        for (int i = loopEnd + 1; i < program.size(); i++) {
            if (!(program.get(i) instanceof IConsumer)) {
                continue;
            }
            addDependencies(postLoopDependencies, i, (IConsumer) program.get(i), getProduced(loopStart, loopEnd + 1));
        }
    }

    private void addDependencies(ArrayList<Set<AbstractProducer>> dependencies, int index,
            IConsumer consumer,
            Set<AbstractProducer> producers) {
        int[] consumedRegisters = consumer.getReadRegisters();
        for (int i = 0; i < consumedRegisters.length; i++) {
            for (AbstractProducer producer : producers) {
                if (producer.getDestination() == consumedRegisters[i]) {
                    dependencies.get(index).add(producer);
                }
            }
        }
    }

    private Set<AbstractProducer> getProduced(int start, int end) {
        Set<AbstractProducer> produced = new HashSet<>();
        for (int i = start; i < end; i++) {
            if (program.get(i) instanceof AbstractProducer) {
                produced.add((AbstractProducer) program.get(i));
            }
        }
        return produced;
    }

    private Set<AbstractProducer> setDifference(Set<AbstractProducer> set1,
            Set<AbstractProducer> set2) {
        Set<AbstractProducer> output = new HashSet<>();
        boolean overlap;
        for (AbstractProducer d1 : set1) {
            overlap = false;
            for (AbstractProducer d2 : set2) {
                if (d1.getDestination() == d2.getDestination()) {
                    overlap = true;
                    break;
                }
            }
            if (!overlap) {
                output.add(d1);
            }
        }
        return output;
    }

    private Set<AbstractProducer> addAllIfExists(Set<AbstractProducer> base, Set<AbstractProducer> extra) {
        Set<AbstractProducer> output = new HashSet<>(base);
        for (AbstractProducer d1 : base) {
            for (AbstractProducer d2 : extra) {
                if (d1.getDestination() == d2.getDestination()) {
                    output.add(d2);
                }
            }
        }
        return output;
    }

    private ArrayList<Set<AbstractProducer>> freshArrayList() {
        ArrayList<Set<AbstractProducer>> array = new ArrayList<>();
        for (int i = 0; i < program.size(); i++) {
            array.add(new HashSet<AbstractProducer>());
        }
        return array;
    }

    @Override
    public String toString() {
        int width = 81;
        StringBuilder sb = new StringBuilder();
        AbstractInstruction instr;

        /* preamble */
        sb.append(new String(new char[width]).replace("\0", "-"));
        sb.append("\n");
        sb.append("| id | instr | dest |   local   |   interloop   |   invariant   |   post loop   |");
        sb.append("\n");
        sb.append(new String(new char[width]).replace("\0", "-"));
        sb.append("\n");

        /* body */
        for (int i = 0; i < program.size(); i++) {
            instr = program.get(i);
            String line;

            if (instr instanceof MovLoop) {
                line = String.format(
                        "| %2s | %5s | %-3s |           |               |               |               |",
                        instr.getAddress(), instr.getMnemonic(),
                        ((MovLoop) instr).getDest());
            }

            else if (!(instr instanceof AbstractProducer)) {
                line = String.format("| %2s | %5s |      | %-9s | %-13s | %-13s | %-13s |",
                        instr.getAddress(), instr.getMnemonic(),
                        getDependencyAsString(localDependencies.get(i)),
                        getDependencyAsString(interloopDependencies.get(i)),
                        getDependencyAsString(loopInvariantDependencies.get(i)),
                        getDependencyAsString(postLoopDependencies.get(i)));
            }

            else {
                line = String.format("| %2d | %5s | x%-3s | %-9s | %-13s | %-13s | %-13s |",
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

    private String getDependencyAsString(Set<AbstractProducer> dependencies) {
        if (dependencies.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        Map<Integer, ArrayList<Integer>> mapping = new HashMap<>();

        for (AbstractProducer producer : dependencies) {
            if (mapping.containsKey(producer.getDestination())) {
                mapping.get(producer.getDestination()).add(producer.getAddress());
            }

            else {
                mapping.put(producer.getDestination(), new ArrayList<>());
                mapping.get(producer.getDestination()).add(producer.getAddress());
            }
        }

        for (Integer register : mapping.keySet()) {
            sb.append(String.format("x%d: ", register));
            if (mapping.get(register).size() > 1) {
                sb.append("(");
                sb.append(mapping.get(register).stream().map(Object::toString).collect(Collectors.joining(" or ")));
                sb.append("), ");
            }

            else {
                sb.append(String.format("%d, ", mapping.get(register).get(0)));
            }
        }

        return sb.toString().substring(0, sb.toString().length() - 2);
    }

    private boolean noLoop() {
        return loopStart == -1 && loopEnd == -1;
    }

}
