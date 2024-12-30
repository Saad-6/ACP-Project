package acp.acp_project.Domain;

import acp.acp_project.Entities.Action;
import com.aspose.words.Document;
import com.aspose.words.Paragraph;
import com.aspose.words.Run;
import com.aspose.words.SaveFormat;

import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;

import static acp.acp_project.UI.Utility.ensureDirectoryExists;

public class TextHelper {

Response response = new Response();



    public void printTextDocuments(List<File> textFiles) {
        for (File textFile : textFiles) {
            try {
                // Read the content of the text file
                String content = new String(java.nio.file.Files.readAllBytes(textFile.toPath()));

                // Prepare the Printable object
                Printable printable = (graphics, pageFormat, pageIndex) -> {
                    if (pageIndex != 0) return Printable.NO_SUCH_PAGE;

                    // Set up graphics for printing
                    graphics.drawString(content, 100, 100); // Start at (100, 100)
                    return Printable.PAGE_EXISTS;
                };

                // Create a PrinterJob
                PrinterJob printerJob = PrinterJob.getPrinterJob();
                printerJob.setPrintable(printable);

                // Display the printer dialog and print if the user confirms
                if (printerJob.printDialog()) {
                    printerJob.print();
                    System.out.println("Printed: " + textFile.getName());
                } else {
                    System.out.println("Printing cancelled for: " + textFile.getName());
                }
            } catch (IOException e) {
                System.err.println("Error reading file: " + textFile.getName());
                e.printStackTrace();
            } catch (PrinterException e) {
                System.err.println("Error printing file: " + textFile.getName());
                e.printStackTrace();
            }
        }
    }


    public Response searchByKeyword(Action action, List<File> textFiles) {
        String keyword = action.getActionParameter("Keyword");
        String destinationPath = action.getOutputFolder();
        ensureDirectoryExists(destinationPath);

        StringBuilder resultMessage = new StringBuilder();

        for (File file : textFiles) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                int lineNumber = 1;

                while ((line = reader.readLine()) != null) {
                    if (line.contains(keyword)) {
                        resultMessage.append("Found in file: ")
                                .append(file.getName())
                                .append(", Line ")
                                .append(lineNumber)
                                .append(": ")
                                .append(line.trim())
                                .append(System.lineSeparator());
                    }
                    lineNumber++;
                }
            } catch (IOException e) {
                System.err.println("Error reading file: " + file.getName() + " - " + e.getMessage());
                e.printStackTrace();
            }
        }

        if (resultMessage.length() == 0) {
            resultMessage.append("No occurrences of the keyword '").append(keyword).append("' were found.");
        }

        response.Message = resultMessage.toString();
        return response;
    }



    public Response textToPdf(List<File> textFiles, String destinationPath) {
        for (File textFile : textFiles) {
            try {
                // Read the content of the text file
                String content = new String(Files.readAllBytes(textFile.toPath()));

                // Create a new Word document
                Document document = new Document();
                Paragraph paragraph = new Paragraph(document); // Create a new paragraph in the document
                Run run = new Run(document); // Create a new text run to hold the text content
                run.setText(content); // Set the content as the text of the run
                paragraph.appendChild(run); // Append the run to the paragraph
                document.getFirstSection().getBody().appendChild(paragraph); // Append the paragraph to the document body

                // Save the document as a PDF
                String outputFileName = textFile.getName().replace(".txt", ".pdf"); // Replace .txt with .pdf
                Path destinationFilePath = Paths.get(destinationPath, outputFileName);
                document.save(destinationFilePath.toString(), SaveFormat.PDF);

                response.Message = "Text file successfully converted to PDF: " + destinationFilePath.toString();
            } catch (IOException e) {

                response.Message = textFile.getName() + " - " + e.getMessage();

            } catch (Exception e) {

                response.Message = textFile.getName() + " - " + e.getMessage();
            }

            }
        return response;
        }


    public Response textToWord(List<File> textFiles, String destinationPath) {
        for (File textFile : textFiles) {
            try {
                // Read the content of the text file
                String content = new String(Files.readAllBytes(textFile.toPath()));

                // Create a new Word document
                Document document = new Document();
                Paragraph paragraph = new Paragraph(document); // Create a new paragraph in the document
                Run run = new Run(document); // Create a new text run to hold the text content
                run.setText(content); // Set the content as the text of the run
                paragraph.appendChild(run); // Append the run to the paragraph
                document.getFirstSection().getBody().appendChild(paragraph); // Append the paragraph to the document body

                // Save the document to the specified destination path as a DOCX file
                String outputFileName = textFile.getName().replace(".txt", ".docx"); // Replace .txt with .docx
                Path destinationFilePath = Paths.get(destinationPath, outputFileName);
                document.save(destinationFilePath.toString(), SaveFormat.DOCX);
                 response.Message = "Text file successfully converted to Word: " + destinationFilePath.toString();

            } catch (IOException e) {

                System.err.println("Error reading the text file: " + textFile.getName() + " - " + e.getMessage());
                e.printStackTrace();
                response.Message = e.getMessage();

            } catch (Exception e) {

                System.err.println("Error converting text to Word document: " + textFile.getName() + " - " + e.getMessage());
                e.printStackTrace();
                response.Message = e.getMessage();

            }
        }
        return response;
    }



    public Response mergeAllTextFiles(List<File> files, String destinationPath) throws IOException {
        try {
            // Create the destination file path
            Path destination = Paths.get(destinationPath, "merged_textfiles_output.txt");

            // Create a list to store all lines from all files
            StringBuilder mergedContent = new StringBuilder();

            // Iterate over all files and append their content
            for (File file : files) {
                if (file.exists() && file.isFile()) {
                    List<String> lines = Files.readAllLines(file.toPath());
                    for (String line : lines) {
                        mergedContent.append(line).append(System.lineSeparator()); // Append line with a new line separator
                    }
                } else {
                    System.err.println("Skipping invalid file: " + file.getAbsolutePath());
                }
            }

            // Write the merged content to the destination file
            Files.write(destination, mergedContent.toString().getBytes());

            System.out.println();
            response.Message = "Successfully merged files. Saved to: " + destination.toString();
        } catch (IOException e) {

            response.Message = e.getMessage();

        }

        return response;
    }


    public void findAndReplaceTextDocument(File file, String find, String replace, String destinationPath) throws IOException {
        try {
            // Make sure the file exists and is readable
            if (!file.exists() || !file.isFile()) {
                throw new IOException("File does not exist or is not a valid file: " + file.getAbsolutePath());
            }

            // Read the entire content of the file as a string
            String content = new String(Files.readAllBytes(file.toPath()));

            // Perform the find and replace operation
            content = content.replaceAll(Pattern.quote(find), replace);

            // Create a destination path for the modified file
            Path destination = Paths.get(destinationPath, file.getName());

            // Write the modified content back to the destination file
            Files.write(destination, content.getBytes());

            System.out.println("Successfully processed the file. Saved to: " + destination.toString());
        } catch (IOException e) {
            System.err.println("Error processing the text document: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
