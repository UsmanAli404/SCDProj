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
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClassDiagramController {

    private static final Logger LOGGER = Logger.getLogger(ClassDiagramController.class.getName());

    public ProjectPageController projectPageController;
    public ClassDiagram classDiagram;
    private Map<String, BiConsumer<Double, Double>> drawActions = new HashMap<>();
    @FXML
    public Label model_name;

    @FXML
    public Pane drawingPane;
    private Canvas canvas;
    private GraphicsContext graphicsContext;
    public String currentlySelectedBtn = "";//activeTool
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
        drawActions.put("Class", this::drawClass);
        drawActions.put("Interface", this::drawInterface);
        drawActions.put("TextBox", this::drawTextBox);
        drawingPane.setOnMouseMoved(this::trackMouse);
        drawingPane.setOnMouseClicked(this::handleDrawingCanvasClicked);
        drawingPane.setOnMousePressed(this::handleMousePressed);
        drawingPane.setOnMouseDragged(this::handleMouseDragged);
        drawingPane.setOnMouseReleased(this::handleMouseReleased);
    }

     public void setDrawingPane(Pane drawingPane){
        this.drawingPane = drawingPane;
    }
//    public Pane getDrawingPane(){
//        return drawingPane;
//    }
    /*
    *
    * Handle functions
    *
    */
    private void handleMousePressed(MouseEvent event) {
        System.out.println("handleMousePressed");
        double x = event.getX();
        double y = event.getY();
        if ("Association".equals(activeTool)) {
            initialPoint = new Point(x, y);
            tempLine = new Line(initialPoint.getX(), initialPoint.getY(), x, y);
            tempLine.getStrokeDashArray().addAll(5.0, 5.0);
            drawingPane.getChildren().add(tempLine);
            return;
        }

        if ("DashedLine".equals(activeTool)) {
            initialPoint = new Point(x, y);
            tempLine = new Line(initialPoint.getX(), initialPoint.getY(), x, y);
            tempLine.getStrokeDashArray().addAll(5.0, 5.0);
            drawingPane.getChildren().add(tempLine);
            return;
        }
        
        if("Inheritance".equals(activeTool)){
            initialPoint = new Point(x, y);
            tempLine = new Line(initialPoint.getX(), initialPoint.getY(), x, y);
            tempLine.getStrokeDashArray().addAll(5.0, 5.0);
            drawingPane.getChildren().add(tempLine);
            return;
        }

        if("Aggregation".equals(activeTool)){
            initialPoint = new Point(x, y);
            tempLine = new Line(initialPoint.getX(), initialPoint.getY(), x, y);
            tempLine.getStrokeDashArray().addAll(5.0, 5.0);
            drawingPane.getChildren().add(tempLine);
            return;
        }

        if("Composition".equals(activeTool)){
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
            if (element instanceof Class) {
                Class _class = (Class) element;
                _class.getInitialPoint().setX(_class.getInitialPoint().getX() + deltaX);
                _class.getInitialPoint().setY(_class.getInitialPoint().getY() + deltaY);
            } else if(element instanceof Interface){
                Interface _interface = (Interface) element;
                _interface.getInitialPoint().setX(_interface.getX()+deltaX);
                _interface.getInitialPoint().setY(_interface.getY()+deltaY);
            } else if(element instanceof TextBox){
                TextBox textBox = (TextBox) element;
                textBox.getInitialPoint().setX(textBox.getX() + deltaX);
                textBox.getInitialPoint().setY(textBox.getY() + deltaY);
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
                drawAssociation(initialPoint, finalPoint);
            } else if(Objects.equals(activeTool, "DashedLine")){
                drawDashedLine(initialPoint, finalPoint);
            } else if(Objects.equals(activeTool, "Inheritance")){
                drawInheritance(initialPoint, finalPoint);
            } else if(Objects.equals(activeTool, "Aggregation")){
                drawAggregation(initialPoint, finalPoint);
            } else if(Objects.equals(activeTool, "Composition")){
                drawComposition(initialPoint, finalPoint);
            }
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
                if (isNearLine(((Association) component).getLine(), x, y)) {
                    if (event.getClickCount() == 2) {
                        showAssociationDetailsForm((Association) component);
                    }
                    return;
                }
            } else if(component instanceof Inheritance){
                if (isNearLine(((Inheritance) component).getLine(), x, y)) {
                    if (event.getClickCount() == 2) {
                        showInheritanceDetailsForm((Inheritance) component);
                    }
                    return;
                }
            } else if(component instanceof Aggregation){
                if (isNearLine(((Aggregation) component).getLine(), x, y)) {
                    if (event.getClickCount() == 2) {
                        showAggregationDetailsForm((Aggregation) component);
                    }
                    return;
                }
            } else if(component instanceof Composition){
                if (isNearLine(((Composition) component).getLine(), x, y)) {
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
            BiConsumer<Double, Double> drawAction = drawActions.get(activeTool);
            if (drawAction != null) {
                drawAction.accept(x, y);
            }
        }
    }

    /*
     *
     * Draw functions
     *
     */
    public void drawInterface(Double x, Double y) {

        System.out.println("drawInterface called! x = " + x + " y = " + y);
        activeTool = null;
        Point initialPoint = new Point(x, y);
        Interface myClass = new Interface(classDiagram.getUpcomingComponentID(), initialPoint);
        double initialWidth = 120;
        VBox classBox = new VBox();
        classBox.setLayoutX(x);
        classBox.setLayoutY(y);
        classBox.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-padding: 5; -fx-background-color: #F5E49C;");
        Label classNameLabel = new Label(myClass.getClassName());
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
        ArrayList<Function> functions = myClass.getFunctions();
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

        classDiagram.addComponent(myClass);

        //updating the model explorer
        TreeItem<String> currentClassDiagramTreeItem = projectPageController.getModelTreeItemByName(classDiagram.getModelName());
        if(currentClassDiagramTreeItem==null){
            System.out.println("Class TreeItem is null, can't add a new treeItem to it!");
        } else {
            System.out.println("TreeItem with interface name: "+currentClassDiagramTreeItem.getValue()+" found!");
            TreeItem<String> newComponentTreeItem = new TreeItem<>(myClass.getName()+" ("+myClass.getId()+")");
            currentClassDiagramTreeItem.getChildren().add(newComponentTreeItem);
        }
        elementMap.put(classBox, myClass);
    }

    private void drawClass(double x, double y) {
        System.out.println("drawing class");
        activeTool = null;
        Point initialPoint = new Point(x, y);
        Class myClass = new Class(classDiagram.getUpcomingComponentID(), initialPoint);
        double initialWidth = 120;
        VBox classBox = new VBox();
        classBox.setLayoutX(x);
        classBox.setLayoutY(y);
        classBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-padding: 0; -fx-background-color: #F5E49C;");
        Label classNameLabel = new Label(myClass.getClassName());
        classNameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        VBox classNameBox = new VBox(classNameLabel);
        classNameBox.setMinWidth(initialWidth);
        classNameBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-padding: 5;");
        VBox attributesBox = new VBox();
        attributesBox.setMinWidth(initialWidth);
        attributesBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-padding: 5;");
        ArrayList<Attribute> attributes = myClass.getAttributes();
        for (Attribute attribute : attributes) {
            Label attributeLabel = new Label(attribute.toString());
            attributesBox.getChildren().add(attributeLabel);
        }
        VBox functionsBox = new VBox();
        functionsBox.setMinWidth(initialWidth);
        functionsBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-padding: 5;");
        ArrayList<Function> functions = myClass.getFunctions();
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

        classDiagram.addComponent(myClass);

        //updating the model explorer
        TreeItem<String> currentClassDiagramTreeItem = projectPageController.getModelTreeItemByName(classDiagram.getModelName());
        if(currentClassDiagramTreeItem==null){
            System.out.println("Class TreeItem is null, can't add a new treeItem to it!");
        } else {
            System.out.println("TreeItem with class name: "+currentClassDiagramTreeItem.getValue()+" found!");
            TreeItem<String> newComponentTreeItem = new TreeItem<>(myClass.getName()+" ("+myClass.getId()+")");
            currentClassDiagramTreeItem.getChildren().add(newComponentTreeItem);
        }

        elementMap.put(classBox, myClass);
    }

    private void drawAssociation(Point initialPoint, Point finalPoint) {
        System.out.println("drawing association");
        activeTool = null;
        System.out.println("initial point: x = "+initialPoint.getX() +  ", y =  " + initialPoint.getY());
        System.out.println("final point: x = "+finalPoint.getX() +  ", y =  " + finalPoint.getY());
        if(initialPoint==null){
            System.out.println("initial point is null!");
            //return;
        }

        if(finalPoint==null){
            System.out.println("final point is null!");
            //return;
        }

        Component startClass = getClassAtPoint(initialPoint);
        Component endClass = getClassAtPoint(finalPoint);
        if(startClass==null){
            System.out.println("start class is null!");
        }

        if(endClass==null){
            System.out.println("end class is null!");
        }

        if(initialPoint==null || finalPoint==null ||startClass==null||endClass==null){
            return;
        }

        //search if both the start and end classes already have an association
        boolean associationExists = hasAssociation(startClass, endClass);
        if(associationExists){
            System.out.println("There is already an association between the two classes");
            return;
        }

        if (startClass != null && endClass != null) {
            System.out.println("Start Class name: "+startClass.getName());
            System.out.println("End Class name: "+endClass.getName());

            if(startClass instanceof TextBox || endClass instanceof TextBox){
                System.out.println("One or both components are of type TextBox");
                return;
            }

            Line line = new Line(
                    startClass.getInitialPoint().getX(), startClass.getInitialPoint().getY(),
                    endClass.getInitialPoint().getX(), endClass.getInitialPoint().getY()
            );
            line.setStrokeWidth(2.0);
            drawingPane.getChildren().add(line);

            Association association = new Association(classDiagram.getUpcomingComponentID(), initialPoint.getX(), initialPoint.getY(), startClass, endClass);
            classDiagram.addComponent(association);

            elementMap.put(line, association);

            Text associationText = new Text(association.getName());
            associationText.setX((association.getLine().getStartX() + association.getLine().getEndX()) / 2);
            associationText.setY((association.getLine().getStartY() + association.getLine().getEndY()) / 2 - 10);
            drawingPane.getChildren().add(associationText);

            Text startMultiplicityText;
            if(association.getStartInitialMultiplicity().isEmpty() || association.getStartEndMultiplicity().isEmpty()){
                startMultiplicityText = new Text("");
            } else {
                startMultiplicityText = new Text(association.getStartInitialMultiplicity()+".."+association.getStartEndMultiplicity());
            }

            startMultiplicityText.setX(association.getLine().getStartX() - 15);
            startMultiplicityText.setY(association.getLine().getStartY() - 5);
            drawingPane.getChildren().add(startMultiplicityText);

            Text endMultiplicityText;
            if(association.getEndStartMultiplicity().isEmpty() || association.getEndEndMultiplicity().isEmpty()){
                endMultiplicityText = new Text("");
            } else {
                endMultiplicityText = new Text(association.getStartInitialMultiplicity()+".."+association.getStartEndMultiplicity());
            }

            endMultiplicityText.setX(association.getLine().getEndX() + 5);
            endMultiplicityText.setY(association.getLine().getEndY() - 5);
            drawingPane.getChildren().add(endMultiplicityText);

            //updating the model explorer
            TreeItem<String> currentClassDiagramTreeItem = projectPageController.getModelTreeItemByName(classDiagram.getModelName());
            if(currentClassDiagramTreeItem==null){
                System.out.println("Class TreeItem is null, can't add a new treeItem to it!");
            } else {
                System.out.println("TreeItem with class name: "+currentClassDiagramTreeItem.getValue()+" found!");
                TreeItem<String> newComponentTreeItem = new TreeItem<>(association.getName()+" ("+association.getId()+")");
                currentClassDiagramTreeItem.getChildren().add(newComponentTreeItem);
            }
        } else {
            showWarning("Association Error", "Both endpoints must be inside a class.");
        }
    }
    
    private void drawInheritance(Point initialPoint, Point finalPoint){
        System.out.println("drawing inheritance");
        activeTool = null;
        System.out.println("initial point: x = "+initialPoint.getX() +  ", y =  " + initialPoint.getY());
        System.out.println("final point: x = "+finalPoint.getX() +  ", y =  " + finalPoint.getY());
        if(initialPoint==null){
            System.out.println("initial point is null!");
            //return;
        }

        if(finalPoint==null){
            System.out.println("final point is null!");
            //return;
        }

        Component startClass = getClassAtPoint(initialPoint);
        Component endClass = getClassAtPoint(finalPoint);
        if(startClass==null){
            System.out.println("start class is null!");
        }

        if(endClass==null){
            System.out.println("end class is null!");
        }

        if(initialPoint==null || finalPoint==null ||startClass==null || endClass==null){
            return;
        }

        //search if both the start and end classes already have an association
        boolean inheritanceExists = hasInheritance(startClass, endClass);
        if(inheritanceExists){
            System.out.println("There is already an association between the two classes");
            return;
        }

        if (startClass != null && endClass != null) {
            System.out.println("Start Class name: "+startClass.getName());
            System.out.println("End Class name: "+endClass.getName());

            if(startClass instanceof TextBox || endClass instanceof TextBox){
                System.out.println("One or both components are of type TextBox");
                return;
            }

            Line line = new Line(
                    startClass.getInitialPoint().getX(), startClass.getInitialPoint().getY(),
                    endClass.getInitialPoint().getX(), endClass.getInitialPoint().getY()
            );
            line.setStrokeWidth(2.0);
            drawingPane.getChildren().add(line);
            //the line must have an arrow head on the end class side

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

            Inheritance inheritance = new Inheritance(classDiagram.getUpcomingComponentID(), initialPoint.getX(), initialPoint.getY(), startClass, endClass);
            classDiagram.addComponent(inheritance);

            elementMap.put(line, inheritance);

            Text inheritanceText = new Text(inheritance.getName());
            inheritanceText.setX((inheritance.getLine().getStartX() + inheritance.getLine().getEndX()) / 2);
            inheritanceText.setY((inheritance.getLine().getStartY() + inheritance.getLine().getEndY()) / 2 - 10);
            drawingPane.getChildren().add(inheritanceText);

            Text startMultiplicityText;
            if(inheritance.getStartInitialMultiplicity().isEmpty() || inheritance.getStartEndMultiplicity().isEmpty()){
                startMultiplicityText = new Text("");
            } else {
                startMultiplicityText = new Text(inheritance.getStartInitialMultiplicity()+".."+inheritance.getStartEndMultiplicity());
            }

            startMultiplicityText.setX(inheritance.getLine().getStartX() - 15);
            startMultiplicityText.setY(inheritance.getLine().getStartY() - 5);
            drawingPane.getChildren().add(startMultiplicityText);

            Text endMultiplicityText;
            if(inheritance.getEndStartMultiplicity().isEmpty() || inheritance.getEndEndMultiplicity().isEmpty()){
                endMultiplicityText = new Text("");
            } else {
                endMultiplicityText = new Text(inheritance.getStartInitialMultiplicity()+".."+inheritance.getStartEndMultiplicity());
            }

            endMultiplicityText.setX(inheritance.getLine().getEndX() + 5);
            endMultiplicityText.setY(inheritance.getLine().getEndY() - 5);
            drawingPane.getChildren().add(endMultiplicityText);

            //updating the model explorer
            TreeItem<String> currentClassDiagramTreeItem = projectPageController.getModelTreeItemByName(classDiagram.getModelName());
            if(currentClassDiagramTreeItem==null){
                System.out.println("Class TreeItem is null, can't add a new treeItem to it!");
            } else {
                System.out.println("TreeItem with class name: "+currentClassDiagramTreeItem.getValue()+" found!");
                TreeItem<String> newComponentTreeItem = new TreeItem<>(inheritance.getName()+" ("+inheritance.getId()+")");
                currentClassDiagramTreeItem.getChildren().add(newComponentTreeItem);
            }
        } else {
            showWarning("Inheritacne Error", "Both endpoints must be inside a class.");
        }
    }
    
    private void drawAggregation(Point initialPoint, Point finalPoint){
        System.out.println("drawing aggregation");
        activeTool = null;
        System.out.println("initial point: x = "+initialPoint.getX() +  ", y =  " + initialPoint.getY());
        System.out.println("final point: x = "+finalPoint.getX() +  ", y =  " + finalPoint.getY());
        if(initialPoint==null){
            System.out.println("initial point is null!");
            //return;
        }

        if(finalPoint==null){
            System.out.println("final point is null!");
            //return;
        }

        Component startClass = getClassAtPoint(initialPoint);
        Component endClass = getClassAtPoint(finalPoint);
        if(startClass==null){
            System.out.println("start class is null!");
        }

        if(endClass==null){
            System.out.println("end class is null!");
        }

        if(initialPoint==null || finalPoint==null ||startClass==null || endClass==null){
            return;
        }

        //search if both the start and end classes already have an association
        boolean aggregationExists = hasAggregation(startClass, endClass);
        if(aggregationExists){
            System.out.println("There is already an aggregation between the two classes");
            return;
        }

        // Inside your drawAggregation method
        if (startClass != null && endClass != null) {
            System.out.println("Start Class name: " + startClass.getName());
            System.out.println("End Class name: " + endClass.getName());

            if (startClass instanceof TextBox || endClass instanceof TextBox) {
                System.out.println("One or both components are of type TextBox");
                return;
            }

            // Draw the line
            Line line = new Line(
                    startClass.getInitialPoint().getX(), startClass.getInitialPoint().getY(),
                    endClass.getInitialPoint().getX(), endClass.getInitialPoint().getY()
            );
            line.setStrokeWidth(1.0);
            drawingPane.getChildren().add(line);

            // Calculate diamond coordinates
            double diamondSize = 10; // Adjust as needed for the diamond size
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
            diamond.setFill(Color.WHITE); // Fill the diamond with white
            diamond.setStroke(Color.BLACK); // Black outline for the diamond
            diamond.setStrokeWidth(2);

            // Add the diamond to the pane
            drawingPane.getChildren().add(diamond);

            // Add other components (aggregation object, text labels, etc.)
            Aggregation aggregation = new Aggregation(classDiagram.getUpcomingComponentID(), initialPoint.getX(), initialPoint.getY(), startClass, endClass);
            classDiagram.addComponent(aggregation);

            elementMap.put(line, aggregation);

            Text aggregationText = new Text(aggregation.getName());
            aggregationText.setX((aggregation.getLine().getStartX() + aggregation.getLine().getEndX()) / 2);
            aggregationText.setY((aggregation.getLine().getStartY() + aggregation.getLine().getEndY()) / 2 - 10);
            drawingPane.getChildren().add(aggregationText);

            Text startMultiplicityText;
            if(aggregation.getStartInitialMultiplicity().isEmpty() || aggregation.getStartEndMultiplicity().isEmpty()){
                startMultiplicityText = new Text("");
            } else {
                startMultiplicityText = new Text(aggregation.getStartInitialMultiplicity()+".."+aggregation.getStartEndMultiplicity());
            }

            startMultiplicityText.setX(aggregation.getLine().getStartX() - 15);
            startMultiplicityText.setY(aggregation.getLine().getStartY() - 5);
            drawingPane.getChildren().add(startMultiplicityText);

            Text endMultiplicityText;
            if(aggregation.getEndStartMultiplicity().isEmpty() || aggregation.getEndEndMultiplicity().isEmpty()){
                endMultiplicityText = new Text("");
            } else {
                endMultiplicityText = new Text(aggregation.getStartInitialMultiplicity()+".."+aggregation.getStartEndMultiplicity());
            }

            endMultiplicityText.setX(aggregation.getLine().getEndX() + 5);
            endMultiplicityText.setY(aggregation.getLine().getEndY() - 5);
            drawingPane.getChildren().add(endMultiplicityText);

            // Updating the model explorer
            TreeItem<String> currentClassDiagramTreeItem = projectPageController.getModelTreeItemByName(classDiagram.getModelName());
            if (currentClassDiagramTreeItem == null) {
                System.out.println("Class TreeItem is null, can't add a new treeItem to it!");
            } else {
                System.out.println("TreeItem with class name: " + currentClassDiagramTreeItem.getValue() + " found!");
                TreeItem<String> newComponentTreeItem = new TreeItem<>(aggregation.getName() + " (" + aggregation.getId() + ")");
                currentClassDiagramTreeItem.getChildren().add(newComponentTreeItem);
            }
        } else {
            showWarning("Aggregation Error", "Both endpoints must be inside a class.");
        }
    }
    
    private void drawComposition(Point initialPoint, Point finalPoint){
        System.out.println("drawing composition");
        activeTool = null;
        System.out.println("initial point: x = "+initialPoint.getX() +  ", y =  " + initialPoint.getY());
        System.out.println("final point: x = "+finalPoint.getX() +  ", y =  " + finalPoint.getY());
        if(initialPoint==null){
            System.out.println("initial point is null!");
            //return;
        }

        if(finalPoint==null){
            System.out.println("final point is null!");
            //return;
        }

        Component startClass = getClassAtPoint(initialPoint);
        Component endClass = getClassAtPoint(finalPoint);
        if(startClass==null){
            System.out.println("start class is null!");
        }

        if(endClass==null){
            System.out.println("end class is null!");
        }

        if(initialPoint==null || finalPoint==null ||startClass==null || endClass==null){
            return;
        }

        //search if both the start and end classes already have an association
        boolean compositionExists = hasComposition(startClass, endClass);
        if(compositionExists){
            System.out.println("There is already a composition between the two classes");
            return;
        }

        // Inside your drawAggregation method
        if (startClass != null && endClass != null) {
            System.out.println("Start Class name: " + startClass.getName());
            System.out.println("End Class name: " + endClass.getName());

            if (startClass instanceof TextBox || endClass instanceof TextBox) {
                System.out.println("One or both components are of type TextBox");
                return;
            }

            // Draw the line
            Line line = new Line(
                    startClass.getInitialPoint().getX(), startClass.getInitialPoint().getY(),
                    endClass.getInitialPoint().getX(), endClass.getInitialPoint().getY()
            );
            line.setStrokeWidth(1.0);
            drawingPane.getChildren().add(line);

            // Calculate diamond coordinates
            double diamondSize = 10; // Adjust as needed for the diamond size
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
            diamond.setFill(Color.BLACK); // Fill the diamond with white
            diamond.setStroke(Color.BLACK); // Black outline for the diamond
            diamond.setStrokeWidth(2);

            // Add the diamond to the pane
            drawingPane.getChildren().add(diamond);

            // Add other components (aggregation object, text labels, etc.)
            Composition composition = new Composition(classDiagram.getUpcomingComponentID(), initialPoint.getX(), initialPoint.getY(), startClass, endClass);
            classDiagram.addComponent(composition);

            elementMap.put(line, composition);

            Text compositionText = new Text(composition.getName());
            compositionText.setX((composition.getLine().getStartX() + composition.getLine().getEndX()) / 2);
            compositionText.setY((composition.getLine().getStartY() + composition.getLine().getEndY()) / 2 - 10);
            drawingPane.getChildren().add(compositionText);

            Text startMultiplicityText;
            if(composition.getStartInitialMultiplicity().isEmpty() || composition.getStartEndMultiplicity().isEmpty()){
                startMultiplicityText = new Text("");
            } else {
                startMultiplicityText = new Text(composition.getStartInitialMultiplicity()+".."+composition.getStartEndMultiplicity());
            }

            startMultiplicityText.setX(composition.getLine().getStartX() - 15);
            startMultiplicityText.setY(composition.getLine().getStartY() - 5);
            drawingPane.getChildren().add(startMultiplicityText);

            Text endMultiplicityText;
            if(composition.getEndStartMultiplicity().isEmpty() || composition.getEndEndMultiplicity().isEmpty()){
                endMultiplicityText = new Text("");
            } else {
                endMultiplicityText = new Text(composition.getStartInitialMultiplicity()+".."+composition.getStartEndMultiplicity());
            }

            endMultiplicityText.setX(composition.getLine().getEndX() + 5);
            endMultiplicityText.setY(composition.getLine().getEndY() - 5);
            drawingPane.getChildren().add(endMultiplicityText);

            // Updating the model explorer
            TreeItem<String> currentClassDiagramTreeItem = projectPageController.getModelTreeItemByName(classDiagram.getModelName());
            if (currentClassDiagramTreeItem == null) {
                System.out.println("Class TreeItem is null, can't add a new treeItem to it!");
            } else {
                System.out.println("TreeItem with class name: " + currentClassDiagramTreeItem.getValue() + " found!");
                TreeItem<String> newComponentTreeItem = new TreeItem<>(composition.getName() + " (" + composition.getId() + ")");
                currentClassDiagramTreeItem.getChildren().add(newComponentTreeItem);
            }
        } else {
            showWarning("Aggregation Error", "Both endpoints must be inside a class.");
        }
    }

    private void drawDashedLine(Point initialPoint, Point finalPoint) {
        System.out.println("drawing dashed line");
        activeTool = null;
        System.out.println("initial point: x = "+initialPoint.getX() +  ", y =  " + initialPoint.getY());
        System.out.println("final point: x = "+finalPoint.getX() +  ", y =  " + finalPoint.getY());
        if(initialPoint==null){
            System.out.println("initial point is null!");
            //return;
        }

        if(finalPoint==null){
            System.out.println("final point is null!");
            //return;
        }

        Component startClass = getClassAtPoint(initialPoint);
        Component endClass = getClassAtPoint(finalPoint);
        if(startClass==null){
            System.out.println("start class is null!");
        }

        if(endClass==null){
            System.out.println("end class is null!");
        }

        if(initialPoint==null || finalPoint==null ||startClass==null||endClass==null){
            return;
        }

        if (startClass != null && endClass != null) {
            System.out.println("Start Class name: "+startClass.getName());
            System.out.println("End Class name: "+endClass.getName());

            if(startClass instanceof Class && endClass instanceof Class){
                System.out.println("Both components are of type class!");
                return;
            }

            Line line = new Line(
                    startClass.getInitialPoint().getX(), startClass.getInitialPoint().getY(),
                    endClass.getInitialPoint().getX(), endClass.getInitialPoint().getY()
            );
            line.setStrokeWidth(1.0);
            line.getStrokeDashArray().addAll(5.0, 5.0);
            drawingPane.getChildren().add(line);

            DashedLine dashedLine = new DashedLine(classDiagram.getUpcomingComponentID(), initialPoint.getX(), initialPoint.getY(), startClass, endClass);
            classDiagram.addComponent(dashedLine);

            elementMap.put(line, dashedLine);

            //updating the model explorer
            TreeItem<String> currentClassDiagramTreeItem = projectPageController.getModelTreeItemByName(classDiagram.getModelName());
            if(currentClassDiagramTreeItem==null){
                System.out.println("Class TreeItem is null, can't add a new treeItem to it!");
            } else {
                System.out.println("TreeItem with class name: "+currentClassDiagramTreeItem.getValue()+" found!");
                TreeItem<String> newComponentTreeItem = new TreeItem<>(dashedLine.getName()+" ("+dashedLine.getId()+")");
                currentClassDiagramTreeItem.getChildren().add(newComponentTreeItem);
            }
        } else {
            showWarning("DashedLine Error", "Both endpoints must be inside a class.");
        }
    }

    public boolean hasAssociation(Component start, Component end){
        for (Map.Entry<Node, Object> entry : elementMap.entrySet()) {
            Object object = entry.getValue();
            if(object instanceof Association){
                Association association = (Association) object;
                if(association.getStartClass() == start || association.getStartClass() == end){
                    if(association.getEndClass() == start || association.getEndClass() == end){
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean hasInheritance(Component start, Component end){
        for (Map.Entry<Node, Object> entry : elementMap.entrySet()) {
            Object object = entry.getValue();
            if(object instanceof Inheritance){
                Inheritance inheritance = (Inheritance) object;
                if(inheritance.getStartClass() == start || inheritance.getStartClass() == end){
                    if(inheritance.getEndClass() == start || inheritance.getEndClass() == end){
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean hasAggregation(Component start, Component end){
        for (Map.Entry<Node, Object> entry : elementMap.entrySet()) {
            Object object = entry.getValue();
            if(object instanceof Aggregation){
                Aggregation aggregation = (Aggregation) object;
                if(aggregation.getStartClass() == start || aggregation.getStartClass() == end){
                    if(aggregation.getEndClass() == start || aggregation.getEndClass() == end){
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean hasComposition(Component start, Component end){
        for (Map.Entry<Node, Object> entry : elementMap.entrySet()) {
            Object object = entry.getValue();
            if(object instanceof Composition){
                Composition composition = (Composition) object;
                if(composition.getStartClass() == start || composition.getStartClass() == end){
                    if(composition.getEndClass() == start || composition.getEndClass() == end){
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean hasDashedLine(Component start, Component end){
        for (Map.Entry<Node, Object> entry : elementMap.entrySet()) {
            Node node = entry.getKey();
            Object object = entry.getValue();
            if(object instanceof DashedLine){
                DashedLine dashedLine = (DashedLine) object;
                if(dashedLine.getStartClass() == start || dashedLine.getStartClass() == end){
                    if(dashedLine.getEndClass() == start || dashedLine.getEndClass() == end){
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public void drawTextBox(double x, double y) {
        System.out.println("drawing TextBox");
        activeTool = null;
        TextBox textBox = new TextBox(classDiagram.getUpcomingComponentID(), x, y);
        double initialWidth = 100;
        double initialHeight = 100;

        VBox textVBox = new VBox();
        textVBox.setLayoutX(x);
        textVBox.setLayoutY(y);
        textVBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-padding: 5; -fx-background-color: #F8EBB5;");

        Label textBoxLabel = new Label(textBox.getText());
        textBoxLabel.setWrapText(true);
        textBoxLabel.setMaxWidth(initialWidth - 10);
        textBoxLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: black;");
        textVBox.getChildren().add(textBoxLabel);

        textVBox.setMinWidth(initialWidth);
        textVBox.setMinHeight(initialHeight);

        drawingPane.getChildren().add(textVBox);
        classDiagram.addComponent(textBox);

        //Update the model explorer (if necessary)
        TreeItem<String> currentClassDiagramTreeItem = projectPageController.getModelTreeItemByName(classDiagram.getModelName());
        if (currentClassDiagramTreeItem == null) {
            System.out.println("TextBox TreeItem is null, can't add a new treeItem to it!");
        } else {
            System.out.println("TreeItem with class name: " + currentClassDiagramTreeItem.getValue() + " found!");
            TreeItem<String> newComponentTreeItem = new TreeItem<>(textBox.getName() + " (" + textBox.getId() + ")");
            currentClassDiagramTreeItem.getChildren().add(newComponentTreeItem);
        }

        elementMap.put(textVBox, textBox);
    }

    public void redrawTextBox(TextBox textBox){
        System.out.println("redrawTextBox called");
        //activeTool = null;
        Point initialPoint = new Point(textBox.getInitialPoint().getX(), textBox.getInitialPoint().getY());
        double initialWidth = 100;
        double initialHeight = 100;

        VBox textVBox = new VBox();
        textVBox.setLayoutX(initialPoint.getX());
        textVBox.setLayoutY(initialPoint.getY());
        textVBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-padding: 5; -fx-background-color: #F8EBB5;");

        Label textBoxLabel = new Label(textBox.getText());
        textBoxLabel.setWrapText(true);
        textBoxLabel.setMaxWidth(initialWidth - 10);
        textBoxLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: black;");
        textVBox.getChildren().add(textBoxLabel);

        textVBox.setMinWidth(initialWidth);
        textVBox.setMinHeight(initialHeight);

        drawingPane.getChildren().add(textVBox);
        elementMap.put(textVBox, textBox);
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
                redrawClass((Class) component);
            } else if(component instanceof Interface){
                redrawInterface((Interface) component);
            } else if(component instanceof Association){
                Association association = (Association) component;
                redrawAssociation(association);

                Text associationText = new Text(association.getName());
                associationText.setX((association.getLine().getStartX() + association.getLine().getEndX()) / 2);
                associationText.setY((association.getLine().getStartY() + association.getLine().getEndY()) / 2 - 10);
                drawingPane.getChildren().add(associationText);

                Text startMultiplicityText;
                if(association.getStartInitialMultiplicity().isEmpty() || association.getStartEndMultiplicity().isEmpty()){
                    startMultiplicityText = new Text("");
                } else {
                    startMultiplicityText = new Text(association.getStartInitialMultiplicity()+".."+association.getStartEndMultiplicity());
                }

                startMultiplicityText.setX(association.getLine().getStartX() - 15);
                startMultiplicityText.setY(association.getLine().getStartY() - 5);
                drawingPane.getChildren().add(startMultiplicityText);

                Text endMultiplicityText;
                if(association.getEndStartMultiplicity().isEmpty() || association.getEndEndMultiplicity().isEmpty()){
                    endMultiplicityText = new Text("");
                } else {
                    endMultiplicityText = new Text(association.getEndStartMultiplicity()+".."+association.getEndEndMultiplicity());
                }

                endMultiplicityText.setX(association.getLine().getEndX() + 5);
                endMultiplicityText.setY(association.getLine().getEndY() - 5);
                drawingPane.getChildren().add(endMultiplicityText);

            } else if(component instanceof TextBox){
                redrawTextBox((TextBox) component);
            } else if(component instanceof DashedLine){
                redrawDashedLine((DashedLine) component);
            } else if(component instanceof Inheritance){
                Inheritance inheritance = (Inheritance) component;
                redrawInheritance((Inheritance) component);

                Text inheritanceText = new Text(inheritance.getName());
                inheritanceText.setX((inheritance.getLine().getStartX() + inheritance.getLine().getEndX()) / 2);
                inheritanceText.setY((inheritance.getLine().getStartY() + inheritance.getLine().getEndY()) / 2 - 10);
                drawingPane.getChildren().add(inheritanceText);

                Text startMultiplicityText;
                if(inheritance.getStartInitialMultiplicity().isEmpty() || inheritance.getStartEndMultiplicity().isEmpty()){
                    startMultiplicityText = new Text("");
                } else {
                    startMultiplicityText = new Text(inheritance.getStartInitialMultiplicity()+".."+inheritance.getStartEndMultiplicity());
                }

                startMultiplicityText.setX(inheritance.getLine().getStartX() - 15);
                startMultiplicityText.setY(inheritance.getLine().getStartY() - 5);
                drawingPane.getChildren().add(startMultiplicityText);

                Text endMultiplicityText;
                if(inheritance.getEndStartMultiplicity().isEmpty() || inheritance.getEndEndMultiplicity().isEmpty()){
                    endMultiplicityText = new Text("");
                } else {
                    endMultiplicityText = new Text(inheritance.getEndStartMultiplicity()+".."+inheritance.getEndEndMultiplicity());
                }

                endMultiplicityText.setX(inheritance.getLine().getEndX() + 5);
                endMultiplicityText.setY(inheritance.getLine().getEndY() - 5);
                drawingPane.getChildren().add(endMultiplicityText);

            } else if(component instanceof Aggregation){
                Aggregation aggregation = (Aggregation) component;
                redrawAggregation(aggregation);

                Text aggregationText = new Text(aggregation.getName());
                aggregationText.setX((aggregation.getLine().getStartX() + aggregation.getLine().getEndX()) / 2);
                aggregationText.setY((aggregation.getLine().getStartY() + aggregation.getLine().getEndY()) / 2 - 10);
                drawingPane.getChildren().add(aggregationText);

                Text startMultiplicityText;
                if(aggregation.getStartInitialMultiplicity().isEmpty() || aggregation.getStartEndMultiplicity().isEmpty()){
                    startMultiplicityText = new Text("");
                } else {
                    startMultiplicityText = new Text(aggregation.getStartInitialMultiplicity()+".."+aggregation.getStartEndMultiplicity());
                }

                startMultiplicityText.setX(aggregation.getLine().getStartX() - 15);
                startMultiplicityText.setY(aggregation.getLine().getStartY() - 5);
                drawingPane.getChildren().add(startMultiplicityText);

                Text endMultiplicityText;
                if(aggregation.getEndStartMultiplicity().isEmpty() || aggregation.getEndEndMultiplicity().isEmpty()){
                    endMultiplicityText = new Text("");
                } else {
                    endMultiplicityText = new Text(aggregation.getEndStartMultiplicity()+".."+aggregation.getEndEndMultiplicity());
                }

                endMultiplicityText.setX(aggregation.getLine().getEndX() + 5);
                endMultiplicityText.setY(aggregation.getLine().getEndY() - 5);
                drawingPane.getChildren().add(endMultiplicityText);

            } else if(component instanceof Composition){
                Composition composition = (Composition) component;
                redrawComposition((Composition) component);

                Text compositionText = new Text(composition.getName());
                compositionText.setX((composition.getLine().getStartX() + composition.getLine().getEndX()) / 2);
                compositionText.setY((composition.getLine().getStartY() + composition.getLine().getEndY()) / 2 - 10);
                drawingPane.getChildren().add(compositionText);

                Text startMultiplicityText;
                if(composition.getStartInitialMultiplicity().isEmpty() || composition.getStartEndMultiplicity().isEmpty()){
                    startMultiplicityText = new Text("");
                } else {
                    startMultiplicityText = new Text(composition.getStartInitialMultiplicity()+".."+composition.getStartEndMultiplicity());
                }

                startMultiplicityText.setX(composition.getLine().getStartX() - 15);
                startMultiplicityText.setY(composition.getLine().getStartY() - 5);
                drawingPane.getChildren().add(startMultiplicityText);

                Text endMultiplicityText;
                if(composition.getEndStartMultiplicity().isEmpty() || composition.getEndEndMultiplicity().isEmpty()){
                    endMultiplicityText = new Text("");
                } else {
                    endMultiplicityText = new Text(composition.getEndStartMultiplicity()+".."+composition.getEndEndMultiplicity());
                }

                endMultiplicityText.setX(composition.getLine().getEndX() + 5);
                endMultiplicityText.setY(composition.getLine().getEndY() - 5);
                drawingPane.getChildren().add(endMultiplicityText);
            }
        }
    }

    private void redrawClass(Class claz) {
        System.out.println("redrawClass called!");
        Point initialPoint = new Point(claz.getInitialPoint().getX(), claz.getInitialPoint().getY());
        double initialWidth = 120;
        VBox classBox = new VBox();
        classBox.setLayoutX(initialPoint.getX());
        classBox.setLayoutY(initialPoint.getY());
        classBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-padding: 0; -fx-background-color: #F5E49C;");
        Label classNameLabel = new Label(claz.getClassName());
        classNameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        VBox classNameBox = new VBox(classNameLabel);
        classNameBox.setMinWidth(initialWidth);
        classNameBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-padding: 5;");
        VBox attributesBox = new VBox();
        attributesBox.setMinWidth(initialWidth);
        attributesBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-padding: 5;");
        ArrayList<Attribute> attributes = claz.getAttributes();
        for (Attribute attribute : attributes) {
            Label attributeLabel = new Label(attribute.toString());
            attributesBox.getChildren().add(attributeLabel);
        }
        VBox functionsBox = new VBox();
        functionsBox.setMinWidth(initialWidth);
        functionsBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-padding: 5;");
        ArrayList<Function> functions = claz.getFunctions();
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
        elementMap.put(classBox, claz);
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

    private void redrawInterface(Interface _interface) {
        System.out.println("reDrawInterface called!");
        Point initialPoint = new Point(_interface.getInitialPoint().getX(), _interface.getInitialPoint().getY());
        double initialWidth = 120;
        VBox classBox = new VBox();
        classBox.setLayoutX(initialPoint.getX());
        classBox.setLayoutY(initialPoint.getY());
        classBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-padding: 0; -fx-background-color: #F5E49C;");
        Label classNameLabel = new Label(_interface.getClassName());
        Label InterfaceLable = new Label("  <<Interface>>");
        classNameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        InterfaceLable.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        VBox classNameBox = new VBox();
        classNameBox.getChildren().add(InterfaceLable);
        classNameBox.getChildren().add(classNameLabel);
        classNameBox.setMinWidth(initialWidth);
        classNameBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-padding: 5;");
        VBox attributesBox = new VBox();
        attributesBox.setMinWidth(initialWidth);
        attributesBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-padding: 5;");
        VBox functionsBox = new VBox();
        functionsBox.setMinWidth(initialWidth);
        functionsBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-padding: 5;");
        ArrayList<Function> functions = _interface.getFunctions();
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
        elementMap.put(classBox, _interface);
    }

    private void redrawInheritance(Inheritance inheritance) {
        System.out.println("redrawInheritance called!");

        Component startClass = inheritance.getStartClass();
        Component endClass = inheritance.getEndClass();

        if(startClass==null){
            System.out.println("start class is null!");
        }

        if(endClass==null){
            System.out.println("end class is null!");
        }

        if (startClass != null && endClass != null) {
            Line line = inheritance.getLine();
            line.setStartX(startClass.getInitialPoint().getX());
            line.setStartY(startClass.getInitialPoint().getY());
            line.setEndX(endClass.getInitialPoint().getX());
            line.setEndY(endClass.getInitialPoint().getY());
            if (!drawingPane.getChildren().contains(line)) {
                drawingPane.getChildren().add(line);
            }

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
            if(!drawingPane.getChildren().contains(arrowHead)){
                drawingPane.getChildren().add(arrowHead);
            }

            elementMap.put(line, inheritance);
        } else {
            Line line = inheritance.getLine();
            drawingPane.getChildren().remove(line);
            showWarning("Redraw Error", "One or both associated classes no longer exist at their original positions.");
        }
    }

    private void redrawAssociation(Association association) {
        System.out.println("redrawAssociation called!");

        Component startClass = association.getStartClass();
        Component endClass = association.getEndClass();

        if(startClass==null){
            System.out.println("start class is null!");
        }

        if(endClass==null){
            System.out.println("end class is null!");
        }

        if (startClass != null && endClass != null) {
            Line line = association.getLine();
            line.setStartX(startClass.getInitialPoint().getX());
            line.setStartY(startClass.getInitialPoint().getY());
            line.setEndX(endClass.getInitialPoint().getX());
            line.setEndY(endClass.getInitialPoint().getY());
            if (!drawingPane.getChildren().contains(line)) {
                drawingPane.getChildren().add(line);
            }

            elementMap.put(line, association);
        } else {
            Line line = association.getLine();
            drawingPane.getChildren().remove(line);
            showWarning("Redraw Error", "One or both associated classes no longer exist at their original positions.");
        }
    }

    public void redrawDashedLine(DashedLine dashedLine){
        System.out.println("redrawDashedLien called!");

        Component startClass = dashedLine.getStartClass();
        Component endClass = dashedLine.getEndClass();
        if(startClass==null){
            System.out.println("start class is null!");
        }

        if(endClass==null){
            System.out.println("end class is null!");
        }

        if (startClass != null && endClass != null) {
            System.out.println("Start Class name: "+startClass.getName());
            System.out.println("End Class name: "+endClass.getName());

            Line line = dashedLine.getLine();
            line.setStartX(startClass.getInitialPoint().getX());
            line.setStartY(startClass.getInitialPoint().getY());
            line.setEndX(endClass.getInitialPoint().getX());
            line.setEndY(endClass.getInitialPoint().getY());
            line.setStrokeWidth(1.0);
            line.getStrokeDashArray().addAll(5.0, 5.0);
            if (!drawingPane.getChildren().contains(line)) {
                drawingPane.getChildren().add(line);
            }

            elementMap.put(line, dashedLine);
        } else {
            Line line = dashedLine.getLine();
            drawingPane.getChildren().remove(line);
            showWarning("Redraw Error", "One or both associated classes no longer exist at their original positions.");
        }
    }

    public void redrawAggregation(Aggregation aggregation){
        System.out.println("redrawAggregation called!");

        Component startClass = aggregation.getStartClass();
        Component endClass = aggregation.getEndClass();

        if(startClass==null){
            System.out.println("start class is null!");
        }

        if(endClass==null){
            System.out.println("end class is null!");
        }

        // Inside your drawAggregation method
        if (startClass != null && endClass != null) {
            System.out.println("Start Class name: " + startClass.getName());
            System.out.println("End Class name: " + endClass.getName());

            if (startClass instanceof TextBox || endClass instanceof TextBox) {
                System.out.println("One or both components are of type TextBox");
                return;
            }

            // Draw the line
            Line line = aggregation.getLine();
            line.setStartX(startClass.getInitialPoint().getX());
            line.setStartY(startClass.getInitialPoint().getY());
            line.setEndX(endClass.getInitialPoint().getX());
            line.setEndY(endClass.getInitialPoint().getY());
            line.setStrokeWidth(1.0);
            if(!drawingPane.getChildren().contains(line)){
                drawingPane.getChildren().add(line);
            }

            // Calculate diamond coordinates
            double diamondSize = 10; // Adjust as needed for the diamond size
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
            diamond.setFill(Color.WHITE); // Fill the diamond with white
            diamond.setStroke(Color.BLACK); // Black outline for the diamond
            diamond.setStrokeWidth(2);

            // Add the diamond to the pane
            if(!drawingPane.getChildren().contains(diamond)){
                drawingPane.getChildren().add(diamond);
            }

            elementMap.put(line, aggregation);
        } else {
            Line line = aggregation.getLine();
            drawingPane.getChildren().remove(line);
            showWarning("Aggregation Error", "Both endpoints must be inside a class.");
        }
    }

    public void redrawComposition(Composition composition){
        System.out.println("redrawComposition called!");

        Component startClass = composition.getStartClass();
        Component endClass = composition.getEndClass();

        if(startClass==null){
            System.out.println("start class is null!");
        }

        if(endClass==null){
            System.out.println("end class is null!");
        }

        // Inside your drawAggregation method
        if (startClass != null && endClass != null) {
            System.out.println("Start Class name: " + startClass.getName());
            System.out.println("End Class name: " + endClass.getName());

            if (startClass instanceof TextBox || endClass instanceof TextBox) {
                System.out.println("One or both components are of type TextBox");
                return;
            }

            // Draw the line
            Line line = composition.getLine();
            line.setStartX(startClass.getInitialPoint().getX());
            line.setStartY(startClass.getInitialPoint().getY());
            line.setEndX(endClass.getInitialPoint().getX());
            line.setEndY(endClass.getInitialPoint().getY());
            line.setStrokeWidth(1.0);
            if(!drawingPane.getChildren().contains(line)){
                drawingPane.getChildren().add(line);
            }

            // Calculate diamond coordinates
            double diamondSize = 10; // Adjust as needed for the diamond size
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
            diamond.setFill(Color.BLACK); // Fill the diamond with white
            diamond.setStroke(Color.BLACK); // Black outline for the diamond
            diamond.setStrokeWidth(2);

            // Add the diamond to the pane
            if(!drawingPane.getChildren().contains(diamond)){
                drawingPane.getChildren().add(diamond);
            }

            elementMap.put(line, composition);
        } else {
            Line line = composition.getLine();
            drawingPane.getChildren().remove(line);
            showWarning("Composition Error", "Both endpoints must be inside a class.");
        }
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
