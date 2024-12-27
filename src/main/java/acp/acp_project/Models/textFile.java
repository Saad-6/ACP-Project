package acp.acp_project.Models;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class textFile extends File{

public EnumSet<GenericActions> genericActions;
public EnumSet<SpecificActions> specificActions;

public textFile(){
    super("Text File","txt");
    this.genericActions = EnumSet.allOf(GenericActions.class);
    this.specificActions = EnumSet.noneOf(SpecificActions.class);
    this.specificActions.add(SpecificActions.FIND_AND_REPLACE);
    this.specificActions.add(SpecificActions.MERGE);
    this.specificActions.add(SpecificActions.CONVERT_TO_WORD);
    this.specificActions.add(SpecificActions.CONVERT_TO_PDF);
    this.specificActions.add(SpecificActions.SEARCH_BY_KEYWORD);
    this.specificActions.add(SpecificActions.PRINT);
}

}
