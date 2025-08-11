module basic_project_JavaFX_JDBC {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
	requires java.sql;
	requires javafx.base;

	opens application to javafx.graphics, javafx.fxml, javafx.base;
	opens gui to javafx.fxml;
	opens model.entities to javafx.graphics, javafx.fxml, javafx.base;
}
