package quizzer.fivestack.project.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;


@Entity
public class Quiz {
    @Id
    @GeneratedValue
    private Long quizId;

    @Column(nullable = false, length = 200)
    private String quizName;

    @Column(nullable = false, length = 1000)
    private String quizDescription;

    @Column(nullable = false, length = 20)
    private String courseCode;

    @Column(nullable = false)
    private Boolean isPublished;


    public Quiz(){

    }

    public Quiz(String quizName, String quizDescription, String courseCode, Boolean isPublished) {
        super();
        this.quizName = quizName;
        this.quizDescription = quizDescription;
        this.courseCode = courseCode;
        this.isPublished = isPublished;
    }

    //getter & setter
    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public String getQuizName() {
        return quizName;
    }

    public void setQuizName(String quizName) {
        this.quizName = quizName;
    }

    public String getQuizDescription() {
        return quizDescription;
    }

    public void setQuizDescription(String quizDescription) {
        this.quizDescription = quizDescription;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public Boolean getIsPublished() {
        return isPublished;
    }

    public void setIsPublished(Boolean isPublished) {
        this.isPublished = isPublished;
    }


    @Override
    public String toString() {
        return "Quiz [quizId=" + quizId + ", quizName=" + quizName + ", quizDescription=" + quizDescription
                + ", courseCode=" + courseCode + ", isPublished=" + isPublished + "]";
    }

    
}
