package fx.custom;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.util.Duration;

/**
 *
 * @author jeremy.chaut
 */
public class DigitalClock {

    private long beginTime;
    private final StringProperty s = new SimpleStringProperty();
    private int type;
    private SimpleDateFormat simpleDateFormat;
    private String format;
    private Timeline timeline;
    public static int CLOCK = 0;
    public static int CHRONOMETER = 1;

    public DigitalClock(int type) {
        this(type, null);
    }

    public DigitalClock(int type, String format) {
        beginTime = 0;
        this.type = type;
        if (format == null) {
            if (type == CLOCK) {
                this.format = "HH:mm:ss";
            } else if (type == CHRONOMETER) {
                this.format = "%02d:%02d:%02d";
            }
        }
        simpleDateFormat = new SimpleDateFormat(this.format);
        bindToTime();
    }

    private void bindToTime() {
        timeline = new Timeline(
                new KeyFrame(Duration.seconds(0), (ActionEvent actionEvent) -> {
                    if (type == CHRONOMETER) {
                        beginTime += 1000;
                        s.setValue(String.format(format,
                                        TimeUnit.MILLISECONDS.toHours(beginTime),
                                        TimeUnit.MILLISECONDS.toMinutes(beginTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(beginTime)),
                                        TimeUnit.MILLISECONDS.toSeconds(beginTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(beginTime))
                                ));
                    } else if (type == CLOCK) {
                        Calendar time = Calendar.getInstance();
                        s.setValue(simpleDateFormat.format(time.getTime()));
                    }
                }),
                new KeyFrame(Duration.seconds(1))
        );
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    public StringProperty getTimeText() {
        return s;
    }

    public void setTimeText(String s) {
        this.s.setValue(s);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public SimpleDateFormat getSimpleDateFormat() {
        return simpleDateFormat;
    }

    public void setSimpleDateFormat(SimpleDateFormat simpleDateFormat) {
        this.simpleDateFormat = simpleDateFormat;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void start() {
        beginTime = 0;
        timeline.play();
    }

    public void stop() {
        timeline.stop();
        beginTime = 0;
    }
}
