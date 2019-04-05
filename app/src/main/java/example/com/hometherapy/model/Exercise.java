package example.com.hometherapy.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Model class data structure for exercise object.
 * @author Team06
 * @version 1.0
 * @since 2019-03-19
 */
public class Exercise {

    // private member variables
    private String _exerciseID;
    private String _exerciseName;
    private String _discipline;
    private String _modality;
    private String _assignment;
    private String _videoLink;

    // constructors
    public Exercise() {
        // default constructor needed for data snapshot
    }

    public Exercise(String _exerciseID, String _exerciseName, String _discipline, String _modality, String _assignment, String _videoLink) {
        this._exerciseID = _exerciseID;
        this._exerciseName = _exerciseName;
        this._discipline = _discipline;
        this._modality = _modality;
        this._assignment = _assignment;
        this._videoLink = _videoLink;
    }

    public Exercise (Exercise exercise) {
        this._exerciseID = exercise._exerciseID;
        this._exerciseName = exercise._exerciseName;
        this._discipline = exercise._discipline;
        this._modality = exercise._modality;
        this._assignment = exercise._assignment;
        this._videoLink = exercise._videoLink;
    }

    public String get_exerciseID() { return _exerciseID; }

    public void set_exerciseID(String _exerciseID) { this._exerciseID = _exerciseID; }

    public String get_exerciseName() {
        return _exerciseName;
    }

    public void set_exerciseName(String _exerciseName) {
        this._exerciseName = _exerciseName;
    }

    public String get_discipline() {
        return _discipline;
    }

    public void set_discipline(String _discipline) {
        this._discipline = _discipline;
    }

    public String get_modality() {
        return _modality;
    }

    public void set_modality(String _modality) {
        this._modality = _modality;
    }

    public String get_assignment() {
        return _assignment;
    }

    public void set_assignment(String _assignment) {
        this._assignment = _assignment;
    }

    public String get_videoLink() {
        return _videoLink;
    }

    public void set_videoLink(String _videoLink) {
        this._videoLink = _videoLink;
    }

    // toString
    @Override
    public String toString() {
        return "Exercise{" +
                "_exerciseID='" + _exerciseID + '\'' +
                ", _exerciseName='" + _exerciseName + '\'' +
                ", _discipline='" + _discipline + '\'' +
                ", _modality='" + _modality + '\'' +
                ", _assignment='" + _assignment + '\'' +
                ", _videoLink='" + _videoLink + '\'' +
                '}';
    }

    /**
     * stores the exercise into a Map object, and returns the Map object.
     * @return returns a Map object of the exercise.
     */
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("_exerciseID", _exerciseID);
        result.put("_exerciseName", _exerciseName);
        result.put("_discipline", _discipline);
        result.put("_modality", _modality);
        result.put("_assignment", _assignment);
        result.put("_videoLink", _videoLink);

        return result;
    }
}
