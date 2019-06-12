package com.model;

import java.util.ArrayList;

/**
 * @author Michael
 */
public class user {

    String name, title;
    ArrayList<String> portfolioLinks, specialties;
    int hourlyRate;

    public user(String name, String title, ArrayList<String> portfolioLinks, ArrayList<String> specialties, int hourlyRate) {
        this.name = name;
        this.title = title;
        this.portfolioLinks = portfolioLinks;
        this.specialties = specialties;
        this.hourlyRate = hourlyRate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<String> getPortfolioLinks() {
        return portfolioLinks;
    }

    public void setPortfolioLinks(ArrayList<String> portfolioLinks) {
        this.portfolioLinks = portfolioLinks;
    }

    public ArrayList<String> getSpecialties() {
        return specialties;
    }

    public void setSpecialties(ArrayList<String> specialties) {
        this.specialties = specialties;
    }

    public int getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(int hourlyRate) {
        this.hourlyRate = hourlyRate;
    }
}
