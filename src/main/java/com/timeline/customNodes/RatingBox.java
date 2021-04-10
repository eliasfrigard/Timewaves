package com.timeline.customNodes;

import com.timeline.AppProperties;
import com.timeline.Timeline;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.controlsfx.control.Rating;
import sql.RatingMethods;

public class RatingBox extends VBox {
    public Rating rating;
    private EventHandler<MouseEvent> handles;
    private Timeline tl;

    public RatingBox() {
        this.setViewOrder(-1);
        rating = new Rating(5);
        rating.setOrientation(Orientation.HORIZONTAL);
        rating.setUpdateOnHover(false);

        this.getChildren().add(rating);
    }

    public void setTL(Timeline tl) {
        this.tl = tl;
        rating.setRating(tl.getRating());
    }

    public boolean tlIsNull() {
        return tl == null;
    }

    /**
     * Adds an event handler to the ratings control that will send information to a select timeline
     */
    public void addEventHandler() {

        if (handles != null) {
            System.err.println("There is already an active handler!");
        } else if (tlIsNull()) {
            System.err.println("Timeline for this object must be set first!");
        } else {
            handles = new RatingHandler(tl);
            rating.setOnMouseClicked(handles);
        }
    }

    /**
     * Removes the event handler from rating
     */
    public void removeEventHandler() {
        if (handles == null) return;

        rating.removeEventHandler(MouseEvent.MOUSE_CLICKED, handles);
        handles = null;
    }

    private class RatingHandler implements EventHandler<MouseEvent> {
        private Timeline timelineBinding;

        public RatingHandler(Timeline tl) {
            timelineBinding = tl;
        }

        @Override
        public void handle(MouseEvent event) {
            if (event.getSource() == rating) {
                timelineBinding.setRating(rating.getRating());
            }
        }
    }

}
