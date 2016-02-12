/*
 * Copyright (c) 2013 by Gerrit Grunwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.als.notification;

import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.Toolkit;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * Created by User: hansolo Date: 01.07.13 Time: 07:10
 */
public class Notification {

    public static final Image INFO_ICON = new Image(Notifier.class.getResourceAsStream("/resources/images/info.png"));
    public static final Image WARNING_ICON = new Image(Notifier.class.getResourceAsStream("/resources/images/warning.png"));
    public static final Image SUCCESS_ICON = new Image(Notifier.class.getResourceAsStream("/resources/images/success.png"));
    public static final Image ERROR_ICON = new Image(Notifier.class.getResourceAsStream("/resources/images/error.png"));
    public final String TITLE;
    public final String MESSAGE;
    public final Image IMAGE;

    public Notification(final String TITLE, final String MESSAGE) {
        this(TITLE, MESSAGE, null);
    }

    public Notification(final String MESSAGE, final Image IMAGE) {
        this("", MESSAGE, IMAGE);
    }

    public Notification(final String TITLE, final String MESSAGE, final Image IMAGE) {
        this.TITLE = TITLE;
        this.MESSAGE = MESSAGE;
        this.IMAGE = IMAGE;
    }

    public enum Notifier {

        INSTANCE;

        private static final double ICON_WIDTH = 24;
        private static final double ICON_HEIGHT = 24;
        private static double width = 300;
        private static double height = 80;
        private static double offsetX = 0;
        private static double offsetY = 25;
        private static double spacingY = 5;
        private static Pos popupLocation = Pos.TOP_RIGHT;
        private static Stage stageRef = null;
        private Duration popupLifetime;
        private Stage stage;
        private Scene scene;
        private ObservableList<Popup> popups;

        private Notifier() {
            init();
            initGraphics();
        }

        private void init() {
            popupLifetime = Duration.millis(5000);
            popups = FXCollections.observableArrayList();
        }

        private void initGraphics() {
            scene = new Scene(new Region());
            scene.setFill(null);
            scene.getStylesheets().add(getClass().getResource("notifier.css").toExternalForm());

            stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);
        }

        public static void setPopupLocation(final Stage STAGE_REF, final Pos POPUP_LOCATION) {
            if (null != STAGE_REF) {
                INSTANCE.stage.initOwner(STAGE_REF);
                Notifier.stageRef = STAGE_REF;
            }
            Notifier.popupLocation = POPUP_LOCATION;
        }

        public static void setNotificationOwner(final Stage OWNER) {
            INSTANCE.stage.initOwner(OWNER);
        }

        public static void setOffsetX(final double OFFSET_X) {
            Notifier.offsetX = OFFSET_X;
        }

        public static void setOffsetY(final double OFFSET_Y) {
            Notifier.offsetY = OFFSET_Y;
        }

        public static void setWidth(final double WIDTH) {
            Notifier.width = WIDTH;
        }

        public static void setHeight(final double HEIGHT) {
            Notifier.height = HEIGHT;
        }

        public static void setSpacingY(final double SPACING_Y) {
            Notifier.spacingY = SPACING_Y;
        }

        public void stop() {
            popups.clear();
            stage.close();
        }

        public Duration getPopupLifetime() {
            return popupLifetime;
        }

        public void setPopupLifetime(final Duration POPUP_LIFETIME) {
            popupLifetime = Duration.millis(clamp(2000, 20000, POPUP_LIFETIME.toMillis()));
        }

        public void notify(final Notification NOTIFICATION) {
            preOrder(NOTIFICATION);
            showPopup(NOTIFICATION);
        }

        public void notify(final String TITLE, final String MESSAGE, final Image IMAGE) {
            notify(new Notification(TITLE, MESSAGE, IMAGE));
        }

        public void notifyInfo(final String TITLE, final String MESSAGE) {
            notify(new Notification(TITLE, MESSAGE, Notification.INFO_ICON));
        }

        public void notifyWarning(final String TITLE, final String MESSAGE) {
            notify(new Notification(TITLE, MESSAGE, Notification.WARNING_ICON));
        }

        public void notifySuccess(final String TITLE, final String MESSAGE) {
            notify(new Notification(TITLE, MESSAGE, Notification.SUCCESS_ICON));
        }

        public void notifyError(final String TITLE, final String MESSAGE) {
            notify(new Notification(TITLE, MESSAGE, Notification.ERROR_ICON));
        }

        private double clamp(final double MIN, final double MAX, final double VALUE) {
            if (VALUE < MIN) {
                return MIN;
            }
            if (VALUE > MAX) {
                return MAX;
            }
            return VALUE;
        }

