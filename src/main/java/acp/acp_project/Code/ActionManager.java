package acp.acp_project.Code;

import acp.acp_project.Domain.Mover;
import acp.acp_project.Domain.Response;
import acp.acp_project.Entities.Action;
import acp.acp_project.Entities.Task;

import acp.acp_project.Models.GenericActions;
import acp.acp_project.Models.SpecificActions;
import acp.acp_project.Repository.GenericRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static acp.acp_project.UI.Utility.prepareFiles;

public class ActionManager {

    private final GenericRepository<Action> actionRepo = new GenericRepository<>(Action.class);
    // Default success value will be true
    Response response =  new Response();
    Mover mover = new Mover();
    List<File> files = new ArrayList<>();
    public Response delete(Action action){

     try {

        actionRepo.delete(action.getId());
        Task parentTask = action.getTask();
        parentTask.removeAction(action);
      return  response;

    } catch (Exception e) {

        e.printStackTrace();
        response.success = false;
        response.Message = e.getMessage();
        return response;
    }

    }
    public Response runAction(Action action) {

        if (!action.getIsActive()) {
            response.Message = "Task is InActive";
            return response;
        }

        String actionName = action.getActionName();

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
        try{
             files = prepareFiles(action);
        }

        catch (Exception e){
            response.success= false;
            response.Message = e.getMessage();
        }
        switch (genericAction) {
            case MOVE:
                return moveFiles(action,files);
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
        try{
            files = prepareFiles(action);
        }

        catch (Exception e){
            response.success= false;
            response.Message = e.getMessage();
        }
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

    // Placeholder methods for generic actions
    private Response moveFiles(Action action, List<File> files) {



        return response;
    }

    private Response copyFiles(Action action) {
        // Implement copy files logic

        return response;
    }

    private Response deleteFiles(Action action) {
        // Implement delete files logic
        return response;
    }

    private Response renameFiles(Action action) {
        // Implement rename files logic
        return response;
    }

    private Response compressFiles(Action action) {
        // Implement compress files logic
        return response;
    }

    // Placeholder methods for specific actions
    private Response findAndReplace(Action action) {
        // Implement find and replace logic
        return response;
    }

    private Response mergeFiles(Action action) {
        // Implement merge files logic
        return response;
    }

    private Response convertToWord(Action action) {
        // Implement convert to Word logic
        return response;
    }

    private Response convertToExcel(Action action) {
        // Implement convert to Excel logic
        return response;
    }

    private Response convertToPdf(Action action) {
        // Implement convert to PDF logic
        return response;
    }

    private Response convertToText(Action action) {
        // Implement convert to text logic
        return response;
    }

    private Response convertToCsv(Action action) {
        // Implement convert to CSV logic
        return response;
    }

    private Response convertToJson(Action action) {
        // Implement convert to JSON logic
        return response;
    }

    private Response removeDuplicates(Action action) {
        // Implement remove duplicates logic
        return response;
    }

    private Response addWatermark(Action action) {
        // Implement add watermark logic
        return response;
    }

    private Response searchByKeyword(Action action) {
        // Implement search by keyword logic
        return response;
    }

    private Response extractFiles(Action action) {
        // Implement extract files logic
        return response;
    }

    private Response convertToPng(Action action) {
        // Implement convert to PNG logic
        return response;
    }

    private Response convertToJpeg(Action action) {
        // Implement convert to JPEG logic
        return response;
    }

    private Response reduceImageSize(Action action) {
        // Implement reduce image size logic
        return response;
    }

    private Response reduceVideoSize(Action action) {
        // Implement reduce video size logic
        return response;
    }

    private Response extractAudio(Action action) {
        // Implement extract audio logic
        return response;
    }

    private Response printFiles(Action action) {
        // Implement print files logic
        return response;
    }
}