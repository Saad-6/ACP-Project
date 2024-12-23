package acp.acp_project.Models;

import java.util.EnumSet;

public class wordFile extends File {

    public EnumSet<GenericActions> genericActions;
    public EnumSet<SpecificActions> specificActions;

    public wordFile(){
        super("Word Document",".docx");
        this.genericActions = EnumSet.allOf(GenericActions.class);
        this.specificActions = EnumSet.noneOf(SpecificActions.class);
        this.specificActions.add(SpecificActions.FIND_AND_REPLACE);
        this.specificActions.add(SpecificActions.MERGE);
        this.specificActions.add(SpecificActions.CONVERT_TO_TEXT);
        this.specificActions.add(SpecificActions.CONVERT_TO_PDF);
        this.specificActions.add(SpecificActions.WATERMARK);
        this.specificActions.add(SpecificActions.PRINT);
    }
}
