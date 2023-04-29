package src;

import java.io.FileNotFoundException;

// ./build.sh && ./run.sh
// javac -cp ".:./src/gson-2.10.1.jar" -d bin --source-path ./src src/*.java && java -cp bin src.Main
// ./build.sh && ./run.sh test/02/input.json test/02/simple_user.json test/02/pip_user.json

class Main {
    public static void main(String[] args) throws FileNotFoundException {
        if (args.length < 3) {
            System.out.printf("Usage: java Main </path/to/input.json> </path/to/loop.json> </path/to/looppip.json>\n");
            System.exit(0);
        }

        DecodedProgram decodedProgram;
        DependencyTable dependencyTable;

        decodedProgram = new DecodedProgram(IO.read(args[0]));
        dependencyTable = new DependencyTable(decodedProgram);

        System.out.printf("%s", decodedProgram);

        IO.write(new SimpleScheduler(decodedProgram, dependencyTable).schedule(), args[1]);
        IO.write(new PipelinedScheduler(decodedProgram, dependencyTable).schedule(), args[2]);
    }
}