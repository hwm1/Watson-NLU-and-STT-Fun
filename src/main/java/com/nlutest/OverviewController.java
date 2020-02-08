package com.nlutest;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class OverviewController implements Initializable {

    private Stage stage;
    private Scene scene;

    @FXML
    private AnchorPane ovroot;

    @FXML
    private PieChart pieChart;

    @FXML
    private TextArea textArea;

    @FXML
    private Button button1;

    @FXML
    private Label label1;

    private NLUtest nlutest;

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param nlutest
     */
    void setMainApp(NLUtest nlutest) {
        this.nlutest = nlutest;
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

    }

    //decorate the stage
    void drawLines(Stage sstage) {
        scene = ovroot.getScene();
        stage = (Stage) scene.getWindow();

        // draw lines on screen
        double h = stage.getHeight() * 0.66;
        Line line1 = new Line();
        line1.setStartX(0);
        line1.setStartY(h);
        line1.setEndX(stage.getWidth());
        line1.setEndY(h);
        ovroot.getChildren().add(line1);
        Line line2 = new Line();
        line2.setStartX(stage.getWidth() / 2);
        line2.setEndX(stage.getWidth() / 2);
        line2.setStartY(0);
        line2.setEndY(h);
        ovroot.getChildren().add(line2);

    }

    //write data to pie chart
    @FXML
    void changePieData(ObservableList<PieChart.Data> pieChartData) {

        pieChartData.forEach(data
                -> data.nameProperty().bind(
                        Bindings.concat(
                                //          data.getName(), " ", data.pieValueProperty(), " %"
                                data.getName(), " ", Math.round(data.getPieValue() * 10.0) / 10.0, " %")) //round to 1 dec place
        );
        //     ovroot.layout();
        pieChart.setData(pieChartData);
        pieChart.setVisible(true);

    }

    void enableButton(Boolean b) {
        button1.setDisable(!b);
    }

    void setButtonText(String msg) {
        button1.setText(msg);

    }

    //write to text area
    void setTextArea(String text) {

        textArea.setText(text);
    }

    void setLabel(String s) {

        label1.setText(s);
    }

    @FXML
    void handleButton1() {
        if (button1.getText().startsWith("Stop")) {
            Shared.stopRecording = true;
        } else {
            button1.setText("Stop");
            nlutest.startRecorderService();
        }
    }

    void showPieChart(Boolean b) {
        pieChart.setVisible(b);
    }

    void showMessage(String msg) {

        textArea.setText(msg);
    }

}
