package acp.acp_project.Models;

import java.util.EnumSet;

public class zipFile extends File{

    public EnumSet<GenericActions> genericActions;
    public EnumSet<SpecificActions> specificActions;

    public zipFile(){
        super("Zip File","zip");
        this.genericActions = EnumSet.allOf(GenericActions.class);
        this.specificActions = EnumSet.noneOf(SpecificActions.class);
        this.specificActions.add(SpecificActions.EXTRACT_FILES);

    }
}
