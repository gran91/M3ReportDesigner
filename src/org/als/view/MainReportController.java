package org.als.view;

import api.ConnexionAPI;
import fx.custom.FxUtil;
import fx.custom.InputConstraints;
import javafx.scene.paint.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import static javafx.concurrent.Worker.State.CANCELLED;
import static javafx.concurrent.Worker.State.FAILED;
import static javafx.concurrent.Worker.State.SUCCEEDED;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.als.MainApp;
import org.als.converter.FontSizeConverter;
import org.als.model.AvailableValue;
import org.als.model.CodeParameter;
import org.als.model.Condition;
import org.als.model.Division;
import org.als.model.DivisionUI;
import org.als.model.Environment;
import org.als.model.Prestation;
import org.als.model.PrestationUI;
import org.als.model.TitleCodeParameter;
import org.als.notification.Notification;
import org.als.task.ManageWindowsService;
import resources.Resource;
import tools.ColorFontTools;
import znio.MOMFile;

/**
 * FXML Controller class
 *
 * @author Jeremy.CHAUT
 */
public class MainReportController {

    @FXML
    private ComboBox<Environment> comboEnvironment;
    @FXML
    private ComboBox<Division> comboDivision;
    @FXML
    private Button buttonDivision;
    @FXML
    private Button breloadPrestation;
    @FXML
    private ComboBox<Font> comboFont;
    @FXML
    private ComboBox<Integer> comboFontSize;
    @FXML
    private Label labelDefault;
    @FXML
    private ColorPicker buttonDefault;
    @FXML
    private Label labelAlternative;
    @FXML
    private ColorPicker buttonAlternative;
    @FXML
    private TextField divisionLogoL;
    @FXML
    private TextField divisionLogoH;

//PRESTATION
    @FXML
    private ComboBox<Prestation> comboPrestation;
    @FXML
    private Label labelPrestation;
    @FXML
    private ProgressIndicator progressPrestation;
    @FXML
    private CheckBox checkBoxDoctor;
    @FXML
    private CheckBox checkBoxPrescriberInit;
    @FXML
    private CheckBox checkBoxPrescriber;
    @FXML
    private CheckBox checkBoxTreatDoctor;
    @FXML
    private CheckBox checkBoxTechnician;
    @FXML
    private Label labelTechnicianTel;
    @FXML
    private Label labelTechnicianAgency;
    @FXML
    private CheckBox checkBoxTechnicianTel;
    @FXML
    private CheckBox checkBoxTechnicianAgency;
    @FXML
    private CheckBox checkBoxNurse;
    @FXML
    private TextField prestationLogoL;
    @FXML
    private TextField prestationLogoH;
//TITLE
    @FXML
    private Label labelSampleTitle;
    @FXML
    private ComboBox<String> comboTitleShapeType;
    @FXML
    private ComboBox<Integer> comboTitleFontSize;
    @FXML
    private ColorPicker buttonTitleFontColor;
    @FXML
    private ColorPicker buttonForegroundTitleColor;
    @FXML
    private ColorPicker buttonBorderTitleColor;
    @FXML
    private Rectangle titleShape;
//BLOCK
    @FXML
    private ComboBox<String> comboBlockShapeType;
    @FXML
    private ColorPicker buttonForegroundBlockColor;
    @FXML
    private ColorPicker buttonBorderBlockColor;
    @FXML
    private Rectangle blockShape;
//PARAMETER
    @FXML
    private TableView<TitleCodeParameter> parameterTable;
    @FXML
    private TableColumn<TitleCodeParameter, String> codeColumn;
    @FXML
    private TableColumn<TitleCodeParameter, String> descriptionColumn;
    @FXML
    private TableColumn<TitleCodeParameter, String> typeColumn;
    @FXML
    private ProgressIndicator progressTable;
    @FXML
    private Label messageTable;

//GENERAL
    @FXML
    private Button buttonGenerate;
    @FXML
    private Accordion accordion;
    @FXML
    private TitledPane titledDivision;
    @FXML
    private TitledPane titledPrestation;
    @FXML
    private TitledPane titledBlock;
    @FXML
    private TitledPane titledParameter;

    private MainApp mainApp;

    private final ObservableList<Prestation> listPrestations = FXCollections.observableArrayList();
    private DivisionUI divisionUI;
    private PrestationUI prestationUI;
    private ObjectProperty<Font> generalFont, alternativeFont, titleFont;
    private ObservableList<String> listType;

    private ConnexionAPI conAPI;
    private final ResourceBundle resourceMessage = ResourceBundle.getBundle("resources/language", Locale.getDefault());
    private Notification.Notifier notifier;
    private BooleanProperty isGenerate;
    private int cptParameterCode, countParmeter = 0;

    @FXML
    public void initialize() {
        InputConstraints.numbersOnly(divisionLogoL);
        InputConstraints.numbersOnly(divisionLogoH);
        InputConstraints.numbersOnly(prestationLogoL);
        InputConstraints.numbersOnly(prestationLogoH);
    }

