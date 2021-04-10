package com.timeline;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Class containing methods for changing and managing different scenes.
 * @author Joel Salo js225fg
 */
public class SceneMethods {

    /**
     * Changes to the desired scene on a new stage.
     * @param view - the scene to switch to (enum)
     * @param previousScene - node from the scene we want to close
     * @author Susanna Persson sp222xw
     * @author Joel Salo js225fg
     */
    public static void changeScene(View view, Node previousScene) {
        Parent root;

        // Load desired view
        FXMLLoader loader = getLoader(view);

        try {
            root = loader.load();

            // Show second scene in new window
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.setResizable(false);
            newStage.show();

            // Close current stage
            Stage oldStage = (Stage) previousScene.getScene().getWindow();
            oldStage.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the desired view as a popup window
     * Code taken from that made by Susanna Persson and Joel Salo
     * @param view - the scene to switch to (enum)
     * @author Susanna Persson sp222xw
     * @author Joel Salo js225fg
     * @modified Stefan & Victor 05-14 - Now returns loader, to fix bug in addEventPopup & editTimeline
     */
    public static FXMLLoader openPopup(View view, boolean isDarkTheme, String darkBg, String lightBg) {
        Parent root;
        // Properties required to move the window without window panel
        AtomicReference<Double> balancedXPos = new AtomicReference<>((double) 0);
        AtomicReference<Double> balancedYPos = new AtomicReference<>((double) 0);

        // Load desired view
        FXMLLoader loader = getLoader(view);

        try {
            root = loader.load();

            if (isDarkTheme) {
                root.setStyle(darkBg);
            } else {
                root.setStyle(lightBg);
            }

            // Show second scene in new window
            Stage newStage = new Stage();

            root.setOnMousePressed(event -> {
                balancedXPos.set(event.getSceneX());
                balancedYPos.set(event.getSceneY());
            });

            root.setOnMouseDragged(event -> {
                newStage.setX(event.getScreenX() - balancedXPos.get());
                newStage.setY(event.getScreenY() - balancedYPos.get());
            });

            newStage.setScene(new Scene(root));
            newStage.initStyle(StageStyle.UNDECORATED);
            newStage.setResizable(false);
            newStage.show();
        }
        catch (IOException e) {
        }
        return loader;
    }

    /**
     * Returns the controller for the desired view.
     * @param view - the scene to switch to (enum)
     * @return - controller for the specified view
     * @author Joel Salo js225fg
     */
    public static Object getController(View view) {
        FXMLLoader loader = getLoader(view);

        // FXML has to be loaded before accessing the controller
        try {
            Parent root;
            root = loader.load();
        }
        catch (IOException e) {
        }

        return loader.getController();
    }

    /**
     * Returns the loader for the requested view.
     * @param view - the view
     * @return - the loader
     * @author Joel Salo js225fg
     */
    public static FXMLLoader getLoader(View view) {
        return new FXMLLoader(App.class.getResource(view.getFxml()));
    }
}
