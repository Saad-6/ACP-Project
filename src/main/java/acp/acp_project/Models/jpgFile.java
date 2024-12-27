package acp.acp_project.Models;

import java.util.EnumSet;

public class jpgFile extends File{

    public EnumSet<GenericActions> genericActions;
    public EnumSet<SpecificActions> specificActions;

    public jpgFile(){
        super("Image","jpg");
        this.genericActions = EnumSet.allOf(GenericActions.class);
        this.specificActions = EnumSet.noneOf(SpecificActions.class);
        this.specificActions.add(SpecificActions.REDUCE_IMAGE_SIZE);
        this.specificActions.add(SpecificActions.CONVERT_TO_PDF);
        this.specificActions.add(SpecificActions.CONVERT_TO_PNG);
        this.specificActions.add(SpecificActions.CONVERT_TO_JPEG);
        this.specificActions.add(SpecificActions.WATERMARK);
        this.specificActions.add(SpecificActions.PRINT);
    }
}
