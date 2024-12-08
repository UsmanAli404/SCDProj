package BusinessLayer.OtherControllers;

import BusinessLayer.Models.Component;
import BusinessLayer.Models.Components.UseCaseDiagramComponents.*;
import BusinessLayer.Models.Diagrams.UsecaseDiagram;
import BusinessLayer.Models.Point;
import BusinessLayer.PageControllers.ProjectPageController;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class UsecaseDiagramController {

    private static final Logger LOGGER = Logger.getLogger(UsecaseDiagramController.class.getName());

    public ProjectPageController projectPageController;
    public UsecaseDiagram usecaseDiagram;
    @FXML
    private Pane drawingPane;
    private Canvas canvas;
    private GraphicsContext gc;
    List<UseCase> useCases = new ArrayList<>();
    List<UseCaseAssociation> associations = new ArrayList<>();
    List<UseCaseActor> actors = new ArrayList<>();
    List<DependencyRelationship> includeRelations = new ArrayList<>();
    List<DependencyRelationship> excludeRelations = new ArrayList<>();
    List<UseCaseSystemBoundaryBox> boundaryBoxes = new ArrayList<>();
    private final Map<UseCaseAssociation, Line> associationLines = new HashMap<>();
    private UseCaseAssociation currentlySelectedAssociation = null;
    private final Map<DependencyRelationship, DottedLineComponents> dottedLineComponentsMap = new HashMap<>();
    private DependencyRelationship currentlySelectedDependency = null;
    private String activeTool = null;
    private Point initialPoint = null;
    private final Map<Node, Object> elementMap = new HashMap<>();
    private Object currentlySelectedElement = null;
    @FXML
    public Label model_name;


    public void canvasInitialize() {
        canvas = new Canvas(drawingPane.getWidth(), drawingPane.getHeight());
        gc = canvas.getGraphicsContext2D();
        drawingPane.getChildren().add(canvas);
        canvas.widthProperty().bind(drawingPane.widthProperty());
        canvas.heightProperty().bind(drawingPane.heightProperty());
        drawingPane.setOnMouseMoved(this::trackMouseCoordinates);
        drawingPane.setOnMousePressed(this::handleMousePress);
        drawingPane.setOnMouseReleased(this::handleMouseRelease);
        drawingPane.setOnMouseClicked(this::handleMouseClick);
        drawingPane.setOnMouseDragged(this::handleMouseDrag);
        drawingPane.setFocusTraversable(true);
        drawingPane.setOnKeyPressed(this::handleKeyPress);
    }

    public void setProjectPageController(ProjectPageController projectPageController){
        this.projectPageController = projectPageController;
    }

    /*
     *
     * Handle functions
     *
     */
    private Point trackMouseCoordinates(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();

        return new Point(x, y);
    }

    @FXML
    private void handleMouseClick(MouseEvent event) {
        Point clickPoint = trackMouseCoordinates(event);
        //System.out.println("x = " + clickPoint.getX() + " y = " + clickPoint.getY());
        if (activeTool != null) {
            return;
        }
        if (event.getClickCount() == 2) {
            for (Map.Entry<DependencyRelationship, DottedLineComponents> entry : dottedLineComponentsMap.entrySet()) {
                Line line = entry.getValue().getLine();
                if (isPointNearLine(clickPoint, line, 5.0)) {
                    if (currentlySelectedDependency != null) {
                        highlightDependency(currentlySelectedDependency, Color.BLACK);
                    }
                    currentlySelectedDependency = entry.getKey();
                    highlightDependency(currentlySelectedDependency, Color.BLUE);
                    return;
                }
            }
            Object selectedElement = findElementNearPoint(clickPoint);
            if (selectedElement != null) {
                currentlySelectedElement = selectedElement;
                showDetailsIfSelected();
                return;
            }
        }
        if (event.getClickCount() == 1) {
            Object selectedElement = findElementNearPoint(clickPoint);
            if (selectedElement != null) {
                currentlySelectedElement = selectedElement;
                if (currentlySelectedDependency != null) {
                    highlightDependency(currentlySelectedDependency, Color.BLACK);
                    currentlySelectedDependency = null;
                }
            } else {
                boolean associationSelected = false;
                for (Map.Entry<UseCaseAssociation, Line> entry : associationLines.entrySet()) {
                    if (isPointNearLine(clickPoint, entry.getValue(), 5.0)) {
                        if (currentlySelectedAssociation != null) {
                            highlightAssociation(currentlySelectedAssociation, Color.BLACK);
                        }
                        currentlySelectedAssociation = entry.getKey();
                        highlightAssociation(currentlySelectedAssociation, Color.BLUE);
                        associationSelected = true;
                        break;
                    }
                }
                if (!associationSelected) {
                    for (Map.Entry<DependencyRelationship, DottedLineComponents> entry : dottedLineComponentsMap.entrySet()) {
                        if (isPointNearLine(clickPoint, entry.getValue().getLine(), 5.0)) {
                            if (currentlySelectedDependency != null) {
                                highlightDependency(currentlySelectedDependency, Color.BLACK);
                            }
                            currentlySelectedDependency = entry.getKey();
                            highlightDependency(currentlySelectedDependency, Color.BLUE);
                            return;
                        }
                    }
                }
                if (!associationSelected) {
                    if (currentlySelectedAssociation != null) {
                        highlightAssociation(currentlySelectedAssociation, Color.BLACK);
                        currentlySelectedAssociation = null;
                    }
                    if (currentlySelectedDependency != null) {
                        highlightDependency(currentlySelectedDependency, Color.BLACK);
                        currentlySelectedDependency = null;
                    }
                    currentlySelectedElement = null;
                }
            }
        }
        drawingPane.requestFocus();
    }

    @FXML
    private void handleMousePress(MouseEvent event) {
        initialPoint = trackMouseCoordinates(event);
    }

    @FXML
    private void handleMouseDrag(MouseEvent event) {
        if (currentlySelectedElement != null && activeTool == null) {
            Point currentPoint = trackMouseCoordinates(event);
            //System.out.println("x = " + currentPoint.getX() + " y = " + currentPoint.getY());
            double deltaX = currentPoint.getX() - initialPoint.getX();
            double deltaY = currentPoint.getY() - initialPoint.getY();
            if (currentlySelectedElement instanceof UseCaseActor actor) {
                actor.setInitialPoint(new Point(actor.getInitialPoint().getX() + deltaX, actor.getInitialPoint().getY() + deltaY));
            } else if (currentlySelectedElement instanceof UseCase useCase) {
                useCase.setInitialPoint(new Point(useCase.getInitialPoint().getX() + deltaX, useCase.getInitialPoint().getY() + deltaY));
            } else if (currentlySelectedElement instanceof UseCaseSystemBoundaryBox boundaryBox) {
                boundaryBox.setInitialPoint(new Point(boundaryBox.getInitialPoint().getX() + deltaX, boundaryBox.getInitialPoint().getY() + deltaY));
            }
            initialPoint = currentPoint;
            reDrawCanvas();
        }
    }

    @FXML
    private void handleMouseRelease(MouseEvent event) {
        Point releasePoint = trackMouseCoordinates(event);
        if (activeTool != null && initialPoint != null) {
            switch (activeTool) {
                case "Actor" -> drawActor(initialPoint);
                case "Exclude" -> drawExclude(initialPoint, releasePoint);
                case "Include" -> drawInclude(initialPoint, releasePoint);
                case "UseCase" -> drawUseCase(initialPoint);
                case "UseCaseAssociation" -> drawAssociation(initialPoint, releasePoint);
                case "BoundaryBox" -> drawBoundaryBox(initialPoint);
            }
        }
        initialPoint = null;
    }
    @FXML
    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.DELETE) {
            if (currentlySelectedDependency != null) {
                DottedLineComponents components = dottedLineComponentsMap.get(currentlySelectedDependency);
                if (components != null) {
                    drawingPane.getChildren().removeAll(
                            components.getLine(),
                            components.getArrowHead(),
                            components.getText()
                    );
                    dottedLineComponentsMap.remove(currentlySelectedDependency);
                    if ("include".equals(currentlySelectedDependency.getDependencyType())) {
                        includeRelations.remove(currentlySelectedDependency);
                    } else if ("exclude".equals(currentlySelectedDependency.getDependencyType())) {
                        excludeRelations.remove(currentlySelectedDependency);
                    }
                    UseCase startUseCase = currentlySelectedDependency.getStartUseCase();
                    UseCase endUseCase = currentlySelectedDependency.getEndUseCase();
                    if (startUseCase != null) {
                        startUseCase.getAssociatedRelationships().removeIf(
                                rel -> rel.getEndUseCase() == endUseCase && rel == currentlySelectedDependency
                        );
                    }
                    if (endUseCase != null) {
                        endUseCase.getAssociatedRelationships().removeIf(
                                rel -> rel.getEndUseCase() == startUseCase && rel == currentlySelectedDependency
                        );
                    }
                    currentlySelectedDependency = null;
                }
            }
            if (currentlySelectedAssociation != null) {
                Line line = associationLines.get(currentlySelectedAssociation);
                if (line != null) {
                    drawingPane.getChildren().remove(line);
                    associationLines.remove(currentlySelectedAssociation);
                    associations.remove(currentlySelectedAssociation);
                    currentlySelectedAssociation = null;
                }
            }
        }
    }

    /*
     *
     * Draw functions
     *
     */
    public void drawDottedLineWithArrow(Point startPoint, Point endPoint, String name, DependencyRelationship relationship) {
        Point[] optimalPoints = findClosestPoints(startPoint, endPoint);
        Point optimalStart = optimalPoints[0];
        Point optimalEnd = optimalPoints[1];
        Line dottedLine = new Line(optimalStart.getX(), optimalStart.getY(), optimalEnd.getX(), optimalEnd.getY());
        dottedLine.setStroke(Color.BLACK);
        dottedLine.getStrokeDashArray().addAll(10.0, 5.0);
        dottedLine.setStrokeWidth(2);
        double angle = Math.atan2(optimalEnd.getY() - optimalStart.getY(), optimalEnd.getX() - optimalStart.getX());
        double arrowLength = 10;
        double x1 = optimalEnd.getX() - arrowLength * Math.cos(angle - Math.PI / 6);
        double y1 = optimalEnd.getY() - arrowLength * Math.sin(angle - Math.PI / 6);
        double x2 = optimalEnd.getX() - arrowLength * Math.cos(angle + Math.PI / 6);
        double y2 = optimalEnd.getY() - arrowLength * Math.sin(angle + Math.PI / 6);
        Polygon arrowHead = new Polygon();
        arrowHead.getPoints().addAll(
                optimalEnd.getX(), optimalEnd.getY(),
                x1, y1,
                x2, y2
        );
        arrowHead.setFill(Color.BLACK);
        double midX = ((optimalStart.getX() + optimalEnd.getX()) / 2) - 1;
        double midY = ((optimalStart.getY() + optimalEnd.getY()) / 2) + 1;
        Text text = new Text(midX, midY, "<<" + name + ">>");
        text.setFill(Color.BLACK);
        text.getTransforms().add(new Rotate(Math.toDegrees(angle), midX, midY));
        text.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        drawingPane.getChildren().addAll(dottedLine, arrowHead, text);
        dottedLineComponentsMap.put(relationship, new DottedLineComponents(dottedLine, text, arrowHead));
    }

    public void drawUseCase(Point initial) {
        activeTool = null;
        UseCase useCase = new UseCase(usecaseDiagram.getUpcomingComponentID(), initial);
        StackPane useCasePane = new StackPane();
        useCasePane.setLayoutX(initial.getX());
        useCasePane.setLayoutY(initial.getY());
        Ellipse ellipse = new Ellipse();
        ellipse.setRadiusX(20);
        ellipse.setRadiusY(20);
        ellipse.setFill(Color.WHITE);
        ellipse.setStroke(Color.BLACK);
        ellipse.setStrokeWidth(2);
        Text text = new Text(useCase.getName());
        text.setFill(Color.BLACK);
        text.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        text.boundsInLocalProperty().addListener((obs, oldBounds, newBounds) -> {
            double textWidth = newBounds.getWidth();
            double textHeight = newBounds.getHeight();
            ellipse.setRadiusX(Math.max(50, textWidth / 2 + 20));
            ellipse.setRadiusY(Math.max(30, textHeight / 2 + 20));
        });
        useCasePane.getChildren().addAll(ellipse, text);
        drawingPane.getChildren().add(useCasePane);
        elementMap.put(useCasePane, useCase);
        useCases.add(useCase);
        usecaseDiagram.addComponent(useCase);

        //updating the model explorer
        TreeItem<String> currentUsecaseDiagramTreeItem = projectPageController.getModelTreeItemByName(usecaseDiagram.getModelName());
        if(currentUsecaseDiagramTreeItem==null){
            LOGGER.warning("Class TreeItem is null, can't add a new treeItem to it!");
        } else {
            LOGGER.info("TreeItem with class name: "+currentUsecaseDiagramTreeItem.getValue()+" found!");
            TreeItem<String> newComponentTreeItem = new TreeItem<>(useCase.getName()+" ("+useCase.getId()+")");
            currentUsecaseDiagramTreeItem.getChildren().add(newComponentTreeItem);
        }

    }

    public void drawBoundaryBox(Point initial) {
        activeTool = null;
        Rectangle rectangle = new Rectangle();
        rectangle.setX(initial.getX());
        rectangle.setY(initial.getY());
        rectangle.setWidth(300.0);
        rectangle.setHeight(350.0);
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setStroke(Color.BLACK);
        rectangle.setStrokeWidth(2.0);
        UseCaseSystemBoundaryBox boundaryBox = new UseCaseSystemBoundaryBox(initial, 350.0, 300.0);
        Label label = new Label(boundaryBox.getName());
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        label.setLayoutX(initial.getX() + rectangle.getWidth() / 2 - 40);
        label.setLayoutY(initial.getY() + 4);
        boundaryBoxes.add(boundaryBox);
        drawingPane.getChildren().addAll(rectangle, label);
        elementMap.put(rectangle, boundaryBox);
    }

    public void drawInclude(Point initial, Point finalPoint) {
        activeTool = null;
        UseCase startUseCase = checkUseCaseOnPoint(initial);
        UseCase endUseCase = checkUseCaseOnPoint(finalPoint);
        if (startUseCase == null || endUseCase == null) {
            showWarning("Error", "Use Case not found on one or both points");
            return;
        }
        if (startUseCase.hasAnyRelationshipWith(endUseCase)) {
            showWarning("Error", "A dependency relationship already exists between these Use Cases");
            return;
        }
        DependencyRelationship include = new DependencyRelationship(startUseCase, endUseCase, "include");
        includeRelations.add(include);
        startUseCase.addAssociatedRelationship(include);
        endUseCase.addAssociatedRelationship(include);
        drawDottedLineWithArrow(
                include.getStartUseCase().getInitialPoint(),
                include.getEndUseCase().getInitialPoint(),
                include.getDependencyType(),
                include
        );
    }
    public void drawExclude(Point initial, Point finalPoint) {
        activeTool = null;
        UseCase startUseCase = checkUseCaseOnPoint(initial);
        UseCase endUseCase = checkUseCaseOnPoint(finalPoint);
        if (startUseCase == null || endUseCase == null) {
            showWarning("Error", "Use Case not found on one or both points");
            return;
        }
        if (startUseCase.hasAnyRelationshipWith(endUseCase)) {
            showWarning("Error", "A dependency relationship already exists between these Use Cases");
            return;
        }
        DependencyRelationship exclude = new DependencyRelationship(startUseCase, endUseCase, "exclude");
        excludeRelations.add(exclude);
        startUseCase.addAssociatedRelationship(exclude);
        endUseCase.addAssociatedRelationship(exclude);
        drawDottedLineWithArrow(exclude.getStartUseCase().getInitialPoint(), exclude.getEndUseCase().getInitialPoint(), exclude.getDependencyType(), exclude
        );
    }

    private void reDrawExclude(DependencyRelationship exclude) {
        activeTool = null;
        UseCase startUseCase = checkUseCaseOnPoint(exclude.getStartUseCase().getInitialPoint());
        UseCase endUseCase = checkUseCaseOnPoint(exclude.getEndUseCase().getInitialPoint());
        if (startUseCase == null || endUseCase == null) {
            return;
        }
        drawDottedLineWithArrow(exclude.getStartUseCase().getInitialPoint(), exclude.getEndUseCase().getInitialPoint(), exclude.getDependencyType(), exclude);
        excludeRelations.add(exclude);
    }

    public void drawAssociation(Point initial, Point finalPoint) {
        activeTool = null;
        Boolean actor = false;
        Boolean useCase = false;
        UseCaseActor associatedActor = null;
        UseCase associatedUseCase = null;
        Object objectX = findElementNearPoint(initial);
        Object objectY = findElementNearPoint(finalPoint);
        if (objectX != null) {
            if (objectX instanceof UseCaseActor && !actor) {
                actor = true;
                associatedActor = (UseCaseActor) objectX;
            } else if (objectX instanceof UseCase && !useCase) {
                useCase = true;
                associatedUseCase = (UseCase) objectX;
            }
        } else {
            showWarning("No Actor Found", "No Actor or Use Case Found at Initial Point");
            return;
        }
        if (objectY != null) {
            if (objectY instanceof UseCaseActor && !actor) {
                actor = true;
                associatedActor = (UseCaseActor) objectY;
            } else if (objectY instanceof UseCase && !useCase) {
                useCase = true;
                associatedUseCase = (UseCase) objectY;
            } else {
                showWarning("Warning", "Cannot Have Association from Actor to Actor or Use Case to Use Case");
                return;
            }
        } else {
            showWarning("No Use Case Found", "No Actor or Use Case Found at Final Point");
            return;
        }
        UseCaseAssociation association = new UseCaseAssociation(usecaseDiagram.getUpcomingComponentID(), initial, finalPoint, associatedUseCase, associatedActor);
        Point actorInitial = association.getActor().getInitialPoint();
        Point useCaseInitial = association.getUseCase().getInitialPoint();
        Point actorPoint_y55 = new Point(actorInitial.getX(), actorInitial.getY() + 55);
        Point actorPoint_x40_y55 = new Point(actorInitial.getX() + 50, actorInitial.getY() + 55);
        Point useCasePoint_y55 = new Point(useCaseInitial.getX(), useCaseInitial.getY() + 35);
        Point useCasePoint_x40_y55 = new Point(useCaseInitial.getX() + 100, useCaseInitial.getY() + 35);
        double distance1 = calculateDistance(actorPoint_y55, useCasePoint_y55);
        double distance2 = calculateDistance(actorPoint_x40_y55, useCasePoint_y55);
        double distance3 = calculateDistance(actorPoint_x40_y55, useCasePoint_x40_y55);
        double distance4 = calculateDistance(actorPoint_y55, useCasePoint_x40_y55);
        Point start = actorPoint_y55;
        Point end = useCasePoint_y55;
        if (distance2 < distance1 && distance2 <= distance3 && distance2 <= distance4) {
            start = actorPoint_x40_y55;
            end = useCasePoint_y55;
        } else if (distance3 < distance1 && distance3 <= distance2 && distance3 <= distance4) {
            start = actorPoint_x40_y55;
            end = useCasePoint_x40_y55;
        } else if (distance4 < distance1 && distance4 <= distance2 && distance4 <= distance3) {
            start = actorPoint_y55;
            end = useCasePoint_x40_y55;
        }
        Line line = new Line();
        line.setStartX(start.getX());
        line.setStartY(start.getY());
        line.setEndX(end.getX());
        line.setEndY(end.getY());
        line.setStrokeWidth(2);
        line.setStroke(Color.BLACK);
        drawingPane.getChildren().add(line);
        associationLines.put(association, line);
        associations.add(association);
        elementMap.put(line, association);
        //updating the model explorer
        TreeItem<String> currentUsecaseDiagramTreeItem = projectPageController.getModelTreeItemByName(usecaseDiagram.getModelName());
        if(currentUsecaseDiagramTreeItem==null){
            System.out.println("Class TreeItem is null, can't add a new treeItem to it!");
        } else {
            System.out.println("TreeItem with class name: "+currentUsecaseDiagramTreeItem.getValue()+" found!");
            TreeItem<String> newComponentTreeItem = new TreeItem<>(association.getName()+" ("+association.getId()+")");
            currentUsecaseDiagramTreeItem.getChildren().add(newComponentTreeItem);
        }
    }

    public void drawActor(Point initial) {
        activeTool = null;
        UseCaseActor actor = new UseCaseActor(usecaseDiagram.getUpcomingComponentID(), initial);
        double size = 50.0;
        String svgFilePath = "src/main/resources/actor.svg";
        Image svgImage = null;
        try {
            File svgFile = new File(svgFilePath);
            File pngFile = File.createTempFile("temp-actor", ".png");
            TranscoderInput inputSvgImage = new TranscoderInput(new FileInputStream(svgFile));
            TranscoderOutput outputPngImage = new TranscoderOutput(new FileOutputStream(pngFile));
            PNGTranscoder transcoder = new PNGTranscoder();
            transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, (float) size * 2);
            transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, (float) size * 4);
            transcoder.transcode(inputSvgImage, outputPngImage);
            svgImage = new Image(pngFile.toURI().toString());
            pngFile.deleteOnExit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ImageView svgImageView = new ImageView(svgImage);
        svgImageView.setFitWidth(size);
        svgImageView.setPreserveRatio(true);
        Label actorNameLabel = new Label(actor.getName());
        actorNameLabel.setTextFill(javafx.scene.paint.Color.BLACK);
        StackPane actorPane = new StackPane();
        actorPane.setLayoutX(initial.getX());
        actorPane.setLayoutY(initial.getY());
        StackPane.setAlignment(svgImageView, Pos.TOP_CENTER);
        StackPane.setAlignment(actorNameLabel, Pos.BOTTOM_CENTER);
        actorPane.getChildren().addAll(svgImageView, actorNameLabel);
        drawingPane.getChildren().add(actorPane);
        elementMap.put(actorPane, actor);
        actors.add(actor);
        usecaseDiagram.addComponent(actor);

        //updating the model explorer
        TreeItem<String> currentUsecaseDiagramTreeItem = projectPageController.getModelTreeItemByName(usecaseDiagram.getModelName());
        if(currentUsecaseDiagramTreeItem==null){
            System.out.println("Class TreeItem is null, can't add a new treeItem to it!");
        } else {
            System.out.println("TreeItem with class name: "+currentUsecaseDiagramTreeItem.getValue()+" found!");
            TreeItem<String> newComponentTreeItem = new TreeItem<>(actor.getName()+" ("+actor.getId()+")");
            currentUsecaseDiagramTreeItem.getChildren().add(newComponentTreeItem);
        }

    }

    /*
     *
     * Redraw functions
     *
     */
    public void clearCanvas() {
        drawingPane.getChildren().clear();
        actors.clear();
        useCases.clear();
        associations.clear();
        boundaryBoxes.clear();
        includeRelations.clear();
        elementMap.clear();
        excludeRelations.clear();
        dottedLineComponentsMap.clear();
        currentlySelectedDependency = null;
        currentlySelectedAssociation = null;
    }

    public void reDrawCanvas() {
        List<UseCaseActor> actorsCopy = new ArrayList<>(actors);
        List<UseCase> useCasesCopy = new ArrayList<>(useCases);
        List<UseCaseSystemBoundaryBox> boundaryBoxesCopy = new ArrayList<>(boundaryBoxes);
        List<UseCaseAssociation> associationsCopy = new ArrayList<>(associations);
        List<DependencyRelationship> includeRelationsCopy = new ArrayList<>(includeRelations);
        List<DependencyRelationship> excludeRelationsCopy = new ArrayList<>(excludeRelations);
        clearCanvas();
        for (UseCaseActor actor : actorsCopy) {
            reDrawActor(actor);
        }
        for (UseCase useCase : useCasesCopy) {
            reDrawUseCase(useCase);
        }
        for (UseCaseSystemBoundaryBox boundaryBox : boundaryBoxesCopy) {
            reDrawBoundaryBox(boundaryBox);
        }
        for (UseCaseAssociation association : associationsCopy) {
            reDrawAssociation(association);
        }
        for (DependencyRelationship relationship : includeRelationsCopy) {
            redrawInclude(relationship);
        }
        for (DependencyRelationship relationship : excludeRelationsCopy) {
            reDrawExclude(relationship);
        }
    }

    public void reDrawActor(UseCaseActor actor) {
        activeTool = null;
        double size = 50.0;
        String svgFilePath = "src/main/resources/actor.svg";
        Image svgImage = null;
        try {
            File svgFile = new File(svgFilePath);
            File pngFile = File.createTempFile("temp-actor", ".png");
            TranscoderInput inputSvgImage = new TranscoderInput(new FileInputStream(svgFile));
            TranscoderOutput outputPngImage = new TranscoderOutput(new FileOutputStream(pngFile));
            PNGTranscoder transcoder = new PNGTranscoder();
            transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, (float) size * 2);
            transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, (float) size * 4);
            transcoder.transcode(inputSvgImage, outputPngImage);
            svgImage = new Image(pngFile.toURI().toString());
            pngFile.deleteOnExit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ImageView svgImageView = new ImageView(svgImage);
        svgImageView.setFitWidth(size);
        svgImageView.setPreserveRatio(true);
        Label actorNameLabel = new Label(actor.getName());
        actorNameLabel.setTextFill(javafx.scene.paint.Color.BLACK);
        StackPane actorPane = new StackPane();
        actorPane.setLayoutX(actor.getInitialPoint().getX());
        actorPane.setLayoutY(actor.getInitialPoint().getY());
        StackPane.setAlignment(svgImageView, Pos.TOP_CENTER);
        StackPane.setAlignment(actorNameLabel, Pos.BOTTOM_CENTER);
        actorPane.getChildren().addAll(svgImageView, actorNameLabel);
        drawingPane.getChildren().add(actorPane);
        elementMap.put(actorPane, actor);
        actors.add(actor);
    }
    public void reDrawUseCase(UseCase useCase) {
        activeTool = null;
        StackPane useCasePane = new StackPane();
        useCasePane.setLayoutX(useCase.getInitialPoint().getX());
        useCasePane.setLayoutY(useCase.getInitialPoint().getY());
        Ellipse ellipse = new Ellipse();
        ellipse.setRadiusX(100);
        ellipse.setRadiusY(50);
        ellipse.setFill(Color.WHITE);
        ellipse.setStroke(Color.BLACK);
        ellipse.setStrokeWidth(2);
        Text text = new Text(useCase.getName());
        text.setFill(Color.BLACK);
        text.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        text.boundsInLocalProperty().addListener((obs, oldBounds, newBounds) -> {
            double textWidth = newBounds.getWidth();
            double textHeight = newBounds.getHeight();
            ellipse.setRadiusX(Math.max(50, textWidth / 2 + 20));
            ellipse.setRadiusY(Math.max(30, textHeight / 2 + 20));
        });
        useCasePane.getChildren().addAll(ellipse, text);
        drawingPane.getChildren().add(useCasePane);
        elementMap.put(useCasePane, useCase);
        useCases.add(useCase);
    }
    public void reDrawBoundaryBox(UseCaseSystemBoundaryBox box) {
        activeTool = null;
        Rectangle rectangle = new Rectangle();
        rectangle.setX(box.getInitialPoint().getX());
        rectangle.setY(box.getInitialPoint().getY());
        rectangle.setWidth(box.getWidth());
        rectangle.setHeight(box.getLength());
        rectangle.setFill(Color.TRANSPARENT);
        rectangle.setStroke(Color.BLACK);
        rectangle.setStrokeWidth(2.0);
        Label label = new Label(box.getName());
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        label.setLayoutX(box.getInitialPoint().getX() + rectangle.getWidth() / 2 - 40);
        label.setLayoutY(box.getInitialPoint().getY() + 4);
        boundaryBoxes.add(box);
        drawingPane.getChildren().addAll(rectangle, label);
        elementMap.put(rectangle, box);
    }
    private void redrawInclude(DependencyRelationship include) {
        activeTool = null;
        UseCase startUseCase = checkUseCaseOnPoint(include.getStartUseCase().getInitialPoint());
        UseCase endUseCase = checkUseCaseOnPoint(include.getEndUseCase().getInitialPoint());
        if (startUseCase == null || endUseCase == null) {
            return;
        }
        drawDottedLineWithArrow(include.getStartUseCase().getInitialPoint(),include.getEndUseCase().getInitialPoint(), include.getDependencyType(),include);
        includeRelations.add(include);
    }
    private void reDrawAssociation(UseCaseAssociation association) {
        activeTool = null;
        if (findElementNearPoint(association.getActor().getInitialPoint()) instanceof UseCaseActor &&
                findElementNearPoint(association.getUseCase().getInitialPoint()) instanceof UseCase) {
            Point actorInitial = association.getActor().getInitialPoint();
            Point useCaseInitial = association.getUseCase().getInitialPoint();
            Point actorPoint_y55 = new Point(actorInitial.getX(), actorInitial.getY() + 55);
            Point actorPoint_x40_y55 = new Point(actorInitial.getX() + 50, actorInitial.getY() + 55);
            Point useCasePoint_y55 = new Point(useCaseInitial.getX(), useCaseInitial.getY() + 35);
            Point useCasePoint_x40_y55 = new Point(useCaseInitial.getX() + 100, useCaseInitial.getY() + 35);
            double distance1 = calculateDistance(actorPoint_y55, useCasePoint_y55);
            double distance2 = calculateDistance(actorPoint_x40_y55, useCasePoint_y55);
            double distance3 = calculateDistance(actorPoint_x40_y55, useCasePoint_x40_y55);
            double distance4 = calculateDistance(actorPoint_y55, useCasePoint_x40_y55);
            Point start = actorPoint_y55;
            Point end = useCasePoint_y55;
            if (distance2 < distance1 && distance2 <= distance3 && distance2 <= distance4) {
                start = actorPoint_x40_y55;
                end = useCasePoint_y55;
            } else if (distance3 < distance1 && distance3 <= distance2 && distance3 <= distance4) {
                start = actorPoint_x40_y55;
                end = useCasePoint_x40_y55;
            } else if (distance4 < distance1 && distance4 <= distance2 && distance4 <= distance3) {
                start = actorPoint_y55;
                end = useCasePoint_x40_y55;
            }
            Line line = new Line();
            line.setStartX(start.getX());
            line.setStartY(start.getY());
            line.setEndX(end.getX());
            line.setEndY(end.getY());
            line.setStrokeWidth(2);
            line.setStroke(Color.BLACK);
            drawingPane.getChildren().add(line);
            associationLines.put(association, line);
            if (!associations.contains(association)) {
                associations.add(association);
            }
            elementMap.put(line, association);
        }
    }

    /*
     *
     * Hightlight functions
     *
     */
    private void highlightAssociation(UseCaseAssociation association, Color color) {
        Line line = associationLines.get(association);
        if (line != null) {
            line.setStroke(color);
        }
    }

    private void highlightDependency(DependencyRelationship dependency, Color color) {
        DottedLineComponents components = dottedLineComponentsMap.get(dependency);
        if (components != null) {
            components.getLine().setStroke(color);
            components.getArrowHead().setFill(color);
            components.getText().setFill(color);
        }
    }

    /*
     *
     * Show details functions
     *
     */
    public void showBoundaryBoxDetails(UseCaseSystemBoundaryBox box) {
        System.out.println("box"+box.getName());
    }
    private void showUseCaseDetails(UseCase useCase) {
        Stage stage = new Stage();
        stage.setTitle("UseCase Details");
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        Label nameLabel = new Label("UseCase Name:");
        TextField nameField = new TextField(useCase.getName());
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            String newName = nameField.getText();
            useCase.setName(newName);
            reDrawCanvas();
            stage.close();
        });
        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            useCases.remove(useCase);
            elementMap.remove(useCase);
            stage.close();
            reDrawCanvas();
        });
        layout.getChildren().addAll(nameLabel, nameField, submitButton,deleteButton);
        Scene scene = new Scene(layout, 300, 150);
        stage.setScene(scene);
        stage.show();
    }

    private void showActorDetails(UseCaseActor actor) {
        Stage stage = new Stage();
        stage.setTitle("Actor Details");
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        Label nameLabel = new Label("Actor Name:");
        TextField nameField = new TextField(actor.getName());
        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> {
            String newName = nameField.getText();
            actor.setName(newName);
            reDrawCanvas();
            stage.close();
        });
        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            actors.remove(actor);
            elementMap.remove(actor);
            stage.close();
            reDrawCanvas();
        });
        layout.getChildren().addAll(nameLabel, nameField, submitButton,deleteButton);
        Scene scene = new Scene(layout, 300, 150);
        stage.setScene(scene);
        stage.show();
    }

    private void showDetailsIfSelected() {
        if (currentlySelectedElement instanceof UseCaseActor) {
            showActorDetails((UseCaseActor) currentlySelectedElement);
        } else if (currentlySelectedElement instanceof UseCase) {
            showUseCaseDetails((UseCase) currentlySelectedElement);
        } else if (currentlySelectedElement instanceof UseCaseSystemBoundaryBox) {
            showBoundaryBoxDetails((UseCaseSystemBoundaryBox) currentlySelectedElement);
        }
    }

    /*
     *
     * Other functions
     *
     */

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
    private boolean isPointNearLine(Point point, Line line, double tolerance) {
        double x1 = line.getStartX();
        double y1 = line.getStartY();
        double x2 = line.getEndX();
        double y2 = line.getEndY();
        double px = point.getX();
        double py = point.getY();
        double lineLength = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        double area = Math.abs((px - x1) * (y2 - y1) - (py - y1) * (x2 - x1));
        double distance = area / lineLength;
        boolean withinBounds = (px >= Math.min(x1, x2) && px <= Math.max(x1, x2)) &&
                (py >= Math.min(y1, y2) && py <= Math.max(y1, y2));
        return distance <= tolerance && withinBounds;
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private UseCase checkUseCaseOnPoint(Point point) {
        Object object = findElementNearPoint(point);
        return object instanceof UseCase ? (UseCase) object : null;
    }
    private Object findElementNearPoint(Point point) {
        for (Map.Entry<Node, Object> entry : elementMap.entrySet()) {
            if (entry.getKey() instanceof VBox) {
                VBox vbox = (VBox) entry.getKey();
                Bounds bounds = vbox.getBoundsInParent();
                if (bounds.contains(point.getX(), point.getY())) {
                    return entry.getValue();
                }
            }
            if (entry.getKey() instanceof StackPane) {
                StackPane stackPane = (StackPane) entry.getKey();
                Bounds bounds = stackPane.getBoundsInParent();
                if (bounds.contains(point.getX(), point.getY())) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }
    private double calculateDistance(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p2.getX() - p1.getX(), 2) + Math.pow(p2.getY() - p1.getY(), 2));
    }

    private Point[] findClosestPoints(Point p1, Point p2) {
        List<Point> p1Variants = new ArrayList<>();
        p1Variants.add(new Point(p1.getX() + 50, p1.getY() + 10));
        p1Variants.add(new Point(p1.getX() + 50, p1.getY() + 60));
        p1Variants.add(new Point(p1.getX(), p1.getY() + 40));
        p1Variants.add(new Point(p1.getX() + 100, p1.getY() + 40));
        List<Point> p2Variants = new ArrayList<>();
        p2Variants.add(new Point(p2.getX() + 50, p2.getY() + 10));
        p2Variants.add(new Point(p2.getX() + 50, p2.getY() + 60));
        p2Variants.add(new Point(p2.getX(), p2.getY() + 40));
        p2Variants.add(new Point(p2.getX() + 100, p2.getY() + 40));
        double shortestDistance = Double.MAX_VALUE;
        Point optimalP1 = null;
        Point optimalP2 = null;
        for (Point variant1 : p1Variants) {
            for (Point variant2 : p2Variants) {
                double distance = calculateDistance(variant1, variant2);
                if (distance < shortestDistance) {
                    shortestDistance = distance;
                    optimalP1 = variant1;
                    optimalP2 = variant2;
                }
            }
        }
        return new Point[]{optimalP1, optimalP2};
    }

    private void saveImage(WritableImage writableImage) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Canvas Snapshot");
        fileChooser.setInitialFileName("canvas_snapshot.png");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PNG Files", "*.png")
        );
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", file);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Save Canvas Snapshot");
            alert.setHeaderText("Image Saved");
            alert.showAndWait();
        } else {
            showWarning("Save Cancelled", "No file was selected.");
        }
    }
    public void setModelName(String modelName){
        model_name.setText("Usecase Diagram: "+modelName);
    }

    public void setDrawingPane(Pane drawingPane){
        this.drawingPane = drawingPane;
    }

    public void textBoxFunc(ActionEvent event) {
    }

    public void associationFunc(ActionEvent event) {
        activeTool = "UseCaseAssociation";
    }

    public void actorFunc(ActionEvent event) {
        activeTool = "Actor";
    }

    public void usecaseFunc(ActionEvent event) {
        activeTool = "UseCase";
    }

    public void includeFunc(ActionEvent event){
        activeTool = "Include";
    }

    public void extendFunc(ActionEvent event){
        activeTool = "Extend";
    }
}
