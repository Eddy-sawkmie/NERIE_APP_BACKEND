package com.nic.nerie.t_researchpaper.model;

import com.nic.nerie.t_facultyprofile.model.T_FacultyProfile;
import jakarta.persistence.*;

@Entity
@Table(name = "t_researchpaper")
public class T_ResearchPaper {
    @Id
    private String researchpaperid;

    private String title;
    private String journal;
    private int year;
    private String category;
    private String publisher;
    private String authors;
    private String link;

    @ManyToOne
    @JoinColumn(name = "facultyprofileid")
    private T_FacultyProfile facultyProfile;

    public T_ResearchPaper() {
    }

    public String getResearchpaperid() {
        return researchpaperid;
    }

    public void setResearchpaperid(String researchpaperid) {
        this.researchpaperid = researchpaperid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getJournal() {
        return journal;
    }

    public void setJournal(String journal) {
        this.journal = journal;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public T_FacultyProfile getFacultyProfile() {
        return facultyProfile;
    }

    public void setFacultyProfile(T_FacultyProfile facultyProfile) {
        this.facultyProfile = facultyProfile;
    }
}

