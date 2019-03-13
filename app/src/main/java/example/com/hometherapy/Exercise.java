package example.com.hometherapy;

public class Exercise {

    // private member variables
    private String _exerciseName;
    private String _discipline;
    private String _modality;
    private String _assignment;
    private String _videoLink;

    // constructors
    public Exercise(String _exerciseName, String _discipline, String _modality, String _assignment, String _videoLink) {
        this._exerciseName = _exerciseName;
        this._discipline = _discipline;
        this._modality = _modality;
        this._assignment = _assignment;
        this._videoLink = _videoLink;
    }

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
                " _exerciseName='" +  _exerciseName + '\'' +
                ", _discipline='" + _discipline + '\'' +
                ", _modality='" + _modality + '\'' +
                ", _assignment='" + _assignment + '\'' +
                ", _videoLink='" + _videoLink + '\'' +
                '}';
    }

}
