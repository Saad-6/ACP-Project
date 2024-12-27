package acp.acp_project.Domain;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import static acp.acp_project.UI.Utility.*;
public class Merger {
    private final String fileType;

    public Merger(String fileType) {
        this.fileType = fileType.toLowerCase(); // Normalize file type to lowercase for case-insensitive comparisons
    }

    public boolean merge(String rootPath, String outputFilePath) {
        File rootDirectory = new File(rootPath);

        if (!rootDirectory.exists() || !rootDirectory.isDirectory()) {
            System.err.println("Invalid root path: Directory does not exist.");
            return false;
        }

        List<File> filesToMerge = findFiles(rootDirectory,"");

        if (filesToMerge.isEmpty()) {
            System.out.println("No " + fileType + " files found in the specified directory.");
            return false;
        }

        return mergeFiles(filesToMerge, outputFilePath);
    }



    private boolean mergeFiles(List<File> files, String outputFilePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            for (File file : files) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    writer.write("------ Contents of: " + file.getName() + " ------\n");
                    String line;
                    while ((line = reader.readLine()) != null) {
                        writer.write(line);
                        writer.newLine();
                    }
                    writer.write("\n");
                } catch (IOException e) {
                    System.err.println("Error reading file: " + file.getAbsolutePath());
                }
            }
            System.out.println("Files merged successfully into: " + outputFilePath);
            return true;
        } catch (IOException e) {
            System.err.println("Error writing to output file: " + outputFilePath);
            return false;
        }
    }

}
