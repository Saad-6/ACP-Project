package acp.acp_project.Code;

import acp.acp_project.Domain.*;
import acp.acp_project.Entities.Action;
import acp.acp_project.Entities.Task;
import acp.acp_project.Models.GenericActions;
import acp.acp_project.Models.SpecificActions;
import acp.acp_project.Repository.GenericRepository;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static acp.acp_project.UI.Utility.*;

public class ActionManager {

    // Repository
    private final GenericRepository<Action> actionRepo = new GenericRepository<>(Action.class);
    // Return Response
    Response response = new Response();
    // Helpers
    WordHelper wordHelper = new WordHelper();
    TextHelper textHelper = new TextHelper();
    ZipHelper zipHelper = new ZipHelper();

    Mover mover = new Mover();
    List<File> files = new ArrayList<>();

    public Response delete(Action action) {
        try {
            actionRepo.delete(action.getId());
            Task parentTask = action.getTask();
            parentTask.removeAction(action);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            response.success = false;
            response.Message = e.getMessage();
            return response;
        }
    }

    public Response runAction(Action action) {
        action = actionRepo.getById(action.getId());
        if (!action.getIsActive()) {
            response.Message = "Action is InActive";
            return response;
        }

        String actionName = action.getActionName();

        try {
            files = prepareFiles(action);
        } catch (Exception e) {
            response.success = false;
            response.Message = e.getMessage();
            return response;
        }

        // First, check if it's a GenericAction
        try {
            GenericActions genericAction = GenericActions.valueOf(action.selectedFileAndAction.selectedAction);
            return runGenericAction(genericAction, action);
        } catch (IllegalArgumentException e) {
            // If it's not a GenericAction, check if it's a SpecificAction
            try {
                SpecificActions specificAction = SpecificActions.valueOf(action.selectedFileAndAction.selectedAction);
                return runSpecificAction(specificAction, action);
            } catch (IllegalArgumentException ex) {
                System.err.println("Unknown action type: " + actionName);
                response.Message = ex.getMessage();
                return response;
            }
        }
    }

    private Response runGenericAction(GenericActions genericAction, Action action) {
        switch (genericAction) {
            case MOVE:
                return moveFiles(action, files);
            case COPY:
                return copyFiles(action);
            case DELETE:
                return deleteFiles();
            case RENAME:
                return renameFiles(action);
            case COMPRESS:
                return compressFiles(action);
            default:
                System.err.println("Unhandled generic action: " + genericAction);
                return response;
        }
    }

    private Response runSpecificAction(SpecificActions specificAction, Action action) {
        switch (specificAction) {
            case FIND_AND_REPLACE:
                return findAndReplace(action);
            case MERGE:
                return mergeFiles(action);
            case CONVERT_TO_WORD:
                return convertToWord(action);
            case CONVERT_TO_EXCEL:
                return convertToExcel(action);
            case CONVERT_TO_PDF:
                return convertToPdf(action);
            case CONVERT_TO_TEXT:
                return convertToText(action);
            case CONVERT_TO_CSV:
                return convertToCsv(action);
            case CONVERT_TO_JSON:
                return convertToJson(action);
            case REMOVE_DUPLICATES:
                return removeDuplicates(action);
            case WATERMARK:
                return addWatermark(action);
            case SEARCH_BY_KEYWORD:
                return searchByKeyword(action);
            case EXTRACT_FILES:
                return extractFiles(action);
            case CONVERT_TO_PNG:
                return convertToPng(action);
            case CONVERT_TO_JPEG:
            case CONVERT_TO_JPG:
                return convertToJpeg(action);
            case REDUCE_IMAGE_SIZE:
                return reduceImageSize(action);
            case REDUCE_VIDEO_SIZE:
                return reduceVideoSize(action);
            case EXTRACT_AUDIO:
                return extractAudio(action);
            case PRINT:
                return printFiles(action);
            default:
                System.err.println("Unhandled specific action: " + specificAction);
                response.Message = "Action not supported";
                return response;
        }
    }

