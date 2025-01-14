package BusinessLayer.OtherControllers;

import BusinessLayer.Models.Component;
import BusinessLayer.Models.Components.ClassDiagramComponents.Association;
import BusinessLayer.Models.Components.ClassDiagramComponents.Class;
import BusinessLayer.Models.Components.ClassDiagramComponents.Function;
import BusinessLayer.Models.Components.ClassDiagramComponents.Interface;
import BusinessLayer.Models.Diagrams.ClassDiagram;
import BusinessLayer.Models.Point;
import BusinessLayer.PageControllers.ProjectPageController;
import BusinessLayer.Models.Components.ClassDiagramComponents.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.canvas.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClassDiagramController {

    private static final Logger LOGGER = Logger.getLogger(ClassDiagramController.class.getName());

    public ProjectPageController projectPageController;
    public ClassDiagram classDiagram;
    @FXML
    public Label model_name;

    @FXML
    public Pane drawingPane;
    private Canvas canvas;
    private GraphicsContext graphicsContext;
    private String activeTool = null;
    private Map<Node, Object> elementMap = new HashMap<>();
    private double mouseX, mouseY;
    private Line tempLine = null;
    private Point initialPoint = null;
    private Node selectedNode = null;
    private Point initialMousePosition = null;

    public void canvasInitialize(){
        canvas = new Canvas(drawingPane.getWidth(), drawingPane.getHeight());
        graphicsContext = canvas.getGraphicsContext2D();
        drawingPane.getChildren().add(canvas);
        drawingPane.setOnMouseMoved(this::trackMouse);
        drawingPane.setOnMouseClicked(this::handleDrawingCanvasClicked);
        drawingPane.setOnMousePressed(this::handleMousePressed);
        drawingPane.setOnMouseDragged(this::handleMouseDragged);
        drawingPane.setOnMouseReleased(this::handleMouseReleased);
    }

     public void setDrawingPane(Pane drawingPane){
        this.drawingPane = drawingPane;
    }
    /*
    *
    * Handle functions
    *
    */
    private void handleMousePressed(MouseEvent event) {
        System.out.println("handleMousePressed");
        double x = event.getX();
        double y = event.getY();

        if(Objects.equals(activeTool, "Association")
                || Objects.equals(activeTool, "Inheritance")
                || Objects.equals(activeTool, "Aggregation")
                || Objects.equals(activeTool, "Composition")
                || Objects.equals(activeTool, "DashedLine")){
            initialPoint = new Point(x, y);
            tempLine = new Line(initialPoint.getX(), initialPoint.getY(), x, y);
            tempLine.getStrokeDashArray().addAll(5.0, 5.0);
            drawingPane.getChildren().add(tempLine);
            return;
        }

        for (Map.Entry<Node, Object> entry : elementMap.entrySet()) {
            Node node = entry.getKey();
            if (isWithinBounds(node, x, y)) {
                selectedNode = node;
                initialMousePosition = new Point(x, y);
                System.out.println("Selected component has id: "+((Component)entry.getValue()).getId());
                System.out.println("Mouse position: "+initialMousePosition.getX()+", "+initialMousePosition.getY());
                return;
            }
        }
    }

    private void handleMouseDragged(MouseEvent event) {
        //System.out.println("mouse drag event");
        if (tempLine != null) {
            tempLine.setEndX(event.getX());
            tempLine.setEndY(event.getY());
            return;
        } else {
            System.out.println("tempLine is null");
        }

        if (selectedNode != null && initialMousePosition != null) {
            double deltaX = event.getX() - initialMousePosition.getX();
            double deltaY = event.getY() - initialMousePosition.getY();
            selectedNode.setLayoutX(selectedNode.getLayoutX() + deltaX);
            selectedNode.setLayoutY(selectedNode.getLayoutY() + deltaY);

            Object element = elementMap.get(selectedNode);
            Component component = (Component) element;
            if(component instanceof Class ||
                    component instanceof Interface ||
                    component instanceof TextBox){
                component.setX(component.getX()+deltaX);
                component.setY(component.getY()+deltaY);
            }

            redrawCanvas();
            initialMousePosition.setX(event.getX());
            initialMousePosition.setY(event.getY());
        } else{
            if(selectedNode==null){
                System.out.println("selectedNode is null");
            } else if(initialMousePosition==null){
                System.out.println("initialMousePosition is null");
            }
        }
    }


    private void handleMouseReleased(MouseEvent event) {
        //System.out.println("mouse release event");
        if (tempLine != null) {
            drawingPane.getChildren().remove(tempLine);
            tempLine = null;
            Point finalPoint = new Point(event.getX(), event.getY());
            if(Objects.equals(activeTool, "Association")){
                drawAssociation(null, initialPoint, finalPoint);
            } else if(Objects.equals(activeTool, "DashedLine")){
                drawDashedLine(null, initialPoint, finalPoint);
            } else if(Objects.equals(activeTool, "Inheritance")){
                drawInheritance(null, initialPoint, finalPoint);
            } else if(Objects.equals(activeTool, "Aggregation")){
                drawAggregation(null, initialPoint, finalPoint);
            } else if(Objects.equals(activeTool, "Composition")){
                drawComposition(null, initialPoint, finalPoint);
            }
            activeTool = null;
        }
        //selectedNode = null;
        initialMousePosition = null;
    }

    private void handleDrawingCanvasClicked(MouseEvent event) {
        System.out.println("handleDrawingCanvasClicked");
        double x = event.getX();
        double y = event.getY();
        for (Component component : classDiagram.getComponents()) {
            if(component instanceof Association){
                if (isNearLine((getLineFromAssociation((Association) component)), x, y)) {
                    if (event.getClickCount() == 2) {
                        showAssociationDetailsForm((Association) component);
                    }
                    return;
                }
            } else if(component instanceof Inheritance){
                if (isNearLine((getLineFromInheritance((Inheritance) component)), x, y)) {
                    if (event.getClickCount() == 2) {
                        showInheritanceDetailsForm((Inheritance) component);
                    }
                    return;
                }
            } else if(component instanceof Aggregation){
                if (isNearLine((getLineFromAggregation((Aggregation) component)), x, y)) {
                    if (event.getClickCount() == 2) {
                        showAggregationDetailsForm((Aggregation) component);
                    }
                    return;
                }
            } else if(component instanceof Composition){
                if (isNearLine((getLineFromComposition((Composition) component)), x, y)) {
                    if (event.getClickCount() == 2) {
                        showCompositionDetailsForm((Composition) component);
                    }
                    return;
                }
            }
        }

        for (Map.Entry<Node, Object> entry : elementMap.entrySet()) {
            Node node = entry.getKey();
            if (isWithinBounds(node, x, y)) {
                Object element = entry.getValue();
                if (element instanceof Class) {
                    if (event.getClickCount() == 2) {
                        Class clazz = (Class) element;
                        showClassDetails(clazz);
                    }
                    return;
                } else if (element instanceof Interface) {
                    if (event.getClickCount() == 2) {
                        Interface clazz = (Interface) element;
                        showInterfaceDetails(clazz);
                    }
                    return;
                } else if(element instanceof TextBox){
                    if(event.getClickCount() == 2){
                        TextBox textBox = (TextBox) element;
                        showTextBoxDetailsForm(textBox);
                    }
                    return;
                }
            }
        }

        if (activeTool != null) {
            //associate x, y coordinates with the active tool
            if(activeTool.equals("Class")){
                drawClass(null, x, y);
            } else if(activeTool.equals("Interface")){
                drawInterface(null, x, y);
            } else if(activeTool.equals("TextBox")){
                drawTextBox(null, x, y);
            }
            activeTool = null;
        }
    }

    /*
     *
     * Draw functions
     *
     */

    private void drawClass(Component class_, double x, double y) {
        System.out.println("drawing class");
        Point initialPoint;
        if(class_ == null){
            //will be called whenever a new class component is created
            initialPoint = new Point(x, y);
            class_ = new Class(classDiagram.getUpcomingComponentID(), initialPoint);

            addComponentToListAndUpdateTree(class_);
        } else {
            initialPoint = new Point(class_.getInitialPoint().getX(), class_.getInitialPoint().getY());
        }

        double initialWidth = 120;
        VBox classBox = new VBox();
        classBox.setLayoutX(initialPoint.getX());
        classBox.setLayoutY(initialPoint.getY());
        classBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-padding: 0; -fx-background-color: #F5E49C;");
        Label classNameLabel = new Label(((Class)class_).getClassName());
        classNameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        VBox classNameBox = new VBox(classNameLabel);
        classNameBox.setMinWidth(initialWidth);
        classNameBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-padding: 5;");
        VBox attributesBox = new VBox();
        attributesBox.setMinWidth(initialWidth);
        attributesBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-padding: 5;");
        ArrayList<Attribute> attributes = ((Class)class_).getAttributes();
        for (Attribute attribute : attributes) {
            Label attributeLabel = new Label(attribute.toString());
            attributesBox.getChildren().add(attributeLabel);
        }
        VBox functionsBox = new VBox();
        functionsBox.setMinWidth(initialWidth);
        functionsBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-padding: 5;");
        ArrayList<Function> functions = ((Class)class_).getFunctions();
        for (Function function : functions) {
            Label functionLabel = new Label(function.toString());
            functionsBox.getChildren().add(functionLabel);
        }
        double maxWidth = Math.max(initialWidth, Math.max(getMaxLabelWidth(classNameBox), Math.max(getMaxLabelWidth(attributesBox), getMaxLabelWidth(functionsBox))));
        classNameBox.setMinWidth(maxWidth);
        attributesBox.setMinWidth(maxWidth);
        functionsBox.setMinWidth(maxWidth);
        classBox.getChildren().addAll(classNameBox, attributesBox, functionsBox);

        drawingPane.getChildren().add(classBox);

        if(!IsComponentInElementMap(class_)){
            elementMap.put(classBox, class_);
        }
    }

    public void drawInterface(Component interface_, double x, double y) {
        System.out.println("drawInterface called! x = " + x + " y = " + y);
        Point initialPoint;
        if(interface_ == null){
            initialPoint = new Point(x, y);
            interface_ = new Interface(classDiagram.getUpcomingComponentID(), initialPoint);

            addComponentToListAndUpdateTree(interface_);
        } else {
            initialPoint = new Point(interface_.getInitialPoint().getX(), interface_.getInitialPoint().getY());
        }

        double initialWidth = 120;
        VBox classBox = new VBox();
        classBox.setLayoutX(initialPoint.getX());
        classBox.setLayoutY(initialPoint.getY());
        classBox.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-padding: 5; -fx-background-color: #F5E49C;");
        Label classNameLabel = new Label(((Interface)interface_).getClassName());
        Label interfaceLabel = new Label("  <<Interface>>");
        classNameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        interfaceLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        VBox classNameBox = new VBox();
        classNameBox.getChildren().addAll(interfaceLabel,classNameLabel);
        classNameBox.setMinWidth(initialWidth);
        classNameBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-padding: 5;");
        VBox attributesBox = new VBox();
        attributesBox.setMinWidth(initialWidth);
        attributesBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-padding: 5;");
        VBox functionsBox = new VBox();
        functionsBox.setMinWidth(initialWidth);
        functionsBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-padding: 5;");
        ArrayList<Function> functions = ((Interface)interface_).getFunctions();
        for (Function function : functions) {
            Label functionLabel = new Label(function.toString());
            functionsBox.getChildren().add(functionLabel);
        }
        double maxWidth = Math.max(initialWidth, Math.max(getMaxLabelWidth(classNameBox), Math.max(getMaxLabelWidth(attributesBox), getMaxLabelWidth(functionsBox))));
        classNameBox.setMinWidth(maxWidth);
        attributesBox.setMinWidth(maxWidth);
        functionsBox.setMinWidth(maxWidth);
        classBox.getChildren().addAll(classNameBox, attributesBox, functionsBox);
        drawingPane.getChildren().add(classBox);

        if(!IsComponentInElementMap(interface_)) {
            elementMap.put(classBox, interface_);
        }
    }

    public void drawTextBox(Component textBox, double x, double y) {
        System.out.println("drawing TextBox");
        Point initialPoint;
        if(textBox == null){
            initialPoint = new Point(x, y);
            textBox = new TextBox(classDiagram.getUpcomingComponentID(), x, y);

            addComponentToListAndUpdateTree(textBox);
        } else {
            initialPoint = new Point(textBox.getX(), textBox.getY());
        }

        double initialWidth = 100;
        double initialHeight = 100;

        VBox textVBox = new VBox();
        textVBox.setLayoutX(initialPoint.getX());
        textVBox.setLayoutY(initialPoint.getY());
        textVBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-padding: 5; -fx-background-color: #F8EBB5;");

        Label textBoxLabel = new Label(((TextBox)textBox).getText());
        textBoxLabel.setWrapText(true);
        textBoxLabel.setMaxWidth(initialWidth - 10);
        textBoxLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: black;");
        textVBox.getChildren().add(textBoxLabel);

        textVBox.setMinWidth(initialWidth);
        textVBox.setMinHeight(initialHeight);

        drawingPane.getChildren().add(textVBox);

        if(!IsComponentInElementMap(textBox)){
            elementMap.put(textVBox, textBox);
        }
    }

    private void drawAssociation(BusinessLayer.Models.Components.ClassDiagramComponents.Line association, Point initialPoint, Point finalPoint) {
        if(association==null) {
            //make a new line of type association if association is null
            // (only the first time)
            association = drawLineFuncTopUtil(association, LineType.ASSOCIATION, initialPoint, finalPoint);
        }

        //if the attempt at creating a new association line failed, return
        if(association==null){
            return;
        }

        Line line = drawLine(association, false);

        drawLineFuncBottomUtil(line, association);
    }

    private void drawInheritance(BusinessLayer.Models.Components.ClassDiagramComponents.Line inheritance, Point initialPoint, Point finalPoint){
        if(inheritance==null) {
            inheritance = drawLineFuncTopUtil(inheritance, LineType.INHERITANCE, initialPoint, finalPoint);
        }

        if(inheritance==null){
            return;
        }

        Line line = drawLine(inheritance, false);
        drawArrowHead(line);
        drawLineFuncBottomUtil(line, inheritance);
    }

    private void drawAggregation(BusinessLayer.Models.Components.ClassDiagramComponents.Line aggregation, Point initialPoint, Point finalPoint){
        if(aggregation==null) {
            aggregation = drawLineFuncTopUtil(aggregation, LineType.AGGREGATION, initialPoint, finalPoint);
        }

        if(aggregation==null){
            return;
        }

        // Draw the line
        Line line = drawLine(aggregation, false);

        drawDiamond(line, false);

        drawLineFuncBottomUtil(line, aggregation);
    }

    private void drawComposition(BusinessLayer.Models.Components.ClassDiagramComponents.Line composition, Point initialPoint, Point finalPoint){
        if(composition==null) {
            composition = drawLineFuncTopUtil(composition, LineType.COMPOSITION, initialPoint, finalPoint);
        }

        if(composition==null){
            return;
        }

        // Draw the line
        Line line = drawLine(composition, false);
        drawDiamond(line, true);

        drawLineFuncBottomUtil(line, composition);
    }

    private void drawDashedLine(BusinessLayer.Models.Components.ClassDiagramComponents.Line dashedLine, Point initialPoint, Point finalPoint) {
        if(dashedLine==null){
            dashedLine = drawLineFuncTopUtil(dashedLine, LineType.DASHED, initialPoint, finalPoint);
        }

        if(dashedLine==null){
            return;
        }

        Line line = drawLine(dashedLine, true);

        drawLineFuncBottomUtil(line, dashedLine);
    }

    private void showText(BusinessLayer.Models.Components.ClassDiagramComponents.Line line){
        Text text = new Text(line.getName());
        text.setX((line.getStartComp().getX() + line.getEndComp().getX()) / 2);
        text.setY((line.getStartComp().getY() + line.getEndComp().getY()) / 2 - 10);
        drawingPane.getChildren().add(text);
    }

    private void showMultiplicity(Multiplicity multiplicity, Component referenceComponent){
        Text multiplicityText;
        if(multiplicity.getFirst().isEmpty() || multiplicity.getSecond().isEmpty()){
            multiplicityText = new Text("");
        } else {
            multiplicityText = new Text(multiplicity.getFirst()+".."+multiplicity.getSecond());
        }

        multiplicityText.setX(referenceComponent.getX() + 5);
        multiplicityText.setY(referenceComponent.getY() - 5);
        drawingPane.getChildren().add(multiplicityText);
    }

    private void drawArrowHead(Line line){
        // Calculate arrowhead coordinates
        double arrowLength = 15; // Length of the arrowhead
        double arrowWidth = 7; // Width of the arrowhead
        double startX = line.getEndX();
        double startY = line.getEndY();
        double endX = line.getStartX();
        double endY = line.getStartY();

        double angle = Math.atan2(startY - endY, startX - endX);
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);

        // Points for the arrowhead
        double x1 = startX - arrowLength * cos + arrowWidth * sin;
        double y1 = startY - arrowLength * sin - arrowWidth * cos;
        double x2 = startX - arrowLength * cos - arrowWidth * sin;
        double y2 = startY - arrowLength * sin + arrowWidth * cos;

        // Create the arrowhead
        Polygon arrowHead = new Polygon();
        arrowHead.getPoints().addAll(
                startX, startY,
                x1, y1,
                x2, y2
        );
        arrowHead.setStyle("-fx-fill: black;");
        drawingPane.getChildren().add(arrowHead);
    }

    private BusinessLayer.Models.Components.ClassDiagramComponents.Line drawLineFuncTopUtil(BusinessLayer.Models.Components.ClassDiagramComponents.Line line, LineType lineType, Point initialPoint, Point finalPoint){
        System.out.println("drawing "+lineType.getType());
        System.out.println("initial point: x = "+initialPoint.getX() +  ", y =  " + initialPoint.getY());
        System.out.println("final point: x = "+finalPoint.getX() +  ", y =  " + finalPoint.getY());
        if(initialPoint==null || finalPoint==null){
            System.out.println("initial and or final point is null!");
            return null;
        }

        Component startComp = getClassAtPoint(initialPoint);
        Component endComp = getClassAtPoint(finalPoint);

        if(startComp==null || endComp == null){
            System.out.println("start or end component is null!");
            return null;
        }

        if(Objects.equals(lineType.getType(), "DashedLine")){
            //if both the start and end components are not texboxes, then don't put a dashed line between them
            if(!(startComp instanceof TextBox) && !(endComp instanceof TextBox)){
                return null;
            }
        } else {
            if(startComp instanceof TextBox || endComp instanceof TextBox){
                System.out.println("One or both components are of type TextBox");
                return null;
            }
        }

        boolean linkExists = hasLink(startComp, endComp);
        if(linkExists){
            System.out.println("There is already a link between the two classes");
            return null;
        }

        line = new BusinessLayer.Models.Components.ClassDiagramComponents.Line(
                classDiagram.getUpcomingComponentID(),
                initialPoint.getX(), initialPoint.getY(),
                lineType,
                startComp, endComp
        );

        addComponentToListAndUpdateTree(line);

        return line;
    }

    private void drawLineFuncBottomUtil(Line line, BusinessLayer.Models.Components.ClassDiagramComponents.Line line_){
        showText(line_);
        showMultiplicity(line_.getStartMultiplicity(), line_.getStartComp());
        showMultiplicity(line_.getEndMultiplicity(), line_.getEndComp());

        if(!IsComponentInElementMap(line_)){
            elementMap.put(line, line_);
        }
    }

    public void drawDiamond(Line line, boolean fillDiamond){
        // Calculate diamond coordinates
        double diamondSize = 10;
        double startX = line.getEndX();
        double startY = line.getEndY();
        double endX = line.getStartX();
        double endY = line.getStartY();

        double angle = Math.atan2(startY - endY, startX - endX);
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);

        // Points for the diamond (4 points)
        double x1 = startX - diamondSize * cos;
        double y1 = startY - diamondSize * sin;

        double x2 = x1 - diamondSize * sin;
        double y2 = y1 + diamondSize * cos;

        double x3 = x1 + diamondSize * sin;
        double y3 = y1 - diamondSize * cos;

        double x4 = startX - 2 * diamondSize * cos;
        double y4 = startY - 2 * diamondSize * sin;

        // Create the diamond shape
        Polygon diamond = new Polygon(
                startX, startY,  // Tip of the diamond (closest to end class)
                x2, y2,          // Left point
                x4, y4,          // Bottom point (opposite tip)
                x3, y3           // Right point
        );
        if(fillDiamond){
            diamond.setFill(Color.BLACK); // Fill the diamond with white
        } else {
            diamond.setFill(Color.WHITE);
        }
        diamond.setStroke(Color.BLACK); // Black outline for the diamond
        diamond.setStrokeWidth(2);

        // Add the diamond to the pane
        drawingPane.getChildren().add(diamond);
    }

    public Line drawLine(BusinessLayer.Models.Components.ClassDiagramComponents.Line line_, Boolean dashed){
        Line line = new Line(
                line_.getStartComp().getX(),
                line_.getStartComp().getY(),
                line_.getEndComp().getX(),
                line_.getEndComp().getY()
        );
        line.setStrokeWidth(1.0);
        if(dashed){
            line.getStrokeDashArray().addAll(5.0, 5.0);
        }
        drawingPane.getChildren().add(line);
        return line;
    }

    public boolean hasLink(Component start, Component end){
        for (Map.Entry<Node, Object> entry : elementMap.entrySet()) {
            Object object = entry.getValue();
            if(object instanceof BusinessLayer.Models.Components.ClassDiagramComponents.Line){
                BusinessLayer.Models.Components.ClassDiagramComponents.Line line = (BusinessLayer.Models.Components.ClassDiagramComponents.Line) object;
                if(line.getStartComp() == start || line.getStartComp() == end){
                    if(line.getEndComp() == start || line.getEndComp() == end){
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void addComponentToListAndUpdateTree(Component component){
        classDiagram.addComponent(component);

        //updating the model explorer
        TreeItem<String> currentClassDiagramTreeItem = projectPageController.getModelTreeItemByName(classDiagram.getModelName());
        if(currentClassDiagramTreeItem==null){
            System.out.println("Class TreeItem is null, can't add a new treeItem to it!");
        } else {
            System.out.println("TreeItem with class name: "+currentClassDiagramTreeItem.getValue()+" found!");
            TreeItem<String> newComponentTreeItem = new TreeItem<>(component.getName()+" ("+component.getId()+")");
            currentClassDiagramTreeItem.getChildren().add(newComponentTreeItem);
        }
    }

    void printElementMap(){
        System.out.print("Element Map: ");
        for(Map.Entry<Node, Object> entry: elementMap.entrySet()){
            Object object = entry.getValue();
            Component component = (Component) object;
            System.out.print(component.getId()+", ");
        }
    }

    /*
     *
     * Show details functions
     *
     */
    public void showInterfaceDetails(Interface clazz){
        System.out.println("showInterfaceDetails called!");
        Stage detailStage = new Stage();
        VBox detailBox = new VBox(10);
        detailBox.setPadding(new Insets(10));
        detailBox.setFillWidth(true);
        ScrollPane scrollPane = new ScrollPane(detailBox);
        scrollPane.setFitToWidth(true);
        detailBox.getChildren().add(new Label("Class Name:"));
        TextField classNameField = new TextField(clazz.getClassName());
        detailBox.getChildren().add(classNameField);
        detailBox.getChildren().add(new Label("Functions:"));
        VBox functionsBox = new VBox(5);
        updateFunctionsBox(clazz, functionsBox);
        Button addFunctionButton = new Button("Add Function");
        addFunctionButton.setOnAction(e -> {
            Function newFunc = new Function("void", "newFunction"); // Default function
            clazz.addFunction(newFunc);
            updateFunctionsBox(clazz, functionsBox);
        });
        detailBox.getChildren().addAll(functionsBox, addFunctionButton);
        Button deleteButton = new Button("Delete Class");
        deleteButton.setOnAction(e -> {
            //need to write custom remove function
            //remove component

            classDiagram.removeComponent(clazz);
            elementMap.values().remove(clazz);
            redrawCanvas();
            detailStage.close();
        });
        detailBox.getChildren().add(deleteButton);
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(event -> {
            String oldClassName = clazz.getName();
            clazz.setName(classNameField.getText());
            //update model explorer
            TreeItem<String> treeItem = projectPageController.findTreeItemWithGivenName(oldClassName+" ("+clazz.getId()+")");
            if(treeItem!=null){
                treeItem.setValue(classNameField.getText()+" ("+ clazz.getId()+")");
            }
            //clear the current selection
            projectPageController.setSelectedComponentTextFieldText("");
            redrawCanvas();
            detailStage.close();
        });
        detailBox.getChildren().add(submitButton);
        Scene scene = new Scene(scrollPane, 400, 300);
        detailStage.setScene(scene);
        detailStage.setTitle("Edit Class: " + clazz.getClassName());
        detailStage.show();
    }

    private void showClassDetails(Class clazz) {
        System.out.println("showClassDetails called!");
        Stage detailStage = new Stage();
        VBox detailBox = new VBox(10);
        detailBox.setPadding(new Insets(10));
        detailBox.setFillWidth(true);
        ScrollPane scrollPane = new ScrollPane(detailBox);
        scrollPane.setFitToWidth(true);
        detailBox.getChildren().add(new Label("Class Name:"));
        TextField classNameField = new TextField(clazz.getClassName());
        detailBox.getChildren().add(classNameField);
        detailBox.getChildren().add(new Label("Attributes:"));
        VBox attributesBox = new VBox(5);
        updateAttributesBox(clazz, attributesBox);
        Button addAttributeButton = new Button("Add Attribute");
        addAttributeButton.setOnAction(e -> {
            Attribute newAttr = new Attribute("", "String");
            clazz.addAttribute(newAttr);
            updateAttributesBox(clazz, attributesBox);
        });
        detailBox.getChildren().addAll(attributesBox, addAttributeButton);
        detailBox.getChildren().add(new Label("Functions:"));
        VBox functionsBox = new VBox(5);
        updateFunctionsBox(clazz, functionsBox);
        Button addFunctionButton = new Button("Add Function");
        addFunctionButton.setOnAction(e -> {
            Function newFunc = new Function("void", "newFunction");
            clazz.addFunction(newFunc);
            updateFunctionsBox(clazz, functionsBox);
        });
        detailBox.getChildren().addAll(functionsBox, addFunctionButton);
        Button deleteButton = new Button("Delete Class");
        deleteButton.setOnAction(e -> {
            classDiagram.removeComponent(clazz);
            elementMap.values().remove(clazz);
            redrawCanvas();
            detailStage.close();
        });
        detailBox.getChildren().add(deleteButton);
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(event -> {
            String oldClassName = clazz.getName();
            clazz.setName(classNameField.getText());
            //update model explorer
            TreeItem<String> treeItem = projectPageController.findTreeItemWithGivenName(oldClassName+" ("+clazz.getId()+")");
            if(treeItem!=null){
                treeItem.setValue(classNameField.getText()+" ("+ clazz.getId()+")");
            }
            //clear the current selection
            projectPageController.setSelectedComponentTextFieldText("");
            redrawCanvas();
            detailStage.close();
        });
        detailBox.getChildren().add(submitButton);
        Scene scene = new Scene(scrollPane, 400, 300);
        detailStage.setScene(scene);
        detailStage.setTitle("Edit Class: " + clazz.getClassName());
        detailStage.show();
    }

    private void showAssociationDetailsForm(Association association) {
        System.out.println("showAssociationDetailsForm called!");
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Association");
        dialog.setHeaderText("Edit Multiplicity and Text for Association");
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        TextField startStartField = new TextField();
        startStartField.setPromptText("Start Multiplicity (Start)");
        TextField startEndField = new TextField();
        startEndField.setPromptText("Start Multiplicity (End)");
        startStartField.setText(association.getStartInitialMultiplicity());
        startEndField.setText(association.getStartEndMultiplicity());

        TextField endStartField = new TextField();
        endStartField.setPromptText("End Multiplicity (Start)");
        TextField endEndField = new TextField();
        endEndField.setPromptText("End Multiplicity (End)");
        endStartField.setText(association.getEndStartMultiplicity());
        endEndField.setText(association.getEndEndMultiplicity());
        TextField textField = new TextField();
        textField.setPromptText("Text");
        textField.setText(association.getName());

        content.getChildren().addAll(
                new Label("Start Multiplicity:"),
                new HBox(5, new Label("Start:"), startStartField, new Label("End:"), startEndField),
                new Label("End Multiplicity:"),
                new HBox(5, new Label("Start:"), endStartField, new Label("End:"), endEndField),
                new Label("Text:"), textField
        );
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                association.setStartInitialMultiplicity((startStartField.getText()));
                association.setStartEndMultiplicity(startEndField.getText());

                association.setEndStartMultiplicity(endStartField.getText());
                association.setEndEndMultiplicity(endEndField.getText());

                String oldAssociationName = association.getName();
                association.setName(textField.getText());
                //update model explorer
                TreeItem<String> treeItem = projectPageController.findTreeItemWithGivenName(oldAssociationName+" ("+association.getId()+")");
                if(treeItem!=null){
                    treeItem.setValue(association.getName()+" ("+ association.getId()+")");
                }
                //clear the current selection
                projectPageController.setSelectedComponentTextFieldText("");
                redrawCanvas();
            } catch (NumberFormatException e) {
                showWarning("Invalid Input", "Please enter valid numbers for multiplicities.");
            }
        }
    }

    private void showTextBoxDetailsForm(TextBox textBox) {
        System.out.println("showTextBoxDetailsForm called!");

        // Create a dialog for editing TextBox details
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit TextBox");
        dialog.setHeaderText("");

        // Create a VBox for dialog content
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        // Create a Label to show the TextBox name
        Label textBoxNameLabel = new Label("TextBox: " + textBox.getName());
        textBoxNameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        // Create a TextField to edit the text content
        TextArea textBoxContentArea = new TextArea();
        textBoxContentArea.setPromptText("Enter new content...");
        textBoxContentArea.setMinWidth(100);
        textBoxContentArea.setMinHeight(100);
        textBoxContentArea.setText(textBox.getText());

        // Add the components to the VBox
        content.getChildren().addAll(
                textBoxNameLabel,
                new Label("Edit Text Content:"),
                textBoxContentArea
        );

        // Add the content to the dialog pane
        dialog.getDialogPane().setContent(content);

        // Add OK and Cancel buttons
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Show the dialog and handle the result
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent()) {
            if(result.get() == ButtonType.OK){
                String newContent = textBoxContentArea.getText();
                if (newContent.isEmpty()) {
                    showWarning("Invalid Input", "TextBox content cannot be empty.");
                } else {
                    // Update the TextBox content
                    textBox.setText(newContent);

                    // Clear the current selection and redraw the canvas
                    projectPageController.setSelectedComponentTextFieldText("");
                    redrawCanvas();
                }
            } else if(result.get() == ButtonType.CANCEL){
                dialog.hide();
            }
        } else {
            dialog.hide();
        }
    }

    private void showInheritanceDetailsForm(Inheritance inheritance) {
        System.out.println("showInheritanceDetailsForm called!");
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Inheritance");
        dialog.setHeaderText("Edit Multiplicity and Text for Inheritance");
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        TextField startStartField = new TextField();
        startStartField.setPromptText("Start Multiplicity (Start)");
        TextField startEndField = new TextField();
        startEndField.setPromptText("Start Multiplicity (End)");
        startStartField.setText(inheritance.getStartInitialMultiplicity());
        startEndField.setText(inheritance.getStartEndMultiplicity());

        TextField endStartField = new TextField();
        endStartField.setPromptText("End Multiplicity (Start)");
        TextField endEndField = new TextField();
        endEndField.setPromptText("End Multiplicity (End)");
        endStartField.setText(inheritance.getEndStartMultiplicity());
        endEndField.setText(inheritance.getEndEndMultiplicity());
        TextField textField = new TextField();
        textField.setPromptText("Text");
        textField.setText(inheritance.getName());

        content.getChildren().addAll(
                new Label("Start Multiplicity:"),
                new HBox(5, new Label("Start:"), startStartField, new Label("End:"), startEndField),
                new Label("End Multiplicity:"),
                new HBox(5, new Label("Start:"), endStartField, new Label("End:"), endEndField),
                new Label("Text:"), textField
        );
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                inheritance.setStartInitialMultiplicity((startStartField.getText()));
                inheritance.setStartEndMultiplicity(startEndField.getText());

                inheritance.setEndStartMultiplicity(endStartField.getText());
                inheritance.setEndEndMultiplicity(endEndField.getText());

                String oldInheritanceName = inheritance.getName();
                inheritance.setName(textField.getText());
                //update model explorer
                TreeItem<String> treeItem = projectPageController.findTreeItemWithGivenName(oldInheritanceName+" ("+inheritance.getId()+")");
                if(treeItem!=null){
                    treeItem.setValue(inheritance.getName()+" ("+ inheritance.getId()+")");
                }
                //clear the current selection
                projectPageController.setSelectedComponentTextFieldText("");
                redrawCanvas();
            } catch (NumberFormatException e) {
                showWarning("Invalid Input", "Please enter valid numbers for multiplicities.");
            }
        }
    }

    private void showAggregationDetailsForm(Aggregation aggregation){
        System.out.println("showAggregationDetailsForm called!");
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Inheritance");
        dialog.setHeaderText("Edit Multiplicity and Text for Inheritance");
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        TextField startStartField = new TextField();
        startStartField.setPromptText("Start Multiplicity (Start)");
        TextField startEndField = new TextField();
        startEndField.setPromptText("Start Multiplicity (End)");
        startStartField.setText(aggregation.getStartInitialMultiplicity());
        startEndField.setText(aggregation.getStartEndMultiplicity());

        TextField endStartField = new TextField();
        endStartField.setPromptText("End Multiplicity (Start)");
        TextField endEndField = new TextField();
        endEndField.setPromptText("End Multiplicity (End)");
        endStartField.setText(aggregation.getEndStartMultiplicity());
        endEndField.setText(aggregation.getEndEndMultiplicity());
        TextField textField = new TextField();
        textField.setPromptText("Text");
        textField.setText(aggregation.getName());

        content.getChildren().addAll(
                new Label("Start Multiplicity:"),
                new HBox(5, new Label("Start:"), startStartField, new Label("End:"), startEndField),
                new Label("End Multiplicity:"),
                new HBox(5, new Label("Start:"), endStartField, new Label("End:"), endEndField),
                new Label("Text:"), textField
        );
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                aggregation.setStartInitialMultiplicity((startStartField.getText()));
                aggregation.setStartEndMultiplicity(startEndField.getText());

                aggregation.setEndStartMultiplicity(endStartField.getText());
                aggregation.setEndEndMultiplicity(endEndField.getText());

                String oldInheritanceName = aggregation.getName();
                aggregation.setName(textField.getText());
                //update model explorer
                TreeItem<String> treeItem = projectPageController.findTreeItemWithGivenName(oldInheritanceName+" ("+aggregation.getId()+")");
                if(treeItem!=null){
                    treeItem.setValue(aggregation.getName()+" ("+ aggregation.getId()+")");
                }
                //clear the current selection
                projectPageController.setSelectedComponentTextFieldText("");
                redrawCanvas();
            } catch (NumberFormatException e) {
                showWarning("Invalid Input", "Please enter valid numbers for multiplicities.");
            }
        }
    }

    private void showCompositionDetailsForm(Composition composition){
        System.out.println("showCompositionDetailsForm called!");
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Inheritance");
        dialog.setHeaderText("Edit Multiplicity and Text for Inheritance");
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        TextField startStartField = new TextField();
        startStartField.setPromptText("Start Multiplicity (Start)");
        TextField startEndField = new TextField();
        startEndField.setPromptText("Start Multiplicity (End)");
        startStartField.setText(composition.getStartInitialMultiplicity());
        startEndField.setText(composition.getStartEndMultiplicity());

        TextField endStartField = new TextField();
        endStartField.setPromptText("End Multiplicity (Start)");
        TextField endEndField = new TextField();
        endEndField.setPromptText("End Multiplicity (End)");
        endStartField.setText(composition.getEndStartMultiplicity());
        endEndField.setText(composition.getEndEndMultiplicity());
        TextField textField = new TextField();
        textField.setPromptText("Text");
        textField.setText(composition.getName());

        content.getChildren().addAll(
                new Label("Start Multiplicity:"),
                new HBox(5, new Label("Start:"), startStartField, new Label("End:"), startEndField),
                new Label("End Multiplicity:"),
                new HBox(5, new Label("Start:"), endStartField, new Label("End:"), endEndField),
                new Label("Text:"), textField
        );
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                composition.setStartInitialMultiplicity((startStartField.getText()));
                composition.setStartEndMultiplicity(startEndField.getText());

                composition.setEndStartMultiplicity(endStartField.getText());
                composition.setEndEndMultiplicity(endEndField.getText());

                String oldInheritanceName = composition.getName();
                composition.setName(textField.getText());
                //update model explorer
                TreeItem<String> treeItem = projectPageController.findTreeItemWithGivenName(oldInheritanceName+" ("+composition.getId()+")");
                if(treeItem!=null){
                    treeItem.setValue(composition.getName()+" ("+ composition.getId()+")");
                }
                //clear the current selection
                projectPageController.setSelectedComponentTextFieldText("");
                redrawCanvas();
            } catch (NumberFormatException e) {
                showWarning("Invalid Input", "Please enter valid numbers for multiplicities.");
            }
        }
    }

    /*
     *
     * update functions
     *
     */
    private void updateAttributesBox(BusinessLayer.Models.Components.ClassDiagramComponents.Class clazz, VBox attributesBox) {
        System.out.println("updateAttributesBox() called");
        attributesBox.getChildren().clear();
        for (Attribute attribute : clazz.getAttributes()) {
            HBox attrBox = new HBox(5);
            TextField nameField = new TextField(attribute.getName());
            ComboBox<String> DataTypeBox = new ComboBox<>();
            DataTypeBox.getItems().addAll("String","Int","Double","Float","Boolean");
            for(Component c : classDiagram.getComponents()){
                if(c instanceof Class){
                    DataTypeBox.getItems().add(((Class)c).getClassName());
                }
            }
            ComboBox<String> accessModifierBox = new ComboBox<>();
            accessModifierBox.getItems().addAll("public", "private", "protected");
            accessModifierBox.setValue(attribute.getAccessModifier());
            Button deleteButton = new Button("Delete");
            deleteButton.setOnAction(e -> {
                clazz.removeAttribute(attribute);
                updateAttributesBox(clazz, attributesBox);
            });
            attrBox.getChildren().addAll(
                    new Label("Name:"), nameField,
                    new Label("Type:"), DataTypeBox,
                    new Label("Access:"), accessModifierBox,
                    deleteButton
            );
            attributesBox.getChildren().add(attrBox);
            nameField.textProperty().addListener((obs, oldText, newText) -> attribute.setName(newText));
            DataTypeBox.valueProperty().addListener((obs, oldText, newText) -> attribute.setDataType(newText));
            accessModifierBox.valueProperty().addListener((obs, oldVal, newVal) -> attribute.setAccessModifier(newVal));
        }
    }

    private void updateFunctionsBox(BusinessLayer.Models.Components.ClassDiagramComponents.Class clazz, VBox functionsBox) {
        System.out.println("updateFunctionsBox for class called!");
        functionsBox.getChildren().clear();
        for (Function function : clazz.getFunctions()) {
            VBox funcBox = new VBox(5);
            TextField nameField = new TextField(function.getName());
            TextField returnTypeField = new TextField(function.getReturnType());
            ComboBox<String> accessModifierBox = new ComboBox<>();
            accessModifierBox.getItems().addAll("public", "private", "protected");
            accessModifierBox.setValue(function.getAccessModifier());
            VBox parametersBox = new VBox(5);
            updateParametersBox(function, parametersBox);
            Button addParameterButton = new Button("Add Parameter");
            addParameterButton.setOnAction(e -> {
                function.addAttribute(new Attribute("param", "String"));
                updateParametersBox(function, parametersBox);
            });
            Button deleteButton = new Button("Delete");
            deleteButton.setOnAction(e -> {
                clazz.removeFunction(function);
                updateFunctionsBox(clazz, functionsBox);
            });
            funcBox.getChildren().addAll(
                    new Label("Function Name:"), nameField,
                    new Label("Return Type:"), returnTypeField,
                    new Label("Access:"), accessModifierBox,
                    new Label("Parameters:"), parametersBox, addParameterButton,
                    deleteButton
            );
            functionsBox.getChildren().add(funcBox);
            nameField.textProperty().addListener((obs, oldText, newText) -> function.setName(newText));
            returnTypeField.textProperty().addListener((obs, oldText, newText) -> function.setReturnType(newText));
            accessModifierBox.valueProperty().addListener((obs, oldVal, newVal) -> function.setAccessModifier(newVal));
        }
    }

    private void updateFunctionsBox(Interface clazz, VBox functionsBox) {
        System.out.println("updateFunctionsBox for interface called!");
        functionsBox.getChildren().clear();
        for (Function function : clazz.getFunctions()) {
            VBox funcBox = new VBox(5);
            TextField nameField = new TextField(function.getName());
            TextField returnTypeField = new TextField(function.getReturnType());
            ComboBox<String> accessModifierBox = new ComboBox<>();
            accessModifierBox.getItems().addAll("public", "private", "protected");
            accessModifierBox.setValue(function.getAccessModifier());
            VBox parametersBox = new VBox(5);
            updateParametersBox(function, parametersBox);
            Button addParameterButton = new Button("Add Parameter");
            addParameterButton.setOnAction(e -> {
                function.addAttribute(new Attribute("param", "String"));
                updateParametersBox(function, parametersBox);
            });
            Button deleteButton = new Button("Delete");
            deleteButton.setOnAction(e -> {
                clazz.removeFunction(function);
                updateFunctionsBox(clazz, functionsBox);
            });
            funcBox.getChildren().addAll(
                    new Label("Function Name:"), nameField,
                    new Label("Return Type:"), returnTypeField,
                    new Label("Access:"), accessModifierBox,
                    new Label("Parameters:"), parametersBox, addParameterButton,
                    deleteButton
            );
            functionsBox.getChildren().add(funcBox);
            nameField.textProperty().addListener((obs, oldText, newText) -> function.setName(newText));
            returnTypeField.textProperty().addListener((obs, oldText, newText) -> function.setReturnType(newText));
            accessModifierBox.valueProperty().addListener((obs, oldVal, newVal) -> function.setAccessModifier(newVal));
        }
    }

    private void updateParametersBox(Function function, VBox parametersBox) {
        System.out.println("updateParametersBox() called");
        parametersBox.getChildren().clear();
        for (Attribute parameter : function.getAttributes()) {
            HBox paramBox = new HBox(5);
            TextField paramNameField = new TextField(parameter.getName());
            TextField paramTypeField = new TextField(parameter.getDataType());
            Button deleteParamButton = new Button("Delete");
            deleteParamButton.setOnAction(e -> {
                function.removeAttribute(parameter);
                updateParametersBox(function, parametersBox);
            });
            paramBox.getChildren().addAll(
                    new Label("Param Name:"), paramNameField,
                    new Label("Type:"), paramTypeField,
                    deleteParamButton
            );
            parametersBox.getChildren().add(paramBox);
            paramNameField.textProperty().addListener((obs, oldText, newText) -> parameter.setName(newText));
            paramTypeField.textProperty().addListener((obs, oldText, newText) -> parameter.setDataType(newText));
        }
    }

    /*
     *
     * select functions
     *
     */

    private boolean isNearLine(Line line, double x, double y) {

        System.out.println("isNearLine called!");
        Point2D start = new Point2D(line.getStartX(), line.getStartY());
        Point2D end = new Point2D(line.getEndX(), line.getEndY());
        Point2D point = new Point2D(x, y);
        return point.distance(start) + point.distance(end) - start.distance(end) < 5;
    }

    private void showWarning(String title, String message) {
        System.out.println("showWarning called!");
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /*
     *
     * Redraw functions
     *
     */
    public void redrawCanvas() {
        System.out.println("redrawCanvas called!");
        drawingPane.getChildren().clear();

        for(Component component : classDiagram.getComponents()){
            System.out.println("Class diagram component with id: "+component.getId()+", called in redrawCanvas");
            if(component instanceof Class){
                drawClass(component, 0, 0);
            } else if(component instanceof Interface){
                drawInterface(component, 0, 0);
            } else if(component instanceof TextBox){
                drawTextBox(component, 0, 0);
            } else if(component instanceof BusinessLayer.Models.Components.ClassDiagramComponents.Line){
                BusinessLayer.Models.Components.ClassDiagramComponents.Line line = (BusinessLayer.Models.Components.ClassDiagramComponents.Line) component;
                if(Objects.equals(line.getType(), "Association")){
                    drawAssociation(line, new Point(0, 0), new Point(0, 0));
                } else if(Objects.equals(line.getType(), "Inheritance")){
                    drawInheritance(line, new Point(0, 0), new Point(0, 0));
                } else if(Objects.equals(line.getType(), "Aggregation")){
                    drawAggregation(line, new Point(0, 0), new Point(0, 0));
                } else if(Objects.equals(line.getType(), "Composition")){
                    drawComposition(line, new Point(0, 0), new Point(0, 0));
                } else if(Objects.equals(line.getType(), "DashedLine")){
                    drawDashedLine(line, new Point(0, 0), new Point(0, 0));
                }
            }
        }

        printElementMap();
        System.out.println("");
    }

    boolean IsComponentInElementMap(Component component){
        for(Map.Entry<Node, Object> entry : elementMap.entrySet()){
            if(component == (Component) entry.getValue()){
                return true;
            }
        }
        return false;
    }

    private double getMaxLabelWidth(VBox vbox) {
        //System.out.println("getMaxLabelWidth called!");
        double maxWidth = 0;
        for (Node node : vbox.getChildren()) {
            if (node instanceof Label) {
                maxWidth = Math.max(maxWidth, ((Label) node).getWidth());
            }
        }
        return maxWidth + 10;
    }

    public void removeComponentWithGivenIDFromElementMap(int id){
        Node nodeToRemove = null;
        for (Map.Entry<Node, Object> entry : elementMap.entrySet()) {
            Node node = entry.getKey();
            Object object = entry.getValue();
            if(((Component) object).getId()==id){
                nodeToRemove = node;
                System.out.println("removing a node from the element map!");
                break;
            }
        }

        if(nodeToRemove!=null){
            elementMap.remove(nodeToRemove);
            System.out.println("removed the node from the element map!");
        }
    }

    /*
     *
     * other utility functions
     *
     */
    private Line getLineFromAssociation(Association association){
        return new Line(association.getStartClass().getX(), association.getStartClass().getY(), association.getEndClass().getX(), association.getEndClass().getY());
    }

    private Line getLineFromAggregation(Aggregation aggregation){
        return new Line(aggregation.getStartClass().getX(), aggregation.getStartClass().getY(), aggregation.getEndClass().getX(), aggregation.getEndClass().getY());
    }

    private Line getLineFromComposition(Composition composition){
        return new Line(composition.getStartClass().getX(), composition.getStartClass().getY(), composition.getEndClass().getX(), composition.getEndClass().getY());
    }

    private Line getLineFromInheritance(Inheritance inheritance){
        return new Line(inheritance.getStartClass().getX(), inheritance.getStartClass().getY(), inheritance.getEndClass().getX(), inheritance.getEndClass().getY());
    }

    private Component getClassAtPoint(Point point) {
        System.out.println("getClassAtPoint called!");
        for (Map.Entry<Node, Object> entry : elementMap.entrySet()) {
            Node node = entry.getKey();
            if (node instanceof VBox && isWithinBounds(node, point.getX(), point.getY())) {
                Object element = entry.getValue();
                if (element instanceof Class || element instanceof Interface || element instanceof TextBox) {
                    return (Component) element;
                }
            }
        }
        return null;
    }

    public void printMap(){
        System.out.println("Map size: "+elementMap.size());
        for(Map.Entry<Node, Object> entry: elementMap.entrySet()){
            System.out.println(Node.getClassCssMetaData());
            System.out.println("Component id: "+((Component) entry.getValue()).getId());
        }
    }

    private boolean isWithinBounds(Node node, double x, double y) {
        //System.out.println("isWithinBounds called!");
        return node.getBoundsInParent().contains(x, y);
    }

    private void trackMouse(MouseEvent event) {
        //System.out.println("trackMouse called!");
        mouseX = event.getX();
        mouseY = event.getY();
    }

    public void setProjectPageController(ProjectPageController projectPageController){
        this.projectPageController = projectPageController;
    }

    public void setModelName(String modelName) {
        model_name.setText("Class Diagram: " + modelName);
    }

    /**
     * <p>
     *     by iterating through the components, the functions filters the classes,
     *     interfaces and inheritance relations. All these relations are mapped
     *     to generate code for classes that can extends other classes and implement
     *     interfaces.
     * </p>
     * */
    public void generateCodeFunc(ActionEvent event) {
        // Retrieve components
        List<Component> components = classDiagram.getComponents();
        List<Interface> interfaces = new ArrayList<>();
        List<Class> classes = new ArrayList<>();
        List<Inheritance> inheritances = new ArrayList<>();

        // Categorize components
        for (Component component : components) {
            if (component instanceof Interface) {
                interfaces.add((Interface) component);
            } else if (component instanceof Class) {
                classes.add((Class) component);
            } else if (component instanceof Inheritance) {
                inheritances.add((Inheritance) component);
            }
        }

        // Map inheritance relationships
        Map<Class, Class> inheritanceMap = new HashMap<>();
        for (Inheritance inheritance : inheritances) {
            Component startClass = inheritance.getStartClass();
            Component endClass = inheritance.getEndClass();

            if (startClass instanceof Class && endClass instanceof Class) {
                inheritanceMap.put((Class) startClass, (Class) endClass);
            }
        }

        // Map classes to the interfaces they implement (if needed)
        Map<Class, List<Interface>> implementationMap = new HashMap<>();
        for (Component component : components) {
            if (component instanceof Association) { // Assuming `Association` represents implementation
                Association association = (Association) component;
                if (association.getStartClass() instanceof Class && association.getEndClass() instanceof Interface) {
                    Class startClass = (Class) association.getStartClass();
                    Interface endInterface = (Interface) association.getEndClass();

                    implementationMap.putIfAbsent(startClass, new ArrayList<>());
                    implementationMap.get(startClass).add(endInterface);
                }
            }
        }

        // StringBuilder for generating code
        StringBuilder codeBuilder = new StringBuilder();

        // Generate code for interfaces
        for (Interface iface : interfaces) {
            String ifaceName = iface.getName().replace(" ", "_");
            codeBuilder.append("public interface ").append(ifaceName).append(" {\n");

            // Add functions in the interface
            for (Function function : iface.getFunctions()) {
                codeBuilder.append("\t")
                        .append(function.getAccessModifier())
                        .append(function.getReturnType()).append(" ").append(function.getName()).append("();\n");
            }

            codeBuilder.append("}\n\n");
        }

        // Generate code for classes
        for (Class cls : classes) {
            String className = cls.getName().replace(" ", "_");
            codeBuilder.append("public class ").append(className);

            // Check inheritance
            if (inheritanceMap.containsKey(cls)) {
                codeBuilder.append(" extends ").append(inheritanceMap.get(cls).getName().replace(" ", "_"));
            }

            // Check implemented interfaces
            if (implementationMap.containsKey(cls)) {
                List<Interface> implementedInterfaces = implementationMap.get(cls);
                if (!implementedInterfaces.isEmpty()) {
                    codeBuilder.append(" implements ");
                    for (int i = 0; i < implementedInterfaces.size(); i++) {
                        codeBuilder.append(implementedInterfaces.get(i).getName().replace(" ", "_"));
                        if (i < implementedInterfaces.size() - 1) {
                            codeBuilder.append(", ");
                        }
                    }
                }
            }

            codeBuilder.append(" {\n");

            // Add attributes
            for (Attribute attribute : cls.getAttributes()) {
                codeBuilder.append("\t")
                        .append(attribute.getAccessModifier()).append(" ")
                        .append(attribute.getDataType()).append(" ")
                        .append(attribute.getName()).append(";\n");
            }

            // Add functions
            for (Function function : cls.getFunctions()) {
                codeBuilder.append("\n\t")
                        .append(function.getAccessModifier())
                        .append(function.getReturnType()).append(" ")
                        .append(function.getName()).append("(");

                // Add function parameters
                List<Attribute> parameters = function.getAttributes();
                for (int i = 0; i < parameters.size(); i++) {
                    Attribute param = parameters.get(i);
                    codeBuilder.append(param.getDataType()).append(" ").append(param.getName());
                    if (i < parameters.size() - 1) {
                        codeBuilder.append(", ");
                    }
                }

                codeBuilder.append(") {\n\t\t// TODO: Implement function\n\t}\n");
            }

            codeBuilder.append("}\n\n");
        }

        // Write the generated code to a file
        try {
            File file = new File("GeneratedCode.java");
            FileWriter writer = new FileWriter(file);
            writer.write(codeBuilder.toString());
            writer.close();
            LOGGER.info("Code successfully written to GeneratedCode.java");

            Alert alert = new Alert(Alert.AlertType.INFORMATION);

            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Code successfully written to GeneratedCode.java");

            alert.showAndWait();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Code could not be written to GeneratedCode.java", e);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);

            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Code could not be written to GeneratedCode.java");

            alert.showAndWait();
        }
    }


    public void textBoxFunc(ActionEvent event) {
        activeTool = "TextBox";
    }

    public void aggregationFunc(ActionEvent event) {
        activeTool = "Aggregation";
    }

    public void compositionFunc(ActionEvent event) {
        activeTool = "Composition";
    }

    public void inheritanceFunc(ActionEvent event){
        activeTool = "Inheritance";
    }

    public void associationFunc(ActionEvent event) {
        activeTool = "Association";
    }

    public void interfaceFunc(ActionEvent event) {
        activeTool = "Interface";
    }

    public void classFunc(ActionEvent event) {
        activeTool = "Class";
    }

    public void dashedLineFunc(ActionEvent event) {
        activeTool = "DashedLine";
    }
}
