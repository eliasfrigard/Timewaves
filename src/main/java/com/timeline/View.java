package com.timeline;

/**
 * Enum for managing the different views of the application.
 * @author Joel Salo js225fg
 */
public enum View {

    LOGIN {
        @Override
        String getFxml() {
            return "login.fxml";
        }
    },
    EDITTIMELINE {
      @Override
      String getFxml() { return "EditTimelinePopup.fxml"; }
    },
    REGISTER {
        @Override
        String getFxml() {
            return "RegisterView.fxml";
        }
    },
    PRIMARY {
        @Override
        String getFxml() {
            return "primaryWindow.fxml";
        }
    },
    CREATENEW {
        @Override
        String getFxml() {
            return "CreateTimelinePopup.fxml";
        }
    },
    ADDEVENT {
        @Override
        String getFxml() {
            return "CreateEventPopup.fxml";
        }
    },
    SHOWEVENT {
        @Override
        String getFxml() {
            return "ShowEventPopOver.fxml";
        }
    },
    EDITEVENT {
        @Override
        String getFxml() { return "ShowEditEventPopOver.fxml"; }
    };



    abstract String getFxml();
}
