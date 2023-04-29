package src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.google.gson.*;

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
            out.println(gson.toJson(schedule));
        }
    }
}