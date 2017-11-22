import java.io.IOException;

public class Main {

    private static String fileReader = "Localization.js";
    static String csvName = "languages.csv";

    public static void main(String args[]) {
        try {
            if (args.length == 1) {
                fileReader = args[0];
            } else if (args.length == 2) {
                fileReader = args[0];
                FileConverter.xlsxFileName = args[1];
            }
            Helpers.prepareFile(fileReader);
            Helpers.createFileIfFileDoesNotExist(csvName);
            Helpers.seperateLanguagesAndContent();
            Helpers.populateCsvWithData();
            Helpers.convertFileFromCSVToXLS(csvName);
            Helpers.removeFile(csvName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
