
package com.aman.firebase.app.notesshare;

public class FriendlyMessage {

    private String text;
    private String name;
    private String photoUrl;
    private String semester;
    private String subject;
    private String college;
    private String course;

    public FriendlyMessage() {  }

    public FriendlyMessage(String text, String name, String photoUrl,String college,String course,String semester,String subject) {
        this.text = text;
        this.name = name;
        this.college = college;
        this.course = course;
        this.photoUrl = photoUrl;
        this.semester = semester;
        this.subject = subject;

    }







    public String getCollege() {
        return college;
    }

    public String getCourse() {
        return course;
    }

    public String getSubject() {
        return subject;
    }



    public String getSemester() {
        return semester;
    }


    public void setOthers(String college, String course, String semester,String subject) {
        this.college = college;
        this.course = course;
        this.semester = semester;
        this.subject = subject;

    }



    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
