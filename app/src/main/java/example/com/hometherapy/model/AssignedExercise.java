package example.com.hometherapy.model;

import java.util.HashMap;
import java.util.Map;

import example.com.hometherapy.model.Exercise;

/**
 * Model class for assigned exercise
 * This is a class / data structure for an assigned exercise.
 * This is the bridge between the user (client) and the exercise.
 * This extends Exercise, so that an Assigned Exercise object
 * not only inherits the fields that an Exercise has,
 * but it extends it and also holds the email address
 * of the user/client the exercise is assigned to,
 * the point value assigned, as well as the status
 * of the assigned exercise. It also holds a boolean value to
 * indicate if the exercise has been completed by the client.
 * @author Team06
 * @version 1.0
 * @since 2019-03-19
 * {@link Exercise}
 */
public class AssignedExercise extends Exercise {

    // private member variables
    private String _assignedExerciseID;
    private String _assignedUserID;
    private Integer _pointValue;
    private String _status;
    private Boolean _completedToday;

    // constructors
    public AssignedExercise() {
        // default constructor for data snapshot
    }

    public AssignedExercise(String _exerciseID, String _exerciseName, String _discipline, String _modality,
                            String _assignment, String _videoLink, String _assignedExerciseID, String _assignedUserID,
                            Integer _pointValue, String _status, Boolean _completedToday) {

        super(_exerciseID, _exerciseName, _discipline, _modality, _assignment, _videoLink);
        this._assignedExerciseID = _assignedExerciseID;
        this._assignedUserID = _assignedUserID;
        this._pointValue = _pointValue;
        this._status = _status;
        this._completedToday = _completedToday;
    }

    // getters and setters
    public String get_assignedExerciseID() { return _assignedExerciseID; }
    public String get_assignedUserID() { return _assignedUserID; }
    public Integer get_pointValue() {
        return _pointValue;
    }
    public String get_status() {
        return _status;
    }
    public Boolean get_completedToday() { return _completedToday; }

    public void set_assignedExerciseID(String _assignedExerciseID) {
        this._assignedExerciseID = _assignedExerciseID;
    }

    public void set_assignedUserID(String _assignedUserID) {
        this._assignedUserID = _assignedUserID;
    }

    public void set_completedToday(Boolean _completedToday) {
        this._completedToday = _completedToday;
    }

    public void set_pointValue(Integer _pointValue) {
        this._pointValue = _pointValue;
    }

    public void set_status(String _status) {
        this._status = _status;
    }

    // to String override
    @Override
    public String toString() {
        return "AssignedExercise{" +
                "_assignedExerciseID='" + _assignedExerciseID + '\'' +
                ", _assignedUserID='" + _assignedUserID + '\'' +
                ", _pointValue=" + _pointValue +
                ", _status='" + _status + '\'' +
                ", _completedToday=" + _completedToday +
                ", _exerciseID=" + this.get_exerciseID() +
                ", _exerciseName=" + this.get_exerciseName() +
                ", _discipline=" + this.get_discipline() +
                ", _modality=" + this.get_modality() +
                ", _assignment=" + this.get_assignment() +
                ", _videoLink=" + this.get_videoLink() +
                '}';
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("_exerciseID", this.get_exerciseID());
        result.put("_exerciseName", this.get_exerciseName());
        result.put("_discipline", this.get_discipline());
        result.put("_modality", this.get_modality());
        result.put("_assignment", this.get_assignment());
        result.put("_videoLink", this.get_videoLink());
        result.put("_assignedExerciseID", _assignedExerciseID);
        result.put("_assignedUserID", _assignedUserID);
        result.put("_pointValue", _pointValue);
        result.put("_status", _status);
        result.put("_completedToday", _completedToday);

        return result;
    }

}
