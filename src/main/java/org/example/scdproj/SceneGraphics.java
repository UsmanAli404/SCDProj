package org.example.scdproj;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class SceneGraphics extends Application{
    @Override
    public void start(Stage stage) throws Exception{
        stage.setTitle("Hello App!");
        stage.setWidth(500);
        stage.setHeight(420);

        Image icon = new Image("me.png");
        stage.getIcons().add(icon);

        Group root = new Group();

        double stageWidth = stage.getWidth();
        double stageHeight = stage.getHeight();

        Text label = new Text("Hello World!");
        label.setFill(Color.WHITE);
        label.setFont(Font.font("Helvetica", 20));
        label.setX(stageWidth/2-label.getLayoutBounds().getWidth()/2);
        label.setY(stageHeight/2-label.getLayoutBounds().getHeight()/2);

        root.getChildren().add(label);

        Line underLine = new Line();
        underLine.setStroke(Color.WHITE);
        underLine.setStrokeWidth(2);
        underLine.setStartX(label.getX());
        underLine.setStartY(label.getY()+10);
        underLine.setEndX(label.getX()+label.getLayoutBounds().getWidth());
        underLine.setEndY(label.getY()+10);

        root.getChildren().add(underLine);

        Rectangle enclosingRect = new Rectangle();
        enclosingRect.setStroke(Color.WHITE);
        enclosingRect.setStrokeWidth(2);
        enclosingRect.setX(label.getX()-4);
        enclosingRect.setY(label.getY()-label.getLayoutBounds().getHeight());
        enclosingRect.setWidth(label.getLayoutBounds().getWidth()+8);
        enclosingRect.setHeight(label.getLayoutBounds().getHeight()+16);
        enclosingRect.setFill(Color.TRANSPARENT);

        root.getChildren().add(enclosingRect);

        Polygon triangle = new Polygon();
        triangle.getPoints().setAll(
                0.0, 0.0,
                50.0, 50.0,
                0.0, 100.0
        );
        triangle.setFill(Color.RED);

        root.getChildren().add(triangle);

        Circle circle = new Circle();
        circle.setFill(Color.BLUE);
        circle.setCenterX(12.0);
        circle.setCenterY(12.0);
        circle.setRadius(12.0);

        root.getChildren().add(circle);

        Image img = new Image("me.png");
        ImageView myImg = new ImageView(img);
        myImg.setX(100);
        myImg.setY(100);
        myImg.setFitHeight(200);
        myImg.setFitWidth(200);
        myImg.setPreserveRatio(true);

        root.getChildren().add(myImg);

        Scene sc1 = new Scene(root, Color.BLACK);

        stage.setScene(sc1);
        stage.show();
    }

    public static void main(String[] args){
        launch(args);
    }
}