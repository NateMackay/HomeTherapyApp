package example.com.hometherapy;

public class AssignedExercise extends Exercise {

    // private member variables
    private int _pointValue;
    private String _status;
    private String _dueDate;

    public AssignedExercise(String _exerciseName, String _discipline, String _modality, String _assignment, String _videoLink, int _pointValue, String _status, String _dueDate) {
        super(_exerciseName, _discipline, _modality, _assignment, _videoLink);
        this._pointValue = _pointValue;
        this._status = _status;
        this._dueDate = _dueDate;
    }

    public int get_pointValue() {
        return _pointValue;
    }

    public void set_pointValue(int _pointValue) {
        this._pointValue = _pointValue;
    }

    public String get_status() {
        return _status;
    }

    public void set_status(String _status) {
        this._status = _status;
    }

    public String get_dueDate() {
        return _dueDate;
    }

    public void set_dueDate(String _dueDate) {
        this._dueDate = _dueDate;
    }
}
