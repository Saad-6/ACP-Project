package acp.acp_project.Code;

import acp.acp_project.Domain.Mover;
import acp.acp_project.Domain.Response;
import acp.acp_project.Entities.Action;
import acp.acp_project.Entities.Task;
import acp.acp_project.Models.GenericActions;
import acp.acp_project.Models.SpecificActions;
import acp.acp_project.Repository.GenericRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static acp.acp_project.UI.Utility.*;

public class ActionManager {

    private final GenericRepository<Action> actionRepo = new GenericRepository<>(Action.class);
    Response response = new Response();
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
                return deleteFiles(action);
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
        response.Message = "Files moved successfully.";
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
        response.Message = "Files copied successfully.";
        return response;
    }

    private Response deleteFiles(Action action) {
        for (File file : files) {
            if (!file.delete()) {
                response.success = false;
                response.Message = "Error deleting file: " + file.getName();
                return response;
            }
        }
        response.Message = "Files deleted successfully.";
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
        response.Message = "Files renamed successfully.";
        return response;
    }


    private Response compressFiles(Action action) {
        // Implement compress files logic
        response.Message = "File compression not implemented yet.";
        return response;
    }

    private Response findAndReplace(Action action) {
        String find = action.getActionParameter("Find");
        String replace = action.getActionParameter("Replace");
        String destinationPath = action.getOutputFolder();
        ensureDirectoryExists(destinationPath);
        for (File file : files) {
            try {
                String content = new String(Files.readAllBytes(file.toPath()));
                content = content.replaceAll(Pattern.quote(find), replace);
                Path destination = Paths.get(destinationPath, file.getName());
                Files.write(destination, content.getBytes());
            } catch (IOException e) {
                response.success = false;
                response.Message = "Error processing file: " + file.getName() + ". " + e.getMessage();
                return response;
            }
        }
        response.Message = "Find and replace completed successfully.";
        return response;
    }

    private Response mergeFiles(Action action) {
        // Implement merge files logic
        response.Message = "File merging not implemented yet.";
        return response;
    }

    private Response convertToWord(Action action) {
        // Implement convert to Word logic
        response.Message = "Convert to Word not implemented yet.";
        return response;
    }

    private Response convertToExcel(Action action) {
        // Implement convert to Excel logic
        response.Message = "Convert to Excel not implemented yet.";
        return response;
    }

    private Response convertToPdf(Action action) {
        // Implement convert to PDF logic
        response.Message = "Convert to PDF not implemented yet.";
        return response;
    }

    private Response convertToText(Action action) {
        // Implement convert to text logic
        response.Message = "Convert to text not implemented yet.";
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
        String keyword = action.getActionParameter("Keyword");
        String destinationPath = action.getOutputFolder();
        ensureDirectoryExists(destinationPath);
        List<File> matchingFiles = new ArrayList<>();
        for (File file : files) {
            try {
                String content = new String(Files.readAllBytes(file.toPath()));
                if (content.contains(keyword)) {
                    matchingFiles.add(file);
                }
            } catch (IOException e) {
                response.success = false;
                response.Message = "Error searching file: " + file.getName() + ". " + e.getMessage();
                return response;
            }
        }

        // Copy matching files to destination
        for (File matchingFile : matchingFiles) {
            try {
                Path source = matchingFile.toPath();
                Path destination = Paths.get(destinationPath, matchingFile.getName());
                Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                response.success = false;
                response.Message = "Error copying matching file: " + matchingFile.getName() + ". " + e.getMessage();
                return response;
            }
        }

        response.Message = "Search by keyword completed. Found " + matchingFiles.size() + " matching files.";
        return response;
    }

    private Response extractFiles(Action action) {
        // Implement extract files logic
        response.Message = "Extract files not implemented yet.";
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
        // Implement printing logic here based on the print quality
        // This is a placeholder implementation
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

