package exp.com.sobot.Models;


public class Note {

    private String note;
    private String timeStamp;

    public Note(String note, String timeStamp) {
        this.note = note;
        this.timeStamp = timeStamp;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