    private Response moveFiles(Action action, List<File> files) {
        String destinationPath = action.getOutputFolder();
        ensureDirectoryExists(destinationPath);


        for (File file : files) {
            try {
                Path source = file.toPath();
                Path destination = Paths.get(destinationPath, file.getName());
                Files.move(source, destination, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                response.success = false;
                response.Message = "Error moving file: " + file.getName() + ". " + e.getMessage();
                return response;
            }
        }
        response.Message = files.size() + "Files moved .";
        return response;
    }

    private Response copyFiles(Action action) {
        String destinationPath = action.getOutputFolder();
        ensureDirectoryExists(destinationPath);
        for (File file : files) {
            try {
                Path source = file.toPath();
                Path destination = Paths.get(destinationPath, file.getName());
                Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                response.success = false;
                response.Message = "Error copying file: " + file.getName() + ". " + e.getMessage();
                return response;
            }
        }
        response.Message = files.size() + "Files copied.";
        return response;
    }

    private Response deleteFiles() {
        for (File file : files) {
            if (!file.delete()) {
                response.success = false;
                response.Message = "Error deleting file: " + file.getName();
                return response;
            }
        }
        response.Message = files.size() + "Files deleted successfully.";
        return response;
    }

    private Response renameFiles(Action action) {
        String renamePattern = action.getActionParameter("Rename Pattern");
        String destinationPath = action.getOutputFolder();
        ensureDirectoryExists(destinationPath);

        for (File file : files) {
            // Retain the original file name and extension
            String originalNameWithoutExt = getFileNameWithoutExtension(file.getName());
            String originalExtension = getFileExtension(file);

            // Append the rename pattern to the original name
            String newName = originalNameWithoutExt + " " + renamePattern + "." + originalExtension;

            try {
                Path source = file.toPath();
                Path destination = Paths.get(destinationPath, newName);
                Files.move(source, destination, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                response.success = false;
                response.Message = "Error renaming file: " + file.getName() + ". " + e.getMessage();
                return response;
            }
        }
        response.success = true;
        response.Message = files.size() + "Files renamed successfully.";
        return response;
    }


    public Response compressFiles(Action action) {

        Response response = new Response();

        String outputFolderPath = action.outputFolderName;
        ensureDirectoryExists(outputFolderPath);
        Path outputFolder = Paths.get(outputFolderPath);
        Path outputZipFilePath = outputFolder.resolve("compressed_files.zip");

        response = zipHelper.Compress(files,outputZipFilePath);

        return response;
    }

    private Response findAndReplace(Action action) {

        String find = action.getActionParameter("Find");
        String replace = action.getActionParameter("Replace");
        String destinationPath = action.getOutputFolder();
        ensureDirectoryExists(destinationPath);

        for (File file : files) {

            String fileName = file.getName().toLowerCase();

            try {

                if (fileName.endsWith(".docx")) {

                    wordHelper.findAndReplaceInWord(file, find, replace, destinationPath);

                } else if (fileName.endsWith(".txt")) {

                    textHelper.findAndReplaceTextDocument(file, find, replace, destinationPath);

                } else {
                    // Skip unsupported file types
                    continue;
                }
            } catch (Exception e) {

                response.success = false;
                response.Message = "Error processing file: " + file.getName() + ". " + e.getMessage();
                return response;

            }
        }

        response.Message = "Find and replace completed successfully.";
        return response;
    }



    private Response mergeFiles(Action action) {

        String outputFolderPath = action.outputFolderName;
        String fileType = action.selectedFileAndAction.selectedFileType;

        if(fileType.equals("docx")){

            wordHelper.mergeWordDocuments(files.toArray(new File[0]),outputFolderPath);

        }else if(fileType.equals("pdf")){

            response.Message = "not implemented";
            return response;

        } else if (fileType.equals("txt")) {

            try {

            response = textHelper.mergeAllTextFiles(files,outputFolderPath);

            }
            catch (IOException ex){

                response.Message = ex.getMessage();

            }

            return response;

        }else{

            response.Message = "not implemented";
            return response;

        }

        response.Message = files.size() + "file merged.";
        return response;
    }

    private Response convertToWord(Action action) {

        String outputFolderPath = action.outputFolderName;
        String fileType = action.selectedFileAndAction.selectedFileType;

        if(fileType.equals("pdf")){

            response.Message = "not implemented";
            return response;

        } else if (fileType.equals("txt")) {

                response = textHelper.textToWord(files,outputFolderPath);

            return response;

        }

        response.Message = "Not Supported.";
        return response;
    }

    private Response convertToExcel(Action action) {
        // Implement convert to Excel logic
        response.Message = "Convert to Excel not implemented yet.";
        return response;
    }

    private Response convertToPdf(Action action) {

        String destinationPath = action.getOutputFolder();
        ensureDirectoryExists(destinationPath);


        for (File file : files) {

            String fileName = file.getName().toLowerCase();

            try {

                if (fileName.endsWith(".docx")) {

                    wordHelper.word2Pdf(file,destinationPath);

                } else if (fileName.endsWith(".png")) {

                    response.Message ="Not yet implemented";
                    return  response;

                } else if (fileName.endsWith(".txt")) {

                    response = textHelper.textToPdf(files,destinationPath);
                    return  response;

                } else {
                    // Skip unsupported file types
                    continue;
                }
            } catch (Exception e) {

                response.success = false;
                response.Message = "Error processing file: " + file.getName() + ". " + e.getMessage();
                return response;

            }
        }

        response.Message = files.size() + "Converted to PDF.";
        return response;
    }

    private Response convertToText(Action action) {
        String fileType = action.selectedFileAndAction.selectedFileType;
        if(fileType.equals("docx")){
            wordHelper.wordToText(action,files);
        }else if(fileType.equals("pdf")){

        }else{
            response.Message =  "File type not supported.";
            return response;
        }


        response.Message = files.size() + "Converted to text.";
        return response;
    }

    private Response convertToCsv(Action action) {
        // Implement convert to CSV logic
        response.Message = "Convert to CSV not implemented yet.";
        return response;
    }

    private Response convertToJson(Action action) {
        // Implement convert to JSON logic
        response.Message = "Convert to JSON not implemented yet.";
        return response;
    }

    private Response removeDuplicates(Action action) {
        // Implement remove duplicates logic
        response.Message = "Remove duplicates not implemented yet.";
        return response;
    }

    private Response addWatermark(Action action) {
        // Implement add watermark logic
        response.Message = "Add watermark not implemented yet.";
        return response;
    }

    private Response searchByKeyword(Action action) {
      response = textHelper.searchByKeyword(action,files);
        return response;
    }

    private Response extractFiles(Action action) {

        String outputFolderPath = action.outputFolderName;
        ensureDirectoryExists(outputFolderPath);
        Path outputFolder = Paths.get(outputFolderPath);

        response = zipHelper.Extract(files,outputFolder,outputFolderPath);

        return response;
    }

    private Response convertToPng(Action action) {
        // Implement convert to PNG logic
        response.Message = "Convert to PNG not implemented yet.";
        return response;
    }

    private Response convertToJpeg(Action action) {
        // Implement convert to JPEG logic
        response.Message = "Convert to JPEG not implemented yet.";
        return response;
    }

    private Response reduceImageSize(Action action) {
        // Implement reduce image size logic
        response.Message = "Reduce image size not implemented yet.";
        return response;
    }

    private Response reduceVideoSize(Action action) {
        // Implement reduce video size logic
        response.Message = "Reduce video size not implemented yet.";
        return response;
    }

    private Response extractAudio(Action action) {
        // Implement extract audio logic
        response.Message = "Extract audio not implemented yet.";
        return response;
    }

    private Response printFiles(Action action) {
        String printQuality = action.getActionParameter("Print Quality");
        String fileType = action.selectedFileAndAction.selectedFileType;

        if(fileType.equals("docx")){

            wordHelper.printWordDocuments(files.toArray(new File[0]));

        }else if(fileType.equals("pdf")){

            response.Message = "not implemented";
            return response;

        } else if (fileType.equals("txt")) {

            textHelper.printTextDocuments(files);
            return response;

        }else{

            response.Message = "not supported";
            return response;

        }

        response.Message = "Print job sent with quality: " + printQuality;
        return response;
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // Empty extension
        }
        return name.substring(lastIndexOf);
    }
}

