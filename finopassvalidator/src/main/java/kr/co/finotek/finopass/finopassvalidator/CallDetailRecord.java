package kr.co.finotek.finopass.finopassvalidator;

/**
 * Created by KoDeokyoon on 2017. 3. 27..
 */

public class CallDetailRecord {
    public final static int TYPE_MISSING_CALL = 1;
    public final static int TYPE_INCOMING_CALL = 2;
    public final static int TYPE_OUTGOING_CALL = 3;

    private int type;
    private String number;
    private String duration;
    private String name;
    private String startTime;
    private String finishTime;

    public CallDetailRecord(int type, String number, String duration, String name, String startTime, String fishTime) {
        this.type = type;
        this.number = number;
        this.duration = duration;
        this.name = name;
        this.startTime = startTime;
        this.finishTime = fishTime;
    }

    public static int getTypeMissingCall() {
        return TYPE_MISSING_CALL;
    }

    public static int getTypeIncomingCall() {
        return TYPE_INCOMING_CALL;
    }

    public static int getTypeOutgoingCall() {
        return TYPE_OUTGOING_CALL;
    }

    public int getType() {
        return type;
    }

    public String getNumber() {
        return number;
    }

    public String getDuration() {
        return duration;
    }

    public String getName() {
        return name;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public String getFinishTime() {
        return this.finishTime;
    }

    @Override
    public String toString() {
        return "CallLogData{" +
                "type=" + type +
                ", number='" + number + '\'' +
                ", duration='" + duration + '\'' +
                ", name='" + name + '\'' +
                ", startTime='" + startTime + '\'' +
                ", finishTime='" + finishTime + '\'' +
                '}';
    }
}