        private void preOrder(final Notification NOTIFICATION) {
            FontLoader fontLoader = Toolkit.getToolkit().getFontLoader();
            Label temp = new Label(NOTIFICATION.MESSAGE, new ImageView(NOTIFICATION.IMAGE));
            temp.getStyleClass().add("message");
            width = fontLoader.computeStringWidth(temp.getText(), temp.getFont()) + ICON_WIDTH + 30;
            if (popups.isEmpty()) {
                return;
            }
            for (int i = 0; i < popups.size(); i++) {
                switch (popupLocation) {
                    case TOP_LEFT:
                    case TOP_CENTER:
                    case TOP_RIGHT:
                        popups.get(i).setY(popups.get(i).getY() + height + spacingY);
                        break;
                    default:
                        popups.get(i).setY(popups.get(i).getY() - height - spacingY);
                }
            }
        }

        private void showPopup(final Notification NOTIFICATION) {
            Label title = new Label(NOTIFICATION.TITLE);
            title.getStyleClass().add("title");

            ImageView icon = new ImageView(NOTIFICATION.IMAGE);
            icon.setFitWidth(ICON_WIDTH);
            icon.setFitHeight(ICON_HEIGHT);

            Label message = new Label(NOTIFICATION.MESSAGE, icon);
            message.getStyleClass().add("message");

            VBox popupLayout = new VBox();
            popupLayout.setSpacing(10);
            popupLayout.setPadding(new Insets(10, 10, 10, 10));
            popupLayout.getChildren().addAll(title, message);

            StackPane popupContent = new StackPane();
            popupContent.setPrefSize(width, height);
            popupContent.getStyleClass().add("notification");
            popupContent.getChildren().addAll(popupLayout);

            final Popup POPUP = new Popup();
            POPUP.setX(getX());
            POPUP.setY(getY());
            POPUP.getContent().add(popupContent);

            popups.add(POPUP);

// Add a timeline for popup fade out
            KeyValue fadeOutBegin = new KeyValue(POPUP.opacityProperty(), 1.0);
            KeyValue fadeOutEnd = new KeyValue(POPUP.opacityProperty(), 0.0);

            KeyFrame kfBegin = new KeyFrame(Duration.ZERO, fadeOutBegin);
            KeyFrame kfEnd = new KeyFrame(Duration.millis(500), fadeOutEnd);

            Timeline timeline = new Timeline(kfBegin, kfEnd);
            timeline.setDelay(popupLifetime);
            timeline.setOnFinished(actionEvent -> Platform.runLater(() -> {
                POPUP.hide();
                popups.remove(POPUP);
                this.stop();
            }));
             // Move popup to the right during fade out
            // POPUP.opacityProperty().addListener((observableValue, oldOpacity, opacity) -> popup.setX(popup.getX() + (1.0 - opacity.doubleValue()) * popup.getWidth()));
            if (stage.isShowing()) {
                stage.toFront();
            } else {
                stage.show();
            }

            POPUP.show(stage);
            timeline.play();
        }

        private double getX() {
            if (null == stageRef) {
                return calcX(0.0, Screen.getPrimary().getBounds().getWidth());
            }

            return calcX(stageRef.getX(), stageRef.getWidth());
        }

        private double getY() {
            if (null == stageRef) {
                return calcY(0.0, Screen.getPrimary().getBounds().getHeight());
            }

            return calcY(stageRef.getY(), stageRef.getHeight());
        }

        private double calcX(final double LEFT, final double TOTAL_WIDTH) {
            switch (popupLocation) {
                case TOP_LEFT:
                case CENTER_LEFT:
                case BOTTOM_LEFT:
                    return LEFT + offsetX;
                case TOP_CENTER:
                case CENTER:
                case BOTTOM_CENTER:
                    return LEFT + (TOTAL_WIDTH - width) * 0.5 - offsetX;
                case TOP_RIGHT:
                case CENTER_RIGHT:
                case BOTTOM_RIGHT:
                    return LEFT + TOTAL_WIDTH - width - offsetX;
                default:
                    return 0.0;
            }
        }

        private double calcY(final double TOP, final double TOTAL_HEIGHT) {
            switch (popupLocation) {
                case TOP_LEFT:
                case TOP_CENTER:
                case TOP_RIGHT:
                    return TOP + offsetY;
                case CENTER_LEFT:
                case CENTER:
                case CENTER_RIGHT:
                    return TOP + (TOTAL_HEIGHT - height) / 2 - offsetY;
                case BOTTOM_LEFT:
                case BOTTOM_CENTER:
                case BOTTOM_RIGHT:
                    return TOP + TOTAL_HEIGHT - height - offsetY;
                default:
                    return 0.0;
            }
        }
    }
}