    private void addBindings() {
        comboFontSize.valueProperty().bindBidirectional(divisionUI.getFontSize().asObject());
        buttonDefault.valueProperty().addListener((ObservableValue<? extends Color> observable, Color oldValue, Color newValue) -> {
            divisionUI.getFontColor().set(newValue);
        });

        buttonAlternative.valueProperty().addListener((ObservableValue<? extends Color> observable, Color oldValue, Color newValue) -> {
            divisionUI.getFontColorAlertnative().set(newValue);
        });

        checkBoxDoctor.selectedProperty().bindBidirectional(prestationUI.getDoctor());
        checkBoxTreatDoctor.selectedProperty().bindBidirectional(prestationUI.getTreatDoctor());
        checkBoxPrescriber.selectedProperty().bindBidirectional(prestationUI.getPrescriber());
        checkBoxPrescriberInit.selectedProperty().bindBidirectional(prestationUI.getPrescriberInit());
        checkBoxNurse.selectedProperty().bindBidirectional(prestationUI.getNurse());
        checkBoxTechnician.selectedProperty().bindBidirectional(prestationUI.getTechnician());
        checkBoxTechnicianAgency.selectedProperty().bindBidirectional(prestationUI.getTechnicianAgency());
        checkBoxTechnicianTel.selectedProperty().bindBidirectional(prestationUI.getTechnicianTel());

        comboTitleFontSize.valueProperty().bindBidirectional(prestationUI.getTitleFontSize().asObject());
        buttonTitleFontColor.valueProperty().addListener((ObservableValue<? extends Color> observable, Color oldValue, Color newValue) -> {
            prestationUI.getFontTitleColor().set(newValue);
        });
        buttonForegroundTitleColor.valueProperty().addListener((ObservableValue<? extends Color> observable, Color oldValue, Color newValue) -> {
            prestationUI.getForegroundTitleColor().set(newValue);
        });

        buttonBorderTitleColor.valueProperty().addListener((ObservableValue<? extends Color> observable, Color oldValue, Color newValue) -> {
            prestationUI.getBorderTitleColor().set(newValue);
        });

        buttonForegroundBlockColor.valueProperty().addListener((ObservableValue<? extends Color> observable, Color oldValue, Color newValue) -> {
            prestationUI.getForegroundBlockColor().set(newValue);
        });
        buttonBorderBlockColor.valueProperty().addListener((ObservableValue<? extends Color> observable, Color oldValue, Color newValue) -> {
            prestationUI.getBorderBlockColor().set(newValue);
        });

        comboTitleShapeType.valueProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            prestationUI.getTitleShape().setValue(comboTitleShapeType.getSelectionModel().getSelectedIndex());
        });

        comboBlockShapeType.valueProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            prestationUI.getBlockShape().setValue(comboBlockShapeType.getSelectionModel().getSelectedIndex());
        });

        prestationLogoL.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            prestationUI.getLogoL().setValue(Integer.parseInt(prestationLogoL.getText()));
        });

        prestationLogoH.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            prestationUI.getLogoH().setValue(Integer.parseInt(prestationLogoH.getText()));
        });

    }

    private void drawShape(String newValue, Rectangle rect) {
        if (newValue != null) {
            if (newValue.equals(prestationUI.listShape.get(0))) {
                rect.setVisible(false);
            } else {
                if (newValue.equals(prestationUI.listShape.get(1))) {
                    rect.setArcHeight(PrestationUI.rectangleCorner);
                    rect.setArcWidth(PrestationUI.rectangleCorner);
                } else {
                    rect.setArcHeight(PrestationUI.roundRectangleCorner);
                    rect.setArcWidth(PrestationUI.roundRectangleCorner);
                }
                rect.setVisible(true);
            }
        } else {
            rect.setVisible(false);
        }
    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        isGenerate = new SimpleBooleanProperty(true);
        Bindings.bindBidirectional(buttonGenerate.disableProperty(), isGenerate);
        notifier = Notification.Notifier.INSTANCE;
        divisionUI = new DivisionUI(null, mainApp);
        prestationUI = new PrestationUI(comboPrestation.getValue());
        parameterTable.setEditable(true);
        typeColumn.setEditable(true);

        listType = TitleCodeParameter.listType();

        codeColumn.setCellValueFactory(cellData -> cellData.getValue().getTitleCode());
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().getTitleDescription());
        typeColumn.setCellValueFactory(cellData -> cellData.getValue().getTitleType());
        typeColumn.setCellFactory(ChoiceBoxTableCell.forTableColumn(listType));
        typeColumn.setOnEditCommit((TableColumn.CellEditEvent<TitleCodeParameter, String> t) -> {
            ((TitleCodeParameter) t.getTableView().getItems()
                    .get(t.getTablePosition().getRow()))
                    .getTitleType().setValue(t.getNewValue());
        });

        parameterTable.setItems(prestationUI.getObservableListTitleParameter());

        accordion.setExpandedPane(titledDivision);
        generalFont = new SimpleObjectProperty<>(divisionUI.getFont().get());
        alternativeFont = new SimpleObjectProperty<>(divisionUI.getFont().get());
        titleFont = new SimpleObjectProperty<>(divisionUI.getFont().get());
        ObservableList<Font> listFont = FXCollections.observableArrayList();
        Font.getFamilies().stream().forEach((s) -> {
            listFont.addAll(new Font(s, mainApp.getGeneral().getDefaultFontSize()));
        });
        comboFont.setItems(listFont);
        FxUtil.autoCompleteComboBox(comboFont, FxUtil.AutoCompleteMode.STARTS_WITH);
        comboFont.setConverter(new StringConverter<Font>() {
            private Object t;

            @Override
            public String toString(Font object) {
                if (object == null) {
                    return "";
                }
                return object.getName();
            }

            @Override
            public Font fromString(String string) {
                return comboFont.getSelectionModel().getSelectedItem();
            }
        });

        int min = 5;
        int max = 25;
        ObservableList<Integer> listFontSize = FXCollections.observableArrayList();
        for (int i = min; i <= max; i++) {
            listFontSize.add(i);
        }
        comboFontSize.setItems(listFontSize);
        comboTitleFontSize.setItems(listFontSize);
        FxUtil.autoCompleteComboBox(comboFontSize, FxUtil.AutoCompleteMode.STARTS_WITH);
        FxUtil.autoCompleteComboBox(comboTitleFontSize, FxUtil.AutoCompleteMode.STARTS_WITH);
        comboFontSize.setConverter(new FontSizeConverter());
        comboTitleFontSize.setConverter(new FontSizeConverter());
        comboFontSize.valueProperty().addListener((ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) -> {
            divisionUI.getFont().set(new Font(divisionUI.getFont().getValue().getName(), newValue));
            divisionUI.getFontSize().set(newValue);
            generalFont.set(new Font(divisionUI.getFont().getValue().getName(), newValue));
            alternativeFont.set(new Font(divisionUI.getFont().getValue().getName(), newValue));
        });

        comboTitleFontSize.valueProperty().addListener((ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) -> {
            prestationUI.getTitleFontSize().set(newValue);
            titleFont.set(new Font(divisionUI.getFont().getValue().getName(), newValue));
        });
        
        divisionLogoL.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            divisionLogoL.setText(divisionUI.getLogoL().getValue().toString());
        });

        divisionLogoH.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            divisionLogoH.setText(divisionUI.getLogoH().getValue().toString());
        });
        
        comboTitleShapeType.setItems(prestationUI.listShape);
        comboBlockShapeType.setItems(prestationUI.listShape);

        comboTitleShapeType.valueProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            drawShape(newValue, titleShape);
        });

        comboBlockShapeType.valueProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            drawShape(newValue, blockShape);
        });

        labelDefault.fontProperty().bind(generalFont);
        labelAlternative.fontProperty().bind(alternativeFont);
        labelSampleTitle.fontProperty().bind(titleFont);

        checkBoxTechnicianTel.visibleProperty().bind(checkBoxTechnician.selectedProperty());
        checkBoxTechnicianAgency.visibleProperty().bind(checkBoxTechnician.selectedProperty());
        labelTechnicianTel.visibleProperty().bind(checkBoxTechnician.selectedProperty());
        labelTechnicianAgency.visibleProperty().bind(checkBoxTechnician.selectedProperty());

        buttonDefault.valueProperty().addListener((ObservableValue<? extends Color> observable, Color oldValue, Color newValue) -> {
            labelDefault.setTextFill(newValue);
        });

        buttonAlternative.valueProperty().addListener((ObservableValue<? extends Color> observable, Color oldValue, Color newValue) -> {
            labelAlternative.setTextFill(newValue);
        });

        titleShape.fillProperty().bind(buttonForegroundTitleColor.valueProperty());
        titleShape.strokeProperty().bind(buttonBorderTitleColor.valueProperty());
        blockShape.fillProperty().bind(buttonForegroundBlockColor.valueProperty());
        blockShape.strokeProperty().bind(buttonBorderBlockColor.valueProperty());
        buttonTitleFontColor.valueProperty().addListener((ObservableValue<? extends Color> observable, Color oldValue, Color newValue) -> {
            labelSampleTitle.setTextFill(newValue);
        });

        comboFont.valueProperty().addListener((ObservableValue<? extends Font> observable, Font oldValue, Font newValue) -> {
            Integer size = (comboFontSize.getValue() != null) ? comboFontSize.getValue() : mainApp.getGeneral().getDefaultFontSize();
            Integer sizetitle = (comboTitleFontSize.getValue() != null) ? comboTitleFontSize.getValue() : mainApp.getGeneral().getDefaultFontSize();
            divisionUI.getFont().set(new Font(newValue.getName(), size));
            generalFont.set(new Font(newValue.getName(), size));
            alternativeFont.set(new Font(newValue.getName(), size));
            titleFont.set(new Font(newValue.getName(), sizetitle));
        });

        comboEnvironment.setItems(mainApp.getEnvironmentData());
        comboEnvironment.setConverter(new StringConverter<Environment>() {

            @Override
            public String toString(Environment object) {
                if (object == null) {
                    return null;
                }
                return object.toString();
            }

            @Override
            public Environment fromString(String string) {
                return comboEnvironment.getItems().get(comboEnvironment.getSelectionModel().getSelectedIndex());
            }
        });
        FxUtil.autoCompleteComboBox(comboEnvironment, FxUtil.AutoCompleteMode.STARTS_WITH);

        comboEnvironment.valueProperty().addListener((ObservableValue<? extends Environment> observable, Environment oldValue, Environment newValue) -> {
            if (newValue != null) {
                comboDivision.setDisable(false);
                buttonDivision.setDisable(false);
            } else {
                comboDivision.setDisable(true);
                buttonDivision.setDisable(true);
            }
            isGenerate.set(true);
        });

        comboDivision.setItems(mainApp.getDivisionData());
        comboDivision.setConverter(new StringConverter<Division>() {

            @Override
            public String toString(Division object) {
                if (object == null) {
                    return null;
                }
                return object.toString();
            }

            @Override
            public Division fromString(String string) {
                return comboDivision.getItems().get(comboDivision.getSelectionModel().getSelectedIndex());
            }
        });
        FxUtil.autoCompleteComboBox(comboDivision, FxUtil.AutoCompleteMode.STARTS_WITH);

        comboDivision.valueProperty().addListener((ObservableValue<? extends Division> observable, Division oldValue, Division newValue) -> {
            labelPrestation.setText("");
            if (newValue != null) {
                parameterTable.getItems().clear();
                prestationUI.getObservableListTitleParameter().clear();
                comboFont.setDisable(false);
                comboFontSize.setDisable(false);
                buttonDefault.setDisable(false);
                buttonAlternative.setDisable(false);
                divisionLogoL.setDisable(false);
                divisionLogoH.setDisable(false);
                getDivisionUIParameter();
                listDivisionPrestation();
            } else {
                comboFont.setDisable(true);
                comboFontSize.setDisable(true);
                buttonDefault.setDisable(true);
                buttonAlternative.setDisable(true);
                comboPrestation.setDisable(true);
                divisionLogoL.setDisable(true);
                divisionLogoH.setDisable(true);
            }
            isGenerate.set(true);
        });

        comboPrestation.setItems(listPrestations);
        FxUtil.autoCompleteComboBox(comboPrestation, FxUtil.AutoCompleteMode.STARTS_WITH);
        comboPrestation.setConverter(new StringConverter<Prestation>() {

            @Override
            public String toString(Prestation object) {
                if (object == null) {
                    return null;
                }
                return object.toString();
            }

            @Override
            public Prestation fromString(String string) {
                return comboPrestation.getItems().get(comboPrestation.getSelectionModel().getSelectedIndex());
            }
        });
        comboPrestation.valueProperty().addListener((ObservableValue<? extends Prestation> observable, Prestation oldValue, Prestation newValue) -> {
            isGenerate.set(true);
            if (newValue != null) {
                parameterTable.getItems().clear();
                prestationUI.getObservableListTitleParameter().clear();
                labelPrestation.setText(newValue.getName());
                getPrestationUIParameter();
                listPrestationParameter();
            } else {
                labelPrestation.setText("");
                checkBoxDoctor.setSelected(false);
                checkBoxPrescriberInit.setSelected(false);
                checkBoxTreatDoctor.setSelected(false);
                checkBoxTechnician.setSelected(false);
                checkBoxTechnicianTel.setSelected(false);
                checkBoxTechnicianAgency.setSelected(false);
                checkBoxNurse.setSelected(false);

            }
            checkBoxDoctor.setDisable(newValue == null);
            checkBoxPrescriberInit.setDisable(newValue == null);
            checkBoxPrescriber.setDisable(newValue == null);
            checkBoxTreatDoctor.setDisable(newValue == null);
            checkBoxTechnician.setDisable(newValue == null);
            checkBoxTechnicianTel.setDisable(newValue == null);
            checkBoxTechnicianAgency.setDisable(newValue == null);
            checkBoxNurse.setDisable(newValue == null);
            prestationLogoL.setDisable(newValue == null);
            prestationLogoH.setDisable(newValue == null);
        });
        labelDefault.textProperty().bind(mainApp.getGeneral().getDefaultTextProperty());
        labelAlternative.textProperty().bind(mainApp.getGeneral().getDefaultTextProperty());
        addBindings();
    }

    private void getDivisionUIParameter() {
        final Service<Void> divisionUIService = new Service<Void>() {

            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        divisionUI.getDivi().set(comboDivision.getValue().getDivi());
                        String path = comboEnvironment.getValue().getPathMOM() + System.getProperty("file.separator") + Resource.pathMOMTable + System.getProperty("file.separator") + Resource.baseFileName + "_" + comboDivision.getValue().getDivi() + Resource.extFile;
                        if (new File(path).exists()) {
                            HashMap<String, String[]> p = MOMFile.readAllLine(Paths.get(path));
                            if (p != null) {
                                String formatFont = (p.get(DivisionUI.listDivisionKey[0]) != null) ? p.get(DivisionUI.listDivisionKey[0])[0] : mainApp.getGeneral().getDefaultFontProperty().getValue().getName();
                                divisionUI.getFontSize().setValue((p.get(DivisionUI.listDivisionKey[1]) != null) ? tools.Tools.convertToInt(p.get(DivisionUI.listDivisionKey[1])[0].trim()) : mainApp.getGeneral().getDefaultFontSizeProperty().getValue());
                                divisionUI.getFont().setValue(new Font(formatFont.replaceAll("_", " "), divisionUI.getFontSize().getValue()));
                                divisionUI.getFormatFont().setValue(formatFont.replaceAll("_", " "));
                                divisionUI.getFontColor().setValue((p.get(DivisionUI.listDivisionKey[2]) != null) ? ColorFontTools.getColor(p.get(DivisionUI.listDivisionKey[2])[0].trim()) : mainApp.getGeneral().getDefaultFontColorProperty().getValue());
                                divisionUI.getFontColorAlertnative().setValue((p.get(DivisionUI.listDivisionKey[3]) != null) ? ColorFontTools.getColor(p.get(DivisionUI.listDivisionKey[3])[0].trim()) : Color.RED);
                                divisionUI.getLogoL().setValue((p.get(DivisionUI.listDivisionKey[4]) != null) ? tools.Tools.convertToInt(p.get(DivisionUI.listDivisionKey[4])[0].trim()) : mainApp.getGeneral().getDefaultLogoLProperty().getValue());
                                divisionUI.getLogoH().setValue((p.get(DivisionUI.listDivisionKey[5]) != null) ? tools.Tools.convertToInt(p.get(DivisionUI.listDivisionKey[5])[0].trim()) : mainApp.getGeneral().getDefaultLogoHProperty().getValue());
                            }

                        } else {
                            new RuntimeException("No Parameter");
                        }
                        return null;
                    }
                };
            }
        };
        divisionUIService.stateProperty().addListener((ObservableValue<? extends Worker.State> observableValue, Worker.State oldValue, Worker.State newValue) -> {
            switch (newValue) {
                case FAILED:
                    System.err.println(divisionUIService.getException());
                    notifier.notify(new Notification(resourceMessage.getString("message.titleDivision"), String.format(resourceMessage.getString("message.nodivisionparameter"), comboDivision.getValue().getName()), Notification.WARNING_ICON));
                    break;
                case CANCELLED:
                case SUCCEEDED:
                    comboFont.setValue(divisionUI.getFont().get());
                    comboFontSize.setValue(divisionUI.getFontSize().get());
                    buttonDefault.setValue(divisionUI.getFontColor().get());
                    buttonAlternative.setValue(divisionUI.getFontColorAlertnative().get());
                    divisionLogoL.setText("" + divisionUI.getLogoL().get());
                    divisionLogoH.setText("" + divisionUI.getLogoH().get());
                    notifier.notify(new Notification(resourceMessage.getString("message.titleDivision"), String.format(resourceMessage.getString("message.okdivisionparameter"), comboDivision.getValue().getName()), Notification.SUCCESS_ICON));
                    break;
            }
        });
        divisionUIService.restart();
    }

    private void getPrestationUIParameter() {
        final Service<Void> prestationUIService = new Service<Void>() {

            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        prestationUI.getPrestation().setValue(comboPrestation.getValue());
                        String path = comboEnvironment.getValue().getPathMOM() + System.getProperty("file.separator") + Resource.pathMOMTable + System.getProperty("file.separator") + Resource.baseFileName + "_" + comboDivision.getValue().getDivi() + "_" + comboPrestation.getValue().getCode().trim() + Resource.extFile;
                        if (new File(path).exists()) {
                            HashMap<String, String[]> p = MOMFile.readAllLine(Paths.get(path));
                            if (p != null) {
                                prestationUI.getTitleShape().set((p.get(PrestationUI.listKeyPrestation[0]) != null) ? tools.Tools.convertToInt(p.get(PrestationUI.listKeyPrestation[0])[0].trim()) : 0);
                                prestationUI.getTitleFontSize().set((p.get(PrestationUI.listKeyPrestation[1]) != null) ? tools.Tools.convertToInt(p.get(PrestationUI.listKeyPrestation[1])[0].trim()) : divisionUI.getFontSize().get());
                                prestationUI.getFontTitleColor().set((p.get(PrestationUI.listKeyPrestation[2]) != null) ? ColorFontTools.getColor(p.get(PrestationUI.listKeyPrestation[2])[0].trim()) : divisionUI.getFontColor().get());
                                prestationUI.getForegroundTitleColor().set((p.get(PrestationUI.listKeyPrestation[3]) != null) ? ColorFontTools.getColor(p.get(PrestationUI.listKeyPrestation[3])[0].trim()) : Color.WHITE);
                                prestationUI.getBorderTitleColor().set((p.get(PrestationUI.listKeyPrestation[4]) != null) ? ColorFontTools.getColor(p.get(PrestationUI.listKeyPrestation[4])[0].trim()) : Color.BLACK);
                                prestationUI.getBlockShape().set((p.get(PrestationUI.listKeyPrestation[5]) != null) ? tools.Tools.convertToInt(p.get(PrestationUI.listKeyPrestation[5])[0].trim()) : 0);
                                prestationUI.getForegroundBlockColor().set((p.get(PrestationUI.listKeyPrestation[6]) != null) ? ColorFontTools.getColor(p.get(PrestationUI.listKeyPrestation[6])[0].trim()) : Color.WHITE);
                                prestationUI.getBorderBlockColor().set((p.get(PrestationUI.listKeyPrestation[7]) != null) ? ColorFontTools.getColor(p.get(PrestationUI.listKeyPrestation[7])[0].trim()) : Color.BLACK);
                                prestationUI.getDoctor().set((p.get(PrestationUI.listKeyPrestation[8]) != null) ? tools.Tools.convertToBoolean(p.get(PrestationUI.listKeyPrestation[8])[0].trim()) : false);
                                prestationUI.getPrescriberInit().set((p.get(PrestationUI.listKeyPrestation[9]) != null) ? tools.Tools.convertToBoolean(p.get(PrestationUI.listKeyPrestation[9])[0].trim()) : false);
                                prestationUI.getPrescriber().set((p.get(PrestationUI.listKeyPrestation[10]) != null) ? tools.Tools.convertToBoolean(p.get(PrestationUI.listKeyPrestation[10])[0].trim()) : false);
                                prestationUI.getTreatDoctor().set((p.get(PrestationUI.listKeyPrestation[11]) != null) ? tools.Tools.convertToBoolean(p.get(PrestationUI.listKeyPrestation[11])[0].trim()) : false);
                                prestationUI.getTechnician().set((p.get(PrestationUI.listKeyPrestation[12]) != null) ? tools.Tools.convertToBoolean(p.get(PrestationUI.listKeyPrestation[12])[0].trim()) : false);
                                prestationUI.getTechnicianTel().set((p.get(PrestationUI.listKeyPrestation[13]) != null) ? tools.Tools.convertToBoolean(p.get(PrestationUI.listKeyPrestation[13])[0].trim()) : false);
                                prestationUI.getTechnicianAgency().set((p.get(PrestationUI.listKeyPrestation[14]) != null) ? tools.Tools.convertToBoolean(p.get(PrestationUI.listKeyPrestation[14])[0].trim()) : false);
                                prestationUI.getNurse().set((p.get(PrestationUI.listKeyPrestation[15]) != null) ? tools.Tools.convertToBoolean(p.get(PrestationUI.listKeyPrestation[15])[0].trim()) : false);
                                prestationUI.getLogoL().set((p.get(PrestationUI.listKeyPrestation[16]) != null) ? tools.Tools.convertToInt(p.get(PrestationUI.listKeyPrestation[16])[0].trim()) : mainApp.getGeneral().getDefaultLogoL());
                                prestationUI.getLogoH().set((p.get(PrestationUI.listKeyPrestation[17]) != null) ? tools.Tools.convertToInt(p.get(PrestationUI.listKeyPrestation[17])[0].trim()) : mainApp.getGeneral().getDefaultLogoH());
                            } else {
                                throw new RuntimeException();
                            }
                        } else {
                            throw new RuntimeException();
                        }
                        return null;
                    }
                };
            }
        };
        prestationUIService.stateProperty().addListener((ObservableValue<? extends Worker.State> observableValue, Worker.State oldValue, Worker.State newValue) -> {
            switch (newValue) {
                case FAILED:
                    System.err.println(prestationUIService.getException());
                    notifier.notify(new Notification(resourceMessage.getString("message.titlePrestationCode"), String.format(resourceMessage.getString("message.noPrestationCode"), new String[]{comboPrestation.getValue().getName(), comboPrestation.getValue().getCode(), comboDivision.getValue().getName()}), Notification.WARNING_ICON));
                    checkBoxDoctor.setSelected(prestationUI.getDoctor().getValue());
                    checkBoxPrescriber.setSelected(prestationUI.getPrescriber().getValue());
                    checkBoxPrescriberInit.setSelected(prestationUI.getPrescriberInit().getValue());
                    checkBoxTreatDoctor.setSelected(prestationUI.getTreatDoctor().getValue());
                    checkBoxTechnician.setSelected(prestationUI.getTechnician().getValue());
                    checkBoxTechnicianTel.setSelected(prestationUI.getTechnicianTel().getValue());
                    checkBoxTechnicianAgency.setSelected(prestationUI.getTechnicianAgency().getValue());
                    checkBoxNurse.setSelected(prestationUI.getNurse().getValue());

                    comboTitleShapeType.getSelectionModel().select(prestationUI.getTitleShape().getValue());
                    comboTitleFontSize.setValue(prestationUI.getTitleFontSize().getValue());
                    buttonTitleFontColor.setValue(prestationUI.getFontTitleColor().getValue());
                    labelSampleTitle.setTextFill(buttonTitleFontColor.getValue());
                    buttonForegroundTitleColor.setValue(prestationUI.getForegroundTitleColor().getValue());
                    buttonBorderTitleColor.setValue(prestationUI.getBorderTitleColor().getValue());

                    comboBlockShapeType.getSelectionModel().select(prestationUI.getBlockShape().getValue());
                    buttonForegroundBlockColor.setValue(prestationUI.getForegroundBlockColor().getValue());
                    buttonBorderBlockColor.setValue(prestationUI.getBorderBlockColor().getValue());
                    prestationLogoL.setText(prestationUI.getLogoL().getValue().toString());
                    prestationLogoH.setText(prestationUI.getLogoH().getValue().toString());
                    
                    
                    comboTitleShapeType.setDisable(false);
                    comboTitleFontSize.setDisable(false);
                    buttonTitleFontColor.setDisable(false);
                    buttonForegroundTitleColor.setDisable(false);
                    buttonBorderTitleColor.setDisable(false);

                    comboBlockShapeType.setDisable(false);
                    buttonForegroundBlockColor.setDisable(false);
                    buttonBorderBlockColor.setDisable(false);
                    prestationLogoL.setDisable(false);
                    prestationLogoH.setDisable(false);
                    break;
                case CANCELLED:
                case SUCCEEDED:
                    checkBoxDoctor.setSelected(prestationUI.getDoctor().getValue());
                    checkBoxPrescriber.setSelected(prestationUI.getPrescriber().getValue());
                    checkBoxPrescriberInit.setSelected(prestationUI.getPrescriberInit().getValue());
                    checkBoxTreatDoctor.setSelected(prestationUI.getTreatDoctor().getValue());
                    checkBoxTechnician.setSelected(prestationUI.getTechnician().getValue());
                    checkBoxTechnicianTel.setSelected(prestationUI.getTechnicianTel().getValue());
                    checkBoxTechnicianAgency.setSelected(prestationUI.getTechnicianAgency().getValue());
                    checkBoxNurse.setSelected(prestationUI.getNurse().getValue());

                    comboTitleShapeType.getSelectionModel().select(prestationUI.getTitleShape().getValue());
                    comboTitleFontSize.setValue(prestationUI.getTitleFontSize().getValue());
                    buttonTitleFontColor.setValue(prestationUI.getFontTitleColor().getValue());
                    labelSampleTitle.setTextFill(buttonTitleFontColor.getValue());
                    buttonForegroundTitleColor.setValue(prestationUI.getForegroundTitleColor().getValue());
                    buttonBorderTitleColor.setValue(prestationUI.getBorderTitleColor().getValue());

                    comboBlockShapeType.getSelectionModel().select(prestationUI.getBlockShape().getValue());
                    buttonForegroundBlockColor.setValue(prestationUI.getForegroundBlockColor().getValue());
                    buttonBorderBlockColor.setValue(prestationUI.getBorderBlockColor().getValue());

                    prestationLogoL.setText(prestationUI.getLogoL().getValue().toString());
                    prestationLogoH.setText(prestationUI.getLogoH().getValue().toString());
                    
                    comboTitleShapeType.setDisable(false);
                    comboTitleFontSize.setDisable(false);
                    buttonTitleFontColor.setDisable(false);
                    buttonForegroundTitleColor.setDisable(false);
                    buttonBorderTitleColor.setDisable(false);

                    comboBlockShapeType.setDisable(false);
                    buttonForegroundBlockColor.setDisable(false);
                    buttonBorderBlockColor.setDisable(false);
                    prestationLogoL.setDisable(false);
                    prestationLogoH.setDisable(false);

                    notifier.notify(new Notification(resourceMessage.getString("message.titlePrestationCode"), String.format(resourceMessage.getString("message.okPrestationCode"), new String[]{comboPrestation.getValue().getName(), comboPrestation.getValue().getCode(), comboDivision.getValue().getName()}), Notification.SUCCESS_ICON));
                    break;
            }
        });
        prestationUIService.restart();
    }

    @FXML
    private void listDivisionPrestation() {
        comboPrestation.setDisable(true);
        breloadPrestation.setVisible(false);
        progressPrestation.setVisible(true);
        final Service<Void> listDivisionService;
        listDivisionService = new Service<Void>() {

            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        String ip = comboEnvironment.getValue().getIP();
                        int port = comboEnvironment.getValue().getPort();
                        String user = comboEnvironment.getValue().getLogin();
                        listPrestations.clear();
                        conAPI = new ConnexionAPI(ip, port);
                        if (conAPI.getResult() != -1) {
                            if (conAPI.verifConnection(user, "")) {
                                String in = String.format("%-15s", Resource.TRANS_CODEJ);
                                in += String.format("%-3s", "");
                                in += String.format("%-3s", "PAT");
                                ArrayList aOut = ConnexionAPI.parseFieldData(conAPI.listField(Resource.APINAME, Resource.TRANS_CODEJ, 'O', user, ""));
                                ArrayList listCodeJ = conAPI.executeAPI(String.format("%-10s", Resource.APINAME), String.format("%-15s", Resource.TRANS_CODEJ), in, user, "");
                                String code = "";
                                String labelCode = "";
                                for (Object listCodeJ1 : listCodeJ) {
                                    for (int j = 0; j < ((ArrayList) aOut.get(0)).size(); j++) {
                                        try {
                                            int begin = Integer.parseInt(((ArrayList) aOut.get(2)).get(j).toString()) - 1;
                                            int end = Integer.parseInt(((ArrayList) aOut.get(3)).get(j).toString());
                                            if (j == 0) {
                                                code = listCodeJ1.toString().substring(begin, end);
                                            } else {
                                                labelCode = listCodeJ1.toString().substring(begin, end);
                                            }
                                        } catch (NumberFormatException ex) {
                                            throw new RuntimeException(ex.getLocalizedMessage());
                                        } catch (Exception e) {
                                            throw new RuntimeException(e.getLocalizedMessage());
                                        }
                                    }
                                    listPrestations.add(new Prestation(code, labelCode));
                                }
                            } else {
                                throw new RuntimeException(String.format(resourceMessage.getString("api.user"), conAPI.getUser()));
                            }
                        } else {
                            throw new RuntimeException(String.format(resourceMessage.getString("api.parameter")));
                        }
                        return null;
                    }
                };
            }
        };
        listDivisionService.stateProperty().addListener((ObservableValue<? extends Worker.State> observableValue, Worker.State oldValue, Worker.State newValue) -> {
            switch (newValue) {
                case FAILED:
                    String msg = listDivisionService.getException().toString();
                    FxUtil.showAlert(AlertType.ERROR, resourceMessage.getString("api.title"), resourceMessage.getString("api.header"), msg, conAPI.getException());
                    notifier.notify(new Notification(resourceMessage.getString("message.titlelistprestation"), String.format(resourceMessage.getString("message.nolistprestation"), comboDivision.getValue().getName()), Notification.ERROR_ICON));
                    progressPrestation.setVisible(false);
                    breloadPrestation.setVisible(true);
                    break;
                case CANCELLED:
                case SUCCEEDED:
                    notifier.notify(new Notification(resourceMessage.getString("message.titlelistprestation"), String.format(resourceMessage.getString("message.oklistprestation"), comboDivision.getValue().getName()), Notification.SUCCESS_ICON));
                    breloadPrestation.setVisible(false);
                    progressPrestation.setVisible(false);
                    comboPrestation.setDisable(false);
                    break;
            }
        });
        listDivisionService.restart();
    }

    private void listPrestationParameter() {
        cptParameterCode = 0;
        parameterTable.setDisable(true);
        progressTable.setVisible(true);
        messageTable.setVisible(true);
        final Service<Void> listPrestationParameterService = new Service<Void>() {

            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        String ip = comboEnvironment.getValue().getIP();
                        int port = comboEnvironment.getValue().getPort();
                        String user = comboEnvironment.getValue().getLogin();
                        conAPI = new ConnexionAPI(ip, port);
                        if (conAPI.getResult() != -1) {
                            if (conAPI.verifConnection(user, "")) {
                                String in = String.format("%-15s", Resource.TRANS_TITLECODEJ);
                                in += String.format("%-3s", "");
                                in += String.format("%-10s", comboDivision.getValue().getINBL());
                                in += String.format("%-10s", comboPrestation.getValue().getCode());
                                ArrayList aOut = ConnexionAPI.parseFieldData(conAPI.listField(Resource.APINAME, Resource.TRANS_TITLECODEJ, 'O', user, ""));
                                ArrayList listParamCodeJ = conAPI.executeAPI(String.format("%-10s", Resource.APINAME), String.format("%-15s", Resource.TRANS_TITLECODEJ), in, user, "");
                                String code = "";
                                String desc = "";
                                for (Object listParamCodeJ1 : listParamCodeJ) {
                                    for (int j = 0; j < ((ArrayList) aOut.get(0)).size(); j++) {
                                        try {
                                            int begin = Integer.parseInt(((ArrayList) aOut.get(2)).get(j).toString()) - 1;
                                            int end = Integer.parseInt(((ArrayList) aOut.get(3)).get(j).toString());
                                            if (j == 0) {
                                                code = listParamCodeJ1.toString().substring(begin, end);
                                            } else {
                                                desc = listParamCodeJ1.toString().substring(begin, end);
                                            }
                                        } catch (NumberFormatException ex) {
                                        } catch (Exception e) {
                                        }
                                    }
                                    updateMessage(String.format(resourceMessage.getString("message.addtitlecode"), code.trim(), desc.trim()));
                                    TitleCodeParameter title = new TitleCodeParameter(code, desc, listType.get(1));
                                    prestationUI.getObservableListTitleParameter().add(title);

//callCodeParameter.add(new ListCodeParameter(ip, port, user, comboDivision.getValue(), title));
                                    listCodeParameter(title);
                                }
                            } else {
                                throw new RuntimeException(String.format(resourceMessage.getString("api.user"), conAPI.getUser()));
                            }
                        } else {
                            throw new RuntimeException(String.format(resourceMessage.getString("api.parameter")));
                        }
                        return null;
                    }
                };
            }
        };
        listPrestationParameterService.stateProperty().addListener((ObservableValue<? extends Worker.State> observableValue, Worker.State oldValue, Worker.State newValue) -> {
            switch (newValue) {
                case FAILED:
                    System.err.println(listPrestationParameterService.getException());
                    FxUtil.showAlert(AlertType.ERROR, resourceMessage.getString("api.title"), resourceMessage.getString("api.header"), listPrestationParameterService.getException().getMessage(), conAPI.getException());
                    notifier.notify(new Notification(resourceMessage.getString("message.titleListParameter"), String.format(resourceMessage.getString("message.nokListParameter"), comboPrestation.getValue().getName().trim(), comboPrestation.getValue().getCode().trim(), comboDivision.getValue().getName()), Notification.ERROR_ICON));
                    prestationUI.getObservableListTitleParameter().clear();
                    progressTable.setVisible(false);
                    messageTable.setVisible(false);
                    break;
                case CANCELLED:
                case SUCCEEDED:
                    if (prestationUI.getObservableListTitleParameter().size() != 0) {
                        listPrestationParameterUI();
                    }
                    break;
            }
        });
        messageTable.textProperty().bind(listPrestationParameterService.messageProperty());
        listPrestationParameterService.restart();
    }

    private void listCodeParameter(TitleCodeParameter titleCode) {
        String ip = comboEnvironment.getValue().getIP();
        int port = comboEnvironment.getValue().getPort();
        String user = comboEnvironment.getValue().getLogin();
        ConnexionAPI conAPICode = new ConnexionAPI(ip, port);
        if (conAPICode.getResult() != -1) {
            if (conAPICode.verifConnection(user, "")) {
                String in = String.format("%-15s", Resource.TRANS_PARAMCODEJ);
                in += String.format("%-3s", comboDivision.getValue().getDivi());
                in += String.format("%-10s", titleCode.getTitleCode().getValue());
                ArrayList aOut = ConnexionAPI.parseFieldData(conAPICode.listField(Resource.APINAME, Resource.TRANS_PARAMCODEJ, 'O', user, ""));
                ArrayList listParamCodeJ = conAPICode.executeAPI(String.format("%-10s", Resource.APINAME), String.format("%-15s", Resource.TRANS_PARAMCODEJ), in, user, "");
                Object[][] tableData = new Object[listParamCodeJ.size()][((ArrayList) aOut.get(0)).size()];
                for (int i = 0; i < listParamCodeJ.size(); i++) {
                    String code = "";
                    String desc = "";
                    String type = "";
                    for (int j = 5; j < 8; j++) {
                        try {
                            int begin = Integer.parseInt(((ArrayList) aOut.get(2)).get(j).toString()) - 1;
                            int end = Integer.parseInt(((ArrayList) aOut.get(3)).get(j).toString());
                            if (j == 5) {
                                code = listParamCodeJ.get(i).toString().substring(begin, end).trim();
                            } else if (j == 7) {
                                desc = listParamCodeJ.get(i).toString().substring(begin, end).trim();
                            } else if (j == 6) {
                                type = listParamCodeJ.get(i).toString().substring(begin, end).trim();
                            }
                        } catch (NumberFormatException ex) {
                        } catch (Exception e) {
                        }
                    }
                    CodeParameter parameterCode = new CodeParameter(code, desc, type);
                    titleCode.getListParameter().add(parameterCode);

                    listAvailableValue(titleCode, parameterCode);
                }
            } else {
                throw new RuntimeException(String.format(resourceMessage.getString("api.user"), (Object) new String[]{conAPICode.getUser()}));
            }
        } else {
            throw new RuntimeException(String.format(resourceMessage.getString("api.parameter")));
        }
    }

    private void listCodeParameterService(TitleCodeParameter titleCode) {
        final Service<Void> listCodeParameterService = new Service<Void>() {

            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        String ip = comboEnvironment.getValue().getIP();
                        int port = comboEnvironment.getValue().getPort();
                        String user = comboEnvironment.getValue().getLogin();
                        ConnexionAPI conAPI = new ConnexionAPI(ip, port);
                        if (conAPI.getResult() != -1) {
                            if (conAPI.verifConnection(user, "")) {
                                String in = String.format("%-15s", Resource.TRANS_PARAMCODEJ);
                                in += String.format("%-3s", comboDivision.getValue().getDivi());
                                in += String.format("%-10s", titleCode.getTitleCode().getValue());
                                ArrayList aOut = ConnexionAPI.parseFieldData(conAPI.listField(Resource.APINAME, Resource.TRANS_PARAMCODEJ, 'O', user, ""));
                                ArrayList listParamCodeJ = conAPI.executeAPI(String.format("%-10s", Resource.APINAME), String.format("%-15s", Resource.TRANS_PARAMCODEJ), in, user, "");
                                Object[][] tableData = new Object[listParamCodeJ.size()][((ArrayList) aOut.get(0)).size()];
                                for (int i = 0; i < listParamCodeJ.size(); i++) {
                                    String code = "";
                                    String desc = "";
                                    String type = "";
                                    for (int j = 5; j < 8; j++) {
                                        try {
                                            int begin = Integer.parseInt(((ArrayList) aOut.get(2)).get(j).toString()) - 1;
                                            int end = Integer.parseInt(((ArrayList) aOut.get(3)).get(j).toString());
                                            if (j == 5) {
                                                code = listParamCodeJ.get(i).toString().substring(begin, end).trim();
                                            } else if (j == 7) {
                                                desc = listParamCodeJ.get(i).toString().substring(begin, end).trim();
                                            } else if (j == 6) {
                                                type = listParamCodeJ.get(i).toString().substring(begin, end).trim();
                                            }
                                        } catch (NumberFormatException ex) {
                                        } catch (Exception e) {
                                        }
                                    }
                                    CodeParameter parameterCode = new CodeParameter(code, desc, type);
                                    titleCode.getListParameter().add(parameterCode);

                                    listAvailableValueService(titleCode, parameterCode);
                                }
                            } else {
                                throw new RuntimeException(String.format(resourceMessage.getString("api.user"), (Object) new String[]{conAPI.getUser()}));
                            }
                        } else {
                            throw new RuntimeException(String.format(resourceMessage.getString("api.parameter")));
                        }
                        return null;
                    }
                };
            }
        };
        listCodeParameterService.stateProperty().addListener((ObservableValue<? extends Worker.State> observableValue, Worker.State oldValue, Worker.State newValue) -> {
            switch (newValue) {
                case FAILED:
                    System.err.println(listCodeParameterService.getException());
                    break;
                case CANCELLED:
                case SUCCEEDED:
                    break;
            }
        });
        listCodeParameterService.restart();
    }

    private void listAvailableValue(TitleCodeParameter title, CodeParameter parameter) {
        String ip = comboEnvironment.getValue().getIP();
        int port = comboEnvironment.getValue().getPort();
        String user = comboEnvironment.getValue().getLogin();
        ConnexionAPI conAPIValue = new ConnexionAPI(ip, port);
        if (conAPIValue.getResult() != -1) {
            if (conAPIValue.verifConnection(user, "")) {
                String in = String.format("%-15s", Resource.TRANS_VALUEENTITY);
                in += String.format("%-3s", comboDivision.getValue().getDivi());
                in += String.format("%-10s", parameter.getParameterCode().getValue());
                ArrayList aOut = ConnexionAPI.parseFieldData(conAPIValue.listField(Resource.APINAME, Resource.TRANS_VALUEENTITY, 'O', user, ""));
                ArrayList listParamCodeJ = conAPIValue.executeAPI(String.format("%-10s", Resource.APINAME), String.format("%-15s", Resource.TRANS_VALUEENTITY), in, user, "");
                String code = "";
                String titleValue = "";
                String value = "";
                countParmeter += listParamCodeJ.size();
                for (int l = 0; l < listParamCodeJ.size(); l++) {
                    String listParamCodeJ1 = listParamCodeJ.get(l).toString();
                    for (int k = 1; k < ((ArrayList) aOut.get(0)).size(); k++) {
                        try {
                            int begin = Integer.parseInt(((ArrayList) aOut.get(2)).get(k).toString()) - 1;
                            int end = Integer.parseInt(((ArrayList) aOut.get(3)).get(k).toString());
                            if (k == 1) {
                                code = listParamCodeJ1.substring(begin, end);
                            } else if (k == 2) {
                                titleValue = listParamCodeJ1.substring(begin, end);
                            } else if (k == 3) {
                                value = listParamCodeJ1.substring(begin, end);
                            }
                        } catch (NumberFormatException ex) {
                        } catch (Exception e) {
                        }
                    }
                    AvailableValue availableValue = new AvailableValue(code, titleValue, value);

                    boolean test = titleValue.trim().isEmpty();
                    for (int k = 0; k < title.getListAvailableValue().size(); k++) {
                        if (title.getListAvailableValue().get(k).getAvailableTitle().getValue().trim().equals(titleValue.trim())) {
                            test = true;
                            break;
                        }
                    }
                    if (!test) {
                        title.getListAvailableValue().add(availableValue);
                    }
                    cptParameterCode++;
                }

            } else {
                throw new RuntimeException(String.format(resourceMessage.getString("api.user"), conAPIValue.getUser()));
            }
        } else {
            throw new RuntimeException(String.format(resourceMessage.getString("api.parameter")));
        }

    }

    private void listAvailableValueService(TitleCodeParameter title, CodeParameter parameter) {
        parameterTable.setDisable(true);
        progressTable.setVisible(true);
        messageTable.setVisible(true);

        final Service<Void> listAvailableValueService = new Service<Void>() {

            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {

                        String ip = comboEnvironment.getValue().getIP();
                        int port = comboEnvironment.getValue().getPort();
                        String user = comboEnvironment.getValue().getLogin();
                        ConnexionAPI conAPIValue = new ConnexionAPI(ip, port);
                        if (conAPIValue.getResult() != -1) {
                            if (conAPIValue.verifConnection(user, "")) {
                                String in = String.format("%-15s", Resource.TRANS_VALUEENTITY);
                                in += String.format("%-3s", comboDivision.getValue().getDivi());
                                in += String.format("%-10s", parameter.getParameterCode().getValue());
                                ArrayList aOut = ConnexionAPI.parseFieldData(conAPIValue.listField(Resource.APINAME, Resource.TRANS_VALUEENTITY, 'O', user, ""));
                                ArrayList listParamCodeJ = conAPIValue.executeAPI(String.format("%-10s", Resource.APINAME), String.format("%-15s", Resource.TRANS_VALUEENTITY), in, user, "");
                                String code = "";
                                String titleValue = "";
                                String value = "";
                                countParmeter += listParamCodeJ.size();
                                for (int l = 0; l < listParamCodeJ.size(); l++) {
                                    String listParamCodeJ1 = listParamCodeJ.get(l).toString();
                                    for (int k = 1; k < ((ArrayList) aOut.get(0)).size(); k++) {
                                        try {
                                            int begin = Integer.parseInt(((ArrayList) aOut.get(2)).get(k).toString()) - 1;
                                            int end = Integer.parseInt(((ArrayList) aOut.get(3)).get(k).toString());
                                            if (k == 1) {
                                                code = listParamCodeJ1.substring(begin, end);
                                            } else if (k == 2) {
                                                titleValue = listParamCodeJ1.substring(begin, end);
                                            } else if (k == 3) {
                                                value = listParamCodeJ1.substring(begin, end);
                                            }
                                        } catch (NumberFormatException ex) {
                                        } catch (Exception e) {
                                        }
                                    }
                                    AvailableValue availableValue = new AvailableValue(code, titleValue, value);

                                    boolean test = false;
                                    for (int k = 0; k < title.getListAvailableValue().size(); k++) {
                                        if (title.getListAvailableValue().get(k).getAvailableTitle().getValue().trim().equals(titleValue.trim()) || titleValue.trim().isEmpty()) {
                                            test = true;
                                            break;
                                        }
                                    }
                                    if (!test) {
                                        title.getListAvailableValue().add(availableValue);
                                    }
                                    cptParameterCode++;
                                }

                            } else {
                                throw new RuntimeException(String.format(resourceMessage.getString("api.user"), conAPIValue.getUser()));
                            }
                        } else {
                            throw new RuntimeException(String.format(resourceMessage.getString("api.parameter")));
                        }
                        return null;
                    }
                };
            }

        };

        listAvailableValueService.stateProperty()
                .addListener((ObservableValue<? extends Worker.State> observableValue, Worker.State oldValue, Worker.State newValue) -> {
                    switch (newValue) {
                        case FAILED:
                            System.err.println(listAvailableValueService.getException());
                            break;
                        case CANCELLED:
                        case SUCCEEDED:
                            if (countParmeter != 0 && countParmeter == cptParameterCode) {
                                listPrestationParameterUI();
                            }
                            break;
                    }
                }
                );
        listAvailableValueService.restart();
    }

    private void listPrestationParameterUI() {
        countParmeter = 0;
        parameterTable.setDisable(true);
        progressTable.setVisible(true);
        messageTable.setVisible(true);

        final Service<Void> listPrestationParameterUIService = new Service<Void>() {

            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        String path = comboEnvironment.getValue().getPathMOM() + System.getProperty("file.separator") + Resource.pathMOMTable + System.getProperty("file.separator") + Resource.baseFileName + "_" + comboDivision.getValue().getDivi() + "_" + comboPrestation.getValue().getCode().trim() + Resource.extFile;
                        if (new File(path).exists()) {
                            HashMap<String, String[]> param = MOMFile.readAllLine(Paths.get(path));
                            HashMap<String, String[]> paramCondition = MOMFile.extractKeyBegin("PARAMETER_", param);
                            for (TitleCodeParameter titleParameter : prestationUI.getObservableListTitleParameter()) {
                                if (param.containsKey("TITLEKEY_" + titleParameter.getTitleCode().getValue().trim())) {
                                    Object[] o = param.get("TITLEKEY_" + titleParameter.getTitleCode().getValue().trim());
                                    titleParameter.getTitleType().set(listType.get(tools.Tools.convertToInt(o[1].toString())));
                                    titleParameter.getTablePadding().set(tools.Tools.convertToInt(o[2].toString()));
                                    titleParameter.getTableParameterColumnWidth().set(tools.Tools.convertToInt(o[3].toString()));
                                    titleParameter.getTableLineColor().set(ColorFontTools.getColor(o[4].toString()));

                                    int i = 0;
                                    boolean test = true;
                                    while (test) {
                                        if (param.containsKey("PARAMVALUE_" + titleParameter.getTitleCode().getValue().trim() + "_" + i)) {
                                            Object[] o1 = param.get("PARAMVALUE_" + titleParameter.getTitleCode().getValue().trim() + "_" + i);
                                            for (int j = 0; j < titleParameter.getListAvailableValue().size(); j++) {
                                                if (o1[0].toString().trim().equals(titleParameter.getListAvailableValue().get(j).getAvailableTitle().getValue().trim())) {
                                                    titleParameter.getListAvailableValue().get(j).getAvailableValue().setValue(o1[1].toString());
                                                    titleParameter.getListAvailableValue().get(j).getAvailablecolor().setValue(ColorFontTools.getColor(o1[2].toString().trim()));
                                                    break;
                                                }
                                            }
                                            i++;
                                        } else {
                                            test = false;
                                        }
                                    }

                                    paramCondition.keySet().stream().filter((mapKey) -> (paramCondition.get(mapKey)[0].trim().equals(titleParameter.getTitleCode().getValue().trim()))).map((mapKey) -> {
                                        Object[] o2 = paramCondition.get(mapKey);
                                        String typeData = "";
                                        String type = o2[1].toString();
                                        for (CodeParameter p : titleParameter.getListParameter()) {
                                            if (p.getParameterCode().getValue().trim().equals(mapKey.split("_")[1])) {
                                                typeData = p.getParameterType().getValue();
                                                break;
                                            }
                                        }
                                        Condition condition = new Condition(mapKey.split("_")[1], typeData, type, o2[2].toString());
                                        condition.getConditionType().setValue("" + type);
                                        condition.getConditionColor().setValue(ColorFontTools.getColor(o2[3].toString()));
                                        if (o2.length >= 5) {
                                            condition.getSubstitutionValue().set(o2[4].toString());
                                        }
                                        return condition;
                                    }).forEach((condition) -> {
                                        titleParameter.getListCondition().add(condition);
                                    });
                                } else {
                                    new RuntimeException("Pas de donnée pour " + titleParameter.getTitleCode().getValue());
                                }

                            }
                        }
                        return null;
                    }
                };
            }
        };
        listPrestationParameterUIService.stateProperty().addListener((ObservableValue<? extends Worker.State> observableValue, Worker.State oldValue, Worker.State newValue) -> {
            switch (newValue) {
                case FAILED:
                    System.err.println(listPrestationParameterUIService.getException());
                    break;
                case CANCELLED:
                case SUCCEEDED:
                    if (prestationUI.getObservableListTitleParameter().size() != 0) {
                        notifier.notify(new Notification(resourceMessage.getString("message.titleListParameter"), String.format(resourceMessage.getString("message.okListParameter"), comboPrestation.getValue().getName().trim(), comboPrestation.getValue().getCode().trim(), comboDivision.getValue().getName()), Notification.SUCCESS_ICON));
                    } else {
                        notifier.notify(new Notification(resourceMessage.getString("message.titleListParameter"), String.format(resourceMessage.getString("message.noListParameter"), comboPrestation.getValue().getName().trim(), comboPrestation.getValue().getCode().trim(), comboDivision.getValue().getName()), Notification.WARNING_ICON));
                    }
                    break;
            }
            progressTable.setVisible(false);
            messageTable.setVisible(false);
            parameterTable.setDisable(false);
            isGenerate.set(false);
        });
        listPrestationParameterUIService.restart();
    }

    @FXML
    private void newEnvironment() {
        Environment tempEnvironment = new Environment();
        boolean okClicked = mainApp.getRootController().showEnvironmentEditDialog(tempEnvironment);
        if (okClicked) {
            mainApp.getEnvironmentData().add(tempEnvironment);
        }
    }

    @FXML
    private void newDivision() {
        Division tempDivision = new Division();
        boolean okClicked = mainApp.getRootController().showDivisionEditDialog(tempDivision);
        if (okClicked) {
            mainApp.getDivisionData().add(tempDivision);
        }
    }

    @FXML
    private void onMousePressed(MouseEvent event) {
        if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
            accessParameter();
        }
    }

    @FXML
    private void accessParameter() {
        TitleCodeParameter selectedTitle = parameterTable.getSelectionModel().getSelectedItem();
        if (selectedTitle != null) {
            showManageParameter(selectedTitle);
        } else {
            FxUtil.showAlert(AlertType.NONE, "Erreur", null, null);
        }
    }

    public void showManageParameter(TitleCodeParameter titleCodeParam) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(ResourceBundle.getBundle("resources.language", mainApp.getLocale()));
            loader.setLocation(MainApp.class.getResource("view/ParameterManagement.fxml"));
            StackPane page = (StackPane) loader.load();

            Stage dialogStage = new Stage();

            dialogStage.setTitle(String.format(resourceMessage.getString("parameter.management"), titleCodeParam.getTitleCode().get().trim() + ": " + titleCodeParam.getTitleDescription().get()));
            dialogStage.getIcons().add(Resource.LOGO_ICON);

            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            ParameterManagementController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setDivision(comboDivision.getValue());
            controller.setEnvironment(comboEnvironment.getValue());
            controller.setTitleCodeParameter(titleCodeParam);
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean showListParameter(TitleCodeParameter titleCodeParam) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(ResourceBundle.getBundle("resources.language", mainApp.getLocale()));
            loader
                    .setLocation(MainApp.class
                            .getResource("view/HistoTablesUI.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();

            dialogStage.setTitle(String.format(resourceMessage.getString("parameter.management"), titleCodeParam.getTitleCode().get().trim() + ": " + titleCodeParam.getTitleDescription().get()));
            dialogStage.initModality(Modality.WINDOW_MODAL);

            dialogStage.getIcons().add(Resource.LOGO_ICON);
            Scene scene = new Scene(page);

            dialogStage.setScene(scene);

            HistoTableUIController controller = loader.getController();

            controller.setDialogStage(dialogStage);

            controller.setTitleCodeParameter(titleCodeParam);

            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void showConditionParameter(TitleCodeParameter titleCodeParam) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(ResourceBundle.getBundle("resources.language", mainApp.getLocale()));
            loader
                    .setLocation(MainApp.class
                            .getResource("view/TableConditionUI.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();

            dialogStage.setTitle(String.format(resourceMessage.getString("parameter.management"), titleCodeParam.getTitleCode().get().trim() + ": " + titleCodeParam.getTitleDescription().get()));
            dialogStage.initModality(Modality.WINDOW_MODAL);

            dialogStage.getIcons().add(Resource.LOGO_ICON);
            Scene scene = new Scene(page);

            dialogStage.setScene(scene);

            TableConditionUIController controller = loader.getController();

            controller.setDivision(comboDivision.getValue());
            controller.setEnvironment(comboEnvironment.getValue());
            controller.setTitleCodeParameter(titleCodeParam);

            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showAvailableValuesParameter(TitleCodeParameter titleCodeParam) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(ResourceBundle.getBundle("resources.language", mainApp.getLocale()));
            loader.setLocation(MainApp.class.getResource("view/TableAvailableValueUI.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();

            dialogStage.setTitle(String.format(resourceMessage.getString("parameter.management"), titleCodeParam.getTitleCode().get().trim() + ": " + titleCodeParam.getTitleDescription().get()));
            dialogStage.initModality(Modality.WINDOW_MODAL);

            dialogStage.getIcons()
                    .add(Resource.LOGO_ICON);
            Scene scene = new Scene(page);

            dialogStage.setScene(scene);

            TableAvailableValueUIController controller = loader.getController();

            controller.setTitleCodeParameter(titleCodeParam);

            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void processPrestation() {
        ProgressDialogController progressControl = mainApp.showProgressDialog();
        final Service<Void> processPrestationService;
        processPrestationService = new Service<Void>() {

            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        if (divisionUI != null) {
                            updateMessage(String.format(resourceMessage.getString("message.divisiondata"), divisionUI.getDivi()));
                            String divi = divisionUI.getDivi().getValue();
                            String path = comboEnvironment.getValue().getPathMOM() + System.getProperty("file.separator") + Resource.pathMOMTable + System.getProperty("file.separator") + Resource.baseFileName + "_" + divi + Resource.extFile;
                            Path p = MOMFile.createMOMFile(path);
                            MOMFile.addLine(p, DivisionUI.listDivisionKey[0] + "\t" + divisionUI.getFormatFont().getValue());
                            MOMFile.addLine(p, DivisionUI.listDivisionKey[1] + "\t" + divisionUI.getFontSize().getValue());
                            MOMFile.addLine(p, DivisionUI.listDivisionKey[2] + "\t" + ColorFontTools.getRGB(divisionUI.getFontColor().getValue()));
                            MOMFile.addLine(p, DivisionUI.listDivisionKey[3] + "\t" + ColorFontTools.getRGB(divisionUI.getFontColorAlertnative().getValue()));
                            MOMFile.addLine(p, DivisionUI.listDivisionKey[4] + "\t" + divisionUI.getLogoL().getValue());
                            MOMFile.addLine(p, DivisionUI.listDivisionKey[5] + "\t" + divisionUI.getLogoH().getValue());
                            
                        }
                        if (prestationUI != null) {
                            updateMessage(String.format(resourceMessage.getString("message.prestationdata"), prestationUI.getPrestation().getValue().getName()));
                            String divi = divisionUI.getDivi().getValue();
                            String path = comboEnvironment.getValue().getPathMOM() + System.getProperty("file.separator") + Resource.pathMOMTable + System.getProperty("file.separator") + Resource.baseFileName + "_" + divi + "_" + prestationUI.getPrestation().getValue().getCode().trim() + Resource.extFile;
                            Path p = MOMFile.createMOMFile(path);
                            MOMFile.addLine(p, PrestationUI.listKeyPrestation[0] + "\t" + prestationUI.getTitleShape().getValue());
                            MOMFile.addLine(p, PrestationUI.listKeyPrestation[1] + "\t" + prestationUI.getTitleFontSize().getValue());
                            MOMFile.addLine(p, PrestationUI.listKeyPrestation[2] + "\t" + ColorFontTools.getRGB(prestationUI.getFontTitleColor().getValue()));
                            MOMFile.addLine(p, PrestationUI.listKeyPrestation[3] + "\t" + ColorFontTools.getRGB(prestationUI.getForegroundTitleColor().getValue()));
                            MOMFile.addLine(p, PrestationUI.listKeyPrestation[4] + "\t" + ColorFontTools.getRGB(prestationUI.getBorderTitleColor().getValue()));
                            MOMFile.addLine(p, PrestationUI.listKeyPrestation[5] + "\t" + prestationUI.getBlockShape().getValue());
                            MOMFile.addLine(p, PrestationUI.listKeyPrestation[6] + "\t" + ColorFontTools.getRGB(prestationUI.getForegroundBlockColor().getValue()));
                            MOMFile.addLine(p, PrestationUI.listKeyPrestation[7] + "\t" + ColorFontTools.getRGB(prestationUI.getBorderBlockColor().getValue()));
                            MOMFile.addLine(p, PrestationUI.listKeyPrestation[8] + "\t" + tools.Tools.convertBooleanToInt(prestationUI.getDoctor().getValue()));
                            MOMFile.addLine(p, PrestationUI.listKeyPrestation[9] + "\t" + tools.Tools.convertBooleanToInt(prestationUI.getPrescriberInit().getValue()));
                            MOMFile.addLine(p, PrestationUI.listKeyPrestation[10] + "\t" + tools.Tools.convertBooleanToInt(prestationUI.getPrescriber().getValue()));
                            MOMFile.addLine(p, PrestationUI.listKeyPrestation[11] + "\t" + tools.Tools.convertBooleanToInt(prestationUI.getTreatDoctor().getValue()));
                            MOMFile.addLine(p, PrestationUI.listKeyPrestation[12] + "\t" + tools.Tools.convertBooleanToInt(prestationUI.getTechnician().getValue()));
                            MOMFile.addLine(p, PrestationUI.listKeyPrestation[13] + "\t" + tools.Tools.convertBooleanToInt(prestationUI.getTechnicianTel().getValue()));
                            MOMFile.addLine(p, PrestationUI.listKeyPrestation[14] + "\t" + tools.Tools.convertBooleanToInt(prestationUI.getTechnicianAgency().getValue()));
                            MOMFile.addLine(p, PrestationUI.listKeyPrestation[15] + "\t" + tools.Tools.convertBooleanToInt(prestationUI.getNurse().getValue()));
                            MOMFile.addLine(p, PrestationUI.listKeyPrestation[16] + "\t" + prestationUI.getLogoL().getValue());
                            MOMFile.addLine(p, PrestationUI.listKeyPrestation[17] + "\t" + prestationUI.getLogoH().getValue());

                            ArrayList<ArrayList<String>> listTitleKey = new ArrayList<>();
                            ArrayList<ArrayList<String>> listConditionKey = new ArrayList<>();
                            ArrayList<ArrayList<String>> listValuesKey = new ArrayList<>();
                            updateMessage(String.format(resourceMessage.getString("message.prestationdata"), prestationUI.getPrestation().getValue().getName()));
                            if (!prestationUI.getObservableListTitleParameter().isEmpty()) {
                                for (TitleCodeParameter listTitleCodeJ : prestationUI.getObservableListTitleParameter()) {
                                    ArrayList<String> o = new ArrayList<>();
                                    o.add("TITLEKEY_" + listTitleCodeJ.getTitleCode().getValue().trim());
                                    o.add(listTitleCodeJ.getTitleDescription().getValue());
                                    o.add("" + listType.indexOf(listTitleCodeJ.getTitleType().getValue()));
                                    o.add(listTitleCodeJ.getTablePadding().getValue().toString());
                                    o.add(listTitleCodeJ.getTableParameterColumnWidth().getValue().toString());
                                    o.add(ColorFontTools.getRGB(listTitleCodeJ.getTableLineColor().getValue()));
                                    listTitleKey.add((ArrayList<String>) o.clone());

                                    String currentCode = "";
                                    int cpt = 0;
                                    if (!listTitleCodeJ.getListCondition().isEmpty()) {
                                        HashMap<String, Integer> counter = new HashMap<>();
                                        for (Condition cond : listTitleCodeJ.getListCondition()) {
                                            ArrayList<String> oCond = new ArrayList<>();
                                            if (!counter.containsKey(cond.getConditionCode().getValue())) {
                                                cpt = 0;
                                            } else {
                                                cpt = counter.get(cond.getConditionCode().getValue()) + 1;
                                            }
                                            counter.put(cond.getConditionCode().getValue(), cpt);
                                            if (!cond.getConditionCode().getValue().trim().equals(currentCode)) {
                                            }
                                            oCond.add("PARAMETER_" + cond.getConditionCode().getValue().trim() + "_" + cpt);
                                            oCond.add(listTitleCodeJ.getTitleCode().getValue().trim());
                                            oCond.add(cond.getConditionType().getValue());
                                            oCond.add(cond.getConditionValue().getValue());
                                            oCond.add(ColorFontTools.getRGB(cond.getConditionColor().getValue()));
                                            oCond.add((cond.getSubstitutionValue().getValue() == null) ? "" : cond.getSubstitutionValue().getValue());

                                            listConditionKey.add((ArrayList<String>) oCond.clone());
                                        }

                                    }
                                    cpt = 0;
                                    if (!listTitleCodeJ.getListAvailableValue().isEmpty()) {
                                        for (AvailableValue value : listTitleCodeJ.getListAvailableValue()) {
                                            ArrayList<String> oValue = new ArrayList<>();

                                            oValue.add("PARAMVALUE_" + listTitleCodeJ.getTitleCode().getValue().trim() + "_" + cpt);
                                            oValue.add(value.getAvailableTitle().getValue());
                                            oValue.add(value.getAvailableValue().getValue());
                                            oValue.add(ColorFontTools.getRGB(value.getAvailablecolor().getValue()));

                                            listValuesKey.add((ArrayList<String>) oValue.clone());
                                            cpt++;
                                        }

                                    }
                                }
                                listTitleKey.stream().forEach((val) -> {
                                    MOMFile.addLine(p, val);
                                });
                                listConditionKey.stream().forEach((val) -> {
                                    MOMFile.addLine(p, val);
                                });
                                listValuesKey.stream().forEach((val) -> {
                                    MOMFile.addLine(p, val);
                                });
                            }
                        }
                        return null;
                    }
                };
            }

        };

        processPrestationService.stateProperty().addListener((ObservableValue<? extends Worker.State> observableValue, Worker.State oldValue, Worker.State newValue) -> {
            switch (newValue) {
                case FAILED:
                    FxUtil.showAlert(AlertType.ERROR, resourceMessage.getString("message.run"), resourceMessage.getString("message.run"), resourceMessage.getString("message.runerror") + "\n" + processPrestationService.getException().getMessage());
                    progressControl.getLabel().textProperty().unbind();
                    progressControl.getLabel().setText(processPrestationService.getException().getLocalizedMessage());
                    progressControl.getStage().close();
                    break;
                case CANCELLED:
                    break;
                case RUNNING:
                    break;
                case SUCCEEDED:
                    progressControl.getLabel().textProperty().unbind();
                    restartService(progressControl);
                    break;
            }
        }
        );
        progressControl.getStage().show();
        progressControl.getLabel().textProperty().bind(processPrestationService.messageProperty());
        processPrestationService.restart();
    }

    private void restartService(ProgressDialogController progressControl) {
        ManageWindowsService manageService = new ManageWindowsService(comboEnvironment.getValue());
        manageService.stateProperty().addListener((ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) -> {
            switch (newValue) {
                case FAILED:
                    System.err.println(manageService.getException());
                    break;
                case CANCELLED:
                    break;
                case RUNNING:
                    break;
                case SUCCEEDED:
                    progressControl.getStage().close();
                    Alert alert = new Alert(AlertType.CONFIRMATION);
                    alert.setTitle(resourceMessage.getString("m3.runreport"));
                    alert.setContentText(resourceMessage.getString("m3.runreportquestion"));

                    ButtonType buttonTypeYes = new ButtonType(resourceMessage.getString("main.yes"));
                    ButtonType buttonTypeNo = new ButtonType(resourceMessage.getString("main.no"), ButtonData.CANCEL_CLOSE);

                    alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == buttonTypeYes) {
                        mainApp.showRunReportDialog();
                    }
                    alert.close();
                    break;
            }
        });
        progressControl.getLabel().textProperty().bind(manageService.messageProperty());
        manageService.restart();
    }
}
