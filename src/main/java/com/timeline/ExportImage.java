package com.timeline;

import com.timeline.customNodes.RatingBox;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

/**
 * Takes a screenshot and saves the image as a PNG file on the system.
 * @author Joel Salo js225fg
 */
public class ExportImage {

    /**
     * Prints the node that's passed in as an argument and saves the image as a PNG-file.
     * Handles toggling certain UI elements inside the class to enhance the UX, allows us to toggle them only if the
     * user actually exports the timeline.
     * @param node - node to print
     * @param switchThemeBtn - pointer to the switch theme button in order to hide during screen shot
     * @param img - pointer to the image view node to hide during screen shot
     */
    public static void capture(Node node, Button switchThemeBtn, ImageView img, Button setToddBtn, ImageView totd,
                               RatingBox rating) {

        FileChooser fileChooser = new FileChooser();

        // set extension filter
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("png files (*.png)", "*.png"));

        // prompt user to select a file
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try {
                // hide the zoomslider, switchthemebutton, setToddBtn, totd (message) and ratingbox (stars)
                switchThemeBtn.setVisible(false);
                img.setVisible(false);
                setToddBtn.setVisible(false);
                totd.setVisible(false);
                rating.setVisible(false);

                // get width and height
                int width = (int) node.getLayoutBounds().getMaxX();
                int height = (int) node.getLayoutBounds().getMaxY();

                WritableImage writableImage = new WritableImage(width, height);
                WritableImage snapshot = node.snapshot(null, writableImage);
                RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);

                // write the snapshot to the chosen file
                ImageIO.write(renderedImage, "png", file);

                // toggle the zoomslider, switchthemebutton, setToddBtn, totd (message) and ratingbox (stars) back on
                switchThemeBtn.setVisible(true);
                img.setVisible(true);
                setToddBtn.setVisible(true);
                totd.setVisible(true);
                rating.setVisible(true);
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
