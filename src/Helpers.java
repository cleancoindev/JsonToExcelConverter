import com.opencsv.CSVWriter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

class Helpers {

    private static List<String> fileInput = new ArrayList<>();
    private static Map<String, ArrayList<String>> languages = new TreeMap<>();
    private static int languageCounter = 0;

    static void prepareFile(String fileName) throws IOException {
        readAllFromFileToList(fileName);
        fileInput.remove(fileInput.size() - 2);
        fileInput.remove(fileInput.size() - 1);
        fileInput.remove(0);
    }

    static void seperateLanguagesAndContent() {
        ArrayList<String> languageContent = new ArrayList<>();
        for (String line : fileInput) {
            if (line.contentEquals("")) {
                String key = languageContent.get(0);
                languageContent.remove(0);
                languages.put(key.substring(0, key.length() - 1), languageContent);
                languageContent = new ArrayList<>();
            } else {
                languageContent.add(line);
            }
        }
        String key = languageContent.get(0);
        languageContent.remove(0);
        languages.put(key.substring(0, key.length() - 1), languageContent);
    }

    private static void readAllFromFileToList(String fileName) throws IOException {
        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            //stream.forEach(System.out::println);
            stream.forEach(Helpers::storeAllToList);
        }
    }

    static void createFileIfFileDoesNotExist(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    private static void storeAllToList(String line) {
        if (!lineIsCommentOrEmpty(line)) {
            //System.out.println(line);
            line = line.replaceAll("^\\s+", "");
            line = line.replaceAll("'", "");
            //line = line.replaceAll(",", "");
            if (line.lastIndexOf(',') != -1) {
                line = line.substring(0, line.length() - 1);
            }
            line = line.replaceAll("\\{", "");
            line = line.replaceAll("}", "");
            fileInput.add(line);
        }
    }


    static void populateCsvWithData() throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(new File(Main.csvName));
        //fileOutputStream.write(0xef);
        //fileOutputStream.write(0xbb);
        //fileOutputStream.write(0xbf);
        CSVWriter writer = new CSVWriter(new OutputStreamWriter(fileOutputStream), ',');
        List<String[]> data = new ArrayList<>();
        List<String> allKeys = new ArrayList<>();
        for (String language : languages.keySet()) {
            //add all keys
            for (String line : languages.get(language)) {
                String key = line.split("\\:")[0];
                if (allKeys.stream().noneMatch(str -> str.trim().contentEquals(key))) {
                    allKeys.add(key);
                    data.add(new String[]{key});
                }
            }
        }

        for (String language : languages.keySet()) {
            languageCounter++;
            //map all values to keys
            for (String line : languages.get(language)) {
                String key = line.split("\\:")[0];
                String value = line.split("\\:")[1].trim().replaceAll("\\'", "");
                addValueToKey(data, key, value);
            }
        }

        List<String> header = new ArrayList<>();
        header.add("KEY:");
        for (String language : languages.keySet()) {
            header.add(language.toUpperCase());
        }
        writer.writeNext(header.toArray(new String[0]));
        writer.writeAll(data);
        writer.close();
    }


    private static void addValueToKey(List<String[]> data, String key, String value) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i)[0].contentEquals(key)) {
                try {
                    if (languageCounter == 1) {
                        data.set(i, new String[]{data.get(i)[0], value});
                        return;
                    } else if (languageCounter == 2) {
                        try {
                            String aData = data.get(i)[1];
                        } catch (ArrayIndexOutOfBoundsException e) {
                            //e.printStackTrace();
                            data.set(i, new String[]{data.get(i)[0], ""});
                        }
                        data.set(i, new String[]{data.get(i)[0], data.get(i)[1], value});
                        return;
                    } else if (languageCounter == 3) {
                        try {
                            String aData = data.get(i)[1];
                        } catch (ArrayIndexOutOfBoundsException e) {
                            try {
                                String bData = data.get(i)[2];
                            } catch (ArrayIndexOutOfBoundsException e2) {
                                data.set(i, new String[]{data.get(i)[0], "", ""});
                            }
                            data.set(i, new String[]{data.get(i)[0], "", data.get(i)[2]});
                        }
                        try {
                            String bData = data.get(i)[2];
                        } catch (ArrayIndexOutOfBoundsException e) {
                            data.set(i, new String[]{data.get(i)[0], data.get(i)[1], ""});
                        }
                        data.set(i, new String[]{data.get(i)[0], data.get(i)[1], data.get(i)[2], value});
                        return;
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    for (String[] aData : data) {
                        System.out.println(Arrays.toString(aData));
                    }
                    System.out.println("-------------------------------------------------------------------------------------------------------");
                }
            }
        }
    }

    private static boolean lineIsCommentOrEmpty(String line) {
        return line.startsWith("        //") || line.length() == 0 || line.trim().length() == 0;
    }

    static void convertFileFromCSVToXLS(String csvName) throws IOException {
        FileConverter.convertCSVToXLS(csvName);
    }

    static void removeFile(String fileName) {
        File file = new File(fileName);
        file.delete();
    }
}
