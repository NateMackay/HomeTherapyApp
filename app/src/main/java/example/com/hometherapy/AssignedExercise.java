package example.com.hometherapy;

public class AssignedExercise extends Exercise {

    // private member variables
    private String _assignedUserEmail;
    private int _pointValue;
    private String _status;

    // for future use - idea is to reset this to false at Midnight of each day
    // possible solution is to create an observable class that this would observe
    // that would track time and update this to false for each day
    private boolean _completedToday;

    public AssignedExercise(String _exerciseName, String _discipline, String _modality,
                            String _assignment, String _videoLink,
                            String _assignedUserEmail, int _pointValue,
                            String _status, boolean _completedToday) {
        super(_exerciseName, _discipline, _modality, _assignment, _videoLink);
        this._assignedUserEmail = _assignedUserEmail;
        this._pointValue = _pointValue;
        this._status = _status;
        this._completedToday = _completedToday;
    }

    public String get_assignedUserEmail() {
        return _assignedUserEmail;
    }

    public void set_assignedUserEmail(String _assignedUserEmail) {
        this._assignedUserEmail = _assignedUserEmail;
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

    public boolean is_completedToday() {
        return _completedToday;
    }

    public void set_completedToday(boolean _completedToday) {
        this._completedToday = _completedToday;
    }

    @Override
    public String toString() {
        return "AssignedExercise{" +
                "_pointValue=" + _pointValue +
                ", _status='" + _status + '\'' +
                ", _completedToday=" + _completedToday +
                '}';
    }

}
