package src;

import java.io.FileNotFoundException;

// ./build.sh && ./run.sh
// javac -cp ".:./src/gson-2.10.1.jar" -d bin --source-path ./src src/*.java && java -cp bin src.Main
// ./build.sh && ./run.sh test/02/input.json test/02/simple_user.json test/02/pip_user.json

class Main {
    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        if (args.length < 3) {
            System.out.printf("Usage: java Main </path/to/input.json> </path/to/loop.json> </path/to/looppip.json>\n");
            System.exit(0);
        }

        DecodedProgram decodedProgram;
        DependencyTable dependencyTable;
        int initiationInterval;
        Schedule schedule;

        decodedProgram = new DecodedProgram(IO.read(args[0]));
        initiationInterval = decodedProgram.optimalInitiationInterval();
        dependencyTable = new DependencyTable(decodedProgram);
        schedule = new SimpleScheduler(decodedProgram, dependencyTable).schedule();
        IO.write(schedule, args[1]);

        decodedProgram = new DecodedProgram(IO.read(args[0]));
        initiationInterval = decodedProgram.optimalInitiationInterval();
        DecodedProgram.transformToPipelined(decodedProgram);
        dependencyTable = new DependencyTable(decodedProgram);

        schedule = null;
        do {
            schedule = new PipelinedScheduler(decodedProgram, dependencyTable, initiationInterval).schedule();
            initiationInterval++;
        } while (schedule == null);
        IO.write(schedule, args[2]);
    }
}