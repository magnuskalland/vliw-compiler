package src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.gson.*;

import src.instructions.AbstractInstruction;
import src.instructions.Nop;
import src.instructions.Reserved;

class IO {
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static String[] read(String inputPath) throws FileNotFoundException {
        String[] json = gson.fromJson(new BufferedReader(new FileReader(inputPath)), String[].class);
        ArrayList<String> tokenized = new ArrayList<>();
        for (int i = 0; i < json.length; i++) {
            String token = json[i].replaceFirst(" ", ",").replaceAll(" ", "").strip();
            tokenized.add(token);
        }
        String[] output = new String[tokenized.size()];
        for (int i = 0; i < tokenized.size(); i++)
            output[i] = tokenized.get(i);
        return output;
    }

    public static void write(Schedule schedule, String outputPath) throws FileNotFoundException {
        try (PrintWriter out = new PrintWriter(outputPath)) {
            out.println(new Output(schedule).toString());
        }
    }

    static class Output {
        Schedule schedule;

        public Output(Schedule schedule) {
            this.schedule = schedule;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("[\n");
            AbstractInstruction[] slots;
            Nop nop = new Nop(-1, " nop");

            for (int i = 0; i < schedule.getSchedule().size(); i++) {
                slots = schedule.getSchedule().get(i).getSlots();
                System.out.printf("%s\n", Arrays.toString(slots));
                sb.append("\t[");
                for (int j = 0; j < slots.length - 1; j++) {
                    if (slots[j] instanceof Reserved || slots[j] == null) {
                        slots[j] = nop;
                    }
                    sb.append(String.format("\"%s\", ", slots[j]));
                }
                sb.append(String.format("\"%s\"", slots[slots.length - 2]));
                sb.append(i != schedule.getSchedule().size() - 1 ? "],\n" : "]\n");
            }
            sb.append("]\n");
            System.out.printf(sb.toString());
            return sb.toString();
        }
    }
}