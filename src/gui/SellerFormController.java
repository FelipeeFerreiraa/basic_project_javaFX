package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable {

	private Seller entity;

	private SellerService sellerService;

	private DepartmentService departmentService;

	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtName;

	@FXML
	private TextField txtEmail;

	@FXML
	private DatePicker txtBirthDate;

	@FXML
	private TextField txtBaseSalary;

	@FXML
	private ComboBox<Department> comboBoxDepartment;

	@FXML
	private Button btnSave;

	@FXML
	private Button btnCancel;

	@FXML
	private Label lblErrorName;

	@FXML
	private Label lblErrorEmail;

	@FXML
	private Label lblErrorBirthDate;

	@FXML
	private Label lblErrorBaseSalary;

	private ObservableList<Department> obsList;

	// ========================================================================
	// ============================== METODS ==================================
	// ========================================================================
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	@FXML
	public void onBtnSaveAction(ActionEvent event) {
		System.out.println("onBtnSaveAction");

		if (entity == null) {
			throw new IllegalStateException("ENTITY WAS NULL");
		}

		if (sellerService == null) {
			throw new IllegalStateException("SERVICE WAS NULL");
		}

		try {
			entity = getFormData();
			sellerService.saveOrUpdate(entity);
			notifyDataChangeListeners();
			Utils.currentStage(event).close();

		} catch (ValidationException e) {
			setErrorMessages(e.getErrors());

		} catch (DbException e) {
			Alerts.showAlerts("ERROR SAVING OBJECT", null, e.getMessage(), AlertType.WARNING);
		}

	}

	public void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	private Seller getFormData() {
		Seller obj = new Seller();

		ValidationException exception = new ValidationException("VALIDATION ERROR");

		obj.setId(Utils.tryParseToInt(txtId.getText()));

		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addErrors("name", "FIELD CAN'T BE EMPTY");
		}
		obj.setName(txtName.getText());

		if (txtEmail.getText() == null || txtEmail.getText().trim().equals("")) {
			exception.addErrors("email", "FIELD CAN'T BE EMPTY");
		}
		obj.setEmail(txtEmail.getText());

		if (txtBirthDate.getValue() == null) {
			exception.addErrors("birthDate", "FIELD CAN'T BE EMPTY");

		} else {
			// PEGA O VALOR DO DATEPICKER USANDO A DATA DO SISTEM,A
			Instant instant = Instant.from(txtBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()));
			obj.setBirthDate(Date.from(instant));
		}

		if (txtBaseSalary.getText() == null || txtBaseSalary.getText().trim().equals("")) {
			exception.addErrors("baseSalary", "FIELD CAN'T BE EMPTY");
		}
		obj.setBaseSalary(Utils.tryParseToDouble(txtBaseSalary.getText()));

		obj.setDepartment(comboBoxDepartment.getValue());

		if (exception.getErrors().size() > 0) {
			throw exception;
		}

		return obj;
	}

	@FXML
	public void onBtnCancelAction(ActionEvent event) {
		System.out.println("onBtnCancelAction");
		Utils.currentStage(event).close();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		Constraints.setTextFieldMaxLength(txtName, 70);
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldDouble(txtBaseSalary);
		Constraints.setTextFieldMaxLength(txtEmail, 60);
		Utils.formatDatePicker(txtBirthDate, "dd/MM/yyyy");

		initializeComboBoxDepartment();
	}

	public void setEntity(Seller entity) {
		this.entity = entity;
	}

	public void setServices(SellerService sellerService, DepartmentService departmentService) {
		this.sellerService = sellerService;
		this.departmentService = departmentService;
	}

	public void updateFormData() {

		if (entity == null) {
			throw new IllegalStateException("ENTITY WAS NULL");
		}

		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
		txtEmail.setText(entity.getEmail());
		Locale.setDefault(Locale.US);
		txtBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));

		if (entity.getBirthDate() != null) {
			txtBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
		}

		if (entity.getDepartment() == null) {
			comboBoxDepartment.getSelectionModel().selectFirst();
		} else {
			comboBoxDepartment.setValue(entity.getDepartment());
		}

	}

	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();

//		if (fields.contains("name")) {
//			lblErrorName.setText(errors.get("name"));
//		} else {
//			lblErrorName.setText("");
//		}

		lblErrorName.setText(fields.contains("name") ? errors.get("name") : "");

//		if (fields.contains("email")) {
//			lblErrorEmail.setText(errors.get("email"));
//		} else {
//			lblErrorEmail.setText("");
//		}

		lblErrorEmail.setText(fields.contains("email") ? errors.get("email") : "");

//		if (fields.contains("baseSalary")) {
//			lblErrorBaseSalary.setText(errors.get("baseSalary"));
//		} else {
//			lblErrorBaseSalary.setText("");
//		}

		lblErrorBaseSalary.setText(fields.contains("baseSalary") ? errors.get("baseSalary") : "");

//		if (fields.contains("birthDate")) {
//			lblErrorBirthDate.setText(errors.get("birthDate"));
//		} else {
//			lblErrorBirthDate.setText("");
//		}

		lblErrorBirthDate.setText(fields.contains("birthDate") ? errors.get("birthDate") : "");

	}

	public void loadAssociatedObjects() {
		if (departmentService == null) {
			throw new IllegalStateException("DEPARTMENTSERVICE WAS NULL");
		}

		List<Department> list = departmentService.findAll();
		obsList = FXCollections.observableArrayList(list);
		comboBoxDepartment.setItems(obsList);
	}

	private void initializeComboBoxDepartment() {
		Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
			@Override
			protected void updateItem(Department item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};

		comboBoxDepartment.setCellFactory(factory);
		comboBoxDepartment.setButtonCell(factory.call(null));
	}

}
