package acp.acp_project.Models;

import java.util.EnumSet;

public class pdfFile extends File {

    public EnumSet<GenericActions> genericActions;
    public EnumSet<SpecificActions> specificActions;

    public pdfFile(){
        super("PDF","pdf");
        this.genericActions = EnumSet.allOf(GenericActions.class);
        this.specificActions = EnumSet.noneOf(SpecificActions.class);
        this.specificActions.add(SpecificActions.MERGE);
        this.specificActions.add(SpecificActions.CONVERT_TO_WORD);
        this.specificActions.add(SpecificActions.CONVERT_TO_EXCEL);
        this.specificActions.add(SpecificActions.CONVERT_TO_PNG);
        this.specificActions.add(SpecificActions.CONVERT_TO_TEXT);
        this.specificActions.add(SpecificActions.WATERMARK);
        this.specificActions.add(SpecificActions.PRINT);
    }
}
