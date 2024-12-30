package acp.acp_project.Domain;

import acp.acp_project.Entities.Action;
import com.aspose.words.*;

import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.awt.print.PrinterJob;
import java.io.File;
import java.nio.file.Files;
import java.util.List;


import static acp.acp_project.UI.Utility.ensureDirectoryExists;

public class WordHelper {

    Response response = new Response();


    public void findAndReplaceInWord(File file, String find, String replace, String destinationPath) {
        try {
            // Load the DOCX file using Aspose.Words Document class
            Document document = new Document(file.getAbsolutePath());

            // Set up FindReplaceOptions
            FindReplaceOptions options = new FindReplaceOptions();
            options.setReplacingCallback((e) -> {
                e.setReplacement(replace); // Set the replacement text
                return ReplaceAction.REPLACE; // Perform the replacement
            });

            // Perform the find and replace operation
            document.getRange().replace(find, replace, options);

            // Save the modified document to the specified destination path
            File outputFile = new File(destinationPath, file.getName());
            document.save(outputFile.getAbsolutePath(), SaveFormat.DOCX);
            System.out.println("Find and Replace completed. Document saved to " + outputFile.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Error processing the Word document: " + e.getMessage());
            e.printStackTrace();  // Optionally print the stack trace for debugging
        }
    }



    public void word2Pdf(File file, String destinationPath) {
        try {
            // Load the DOCX file using Aspose.Words Document class
            Document document = new Document(file.getAbsolutePath());

            // Define the output PDF file
            File outputFile = new File(destinationPath, file.getName().replace(".docx", ".pdf"));

            // Save the document as PDF
            document.save(outputFile.getAbsolutePath(), SaveFormat.PDF);
            System.out.println("Document converted to PDF and saved to " + outputFile.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Error converting the Word document to PDF: " + e.getMessage());
            e.printStackTrace();  // Optionally print the stack trace for debugging
        }
    }




    public void mergeWordDocuments(File[] inputFiles, String destinationPath) {
        try {
            // Create a new empty document to append other documents to
            Document mergedDocument = new Document();

            for (File inputFile : inputFiles) {
                // Load the current document
                Document currentDocument = new Document(inputFile.getAbsolutePath());

                // Append the content of the current document to the merged document
                mergedDocument.appendDocument(currentDocument, ImportFormatMode.KEEP_SOURCE_FORMATTING);
            }

            // Define the output file path
            File outputFile = new File(destinationPath, "merged_document.docx");

            // Save the merged document as a DOCX file
            mergedDocument.save(outputFile.getAbsolutePath(), SaveFormat.DOCX);
            System.out.println("Documents merged successfully and saved to " + outputFile.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Error merging documents: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private  void convertWordToText(File file, String destinationPath) {
        try {
            // Load the Word document
            Document document = new Document(file.getAbsolutePath());

            // Save the document as a plain text file
            File outputFile = new File(destinationPath, file.getName().replace(".docx", ".txt"));
            document.save(outputFile.getAbsolutePath(), SaveFormat.TEXT);

            System.out.println("Document successfully converted to text: " + outputFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error converting document to text.");
        }
    }


    public void printWordDocuments(File[] files) {
        try {
            // Create a PrinterJob instance to handle printing
            PrinterJob printerJob = PrinterJob.getPrinterJob();

            // Show the print dialog for printer selection
            if (printerJob.printDialog()) {
                // Loop through each Word file
                for (File file : files) {
                    if (file.getName().endsWith(".docx")) {
                        // Load the DOCX file using Aspose.Words Document class
                        Document document = new Document(file.getAbsolutePath());

                        // Print the document using the PrinterJob
                        document.print();  // Print the document to the selected printer
                        System.out.println("Printing document: " + file.getName());
                    }
                }
            } else {
                System.out.println("Print dialog was cancelled.");
            }
        } catch (Exception e) {
            System.err.println("Error printing Word documents: " + e.getMessage());
            e.printStackTrace();
        }
    }



    public  Response wordToText(Action action, List<File> files){

        String destinationPath = action.getOutputFolder();
        ensureDirectoryExists(destinationPath);

        for (File file : files) {
            String fileName = file.getName().toLowerCase();
            try {
                if (fileName.endsWith(".docx")) {
                    convertWordToText(file,destinationPath);
                } else if (fileName.endsWith(".pdf")) {
                    response.Message ="Not yet implemented";
                    return  response;

                } else {
                    // Skip unsupported file types
                    continue;
                }
            } catch (Exception e) {
                response.success = false;
                response.Message = "Error processing file: " + file.getName() + ". " + e.getMessage();

            }
        }
        return response;
    }

}
