module acp.acp_project {
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires org.docx4j.core;
    requires org.apache.pdfbox;
    requires jakarta.xml.bind;
    requires com.aspose.words;

    opens acp.acp_project to javafx.fxml;

    exports acp.acp_project;
    exports acp.acp_project.Domain;
    opens acp.acp_project.Domain to javafx.fxml;
    exports acp.acp_project.Entities;
    opens acp.acp_project.Entities to org.hibernate.orm.core, javafx.fxml;
    exports acp.acp_project.Models to org.hibernate.orm.core;
}